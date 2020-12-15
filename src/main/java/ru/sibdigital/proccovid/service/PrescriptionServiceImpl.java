package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsPrescriptionDto;
import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.dto.RegPrescriptionTextDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    @Autowired
    private ClsRestrictionTypeRepo clsRestrictionTypeRepo;

    @Autowired
    private ClsPrescriptionRepo clsPrescriptionRepo;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @Autowired
    private RegTypeRequestRestrictionTypeRepo regTypeRequestRestrictionTypeRepo;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private ClsDepartmentOkvedRepo clsDepartmentOkvedRepo;

    @Autowired
    private RegPrescriptionTextRepo regPrescriptionTextRepo;

    @Autowired
    private RegPrescriptionTextFileRepo regPrescriptionTextFileRepo;

    @Override
    public List<ClsPrescription> getClsPrescriptions() {
        return StreamSupport.stream(clsPrescriptionRepo.findAll(Sort.by(Sort.Direction.DESC, "id")).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClsRestrictionType> getClsRestrictionTypes() {
        return clsRestrictionTypeRepo.findAll(Sort.by("id"));
    }

    @Override
    public ClsRestrictionType saveClsRestrictionType(ClsRestrictionTypeDto dto) {
        ClsRestrictionType clsRestrictionType = ClsRestrictionType.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();

        clsRestrictionTypeRepo.save(clsRestrictionType);

        return clsRestrictionType;
    }

    @Override
    public ClsPrescription getClsPrescription(Long id) {
        return clsPrescriptionRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ClsPrescription savePrescription(ClsPrescriptionDto dto) {
        ClsPrescription prescription = null;

        if (Objects.nonNull(dto.getId())) {
            prescription = clsPrescriptionRepo.findById(dto.getId()).orElse(null);
            if (Objects.nonNull(prescription) && prescription.getStatus() == PrescriptionStatuses.PUBLISHED.getValue()) {
                return prescription;
            }
        }

        ClsTypeRequest typeRequest = clsTypeRequestRepo.findById(dto.getTypeRequestId()).orElse(null);

        if (Objects.nonNull(prescription)) {
            prescription.setTypeRequest(typeRequest);
            prescription.setName(dto.getName());
            prescription.setDescription(dto.getDescription());
            prescription.setStatus(PrescriptionStatuses.NOT_PUBLISHED.getValue());
            prescription.setAdditionalFields(dto.getAdditionalFields());
        } else {
            prescription = ClsPrescription.builder()
                    .typeRequest(typeRequest)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .status(PrescriptionStatuses.NOT_PUBLISHED.getValue())
                    .additionalFields(dto.getAdditionalFields())
                    .build();
        }

        clsPrescriptionRepo.save(prescription);

//        // TODO пока только один тип, возможно будет несколько
//        if (Objects.nonNull(dto.getRestrictionTypeIds())) {
//            if (Objects.nonNull(typeRequest.getRegTypeRequestRestrictionTypes())) {
//                typeRequest.getRegTypeRequestRestrictionTypes().forEach(rtrrt -> {
//                    regTypeRequestRestrictionTypeRepo.delete(rtrrt);
//                });
//            }
//            RegTypeRequestRestrictionType regTypeRequestRestrictionType = new RegTypeRequestRestrictionType();
//            regTypeRequestRestrictionType.setRegTypeRequestRestrictionTypeId(
//                    new RegTypeRequestRestrictionTypeId(typeRequest, ClsRestrictionType.builder().id(dto.getRestrictionTypeIds()).build()));
//            regTypeRequestRestrictionTypeRepo.save(regTypeRequestRestrictionType);
//        }

        if (Objects.nonNull(dto.getPrescriptionTexts()) && dto.getPrescriptionTexts().size() > 0) {
                List<RegPrescriptionText> prescriptionTexts = new ArrayList<>();
                for (RegPrescriptionTextDto prescriptionTextDto : dto.getPrescriptionTexts()) {
                    RegPrescriptionText prescriptionText = RegPrescriptionText.builder()
                            .id(prescriptionTextDto.getId())
                            .prescription(prescription)
                            .num(prescriptionTextDto.getNum())
                            .content(prescriptionTextDto.getContent())
                            .build();
                    regPrescriptionTextRepo.save(prescriptionText);
                    prescriptionTexts.add(prescriptionText);
                }
                prescription.setPrescriptionTexts(prescriptionTexts);
            }

        return prescription;
    }

    @Override
    public RegPrescriptionTextFile savePrescriptionTextFile(MultipartFile file, Long idPrescriptionText) {
        RegPrescriptionText regPrescriptionText = regPrescriptionTextRepo.findById(idPrescriptionText).orElse(null);

        RegPrescriptionTextFile regPrescriptionTextFile = construct(file, regPrescriptionText);
        if (regPrescriptionTextFile != null) {
            regPrescriptionTextFileRepo.save(regPrescriptionTextFile);
        }
        return regPrescriptionTextFile;
    }

    private RegPrescriptionTextFile construct(MultipartFile multipartFile, RegPrescriptionText prescriptionText) {
        RegPrescriptionTextFile rtrpf = null;
        try {
            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
            final String filename = prescriptionText.getId().toString() + "_" + UUID.randomUUID();
            final String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s%s", absolutePath, filename, extension));
            multipartFile.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

//            final List<RegPrescriptionTextFile> files = regTypeRequestPrescriptionFileRepo.findRegTypeRequestPrescriptionFileByPrescriptionAndHash(prescriptionText, fileHash);
            final List<RegPrescriptionTextFile> files = new ArrayList<>();

            if (!files.isEmpty()) {
                rtrpf = files.get(0);
            } else {
                rtrpf = RegPrescriptionTextFile.builder()
                        .prescriptionText(prescriptionText)
                        .attachmentPath(String.format("%s/%s", uploadingDir, filename))
                        .fileName(filename)
                        .originalFileName(originalFilename)
                        .isDeleted(false)
                        .fileExtension(extension)
                        .fileSize(size)
                        .hash(fileHash)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
            }
        } catch (IOException ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return rtrpf;
    }

    private String getFileHash(File file) {
        String result = "NOT";
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            result = DatatypeConverter.printHexBinary(hash);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    @Override
    public boolean deletePrescriptionTextFile(Long id) {
        try {
            RegPrescriptionTextFile prescriptionTextFile = regPrescriptionTextFileRepo.getOne(id);
            prescriptionTextFile.setDeleted(true);
            regPrescriptionTextFileRepo.save(prescriptionTextFile);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public Long getCountOrganizations(ClsTypeRequestDto dto) {
        Long count = 0L;
        if (Objects.nonNull(dto.getAdditionalFields().getOkvedIds()) && dto.getAdditionalFields().getOkvedIds().length > 0) {
            count += clsOrganizationRepo.getCountOrganizationsByOkvedIds(dto.getAdditionalFields().getOkvedIds());
        }
        if (Objects.nonNull(dto.getAdditionalFields().getOrganizationIds()) && dto.getAdditionalFields().getOrganizationIds().length > 0) {
            count += clsOrganizationRepo.getCountOrganizationsByIds(dto.getAdditionalFields().getOrganizationIds());
        }
        return count;
    }

    @Override
    public List<ClsOrganization> findOrganizations(ClsTypeRequestDto dto) {
        return clsOrganizationRepo.getOrganizationsByIds(dto.getAdditionalFields().getOrganizationIds());
    }

    @Override
    public boolean publishPrescription(Long id) {
        boolean published = false;
        try {
            ClsPrescription prescription = clsPrescriptionRepo.findById(id).orElse(null);
            if (Objects.nonNull(prescription) && prescription.getStatus() != PrescriptionStatuses.PUBLISHED.getValue()) {
                prescription.setStatus(PrescriptionStatuses.PUBLISHED.getValue());
                prescription.setTimePublication(Timestamp.valueOf(LocalDateTime.now()));
                clsPrescriptionRepo.save(prescription);
                published = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return published;
    }

    @Async
    @Override
    public void createRequestsByPrescription(Long id) {
        try {
            ClsPrescription prescription = clsPrescriptionRepo.findById(id).orElse(null);
            if (Objects.nonNull(prescription)) {
                UUID[] okvedIds = prescription.getAdditionalFields().getOkvedIds();
                if (Objects.nonNull(okvedIds) && okvedIds.length > 0) {
                    List<Long> organizationIds = clsOrganizationRepo.getOrganizationIdsByOkveds(okvedIds);
                    for (Long organizationId : organizationIds) {
                        // если есть заявки в статусе NEW, то пометим их как EXPIRED
                        List<DocRequest> requests = docRequestRepo.getRequestsByOrganizationIdAndStatusAndOkvedIds(organizationId,
                                ReviewStatuses.NEW.getValue(), okvedIds).orElse(null);
                        if (Objects.nonNull(requests) && requests.size() > 0) {
                            requests.forEach(request -> {
                                request.setStatusReview(ReviewStatuses.EXPIRED.getValue());
                                docRequestRepo.save(request);
                            });
                        }
                        createRequest(prescription, organizationId);
                    }
                }

                Long[] organizationIds = prescription.getAdditionalFields().getOrganizationIds();
                if (Objects.nonNull(organizationIds) && organizationIds.length > 0) {
                    for (Long organizationId : organizationIds) {
                        // если есть заявки, созданные выше по ОКВЕДам, то пропускаем
                        List<DocRequest> requests = docRequestRepo.getRequestsByOrganizationIdAndStatusAndTypeRequestId(organizationId,
                                ReviewStatuses.NEW.getValue(), prescription.getId()).orElse(null);
                        if (Objects.nonNull(requests) && requests.size() > 0) {
                            continue;
                        }
                        createRequest(prescription, organizationId);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void createRequest(ClsPrescription prescription, Long organizationId) {

        ClsDepartment department = prescription.getTypeRequest().getDepartment();

//        RegOrganizationOkved mainOrganizationOkved = organization.getRegOrganizationOkveds()
//                .stream().filter(regOrgOkved -> regOrgOkved.getMain()).findFirst().orElse(null);
//        if (mainOrganizationOkved != null) {
//            ClsDepartmentOkved clsDepartmentOkved = clsDepartmentOkvedRepo.findClsDepartmentOkvedByOkvedId(mainOrganizationOkved.getOkvedId());
//            if (clsDepartmentOkved != null) {
//                department = clsDepartmentOkved.getDepartment();
//            } else {
//                // TODO что делать, если нет связи подразделения и ОКВЭД?
//            }
//        } else {
//            // TODO что делать, если нет основного ОКВЭД? какое подразделение? через настройки может указать подразделение?
//        }

        DocRequest request = DocRequest.builder()
                .organization(new ClsOrganization(organizationId))
                .department(department)
                .personOfficeCnt(0L)
                .personRemoteCnt(0L)
                .personSlrySaveCnt(0L)
                .statusReview(ReviewStatuses.NEW.getValue())
                .statusImport(0)
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .typeRequest(prescription.getTypeRequest())
                .build();

        docRequestRepo.save(request);
    }
}
