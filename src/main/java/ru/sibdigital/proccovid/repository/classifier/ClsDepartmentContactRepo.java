package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsDepartmentContact;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClsDepartmentContactRepo extends JpaRepository<ClsDepartmentContact, Long> {
    @Query(nativeQuery = true, value = "select * from cls_department_contact where id_department = :id")
    Optional<List<ClsDepartmentContact>> findAllByDepartment(Long id);
}
