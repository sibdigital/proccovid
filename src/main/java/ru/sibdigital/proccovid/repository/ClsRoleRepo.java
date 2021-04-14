package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsRole;

@Repository
public interface ClsRoleRepo extends JpaRepository<ClsRole, Long> {

}
