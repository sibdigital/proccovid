package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;

public interface ClsSubsidyRequestStatusRepo extends JpaRepository<ClsSubsidyRequestStatus, Long> {
}
