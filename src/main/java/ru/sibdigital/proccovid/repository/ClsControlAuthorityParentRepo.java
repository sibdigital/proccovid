package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsControlAuthorityParent;

@Repository
public interface ClsControlAuthorityParentRepo extends JpaRepository<ClsControlAuthorityParent, Long>{
}
