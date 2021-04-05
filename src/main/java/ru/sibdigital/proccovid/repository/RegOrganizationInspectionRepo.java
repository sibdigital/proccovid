package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;


@Repository
public interface RegOrganizationInspectionRepo extends JpaRepository<RegOrganizationInspection, Long> {

}
