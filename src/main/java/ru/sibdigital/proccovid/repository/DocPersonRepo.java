package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.DocPerson;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface DocPersonRepo extends JpaRepository<DocPerson, Long> {

    @Query(nativeQuery = true, value = "select count(*) from ( select distinct lastname, firstname, patronymic from doc_person where is_deleted = false) as s")
    Long getTotalPeople();

    @Query(nativeQuery = true, value = "SELECT count(*) FROM ( SELECT DISTINCT firstname, lastname, patronymic " +
            "FROM doc_person " +
            "WHERE is_deleted = false and id_request IN (SELECT id FROM doc_request WHERE status_review = :status)) AS s;")
    Long getTotalApprovedPeopleByReviewStatus(@Param("status") int status);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM ( SELECT DISTINCT firstname, lastname, patronymic F" +
            "ROM doc_person " +
            "WHERE id_request IN (SELECT id " +
            "   FROM doc_request " +
            "   WHERE is_deleted = false and status_review = :status and id_type_request = :id_type_request)" +
            ") AS s;")
    Long getTotalApprovedPeopleByReviewStatus(@Param("status") int status, @Param("id_type_request") int id_type_request);

    @Query(nativeQuery = true, value = "select sum(neobr) as awaiting, sum(utv) as accepted, sum(otkl) as declined from doc_person as d\n" +
            "                                                                        left join ( select id_req, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl\n" +
            "                                                                                    from (\n" +
            "                                                                                             select id_req,\n" +
            "                                                                                                    sum(neobr) as neobr,\n" +
            "                                                                                                    sum(utv)   as utv,\n" +
            "                                                                                                    sum(otkl)  as otkl\n" +
            "                                                                                             from (\n" +
            "                                                                                                      select id as id_req, 0 as neobr, 1 as utv, 0 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review = 1\n" +
            "                                                                                                      union all\n" +
            "                                                                                                      select id as id_req, 0 as neobr, 0 as utv, 1 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review = 2\n" +
            "                                                                                                      union all\n" +
            "                                                                                                      select id as id_req, 1 as neobr, 0 as utv, 0 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review <> 2\n" +
            "                                                                                                        and status_review <> 1\n" +
            "                                                                                                  ) as s\n" +
            "                                                                                             group by id_req\n" +
            "                                                                                         ) as m group by id_req\n" +
            ") as ss on d.id_request = ss.id_req and d.is_deleted = false;")
    Map<String, Long> getTotalPeopleStatistic();

    @Query(nativeQuery = true, value = "select * from doc_person where id_request = :id_request and is_deleted = false")
    Optional<List<DocPerson>> findByDocRequest(Long id_request);
}
