package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.egr.SvRecordEgr;

import java.util.List;

@Repository
public interface SvRecordEgrRepo extends JpaRepository<SvRecordEgr, Long>, JpaSpecificationExecutor<SvRecordEgr> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM egr.sv_record_egr\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteSvRecordEgrsByIdEgruls(@Param("id_egruls") List<Long> id_egruls);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM egr.sv_record_egr\n" +
            "WHERE id_egrip in (:id_egrips)",
            nativeQuery = true)
    void deleteSvRecordEgrsByIdEgrips(@Param("id_egrips") List<Long> id_egrips);
}
