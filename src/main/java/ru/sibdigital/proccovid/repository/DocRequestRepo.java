package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.DocRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocRequestRepo extends JpaRepository<DocRequest, Long>, JpaSpecificationExecutor {
    Optional<DocRequest> getTopByOrganization(ClsOrganization clsOrganization);
    Optional<DocRequest> getTopByOrgHashCode(String sha256code);
    Optional<List<DocRequest>> getAllByDepartmentAndStatusReview(ClsDepartment department, Integer status);

//    @Query("SELECT dr FROM DocRequest dr WHERE  dr.department.id = :dep_id AND dr.statusReview = :status")
    @Query(value = "select dr.* from (\n" +
            "select dr.* FROM doc_request dr  where dr.id_department = :dep_id AND dr.status_review = :status " +
            "   ORDER BY dr.id ASC limit 10\n" +
            ") as dr inner join cls_organization org on dr.id_organization = org.id " , nativeQuery = true)
    Optional<List<DocRequest>> getAllByDepartmentId(@Param("dep_id")Long departmentId, @Param("status") Integer status);

    @Query(value = "SELECT dr.* FROM doc_request dr WHERE  " +
            " dr.status_review = :status " +
            " ORDER BY dr.time_create ASC",
            nativeQuery = true)
    Optional<List<DocRequest>> getAllByStatusId(@Param("status") Integer status);


    @Query(value = "SELECT dr.* FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status " +
            " and (trim(org.inn) like %:innOrName% or lower(trim(org.name)) like %:innOrName%) " +
            " ORDER BY dr.time_create ASC  limit 100",
            nativeQuery = true)
    Optional<List<DocRequest>> getFirst100RequestByDepartmentIdAndStatusAndInnOrName(@Param("dep_id")Long departmentId, @Param("status")
            Integer status, @Param("innOrName")String innOrName);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInnAndStatus(@Param("inn")String inn, @Param("status") Integer status);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrnAndStatus(@Param("ogrn")String ogrn, @Param("status") Integer status);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInn(@Param("inn")String inn);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrn(@Param("ogrn")String ogrn);

    @Query(nativeQuery = true, value = "select dr.* from doc_request dr" +
            "                           inner join ( select co.id as id_organization" +
            "                              from cls_organization co" +
            "                               inner join ( select co.inn as inn" +
            "                                   from cls_organization co" +
            "                                   inner join doc_request dr" +
            "                                   on co.id = dr.id_organization" +
            "                                   where dr.id = :id_request" +
            "                               ) as t_inn" +
            "                               on co.inn = t_inn.inn) as  t_co" +
            "                   on dr.id_organization = t_co.id_organization" +
            "                   order by dr.time_create desc;")
    Optional<List<DocRequest>> getLastRequestByInnOfRequest(@Param("id_request")Long id_request);

    @Query(nativeQuery = true, value = "select * " +
            "from " +
            "   doc_request dr " +
            "where " +
            "   dr.id_organization = :orgId " +
            "   and dr.status_review = :status " +
            "   and exists (select * " +
            "       from (select CAST(jsonb_array_elements_text(jsonb_extract_path((select additional_fields from cls_type_request as ctr where id = 1), 'okvedIds')) as uuid) as id) as t1 " +
            "       where t1.id in (:okvedIds) " +
            "   )")
    Optional<List<DocRequest>> getRequestsByOrganizationIdAndStatusAndOkvedIds(Long orgId, Integer status, UUID[] okvedIds);

    @Query(nativeQuery = true, value = "select dr.* " +
            "from " +
            "   doc_request dr " +
            "where " +
            "   dr.id_organization = :orgId " +
            "   and dr.status_review = :status " +
            "   and dr.id_type_request = :typeRequestId ")
    Optional<List<DocRequest>> getRequestsByOrganizationIdAndStatusAndTypeRequestId(Long orgId, Integer status, Long typeRequestId);





    @Query(nativeQuery = true, value = "select d.name, d.id, coalesce(neobr, 0) as awaiting, coalesce(utv, 0) as accepted, coalesce(otkl, 0) as declined from cls_department as d" +
            "                  left join ( select id_department, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl" +
            "                              from (" +
            "                                       select id_department," +
            "                                              sum(neobr) as neobr," +
            "                                              sum(utv)   as utv," +
            "                                              sum(otkl)  as otkl" +
            "                                       from (" +
            "                                                select id_department, 0 as neobr, 1 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 1" +
            "                                                union all" +
            "                                                select id_department, 0 as neobr, 0 as utv, 1 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 2" +
            "                                                union all" +
            "                                                select id_department, 1 as neobr, 0 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review <> 2" +
            "                                                  and status_review <> 1" +
            "                                            ) as s" +
            "                                       group by id_department" +
            "                                   ) as m group by id_department" +
            ") as ss on d.id = ss.id_department order by d.id")
    public List<Map<String, Object>> getRequestStatisticForEeachDepartment();

    @Query(nativeQuery = true, value = "select d.name, d.id, coalesce(neobr, 0) as awaiting, coalesce(utv, 0) as accepted, coalesce(otkl, 0) as declined from cls_department as d" +
            "                  left join ( select id_department, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl" +
            "                              from (" +
            "                                       select id_department," +
            "                                              sum(neobr) as neobr," +
            "                                              sum(utv)   as utv," +
            "                                              sum(otkl)  as otkl" +
            "                                       from (" +
            "                                                select id_department, 0 as neobr, 1 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 1 and id_type_request = :id_type_request " +
            "                                                union all" +
            "                                                select id_department, 0 as neobr, 0 as utv, 1 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 2 and id_type_request = :id_type_request " +
            "                                                union all" +
            "                                                select id_department, 1 as neobr, 0 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review <> 2" +
            "                                                  and status_review <> 1 and id_type_request = :id_type_request" +
            "                                            ) as s" +
            "                                       group by id_department" +
            "                                   ) as m group by id_department" +
            ") as ss on d.id = ss.id_department order by d.id")
    public List<Map<String, Object>> getRequestStatisticForEeachDepartment(@Param("id_type_request") int id_type_request);


    @Query(nativeQuery = true, value = "SELECT date_trunc('day',doc_request.time_create) as date, COUNT(*) AS total FROM doc_request GROUP BY date_trunc('day',doc_request.time_create) ORDER BY date_trunc('day',doc_request.time_create) DESC;")
    public List<Map<String, Object>> getStatisticForEachDay();

    @Query(nativeQuery = true, value = "SELECT date_trunc('day',doc_request.time_create) as date, COUNT(*) AS total " +
            " FROM doc_request" +
            " WHERE  id_type_request = :id_type_request" +
            " GROUP BY date_trunc('day',doc_request.time_create) " +
            " ORDER BY date_trunc('day',doc_request.time_create) DESC;")
    public List<Map<String, Object>> getStatisticForEachDay(@Param("id_type_request") int id_type_request);

    @Query(nativeQuery = true, value = "with slice_doc_request as(\n" +
            "    select co.inn,  max(time_create) as time_create\n" +
            "    from doc_request as dr\n" +
            "             inner join cls_organization as co\n" +
            "                        on dr.id_organization = co.id\n" +
            "    group by co.inn\n" +
            "),\n" +
            "     drinn as(\n" +
            "         select co.inn,  dr.*\n" +
            "         from doc_request as dr\n" +
            "                  inner join cls_organization as co\n" +
            "                             on dr.id_organization = co.id\n" +
            "     ),\n" +
            "sr as (\n" +
            "    select sdr.inn,\n" +
            "       count(*) filter ( where not dr.is_actualization ) as count_not_actual,\n" +
            "       count(*) filter ( where dr.is_actualization ) as count_actual\n" +
            "    from slice_doc_request as sdr\n" +
            "         inner join drinn as dr\n" +
            "                             on (sdr.inn, sdr.time_create) = (dr.inn, dr.time_create)\n" +
            "    group by sdr.inn\n" +
            ")\n" +
            "select sum(sr.count_not_actual) as count_not_actual, sum(sr.count_actual) as count_actual\n" +
            "from sr;")
    public Map<String, Object> getActualRequestStatisticForEeachOrganization();

    @Query(nativeQuery = true, value = "with slice_doc_request as(\n" +
            "    select co.inn,  max(time_create) as time_create\n" +
            "    from doc_request as dr\n" +
            "             inner join cls_organization as co\n" +
            "                        on dr.id_organization = co.id\n" +
            "    group by co.inn\n" +
            "),\n" +
            "     drinn as(\n" +
            "         select co.inn,  dr.*\n" +
            "         from doc_request as dr\n" +
            "                  inner join cls_organization as co\n" +
            "                             on dr.id_organization = co.id\n" +
            "     )\n" +
            "select cd.name,\n" +
            "       count(*) filter ( where not dr.is_actualization ) as count_not_actual,\n" +
            "       count(*) filter ( where dr.is_actualization )     as count_actual,\n" +
            "       sum(dr.person_office_cnt) filter ( where dr.is_actualization )   as count_worker_office,\n" +
            "       sum(dr.person_remote_cnt) filter ( where dr.is_actualization )   as count_worker_remote\n" +
            "from slice_doc_request as sdr\n" +
            "         inner join drinn as dr\n" +
            "                             on (sdr.inn, sdr.time_create) = (dr.inn, dr.time_create)\n" +
            "         inner join cls_department as cd\n" +
            "                    on (dr.id_department) = (cd.id)\n" +
            "group by cd.name\n" +
            "\n")
    public List<Map<String, Object>> getActualRequestStatisticForEeachDepartment();

    @Query(nativeQuery = true, value = "with slice_doc_request as(\n" +
            "    select co.inn,  max(time_create) as time_create\n" +
            "    from doc_request as dr\n" +
            "             inner join cls_organization as co\n" +
            "                        on dr.id_organization = co.id\n" +
            "    where dr.is_actualization=true\n" +
            "    group by co.inn\n" +
            "),\n" +
            "     drinn as(\n" +
            "         select co.inn,  dr.*\n" +
            "         from doc_request as dr\n" +
            "                  inner join cls_organization as co\n" +
            "                             on dr.id_organization = co.id\n" +
            "     ),\n" +
            "    sr as (\n" +
            "         select sdr.inn,\n" +
            "                sum(dr.person_office_cnt) as count_office,\n" +
            "                sum(dr.person_remote_cnt) as count_remote\n" +
            "         from slice_doc_request as sdr\n" +
            "                  inner join drinn as dr\n" +
            "                             on (sdr.inn, sdr.time_create) = (dr.inn, dr.time_create)\n" +
            "         group by sdr.inn\n" +
            "     )" +
            "select sum(sr.count_office) as count_office, sum(sr.count_remote) as count_remote\n" +
            "from sr;\n")
    public Map<String, Object> getActualNumberWorkerForEachOrganization();

    @Query(nativeQuery = true, value = "with slice_doc_request as(\n" +
            "    select co.inn,  max(time_create) as time_create\n" +
            "    from doc_request as dr\n" +
            "             inner join cls_organization as co\n" +
            "                        on dr.id_organization = co.id\n" +
            "    where dr.is_actualization=true\n" +
            "    group by co.inn\n" +
            "),\n" +
            "     drinn as(\n" +
            "         select co.inn,  dr.*\n" +
            "         from doc_request as dr\n" +
            "                  inner join cls_organization as co\n" +
            "                             on dr.id_organization = co.id\n" +
            "     ),\n" +
            "sr as (\n" +
            "    select co.inn,\n" +
            "           sum(dr.person_office_cnt) as count_office,\n" +
            "           sum(dr.person_remote_cnt) as count_remote\n" +
            "    from slice_doc_request as sdr\n" +
            "             inner join drinn as dr\n" +
            "                             on (sdr.inn, sdr.time_create) = (dr.inn, dr.time_create)\n" +
            "             inner join cls_organization as co\n" +
            "                        on (dr.id_organization = co.id)\n" +
            "    group by co.inn\n" +
            ")\n" +
            "select sum(sr.count_office) as count_worker_office, sum(sr.count_remote) as count_worker_remote\n" +
            "from sr;")
    public Map<String, Object> getActualNumberWorkerForEachDepartment();

    @Query(nativeQuery = true, value = "with slice_mails as (\n" +
            "    select sr.id_principal, id_mailing_list\n" +
            "    from reg_mailing_list_follower as sr\n" +
            "    group by sr.id_mailing_list, sr.id_principal\n" +
            "), cal_mails as (\n" +
            "    select id_mailing_list, count(id_mailing_list) as cm\n" +
            "    from slice_mails\n" +
            "    group by id_mailing_list\n" +
            "    order by cm desc\n" +
            ")\n" +
            "select name, cm\n" +
            "from cls_mailing_list as cml\n" +
            "    inner join cal_mails as cam\n" +
            "        on (cam.id_mailing_list) = (cml.id)")
    public List<Map<String, Object>> getNumberOfSubscribersForEachMailing();

    @Query(nativeQuery = true, value = "select count(id)\n" +
            "from cls_principal")
    public Integer getCountOfSubscribers();

    @Query(value = "with sdr as (\n" +
            "    select id_mailing, count(id_mailing) as cnt\n" +
            "    from reg_mailing_message as rmm\n" +
            "    where rmm.id_mailing is not null and rmm.status = :status and rmm.sending_time between :dateStart and :dateEnd\n" +
            "    group by rmm.id_mailing\n" +
            "    order by id_mailing\n" +
            ")\n" +
            "select name, cnt\n" +
            "from cls_mailing_list\n" +
            "         inner join sdr\n" +
            "                    on (cls_mailing_list.id) = (sdr.id_mailing)",
            nativeQuery = true)
    public List<Map<String, Object>> getNumberOfMailSentForEachMailing(@PathVariable("status") Integer status,
                                                                                 @RequestParam(value = "dateStart") Date dateStart,
                                                                                 @RequestParam(value = "dateEnd") Date dateEnd);
    @Query(nativeQuery = true, value = "with sdr as (\n" +
            "    select id_mailing, count(id_mailing) as cnt\n" +
            "    from reg_mailing_message as rmm\n" +
            "    where rmm.id_mailing is not null and rmm.sending_time between :dateStart and :dateEnd\n" +
            "    group by rmm.id_mailing\n" +
            "    order by id_mailing\n" +
            ")\n" +
            "select name, cnt\n" +
            "from cls_mailing_list\n" +
            "         inner join sdr\n" +
            "                    on (cls_mailing_list.id) = (sdr.id_mailing);")
    public List<Map<String, Object>> getNumberOfMailSentForEachMailing(@RequestParam(value = "dateStart") Date dateStart,
                                                                       @RequestParam(value = "dateEnd") Date dateEnd);
}
