package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsOrganization;

import java.util.List;

public interface DBActualizeService {

    void actualizeOrganizations();

    void actualizeOrganizations(List<ClsOrganization> organizations);
}
