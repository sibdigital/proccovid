package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsSettings;

public interface SettingService {

    ClsSettings findActual();

    ClsSettings findActualByKey(String key);
}
