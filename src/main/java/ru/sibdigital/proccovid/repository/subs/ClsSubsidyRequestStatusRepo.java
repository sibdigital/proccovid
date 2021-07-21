package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;

import java.util.List;
import java.util.Map;

@Repository
public interface ClsSubsidyRequestStatusRepo extends JpaRepository<ClsSubsidyRequestStatus, Long> {

    @Query(value = "select short_name from subs.cls_subsidy_request_status\n" +
            "where code != 'NEW'\n" +
            "group by short_name\n" +
            "order by short_name;\n",
            nativeQuery = true)
    public List<String> getClsSubsidyRequestStatusShort();

    @Query(value = "select * from subs.cls_subsidy_request_status\n" +
            "where code = :code\n" +
            "limit 1\n", nativeQuery = true)
    public ClsSubsidyRequestStatus getClsSubsidyRequestStatusByStatus(@Param("code") String code);
}
