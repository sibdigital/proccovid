package ru.sibdigital.proccovid.config;

import org.springframework.security.core.userdetails.User;
import ru.sibdigital.proccovid.model.ClsUser;

public class CurrentUser extends User {

    private ClsUser clsUser;

    public CurrentUser(User user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
    }

    public ClsUser getClsUser() {
        return clsUser;
    }

    public void setClsUser(ClsUser clsUser) {
        this.clsUser = clsUser;
    }
}
