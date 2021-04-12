package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.repository.ClsUserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClsUserRepo clsUserRepo;

    @Autowired
    RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ClsUser clsUser = clsUserRepo.findByLogin(login);

        User.UserBuilder builder = null;
        if (clsUser != null) {
            builder = User.withUsername(login);
//            builder.password(passwordEncoder.encode(clsUser.getPassword()));
            builder.password(clsUser.getPassword());
//            if (clsUser.getAdmin()) {
//                builder.roles("ADMIN");
//            } else {
//                builder.roles("USER");
//            }
            builder.authorities(roleService.getUserAuthorities(clsUser));
        } else {
            throw new UsernameNotFoundException("User no found.");
        }

        CurrentUser currentUser = new CurrentUser((User) builder.build());
        currentUser.setClsUser(clsUser);

        return currentUser;
    }
}
