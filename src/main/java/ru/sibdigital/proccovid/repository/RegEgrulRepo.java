package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegEgrul;

import java.util.List;

@Repository
public interface RegEgrulRepo extends JpaRepository<RegEgrul, Long> {

    RegEgrul findByInnAndActiveStatus(String inn, int status);

    @Query("select r from RegEgrul r where inn in :inns")
    List<RegEgrul> findAllByInnList(@Param("inns")List<String> inn);

    @Query("select r from RegEgrul r where iogrn in :iogrns")
    List<RegEgrul> findAllByIogrnList(@Param("iogrns") List<Long> iogrns);
}
