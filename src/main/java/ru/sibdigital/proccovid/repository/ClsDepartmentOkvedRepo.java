package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsDepartmentOkved;

import java.util.List;

@Repository
public interface ClsDepartmentOkvedRepo extends JpaRepository<ClsDepartmentOkved, Long> {
    List<ClsDepartmentOkved> findClsDepartmentOkvedByDepartment_Id(Long id_department);
    List<ClsDepartmentOkved> findClsDepartmentOkvedByDepartment(ClsDepartment department);
}
