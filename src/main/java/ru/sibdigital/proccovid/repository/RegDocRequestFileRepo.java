package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegDocRequestFile;

@Repository
public interface RegDocRequestFileRepo extends JpaRepository<RegDocRequestFile, Long> {

}
