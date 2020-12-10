package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegTypeRequestPrescription;

@Repository
public interface RegTypeRequestPrescriptionRepo extends JpaRepository<RegTypeRequestPrescription, Long> {

}
