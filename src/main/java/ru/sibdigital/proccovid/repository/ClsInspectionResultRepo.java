package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsInspectionResult;

@Repository
public interface ClsInspectionResultRepo extends JpaRepository<ClsInspectionResult, Long>{

}
