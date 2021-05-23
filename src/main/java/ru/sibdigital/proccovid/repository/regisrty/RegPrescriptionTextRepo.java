package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegPrescriptionText;

@Repository
public interface RegPrescriptionTextRepo extends JpaRepository<RegPrescriptionText, Long> {

}
