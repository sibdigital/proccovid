package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegOrganizationPrescription;

@Repository
public interface RegOrganizationPrescriptionRepo extends JpaRepository<RegOrganizationPrescription, Long> {

}
