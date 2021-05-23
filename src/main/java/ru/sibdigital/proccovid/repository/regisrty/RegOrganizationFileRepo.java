package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationFile;

import java.util.List;

@Repository
public interface RegOrganizationFileRepo extends JpaRepository<RegOrganizationFile, Long> {
    @Query("select rof " +
            "from RegOrganizationFile rof " +
            "where rof.clsOrganizationByIdOrganization = :organization and rof.deleted = false")
    List<RegOrganizationFile> findRegOrganizationFileByOrganization(ClsOrganization organization);

    @Modifying
    @Query(nativeQuery = true, value = "update reg_organization_file set is_deleted = true where id=:id")
    void setFileIsDeletedTrueById(int id);

    @Query("select rof " +
            "from RegOrganizationFile rof " +
            "where rof.clsOrganizationByIdOrganization = :organization and rof.deleted = false and rof.hash = :hash")
    List<RegOrganizationFile> findRegOrganizationFileByOrganizationAndHash(ClsOrganization organization, String hash);
}
