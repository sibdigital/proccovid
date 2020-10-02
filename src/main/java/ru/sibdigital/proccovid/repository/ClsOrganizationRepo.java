package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;

import java.util.List;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {
    @Query(nativeQuery = true, value = "SELECT DISTINCT email\n" +
            "FROM cls_organization\n" +
            "RIGHT JOIN doc_request\n" +
            "ON cls_organization.id = doc_request.id_organization\n" +
            "WHERE doc_request.status_review = :reviewStatus")
    List<String> getOrganizationsEmailsByDocRequestStatus(int reviewStatus);
}
