package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.ClsFileType;

import java.util.List;

public interface ClsFileTypeRepo extends JpaRepository<ClsFileType, Long> {
    List<ClsFileType> getClsFileTypesByIsDeleted(Boolean isDeleted);
}
