package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.model.egr.EgrActiveStatus;
import ru.sibdigital.proccovid.repository.RegEgripRepo;
import ru.sibdigital.proccovid.repository.RegEgrulRepo;

import java.util.List;

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
        RegEgrul regEgrul = regEgrulRepo.findByInnAndActiveStatus(inn, EgrActiveStatus.ACTIVE.getValue());
        return regEgrul;
    }

    public List<RegEgrip> getEgrip(String inn) {
        if (inn == null || inn.isBlank()) {
            return null;
        }
        List<RegEgrip> regEgrips = regEgripRepo.findByInnAndActiveStatus(inn, EgrActiveStatus.ACTIVE.getValue());
        return regEgrips;
    }
}
