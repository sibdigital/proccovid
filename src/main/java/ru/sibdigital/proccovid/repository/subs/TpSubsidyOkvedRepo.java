package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;

import java.util.List;

@Repository
public interface TpSubsidyOkvedRepo extends JpaRepository<TpSubsidyOkved, Long> {
    List<TpSubsidyOkved> findAllBySubsidyAndIsDeleted(ClsSubsidy subsidy, Boolean isDeleted);
}
