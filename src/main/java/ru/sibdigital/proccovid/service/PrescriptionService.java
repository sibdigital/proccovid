package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.ClsRestrictionType;
import ru.sibdigital.proccovid.model.ClsTypeRequest;

import java.util.List;

public interface PrescriptionService {

    List<ClsRestrictionType> getClsRestrictionTypes();

    ClsRestrictionType saveClsRestrictionType(ClsRestrictionTypeDto dto);

    ClsTypeRequest saveClsTypeRequest(ClsTypeRequestDto dto);

    Long getCountSelectedOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    List<ClsOrganization> findSelectedOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    void publishPrescription(Long id) throws Exception;
}
