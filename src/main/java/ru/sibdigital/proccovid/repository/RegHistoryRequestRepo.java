package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegHistoryRequest;

@Repository
public interface RegHistoryRequestRepo extends JpaRepository<RegHistoryRequest, Long> {

}
