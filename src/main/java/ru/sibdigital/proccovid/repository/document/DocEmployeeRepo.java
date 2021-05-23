package ru.sibdigital.proccovid.repository.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.DocEmployee;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocEmployeeRepo extends JpaRepository<DocEmployee, Long> {

    @Query(nativeQuery = true, value = "select * from doc_employee where id_organization = :id and is_deleted = false")
    Optional<List<DocEmployee>> findAllByOrganization(Long id);
}
