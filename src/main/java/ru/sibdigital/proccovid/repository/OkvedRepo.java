package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.Okved;

import java.util.List;
import java.util.Optional;

@Repository
public interface OkvedRepo extends JpaRepository<Okved, Integer>, JpaSpecificationExecutor<Okved> {
    @Query(value = "SELECT okved.*\n" +
            "FROM okved\n" +
            "WHERE okved.kind_code = :kind_code AND okved.kind_name = :kind_name AND okved.version = :version",
            nativeQuery = true)
    List<Okved> findOkvedByKindCodeAndKindNameAAndVersion(String kind_code, String kind_name, String version);

}
