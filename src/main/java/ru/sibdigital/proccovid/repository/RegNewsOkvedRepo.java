package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsNews;
import ru.sibdigital.proccovid.model.RegNewsOkved;

import java.util.List;

@Repository
public interface RegNewsOkvedRepo extends JpaRepository<RegNewsOkved, Long> {
    List<RegNewsOkved> findClsNewsOkvedByNews_Id(long id_news);
    List<RegNewsOkved> findClsNewsOkvedByNews(ClsNews news);

}