package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsSettings;


import java.util.Optional;

@Repository
public interface ClsSettingsRepo extends JpaRepository<ClsSettings, Long> {

    @Query(value = "select s from ClsSettings s where s.status = 1")
    Optional<ClsSettings> getActual();

    @Query(value = "select s from ClsSettings s where s.key = :key and s.status = 1")
    Optional<ClsSettings> getActualByKey(String key);
}
