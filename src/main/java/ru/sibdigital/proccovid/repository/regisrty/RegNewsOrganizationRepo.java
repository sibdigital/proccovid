package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsNews;
import ru.sibdigital.proccovid.model.RegNewsOrganization;

import java.util.List;

@Repository
public interface RegNewsOrganizationRepo extends JpaRepository<RegNewsOrganization, Long> {
    List<RegNewsOrganization> findRegNewsOrganizationByOrganization_Inn(String inn);
    List<RegNewsOrganization> findRegNewsOrganizationByNews(ClsNews news);

    @Query(value = "SELECT co.inn\n" +
            "FROM cls_organization as co\n" +
            "INNER JOIN reg_news_organization rno on co.id = rno.id_organization\n" +
            "WHERE id_news = :id_news\n" +
            "GROUP BY inn",
            nativeQuery = true)
    List<String> findInnByNews(Long id_news);
}