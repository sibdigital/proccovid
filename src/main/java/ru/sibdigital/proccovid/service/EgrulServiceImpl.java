package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.repository.RegEgripRepo;
import ru.sibdigital.proccovid.repository.RegEgrulRepo;

@Service
public class EgrulServiceImpl implements EgrulService {

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    public RegEgrul getEgrul(String inn) {
        if (inn == null || inn.isBlank()) {
            return null;
        }
        RegEgrul regEgrul = regEgrulRepo.findByInn(inn);
        return regEgrul;
    }

    public RegEgrip getEgrip(String inn) {
        if (inn == null || inn.isBlank()) {
            return null;
        }
        RegEgrip regEgrip = regEgripRepo.findByInn(inn);
        return regEgrip;
    }
}
