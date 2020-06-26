package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.DocRequestWithPerson;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocRequestWithPersonRepo extends JpaRepository<DocRequestWithPerson, Long> {

    @Query(value = "SELECT dr.* FROM doc_request dr WHERE  " +
            " dr.status_review = :status " +
            " ORDER BY dr.time_create ASC",
            nativeQuery = true)
    Optional<List<DocRequestWithPerson>> getAllByStatusId(@Param("status") Integer status);
}
