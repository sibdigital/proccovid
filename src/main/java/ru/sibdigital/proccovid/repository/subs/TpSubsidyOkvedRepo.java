package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;

import java.util.List;

public interface TpSubsidyOkvedRepo extends JpaRepository<TpSubsidyOkved, Long> {
    List<TpSubsidyOkved> findAllBySubsidyAndIsDeleted(ClsSubsidy subsidy, Boolean isDeleted);
}
