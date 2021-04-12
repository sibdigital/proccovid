package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsRole;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.repository.RegUserRoleRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RegUserRoleRepo regUserRoleRepo;

    @Override
    public List<ClsRole> getUserRoles(ClsUser clsUser) {
        return regUserRoleRepo.findAllByUser(clsUser)
                .stream()
                .map(regUserRole -> regUserRole.getRole())
                .collect(Collectors.toList());
    }

    @Override
    public List<GrantedAuthority> getUserAuthorities(ClsUser clsUser) {
        List<ClsRole> userRoles = getUserRoles(clsUser);

        List<GrantedAuthority> authorities = userRoles.stream()
                                            .map(ctr -> new SimpleGrantedAuthority("ROLE_" + ctr.getCode()))
                                            .collect(Collectors.toList());
        return authorities;
    }
}
