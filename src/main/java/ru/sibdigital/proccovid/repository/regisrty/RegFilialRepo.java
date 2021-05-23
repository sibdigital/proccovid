package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.model.RegFilial;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegFilialRepo extends JpaRepository<RegFilial, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_filial\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteRegFilials(@Param("id_egruls") List<Long> id_egruls);

    Optional<List<RegFilial>> findAllByEgrul_Id(Long id_egrul);
}
