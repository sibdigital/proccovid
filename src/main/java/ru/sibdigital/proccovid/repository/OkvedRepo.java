package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.Okved;


@Repository
public interface OkvedRepo extends JpaRepository<Okved, Integer> {

}
