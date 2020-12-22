package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsMigration;

import java.io.File;

public interface MigrationService {

    ClsMigration getClsMigration(File file, Short type);
    ClsMigration addMigrationRecord(ClsMigration migration, File file, Short type, Short status, String error);
    void changeMigrationStatus(ClsMigration migration, Short status, String error);
}
