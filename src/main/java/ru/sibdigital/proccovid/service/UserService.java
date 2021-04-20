package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsUser;

public interface UserService {

    ClsUser findUserByLogin(String login);
    String changeUserPassword(ClsUser clsUser);
}
