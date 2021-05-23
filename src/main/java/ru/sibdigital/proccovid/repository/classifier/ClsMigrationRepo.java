package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsMigration;

import java.util.List;

@Repository
public interface ClsMigrationRepo extends JpaRepository<ClsMigration, Long> {
    ClsMigration findClsMigrationByFilenameAndType(String filename, Short type);
    List<ClsMigration> findAllByTypeAndStatus(Short type, Short status);
    ClsMigration findClsMigrationByHashAndType(String hash, Short type);
}
