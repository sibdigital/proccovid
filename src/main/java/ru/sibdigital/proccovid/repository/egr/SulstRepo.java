package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.egr.Sulst;

@Repository
public interface SulstRepo extends JpaRepository<Sulst, Long> {

}
