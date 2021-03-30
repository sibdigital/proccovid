package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegPersonViolationSearch;

@Repository
public interface RegPersonViolationSearchRepo extends JpaRepository<RegPersonViolationSearch, Long>, JpaSpecificationExecutor<RegPersonViolationSearch> {

}
