package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsMailingList;

import java.util.List;

@Repository
public interface ClsMailingListRepo extends JpaRepository<ClsMailingList, Long> {
    List<ClsMailingList> findAllByOrderByIdAsc();
}