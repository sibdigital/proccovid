package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegTypeRequestRestrictionType;

@Repository
public interface RegTypeRequestRestrictionTypeRepo extends JpaRepository<RegTypeRequestRestrictionType, Long> {

}
