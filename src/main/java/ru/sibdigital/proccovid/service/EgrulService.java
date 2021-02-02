package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;

import java.util.List;


public interface EgrulService {

    RegEgrul getEgrul(String inn);

    List<RegEgrip> getEgrip(String inn);
}
