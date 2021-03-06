package ru.sibdigital.proccovid.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsSettings;
import ru.sibdigital.proccovid.repository.classifier.ClsSettingsRepo;
import ru.sibdigital.proccovid.service.SettingService;


@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private ClsSettingsRepo clsSettingsRepo;

    public ClsSettings findActual() {
        return clsSettingsRepo.getActual().orElse(null);
    }

    public ClsSettings findActualByKey(String key) {
        return clsSettingsRepo.getActualByKey(key).orElse(null);
    }
}
