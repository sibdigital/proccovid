package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.ClsFileType;

public interface ClsFileTypeRepo extends JpaRepository<ClsFileType, Long> {
}
