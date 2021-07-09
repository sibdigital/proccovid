package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;

public interface DocRequestSubsidyRepo extends JpaRepository<DocRequestSubsidy, Long> {
}
