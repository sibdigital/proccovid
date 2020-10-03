package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;

import java.util.List;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {
    @Query(nativeQuery = true, value = "select distinct inn, email from cls_organization as co\n" +
            "inner join (select *\n" +
            "        from doc_request\n" +
            "        where status_review = :reviewStatus\n" +
            "    ) as dr\n" +
            "on co.id = dr.id_organization")
    List<Object[]> getOrganizationsEmailsByDocRequestStatus(int reviewStatus);
}
