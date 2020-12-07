package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegEgrip;

import java.util.List;

@Repository
public interface RegEgripRepo extends JpaRepository<RegEgrip, Long> {

    RegEgrip findByInn(String inn);
    @Query("select r from RegEgrip r where inn in :inns")
    List<RegEgrip> findAllByInnList(@Param("inns")List<String> inn);
}
