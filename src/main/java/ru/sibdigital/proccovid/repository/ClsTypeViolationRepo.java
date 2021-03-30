package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsTypeViolation;

@Repository
public interface ClsTypeViolationRepo extends JpaRepository<ClsTypeViolation, Long> {

}
