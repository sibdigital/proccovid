package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

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
    private ClsDepartmentOkvedRepo clsDepartmentOkvedRepo;

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

        ClsTypeRequest clsTypeRequest = ClsTypeRequest.builder()
                .id(dto.getId())
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
        if (dto.getRestrictionTypeIds() != null) {
            RegTypeRequestRestrictionType regTypeRequestRestrictionType = new RegTypeRequestRestrictionType();
            regTypeRequestRestrictionType.setRegTypeRequestRestrictionTypeId(
                    new RegTypeRequestRestrictionTypeId(clsTypeRequest, ClsRestrictionType.builder().id(dto.getRestrictionTypeIds()).build()));
            regTypeRequestRestrictionTypeRepo.save(regTypeRequestRestrictionType);
        }

        return clsTypeRequest;
    }

    @Override
    public Long getCountSelectedOrganizations(ClsTypeRequestDto dto) {
        Long count = 0L;
        if (Objects.nonNull(dto.getAdditionalFields().getOkvedIds()) && dto.getAdditionalFields().getOkvedIds().length > 0) {
            count += clsOrganizationRepo.getCountSelectedOrganizations(dto.getAdditionalFields().getOkvedIds());
        }
        if (Objects.nonNull(dto.getAdditionalFields().getOrganizationIds()) && dto.getAdditionalFields().getOrganizationIds().length > 0) {
            count += clsOrganizationRepo.getCountSelectedOrganizations(dto.getAdditionalFields().getOrganizationIds());
        }
        return count;
    }

    @Override
    public List<ClsOrganization> findSelectedOrganizations(ClsTypeRequestDto dto) {
        if (dto.getAdditionalFields().getOkvedIds() != null || dto.getAdditionalFields().getOrganizationIds() != null) {
            return clsOrganizationRepo.getSelectedOrganizations(dto.getAdditionalFields().getOkvedIds());
        }
        return null;
    }

    @Override
    public void publishPrescription(Long id) {
        ClsTypeRequest prescription = clsTypeRequestRepo.findById(id).orElse(null);
        if (prescription != null) {

            UUID[] okvedIds = prescription.getAdditionalFields().getOkvedIds();
            if (Objects.nonNull(okvedIds) && okvedIds.length > 0) {
                List<ClsOrganization> organizations = clsOrganizationRepo.getSelectedOrganizations(okvedIds);
                organizations.forEach(organization -> {
                    createRequest(prescription, organization);
                });
            }

            Long[] organizationIds = prescription.getAdditionalFields().getOrganizationIds();
            if (Objects.nonNull(organizationIds) && organizationIds.length > 0) {
                List<ClsOrganization> organizations = clsOrganizationRepo.getSelectedOrganizations(organizationIds);
                organizations.forEach(organization -> {
                    createRequest(prescription, organization);
                });
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
