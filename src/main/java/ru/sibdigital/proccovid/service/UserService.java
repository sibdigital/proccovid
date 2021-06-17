package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsUser;

import java.io.InputStream;
import java.util.List;

public interface UserService {

    ClsUser findUserByLogin(String login);
    String changeUserPassword(ClsUser clsUser);
    List<ClsUser> loadFromCSV(InputStream inputStream);
}
