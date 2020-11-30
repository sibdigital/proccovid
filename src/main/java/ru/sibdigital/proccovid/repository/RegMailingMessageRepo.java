package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegMailingMessage;

import java.util.Date;
import java.util.List;

@Repository
public interface RegMailingMessageRepo extends JpaRepository<RegMailingMessage, Long> {
    List<RegMailingMessage> findAllByStatus(Short status);

    @Query(nativeQuery = true, value = "SELECT * from reg_mailing_message where status = :status and sending_time > :currentTime")
    List<RegMailingMessage> findAllByStatusAndCurrentTime(Short status, Date currentTime);

}