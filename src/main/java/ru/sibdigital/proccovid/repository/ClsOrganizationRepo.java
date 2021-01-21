package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.DocRequestPrs;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long>, JpaSpecificationExecutor<ClsOrganization> {
    @Query(nativeQuery = true, value = "select distinct inn, email, max(name) as name from cls_organization as co\n" +
            "            inner join (select *\n" +
            "                    from doc_request\n" +
            "                    where status_review = 1\n" +
            "                ) as dr\n" +
            "            on co.id = dr.id_organization\n" +
            "group by inn, email")
    List<Object[]> getOrganizationsEmailsByDocRequestStatus(int reviewStatus);


    @Query(nativeQuery = true, value = "select *\n" +
            "from (\n" +
            "         select distinct co.inn, co.email, max(co.name) as name\n" +
            "         from (  select co.*, nedav.email as chk, rah.inn as chki\n" +
            "                 from cls_organization as co\n" +
            "                 left join (\n" +
            "                     select distinct dd.email\n" +
            "                     from (\n" +
            "                              select rmh.*, extract(HOUR FROM :currDate - time_send) as date_diff\n" +
            "                              from reg_mailing_history as rmh\n" +
            "                              where status = :mailingStatus\n" +
            "                          ) as dd\n" +
            "                     where date_diff < 24\n" +
            "                 ) as nedav\n" +
            "                 on co.email = nedav.email\n" +
            "                 left join ( select inn from reg_actualization_history as rah\n" +
            "                     ) as  rah\n" +
            "                 on co.inn = rah.inn\n" +
            "                 where nedav.email is null and rah.inn is null\n" +
            "             ) as co\n" +
            "                  inner join (select *\n" +
            "                              from doc_request\n" +
            "                              where status_review = :reviewStatus\n" +
            "         ) as dr\n" +
            "                             on co.id = dr.id_organization\n" +
            "         group by co.inn, co.email\n" +
            "     ) as mails\n")
    List<Object[]> getOrganizationsEmailsByDocRequestStatusLast24HoursNotMailing(int reviewStatus, int mailingStatus, Date currDate);

    List<ClsOrganization> findAllByInn(String inn);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from " +
            "   cls_organization org join reg_organization_okved orgOkved on org.id = orgOkved.id_organization " +
            "where " +
            "   id_okved in (:okvedIds) and not org.is_deleted")
    Long getCountOrganizationsByOkvedIds(UUID[] okvedIds);

    @Query(nativeQuery = true, value = "select count(*) " +
            "from " +
            "   cls_organization org  " +
            "where " +
            "   id in (:organizationIds) and not org.is_deleted")
    Long getCountOrganizationsByIds(Long[] organizationIds);

    @Query(nativeQuery = true, value = "select org.id " +
            "from " +
            "   cls_organization org join reg_organization_okved orgOkved on org.id = orgOkved.id_organization " +
            "where" +
            "   id_okved in (:okvedIds) and not org.is_deleted")
    List<Long> getOrganizationIdsByOkveds(UUID[] okvedIds);

    @Query(nativeQuery = true, value = "select org.* " +
            "from " +
            "   cls_organization org " +
            "where" +
            "   org.id in (:organizationIds) and not org.is_deleted")
    List<ClsOrganization> getOrganizationsByIds(Long[] organizationIds);

    @Query(nativeQuery = true, value = "with slc as(\n" +
            "    select  id, id_organization, id_type_request, time_create, time_review, 1 as status\n" +
            " from get_request_slice(1) as d\n" +
            " union\n" +
            " select  id, id_organization, id_type_request, time_create, time_review, 4 as status\n" +
            " from get_request_slice(4) as d\n" +
            " union\n" +
            " select  id, id_organization, id_type_request, time_create, time_review, 0 as status" +
            " from get_request_slice(0) as d\n" +
            ")\n" +
            "select * from (\n" +
            "     select org.*, slc.id as sid, slc.status\n" +
            "     from cls_organization as org\n" +
            "     left join slc as slc on org.id = slc.id_organization\n" +
            ") as org\n" +
            "where org.sid is null")
    List<ClsOrganization> getNotActualOrganization(int statusRewievRequest);
}
