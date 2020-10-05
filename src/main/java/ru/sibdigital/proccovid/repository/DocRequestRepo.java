package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.DocRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            "    select id_organization,  max(time_create) as time_create\n" +
            "    from doc_request\n" +
            "    group by id_organization\n" +
            ")\n" +
            "\n" +
            "select co.inn, count(*) filter ( where not dr.is_actualization ) as count_not_actual, count(*) filter ( where dr.is_actualization ) as count_actual\n" +
            "from slice_doc_request as sdr\n" +
            "         inner join cls_organization as co\n" +
            "                    on (sdr.id_organization = co.id)\n" +
            "         inner join doc_request dr\n" +
            "                    on (co.id, sdr.time_create) = (dr.id_organization, dr.time_create)\n" +
            "group by co.inn\n" +
            "order by co.inn;")
    public List<Map<String, Object>> getActualRequestStatisticForEeachOrganization();

    @Query(nativeQuery = true, value = "with slice_doc_request as (\n" +
            "    select id_department, max(time_create) as time_create\n" +
            "    from doc_request\n" +
            "    group by id_department\n" +
            ")\n" +
            "select cd.name, count(*) filter ( where dr.is_actualization ) as count_actual, count(*) filter ( where not dr.is_actualization ) as count_not_actual,\n" +
            "       count(dr.person_remote_cnt) as count_worker_remote, count(dr.person_office_cnt) as count_worker_office\n" +
            "from slice_doc_request as sdr\n" +
            "         inner join doc_request as dr\n" +
            "                    on (sdr.id_department, sdr.time_create) = (dr.id_department, dr.time_create)\n" +
            "         inner join cls_department as cd\n" +
            "                    on (sdr.id_department) = (cd.id)\n" +
            "group by cd.name\n" +
            "order by cd.name;")
    public List<Map<String, Object>> getActualRequestStatisticForEeachDepartment();

    @Query(nativeQuery = true, value = "with slice_doc_request as(\n" +
            "    select id_organization,  max(time_create) as time_create\n" +
            "    from doc_request\n" +
            "    where is_actualization=true\n" +
            "    group by id_organization\n" +
            ")\n" +
            "\n" +
            "select co.inn, sum(dr.person_office_cnt) as count_office, sum(dr.person_remote_cnt) as count_remote\n" +
            "from slice_doc_request as sdr\n" +
            "         inner join cls_organization as co\n" +
            "                    on (sdr.id_organization = co.id)\n" +
            "         inner join doc_request dr\n" +
            "                    on (co.id, sdr.time_create) = (dr.id_organization, dr.time_create)\n" +
            "group by co.inn\n" +
            "order by co.inn;")
    public List<Map<String, Object>> getActualNumberWorkerForEachOrganization();

    @Query(nativeQuery = true, value = "with slice_doc_request as (\n" +
            "    select id_department, max(time_create) as time_create\n" +
            "    from doc_request\n" +
            "    where doc_request.is_actualization = true\n" +
            "    group by id_department\n" +
            ")\n" +
            "\n" +
            "select cd.name, dr.person_office_cnt as count_office, dr.person_remote_cnt as count_remote\n" +
            "from slice_doc_request as sdr\n" +
            "    inner join doc_request as dr\n" +
            "        on (dr.id_department, dr.time_create) = (sdr.id_department, sdr.time_create)\n" +
            "    inner join cls_department as cd\n" +
            "        on (sdr.id_department) = (cd.id)\n" +
            "order by cd.name;")
    public List<Map<String, Object>> getActualNumberWorkerForEachDepartment();

}
