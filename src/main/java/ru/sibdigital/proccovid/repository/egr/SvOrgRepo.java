package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.egr.SvOrg;

@Repository
public interface SvOrgRepo extends JpaRepository<SvOrg, Long> {
    SvOrg findSvOrgByTypeOrgAndCodeAndNameAndAdr(Short type, String code, String fullName, String adr);
}
