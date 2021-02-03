package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.egr.Opf;

@Repository
public interface OpfRepo extends JpaRepository<Opf, Long> {

    Opf findOpfBySprAndCodeAndFullName(String opf, String code, String fullName);
}
