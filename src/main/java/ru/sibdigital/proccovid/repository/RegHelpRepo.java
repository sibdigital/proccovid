package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegHelp;

import java.util.List;
import java.util.Map;

@Repository
public interface RegHelpRepo extends JpaRepository<RegHelp, Long> {

    @Query(nativeQuery = true, value = "select *\n" +
            "from reg_help\n" +
            "where name = :name\n")
    List<Map<String, Object>> getHelpByName(@Param("name") String name);
}
