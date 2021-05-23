package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsTypeRequest;

import java.util.List;

@Repository
public interface ClsTypeRequestRepo extends JpaRepository<ClsTypeRequest, Long> {
    List<ClsTypeRequest> findAllByOrderByIdAsc();
}
