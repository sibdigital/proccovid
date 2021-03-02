package ru.sibdigital.proccovid.service;

import org.springframework.data.domain.Page;
import ru.sibdigital.proccovid.dto.ClsTypeViolationDto;
import ru.sibdigital.proccovid.dto.PersonViolationDto;
import ru.sibdigital.proccovid.dto.ViolationDto;
import ru.sibdigital.proccovid.model.ClsTypeViolation;
import ru.sibdigital.proccovid.model.RegPersonViolation;
import ru.sibdigital.proccovid.model.RegViolation;
import ru.sibdigital.proccovid.repository.specification.RegPersonViolationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegViolationSearchCriteria;

import java.util.List;

public interface ViolationService {

    ClsTypeViolation saveClsTypeViolation(ClsTypeViolationDto dto) throws Exception;

    ClsTypeViolation getClsTypeViolation(Long id);

    List<ClsTypeViolation> getClsTypeViolations();

    Page<RegViolation> getViolationsByCriteria(RegViolationSearchCriteria searchCriteria, int page, int size, Long idUser);

    RegViolation saveRegViolation(ViolationDto dto) throws Exception;

    RegViolation getRegViolation(Long id);

    Page<RegPersonViolation> getPersonViolationsByCriteria(RegPersonViolationSearchCriteria searchCriteria, int page, int size, Long idUser);

    RegPersonViolation saveRegPersonViolation(PersonViolationDto dto) throws Exception;

    RegPersonViolation getRegPersonViolation(Long id);
}
