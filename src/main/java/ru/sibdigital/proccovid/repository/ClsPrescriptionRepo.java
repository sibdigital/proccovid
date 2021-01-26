package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsPrescription;
import ru.sibdigital.proccovid.model.ClsTypeRequest;

@Repository
public interface ClsPrescriptionRepo extends JpaRepository<ClsPrescription, Long> {

    @Query("select p from ClsPrescription p where p.typeRequest.id = :typeRequestId")
    ClsPrescription findByTypeRequestId(Long typeRequestId);
}
