package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.proccovid.model.subs.TpRequiredSubsidyFile;

import java.util.List;

public interface TpRequiredSubsidyFileRepo extends JpaRepository<TpRequiredSubsidyFile, Long> {
    @Query( value = "select trsf\n" +
            "from TpRequiredSubsidyFile as trsf\n" +
            "where trsf.clsSubsidy.id = :id_subsidy and trsf.isDeleted = false")
    List<TpRequiredSubsidyFile> findAllByIdSubsidy(Long id_subsidy);

    @Query(value = "select t\n" +
            "from TpRequiredSubsidyFile as t\n" +
            "where t.isDeleted = false and t.clsSubsidy.id = :subsidyId and t.clsFileType.id = :idFileType")
    TpRequiredSubsidyFile findByIdSubsidyAndIdFileType(Long subsidyId, Long idFileType);
}
