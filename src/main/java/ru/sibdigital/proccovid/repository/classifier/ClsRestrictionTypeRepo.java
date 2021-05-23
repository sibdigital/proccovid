package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsRestrictionType;

@Repository
public interface ClsRestrictionTypeRepo extends JpaRepository<ClsRestrictionType, Long> {

}
