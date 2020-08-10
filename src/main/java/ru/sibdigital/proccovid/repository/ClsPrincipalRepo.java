package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsPrincipal;

@Repository
public interface ClsPrincipalRepo extends JpaRepository<ClsPrincipal, Long> {

}