package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsPrescription;

@Repository
public interface ClsPrescriptionRepo extends JpaRepository<ClsPrescription, Long> {

}
