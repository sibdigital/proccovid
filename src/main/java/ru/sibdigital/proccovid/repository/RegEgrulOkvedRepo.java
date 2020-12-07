package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.RegEgrulOkved;

import java.util.List;

@Repository
public interface RegEgrulOkvedRepo extends JpaRepository<RegEgrulOkved, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_egrul_okved\n" +
            "WHERE id_egrul = :id_egrul",
            nativeQuery = true)
    void deleteRegEgrulOkved(@Param("id_egrul") Long id_egrul);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_egrul_okved\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteRegEgrulOkveds(@Param("id_egruls") List<Long> id_egruls);
}
