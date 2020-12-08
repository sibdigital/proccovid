package ru.sibdigital.proccovid.service;

import java.io.File;

public interface ImportFiasService {

    String importData(File file);

    void importFullData();
}
