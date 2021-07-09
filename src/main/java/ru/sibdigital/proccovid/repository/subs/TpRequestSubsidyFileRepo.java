package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
}
