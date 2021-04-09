package ru.sibdigital.proccovid.service;

import org.springframework.data.domain.Page;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.dto.ClsControlAuthorityDto;
import ru.sibdigital.proccovid.model.ClsControlAuthorityParent;
import ru.sibdigital.proccovid.repository.specification.ClsControlAuthoritySearchCriteria;

import java.util.List;


public interface ControlAuthorityService {

    List<ClsControlAuthorityParent> getControlAuthorityParentsList();
    Page<ClsControlAuthority> getControlAuthoritiesBySearchCriteria(ClsControlAuthoritySearchCriteria searchCriteria, int page, int size);
    ClsControlAuthority saveControlAuthority(ClsControlAuthorityDto clsControlAuthorityDto);
    boolean deleteControlAuthority(Long id);
}
