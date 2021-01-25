package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.egr.SvStatus;

import java.util.List;

@Repository
public interface SvStatusRepo extends JpaRepository<SvStatus, Long>, JpaSpecificationExecutor<SvStatus> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM egr.sv_status\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteSvStatuses(@Param("id_egruls") List<Long> id_egruls);
}
