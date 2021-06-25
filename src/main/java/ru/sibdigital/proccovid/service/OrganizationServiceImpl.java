package ru.sibdigital.proccovid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsOrganizationDto;
import ru.sibdigital.proccovid.model.ClsTypeRequestSettings;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.classifier.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsPrescriptionRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsTypeRequestRepo;
import ru.sibdigital.proccovid.repository.document.DocRequestRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegPrescriptionTextRepo;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSpecification;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSpecification;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final static Logger actualizationOrganizationsLogger = LoggerFactory.getLogger("actualizationOrganizationsLogger");
    private final static Logger actualizationFilesLogger = LoggerFactory.getLogger("actualizationFilesLogger");

    private final static int pageSize = 100;

    @Value("${upload.path}")
    private String uploadingDir;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private DBActualizeService dbActualizeService;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @Autowired
    private ClsPrescriptionRepo clsPrescriptionRepo;

    @Autowired
    private RegPrescriptionTextRepo regPrescriptionTextRepo;

    @Override
    public Page<ClsOrganization> getOrganizationsByCriteria(ClsOrganizationSearchCriteria searchCriteria, int page, int size) {
        ClsOrganizationSpecification specification = new ClsOrganizationSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<ClsOrganization> clsOrganizationsPage = clsOrganizationRepo.findAll(specification, PageRequest.of(page, size, Sort.by("name")));
        return clsOrganizationsPage;
    }

    public void markOrganizationAsDeleted() {
        dbActualizeService.markOrganizationAsDeleted();
    }

    @Async
    public void actualizeOrganizations() {
        long countActualized = 0;
        try {
            actualizationOrganizationsLogger.info("Процесс актуализации организаций начат");
            ClsOrganizationSpecification specification = new ClsOrganizationSpecification();
            ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
//            searchCriteria.setInn("032500136644"); // TODO тест
            specification.setSearchCriteria(searchCriteria);
            long total = clsOrganizationRepo.count(specification);
            actualizationOrganizationsLogger.info("Количество организаций: {}", total);

            int pages = (int) (((total - 1) / pageSize) + 1);
            for (int i = 0; i < pages; i++) {
                Page<ClsOrganization> page = clsOrganizationRepo.findAll(specification, PageRequest.of(i, pageSize, Sort.by("id")));
                countActualized += dbActualizeService.actualizeOrganizations(page.getContent());
                actualizationOrganizationsLogger.info("Актулизировано: {}", countActualized);
            }
            actualizationOrganizationsLogger.info("Процесс актуализации организаций окончен");
        } catch (Exception e) {
            actualizationOrganizationsLogger.info("Ошибка! Процесс актуализации организаций прерван");
            actualizationOrganizationsLogger.info(e.getMessage());
            e.printStackTrace();
        }
        actualizationOrganizationsLogger.info("Итого актуализировано: {}", countActualized);
    }

    @Async
    public void actualizeFiles() {
        long countActualized = 0;
        try {
            actualizationFilesLogger.info("Процесс актуализации файлов заявок начат");
            DocRequestPrsSpecification specification = new DocRequestPrsSpecification();
            DocRequestPrsSearchCriteria searchCriteria = new DocRequestPrsSearchCriteria();
//            searchCriteria.setInnOrName("032500136644"); // TODO тест
            specification.setSearchCriteria(searchCriteria);
            long total = docRequestRepo.count(specification);
            actualizationFilesLogger.info("Количество заявок: {}", total);

            int pages = (int) (((total - 1) / pageSize) + 1);
            for (int i = 0; i < pages; i++) {
                Map<String, String> fileMap = new HashMap<>();
                Page<DocRequest> page = docRequestRepo.findAll(specification, PageRequest.of(i, pageSize, Sort.by("id")));
                countActualized += requestService.actualizeFiles(page.getContent(), fileMap);
                // переименуем файлы в хранилище
                renameFiles(fileMap);
            }
            actualizationFilesLogger.info("Процесс актуализации файлов заявок окончен");
        } catch (Exception e) {
            actualizationFilesLogger.info("Ошибка! Процесс актуализации файлов заявок прерван");
            actualizationFilesLogger.info(e.getMessage());
            e.printStackTrace();
        }
        actualizationFilesLogger.info("Итого актуализировано файлов: {}", countActualized);
    }

    private void renameFiles(Map<String, String> fileMap) {
        final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
        for (Map.Entry<String, String> file: fileMap.entrySet()) {
            File currentFile = new File(String.format("%s/%s", absolutePath, file.getKey()));
            File newFile = new File(String.format("%s/%s", absolutePath, file.getValue()));
            currentFile.renameTo(newFile);
            actualizationFilesLogger.info("Файл переименован. {} -> {}", file.getKey(), file.getValue());
        }
    }

    public void createPrescriptions() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ClsTypeRequest> typeRequests = clsTypeRequestRepo.findAll();
        for (ClsTypeRequest typeRequest: typeRequests) {
            ClsPrescription prescription = ClsPrescription.builder()
                    .typeRequest(typeRequest)
                    .name("Предписание для " + typeRequest.getActivityKind())
                    .description("описание")
                    .status(PrescriptionStatuses.PUBLISHED.getValue())
//                    .timePublication(new Timestamp(System.currentTimeMillis()))
                    .build();
            clsPrescriptionRepo.save(prescription);
            short num = 1;
            if (typeRequest.getPrescription() != null && !typeRequest.getPrescription().isBlank()) {
                RegPrescriptionText prescriptionText = RegPrescriptionText.builder()
                        .prescription(prescription)
                        .num((short) num)
                        .content(typeRequest.getPrescription())
                        .build();
                regPrescriptionTextRepo.save(prescriptionText);
            }
            if (typeRequest.getSettings() != null && !typeRequest.getSettings().isBlank()) {
                try {
                    ClsTypeRequestSettings settings = mapper.readValue(typeRequest.getSettings(), ClsTypeRequestSettings.class);
                    for (ClsTypeRequestSettings.Field field: settings.getFields()) {
                        if (field.getUi().getView().equalsIgnoreCase("template")) {
                            RegPrescriptionText prescriptionText = RegPrescriptionText.builder()
                                    .prescription(prescription)
                                    .num((short) ++num)
                                    .content(field.getUi().getTemplate())
                                    .build();
                            regPrescriptionTextRepo.save(prescriptionText);
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveOrganization(ClsOrganizationDto clsOrganizationDto) {
        ClsOrganization organization = clsOrganizationRepo.findById(clsOrganizationDto.getId()).orElse(null);
        organization.setIdTypeOrganization(clsOrganizationDto.getIdTypeOrganization());
        organization.setEmail(clsOrganizationDto.getEmail());
        organization.setActivated(clsOrganizationDto.getActivated());
        organization.setDeleted(clsOrganizationDto.getDeleted());
        clsOrganizationRepo.save(organization);
    }
}
