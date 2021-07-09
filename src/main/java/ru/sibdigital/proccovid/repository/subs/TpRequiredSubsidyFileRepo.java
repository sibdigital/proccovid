package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.TpRequiredSubsidyFile;

public interface TpRequiredSubsidyFileRepo extends JpaRepository<TpRequiredSubsidyFile, Long> {
}
