package ru.sibdigital.proccovid.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.repository.specification.ClsControlAuthoritySpecification;

import java.util.List;

@Repository
public interface ClsControlAuthorityRepo extends JpaRepository<ClsControlAuthority, Long>, JpaSpecificationExecutor<ClsControlAuthority> {
    List<ClsControlAuthority> findAllByIsDeleted(Boolean isDeleted);
}
