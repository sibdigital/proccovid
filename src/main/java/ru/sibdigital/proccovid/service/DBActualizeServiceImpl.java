package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSpecification;
import ru.sibdigital.proccovid.utils.AppUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * Сервис для актуализации БД под новую схему данных
 *
 */
@Slf4j
@Service
public class DBActualizeServiceImpl implements DBActualizeService {

    private final static int pageSize = 100;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private EgrulService egrulService;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegOrganizationOkvedRepo regOrganizationOkvedRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private DocEmployeeRepo docEmployeeRepo;

    @Autowired
    private RegOrganizationFileRepo regOrganizationFileRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    public void actualizeOrganizations() {
        long total = 0;
        long countActualized = 0;
        try {
            log.info("Процесс актуализации организаций начат");
            ClsOrganizationSpecification specification = new ClsOrganizationSpecification();
            ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
            searchCriteria.setInn("0323091260"); // TODO тест
            specification.setSearchCriteria(searchCriteria);
            total = clsOrganizationRepo.count(specification);
            int pages = (int) (((total - 1) / pageSize) + 1);
            for (int i = 0; i < pages; i++) {
                Page<ClsOrganization> page = clsOrganizationRepo.findAll(specification, PageRequest.of(i, pageSize, Sort.by("id")));
                actualizeOrganizations(page.getContent());
                countActualized += page.getNumberOfElements();
            }
            log.info("Процесс актуализации организаций окончен");
        } catch (Exception e) {
            log.error("Ошибка! Процесс актуализации организаций прерван");
            log.error(e.getMessage());
        }
        log.info("Количество организаций: {}", total);
        log.info("Актуализировано: {}", countActualized);
    }

    @Transactional
    public void actualizeOrganizations(List<ClsOrganization> organizations) {
        for (ClsOrganization organization : organizations) {
            if (organization.getActualized() != null && organization.getActualized()) {
                continue;
            }
            // добавим окведы
            String inn = organization.getInn();
            if (inn.length() == 10) {
                RegEgrul regEgrul = egrulService.getEgrul(inn);
                for (RegEgrulOkved regEgrulOkved : regEgrul.getRegEgrulOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgrulOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgrulOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
            } else {
                RegEgrip regEgrip = egrulService.getEgrip(inn);
                for (RegEgripOkved regEgripOkved : regEgrip.getRegEgripOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgripOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgripOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
            }
            // добавим сотрудников, файлы, адреса
            List<DocRequest> requests = docRequestRepo.getLastRequestByInnAndStatus(organization.getInn(), ReviewStatuses.CONFIRMED.getValue()).orElse(null);
            if (requests != null) {
                DocRequest request = requests.get(0);
                // сотрудники
                if (request.getDocPersonList() != null) {
                    for (DocPerson docPerson : request.getDocPersonList()) {
                        DocEmployee docEmployee = DocEmployee.builder()
                                .organization(organization)
                                .person(docPerson)
                                .build();
                        docEmployeeRepo.save(docEmployee);
                    }
                }
                // файлы
                if (request.getAttachmentPath() != null && !request.getAttachmentPath().isBlank()) {
                    String[] paths = request.getAttachmentPath().split(",");
                    for (String path : paths) {
                        String fullName = AppUtils.getFileNameFromPath(path);
                        String[] subStrings = fullName.split("\\.");
                        String fileName = subStrings[0];
                        String fileExtension = subStrings.length > 1 ? subStrings[subStrings.length - 1] : "";
                        RegOrganizationFile regOrganizationFile = RegOrganizationFile.builder()
                                .clsOrganizationByIdOrganization(organization)
                                .attachmentPath(path)
                                .isDeleted(false)
                                .fileName(fileName)
                                .fileExtension(fileExtension)
                                .originalFileName(fullName)
                                .timeCreate(new Timestamp(System.currentTimeMillis()))
                                .build();
                        regOrganizationFileRepo.save(regOrganizationFile);
                    }
                }
                // адреса
                if (request.getDocAddressFact() != null) {
                    for (DocAddressFact docAddressFact : request.getDocAddressFact()) {
                        RegOrganizationAddressFact regOrganizationAddressFact = RegOrganizationAddressFact.builder()
                                .clsOrganization(organization)
                                .fullAddress(docAddressFact.getAddressFact())
                                .timeCreate(new Timestamp(System.currentTimeMillis()))
                                .build();
                        regOrganizationAddressFactRepo.save(regOrganizationAddressFact);
                    }
                }
            }
            // создадим принципала
            if (organization.getPrincipal() == null) {
                ClsPrincipal principal = ClsPrincipal.builder()
                        .organization(organization)
//                            .password(passwordEncoder.encode(PasswordGenerator.generatePassword(8)))
                        .build();
                clsPrincipalRepo.save(principal);
                organization.setPrincipal(principal);
            }

//            organization.setActivated(true); // TODO
            organization.setActualized(true);
            organization.setTimeActualization(new Timestamp(System.currentTimeMillis()));
            clsOrganizationRepo.save(organization);
        }
    }

    @Transactional
    public void markOrganizationAsDeleted(){
        final List<ClsOrganization> organizations = clsOrganizationRepo.getNotActualOrganization(ReviewStatuses.CONFIRMED.getValue());

        for (ClsOrganization org : organizations){
            org.setDeleted(true);
        }
        clsOrganizationRepo.saveAll(organizations);
    }
}
