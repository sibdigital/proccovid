package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;

public interface EgrulService {

    RegEgrul getEgrul(String inn);

    RegEgrip getEgrip(String inn);
}
