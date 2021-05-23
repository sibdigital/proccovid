package ru.sibdigital.proccovid.repository.classifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsPrincipal;

import java.util.List;

@Repository
public interface ClsPrincipalRepo extends JpaRepository<ClsPrincipal, Long> {
    @Query(nativeQuery = true, value = "SELECT *\n" +
            "FROM cls_principal\n" +
            "INNER JOIN (SELECT rmlf.id_principal\n" +
            "            FROM reg_mailing_list_follower rmlf\n" +
            "                     INNER JOIN reg_mailing_message rmm\n" +
            "                                ON rmlf.id_mailing_list = rmm.id_mailing\n" +
            "            WHERE rmm.id = :id_message and rmlf.deactivation_date IS NULL) AS tbl\n" +
            "ON cls_principal.id = tbl.id_principal")
    List<ClsPrincipal> getClsPrincipalsByMessage_Id(@Param("id_message") Long id_message);

}
