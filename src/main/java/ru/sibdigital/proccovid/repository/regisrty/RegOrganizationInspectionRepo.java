package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegOrganizationInspectionRepo extends JpaRepository<RegOrganizationInspection, Long> {
    Optional<List<RegOrganizationInspection>> findRegOrganizationInspectionsByOrganization(ClsOrganization organization);
    Optional<List<RegOrganizationInspection>> findRegOrganizationInspectionsByOrganizationAndControlAuthority_IsDeleted(ClsOrganization organization, Boolean isDeleted);

}
