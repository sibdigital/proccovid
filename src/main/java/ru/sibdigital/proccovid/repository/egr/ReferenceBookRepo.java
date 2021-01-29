package ru.sibdigital.proccovid.repository.egr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.egr.ReferenceBook;

import java.util.List;

@Repository
public interface ReferenceBookRepo extends JpaRepository<ReferenceBook, Long> {
    List<ReferenceBook> findAllByType(Short type);
}
