package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.proccovid.model.ClsFileType;

import java.util.List;

public interface ClsFileTypeRepo extends JpaRepository<ClsFileType, Long> {
    List<ClsFileType> findAllByIsDeleted(Boolean isDeleted);

    @Query(value = "select t from ClsFileType as t where t.isDeleted = false and t.id not in :ids")
    List<ClsFileType> getAllWithoutExists(Long[] ids);
}
