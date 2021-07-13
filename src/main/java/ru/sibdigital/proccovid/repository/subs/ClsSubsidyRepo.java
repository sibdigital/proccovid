package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;

import java.util.List;
import java.util.Optional;

public interface ClsSubsidyRepo extends JpaRepository<ClsSubsidy, Long> {
    List<ClsSubsidy> findAllByIsDeleted(Boolean isDeleted);
}
