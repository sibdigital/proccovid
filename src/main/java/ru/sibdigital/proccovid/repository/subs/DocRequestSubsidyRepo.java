package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.DocRequestPrs;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;

@Repository
public interface DocRequestSubsidyRepo extends JpaRepository<DocRequestSubsidy, Long>, JpaSpecificationExecutor<DocRequestSubsidy> {
}
