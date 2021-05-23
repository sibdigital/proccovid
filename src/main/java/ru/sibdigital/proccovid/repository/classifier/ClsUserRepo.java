package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsUser;

@Repository
public interface ClsUserRepo extends JpaRepository<ClsUser, Long> {

    ClsUser findByLogin(String login);

}
