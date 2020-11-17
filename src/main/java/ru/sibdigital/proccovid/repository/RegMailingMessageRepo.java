package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegMailingMessage;

@Repository
public interface RegMailingMessageRepo extends JpaRepository<RegMailingMessage, Long> {
}