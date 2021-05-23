package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsTemplate;

@Repository
public interface ClsTemplateRepo extends JpaRepository<ClsTemplate, Long> {

    ClsTemplate findByKey(String type);
}
