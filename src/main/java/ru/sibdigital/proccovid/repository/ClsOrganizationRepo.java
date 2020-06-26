package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {

}
