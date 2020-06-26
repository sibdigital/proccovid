package ru.sibdigital.proccovid.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.DepUser;

@Repository
public interface DepUserRepo extends CrudRepository<DepUser, Long> {

    DepUser findByLogin(String login);

}
