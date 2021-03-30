package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegPersonViolation;

@Repository
public interface RegPersonViolationRepo extends JpaRepository<RegPersonViolation, Long>, JpaSpecificationExecutor<RegPersonViolation> {

}
