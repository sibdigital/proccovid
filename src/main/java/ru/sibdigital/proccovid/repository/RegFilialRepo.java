package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.RegFilial;

import java.util.List;

@Repository
public interface RegFilialRepo extends JpaRepository<RegFilial, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_filial\n" +
            "WHERE id_egrul in (:id_egruls)",
            nativeQuery = true)
    void deleteRegFilials(@Param("id_egruls") List<Long> id_egruls);

    RegFilial findByEgrul_IogrnAndAndKladrAddressHashAndType(Long iogrn, Integer address_hash, Integer type);
}
