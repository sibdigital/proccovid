package ru.sibdigital.proccovid.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.ClsRestrictionType;
import ru.sibdigital.proccovid.model.ClsTypeRequest;
import ru.sibdigital.proccovid.model.RegTypeRequestPrescriptionFile;

import java.util.List;

public interface PrescriptionService {

    List<ClsRestrictionType> getClsRestrictionTypes();

    ClsRestrictionType saveClsRestrictionType(ClsRestrictionTypeDto dto);

    ClsTypeRequest saveClsTypeRequest(ClsTypeRequestDto dto);

    Long getCountOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    List<ClsOrganization> findOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    void publishPrescription(Long id) throws Exception;

    RegTypeRequestPrescriptionFile saveRegTypeRequestPrescriptionFile(MultipartFile file, Long idTypeRequest, Long idTypeRequestPrescription, Short num);
}
