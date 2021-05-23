package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegViolationSearch;

@Repository
public interface RegViolationSearchRepo extends JpaRepository<RegViolationSearch, Long>, JpaSpecificationExecutor<RegViolationSearch> {

}
