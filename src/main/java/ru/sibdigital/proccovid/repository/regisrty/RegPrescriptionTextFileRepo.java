package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegPrescriptionTextFile;

@Repository
public interface RegPrescriptionTextFileRepo extends JpaRepository<RegPrescriptionTextFile, Long> {

}
