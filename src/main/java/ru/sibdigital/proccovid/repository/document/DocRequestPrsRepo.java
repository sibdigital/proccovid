package ru.sibdigital.proccovid.repository.document;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.DocRequestPrs;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocRequestPrsRepo extends JpaRepository<DocRequestPrs, Long>, JpaSpecificationExecutor<DocRequestPrs> {

    @EntityGraph(attributePaths = {"organization", "department", "reassignedUser", "processedUser"})
    Optional<List<DocRequestPrs>> findByDepartmentAndStatusReviewOrderByTimeCreate(ClsDepartment department, Integer status, Pageable pageable);

    @Query(value = "SELECT COUNT(dr) FROM doc_request dr WHERE dr.id_department = :dep_id AND dr.status_review = :status", nativeQuery = true)
    Long getTotalCountByDepartmentAndStatusReview(@Param("dep_id") Long departmentId, @Param("status") Integer status);

    @Query(value = "SELECT dr.* FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status " +
            " and (trim(org.inn) like %:innOrName% or lower(trim(org.name)) like %:innOrName%) " +
            " ORDER BY dr.time_create ASC limit :limit offset :offset",
            nativeQuery = true)
    Optional<List<DocRequestPrs>> getRequestByDepartmentIdAndStatusAndInnOrName(@Param("dep_id") Long departmentId, @Param("status")
            Integer status, @Param("innOrName") String innOrName, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "SELECT COUNT(dr) FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status " +
            " and (trim(org.inn) like %:innOrName% or lower(trim(org.name)) like %:innOrName%)", nativeQuery = true)
    Long getTotalCountRequestByDepartmentIdAndStatusAndInnOrName(@Param("dep_id") Long departmentId, @Param("status") Integer status, @Param("innOrName") String innOrName);

}
