package ru.sibdigital.proccovid.service;

import org.springframework.security.core.GrantedAuthority;
import ru.sibdigital.proccovid.model.ClsRole;
import ru.sibdigital.proccovid.model.ClsUser;

import java.util.List;

public interface RoleService {

    List<ClsRole> getUserRoles(ClsUser clsUser);

    List<GrantedAuthority> getUserAuthorities(ClsUser clsUser);
}
