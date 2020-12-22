package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegPrescriptionTextFile;

@Repository
public interface RegPrescriptionTextFileRepo extends JpaRepository<RegPrescriptionTextFile, Long> {

}
