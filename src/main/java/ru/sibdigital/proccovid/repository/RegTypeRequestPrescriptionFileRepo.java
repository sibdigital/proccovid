package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegTypeRequestPrescriptionFile;

@Repository
public interface RegTypeRequestPrescriptionFileRepo extends JpaRepository<RegTypeRequestPrescriptionFile, Long> {

}
