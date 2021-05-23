package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsDepartment;

@Repository
public interface ClsDepartmentRepo extends JpaRepository<ClsDepartment, Long> {

}
