package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegEgrip;

@Repository
public interface RegEgripRepo extends JpaRepository<RegEgrip, Long> {

    RegEgrip findByInn(String inn);
}
