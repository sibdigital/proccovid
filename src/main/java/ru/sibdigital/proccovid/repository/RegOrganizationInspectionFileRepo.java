package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.model.RegOrganizationInspectionFile;

import java.util.List;
import java.util.Optional;


@Repository
public interface RegOrganizationInspectionFileRepo extends JpaRepository<RegOrganizationInspectionFile, Long> {
    Optional<List<RegOrganizationInspectionFile>> findRegOrganizationInspectionFilesByOrganizationInspectionAndIsDeleted(RegOrganizationInspection inspection, Boolean deleted);
}
