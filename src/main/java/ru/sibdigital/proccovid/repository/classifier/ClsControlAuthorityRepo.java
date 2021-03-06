package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsControlAuthority;

import java.util.List;

@Repository
public interface ClsControlAuthorityRepo extends JpaRepository<ClsControlAuthority, Long>, JpaSpecificationExecutor<ClsControlAuthority> {
    List<ClsControlAuthority> findAllByIsDeleted(Boolean isDeleted);
}
