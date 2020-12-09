package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegOrganizationOkved;

@Repository
public interface RegOrganizationOkvedRepo extends JpaRepository<RegOrganizationOkved, Long> {

}
