package ru.sibdigital.proccovid.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsPrescriptionDto;
import ru.sibdigital.proccovid.dto.ClsRestrictionTypeDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.*;

import java.util.List;

public interface PrescriptionService {

    List<ClsPrescription> getClsPrescriptions();

    List<ClsRestrictionType> getClsRestrictionTypes();

    ClsRestrictionType saveClsRestrictionType(ClsRestrictionTypeDto dto);

    ClsPrescription getClsPrescription(Long id);

    ClsPrescription savePrescription(ClsPrescriptionDto dto);

    Long getCountOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    List<ClsOrganization> findOrganizations(ClsTypeRequestDto clsTypeRequestDto);

    boolean publishPrescription(Long id);

    void createRequestsByPrescription(Long idTypeRequest);

    RegPrescriptionTextFile savePrescriptionTextFile(MultipartFile file, Long idPrescriptionText);

    boolean deletePrescriptionTextFile(Long id);
}
