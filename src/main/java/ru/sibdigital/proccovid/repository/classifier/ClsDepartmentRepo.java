package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.model.ClsDepartment;

import java.util.List;

@Repository
public interface ClsDepartmentRepo extends JpaRepository<ClsDepartment, Long> {
    List<ClsDepartment> findAllByIsReviewer(Boolean isReviewer, Sort sort);
}
