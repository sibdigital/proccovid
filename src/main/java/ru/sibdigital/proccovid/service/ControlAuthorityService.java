package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.dto.ClsControlAuthorityDto;

public interface ControlAuthorityService {

    ClsControlAuthority saveControlAuthority(ClsControlAuthorityDto clsControlAuthorityDto);
    boolean deleteControlAuthority(Long id);
}
