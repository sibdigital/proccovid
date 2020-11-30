package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsMailingList;
import ru.sibdigital.proccovid.model.ClsMailingListOkved;

import java.util.List;

@Repository
public interface ClsMailingListOkvedRepo extends JpaRepository<ClsMailingListOkved, Long> {
    List<ClsMailingListOkved> findClsMailingListOkvedByClsMailingList_Id(Long id_mailing);
    List<ClsMailingListOkved> findClsMailingListOkvedByClsMailingList(ClsMailingList clsMailingList);
}