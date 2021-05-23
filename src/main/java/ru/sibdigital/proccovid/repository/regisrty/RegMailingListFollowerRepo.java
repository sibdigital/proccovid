package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegMailingListFollower;
import ru.sibdigital.proccovid.model.RegMailingMessage;

import java.util.Date;
import java.util.List;

@Repository
public interface RegMailingListFollowerRepo extends JpaRepository<RegMailingListFollower, Long> {


}