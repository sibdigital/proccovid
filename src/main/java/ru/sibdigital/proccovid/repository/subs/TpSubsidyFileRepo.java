package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.TpSubsidyFile;

public interface TpSubsidyFileRepo extends JpaRepository<TpSubsidyFile, Long> {
}
