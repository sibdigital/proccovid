package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.dto.RegTypeRequestPrescriptionDto;
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

@Service
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    @Autowired
    private ClsRestrictionTypeRepo clsRestrictionTypeRepo;

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
    private RegTypeRequestPrescriptionRepo regTypeRequestPrescriptionRepo;

    @Autowired
    private RegTypeRequestPrescriptionFileRepo regTypeRequestPrescriptionFileRepo;

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
    @Transactional
    public ClsTypeRequest saveClsTypeRequest(ClsTypeRequestDto dto) {

        ClsDepartment clsDepartment = clsDepartmentRepo.findById(dto.getDepartmentId()).orElse(null);

        ClsTypeRequest clsTypeRequest = ClsTypeRequest.builder()
                .id(dto.getId())
                .department(clsDepartment)
                .activityKind(dto.getActivityKind())
                .shortName(dto.getShortName())
                .prescription(dto.getPrescription())
                .prescriptionLink(dto.getPrescriptionLink())
                .settings(dto.getSettings())
                .statusRegistration(dto.getStatusRegistration())
                .beginRegistration(dto.getBeginRegistration())
                .endRegistration(dto.getEndRegistration())
                .statusVisible(dto.getStatusVisible())
                .beginVisible(dto.getBeginVisible())
                .endVisible(dto.getEndVisible())
                .sortWeight(dto.getSortWeight())
                .additionalFields(dto.getAdditionalFields())
                .build();

        clsTypeRequestRepo.save(clsTypeRequest);

        // TODO пока только один тип, возможно будет несколько
        if (Objects.nonNull(dto.getRestrictionTypeIds())) {
            RegTypeRequestRestrictionType regTypeRequestRestrictionType = new RegTypeRequestRestrictionType();
            regTypeRequestRestrictionType.setRegTypeRequestRestrictionTypeId(
                    new RegTypeRequestRestrictionTypeId(clsTypeRequest, ClsRestrictionType.builder().id(dto.getRestrictionTypeIds()).build()));
            regTypeRequestRestrictionTypeRepo.save(regTypeRequestRestrictionType);
        }

        if (Objects.nonNull(dto.getRegTypeRequestPrescriptions()) && dto.getRegTypeRequestPrescriptions().size() > 0) {
            for (RegTypeRequestPrescriptionDto rtrpDto: dto.getRegTypeRequestPrescriptions()) {
                RegTypeRequestPrescription regTypeRequestPrescription = RegTypeRequestPrescription.builder()
                        .id(rtrpDto.getId())
                        .typeRequest(clsTypeRequest)
                        .num(rtrpDto.getNum())
                        .content(rtrpDto.getContent())
                        .build();
                regTypeRequestPrescriptionRepo.save(regTypeRequestPrescription);
            }
        }

        return clsTypeRequest;
    }

    @Override
    public RegTypeRequestPrescriptionFile saveRegTypeRequestPrescriptionFile(MultipartFile file, Long idTypeRequest, Long idTypeRequestPrescription, Short num) {
        RegTypeRequestPrescription regTypeRequestPrescription = null;

        RegTypeRequestPrescriptionFile regTypeRequestPrescriptionFile = construct(file, regTypeRequestPrescription);
        if (regTypeRequestPrescriptionFile != null) {
            regTypeRequestPrescriptionFileRepo.save(regTypeRequestPrescriptionFile);
        }
        return regTypeRequestPrescriptionFile;
    }

    private RegTypeRequestPrescriptionFile construct(MultipartFile multipartFile, RegTypeRequestPrescription regTypeRequestPrescription) {
        RegTypeRequestPrescriptionFile rtrpf = null;
        try {
            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
            final String filename = regTypeRequestPrescription.getId().toString() + "_" + UUID.randomUUID();
            final String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s%s", absolutePath, filename, extension));
            multipartFile.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

//            final List<RegTypeRequestPrescriptionFile> files = regTypeRequestPrescriptionFileRepo.findRegTypeRequestPrescriptionFileByPrescriptionAndHash(regTypeRequestPrescription, fileHash);
            final List<RegTypeRequestPrescriptionFile> files = new ArrayList<>();

            if (!files.isEmpty()) {
                rtrpf = files.get(0);
            } else {
                rtrpf = RegTypeRequestPrescriptionFile.builder()
                        .typeRequestPrescription(regTypeRequestPrescription)
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
    public void publishPrescription(Long id) {
        ClsTypeRequest prescription = clsTypeRequestRepo.findById(id).orElse(null);
        if (Objects.nonNull(prescription)/* && prescription.isNotPublished()*/) { // TODO
            UUID[] okvedIds = prescription.getAdditionalFields().getOkvedIds();
            if (Objects.nonNull(okvedIds) && okvedIds.length > 0) {
                List<ClsOrganization> organizations = clsOrganizationRepo.getOrganizationsByOkveds(okvedIds);
                for (ClsOrganization organization: organizations) {
                    // если есть заявки в статусе NEW, то пометим их как EXPIRED
                    List<DocRequest> requests = docRequestRepo.getRequestsByOrganizationIdAndStatusAndOkvedIds(organization.getId(),
                            ReviewStatuses.NEW.getValue(), okvedIds).orElse(null);
                    if (Objects.nonNull(requests) && requests.size() > 0) {
                        requests.forEach(request -> {
                            request.setStatusReview(ReviewStatuses.EXPIRED.getValue());
                            docRequestRepo.save(request);
                        });
                    }
                    createRequest(prescription, organization);
                }
            }

            Long[] organizationIds = prescription.getAdditionalFields().getOrganizationIds();
            if (Objects.nonNull(organizationIds) && organizationIds.length > 0) {
                List<ClsOrganization> organizations = clsOrganizationRepo.getOrganizationsByIds(organizationIds);
                for (ClsOrganization organization: organizations) {
                    // если есть заявки, созданные выше по ОКВЕДам, то пропускаем
                    List<DocRequest> requests = docRequestRepo.getRequestsByOrganizationIdAndStatusAndTypeRequestId(organization.getId(),
                            ReviewStatuses.NEW.getValue(), prescription.getId()).orElse(null);
                    if (Objects.nonNull(requests) && requests.size() > 0) {
                        continue;
                    }
                    createRequest(prescription, organization);
                }
            }
        }
    }

    private void createRequest(ClsTypeRequest prescription, ClsOrganization organization) {

        ClsDepartment department = prescription.getDepartment();

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
                .organization(organization)
                .department(department)
                .personOfficeCnt(0L)
                .personRemoteCnt(0L)
                .personSlrySaveCnt(0L)
                .statusReview(ReviewStatuses.NEW.getValue())
                .statusImport(0)
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .typeRequest(prescription)
                .build();

        docRequestRepo.save(request);
    }
}
