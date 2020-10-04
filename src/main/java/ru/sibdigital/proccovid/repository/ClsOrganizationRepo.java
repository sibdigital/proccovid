package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsOrganization;

import java.util.Date;
import java.util.List;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {
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
}
