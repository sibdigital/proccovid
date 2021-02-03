package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.egr.SvReg;

import java.util.List;

@Repository
public interface SvRegRepo extends JpaRepository<SvReg, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM egr.sv_reg\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteSvRegsByIdEgruls(@Param("id_egruls") List<Long> id_egruls);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM egr.sv_reg\n" +
            "WHERE id_egrip in (:id_egrips)",
            nativeQuery = true)
    void deleteSvRegsByIdEgrips(@Param("id_egrips") List<Long> id_egrips);
}
