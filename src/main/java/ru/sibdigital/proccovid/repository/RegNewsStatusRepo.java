package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsNews;
import ru.sibdigital.proccovid.model.RegNewsOrganization;
import ru.sibdigital.proccovid.model.RegNewsStatus;

import java.util.List;

@Repository
public interface RegNewsStatusRepo extends JpaRepository<RegNewsStatus, Long> {
    List<RegNewsStatus> findRegNewsStatusByNews(ClsNews news);
    List<RegNewsStatus> findRegNewsStatusByNews_Id(Long id_news);
}