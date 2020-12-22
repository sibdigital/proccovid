package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsMigration;
import ru.sibdigital.proccovid.repository.ClsMigrationRepo;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@Service
@Slf4j
public class MigrationServiceImpl implements MigrationService {

    @Autowired
    private ClsMigrationRepo clsMigrationRepo;

    public ClsMigration getClsMigration(File file, Short type) {
        ClsMigration migration = clsMigrationRepo.findClsMigrationByHashAndType(getFileHash(file), type);
        return migration;
    }

    public ClsMigration addMigrationRecord(ClsMigration migration, File file, Short type, Short status, String error){
        if (migration == null) {
            migration = new ClsMigration();
        }

        migration.setFilename(file.getName());
        migration.setHash(getFileHash(file));
        migration.setLoadDate(new Timestamp(System.currentTimeMillis()));
        migration.setType(type);
        migration.setStatus(status);
        migration.setError(error);
        clsMigrationRepo.save(migration);

        return migration;
    }

    public void changeMigrationStatus(ClsMigration migration, Short status, String error){
        migration.setLoadDate(new Timestamp(System.currentTimeMillis()));
        migration.setStatus(status);
        migration.setError(error);
        clsMigrationRepo.save(migration);
    }


    private String getFileHash(File file) {
        String result = "NOT";
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            result = DatatypeConverter.printHexBinary(hash);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return result;
    }
}
