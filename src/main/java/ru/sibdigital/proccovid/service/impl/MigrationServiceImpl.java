package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsMigration;
import ru.sibdigital.proccovid.repository.classifier.ClsMigrationRepo;
import ru.sibdigital.proccovid.service.MigrationService;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@Slf4j
public class MigrationServiceImpl implements MigrationService {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH_mm");

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

    public Boolean renameFile(File file) {
        Boolean success = true;
        String filename = file.getName();
        String fileTime = sdf.format(file.lastModified());
        File newFile = new File(file.getParent(), String.format("%s_%s_error%s", getFileNameWithoutExtension(filename), fileTime, getFileExtension(filename)));
        success = file.renameTo(newFile);
        return success;
    }


    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private String getFileNameWithoutExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(0, lastIndexOf);
    }
}
