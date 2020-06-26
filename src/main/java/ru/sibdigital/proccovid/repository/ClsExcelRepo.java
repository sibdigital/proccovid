package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsExcel;

@Repository
public interface ClsExcelRepo extends JpaRepository<ClsExcel, Long> {
}
