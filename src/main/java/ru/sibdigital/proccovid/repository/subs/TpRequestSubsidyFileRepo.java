package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long>, JpaSpecificationExecutor<TpRequestSubsidyFile> {

    @Query(
            value = "with idFiles as (\n" +
                    "    select *\n" +
                    "    from subs.tp_request_subsidy_file\n" +
                    "    where id_request = :id_request_subsidy\n" +
                    "      and is_signature = false" +
                    "      and is_deleted = false\n" +
                    ")\n" +
                    "select idFiles.id, idFiles.id_request, idFiles.id_organization, idFiles.id_department, idFiles.id_processed_user, idFiles.id_file_type, idFiles.is_deleted, idFiles.time_create, idFiles.attachment_path, idFiles.file_name, idFiles.view_file_name, idFiles.original_file_name, idFiles.file_extension, idFiles.hash, idFiles.file_size, trsf.is_signature, idFiles.id_subsidy_request_file\n" +
                    "from idFiles\n" +
                    "    left join subs.tp_request_subsidy_file as trsf\n" +
                    "        on (idFiles.id) = (trsf.id_subsidy_request_file) and trsf.is_signature = true\n",
            nativeQuery = true
    )
    public List<TpRequestSubsidyFile> getTpRequestSubsidyFilesByDocRequestId(Long id_request_subsidy);


    @Query(
            value = "with idFiles as (\n" +
                    "    select *\n" +
                    "    from subs.tp_request_subsidy_file\n" +
                    "    where id_request = :id_request_subsidy\n" +
                    "      and is_signature = false\n" +
                    ")\n" +
                    "select rvsf.id_request_subsidy_file, rvsf.verify_status, rvsf.verify_result\n" +
                    "from idFiles\n" +
                    "         inner join  subs.tp_request_subsidy_file as trsf\n" +
                    "                     on (idFiles.id) = (trsf.id_subsidy_request_file) and trsf.is_signature = true\n" +
                    "    inner join subs.reg_verification_signature_file as rvsf\n" +
                    "        on (idFiles.id_request, idFiles.id, trsf.id) = (rvsf.id_request, rvsf.id_request_subsidy_file, rvsf.id_request_subsidy_signature_file)",
            nativeQuery = true
    )
    public List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long id_request_subsidy);

    @Query(value = "select *\n" +
            "from subs.tp_request_subsidy_file as t\n" +
            "where t.id_request is not null and t.is_deleted = false and t.is_signature = true\n" +
            "  and t.id_request = :idRequest",
            nativeQuery = true
    )
    public List<TpRequestSubsidyFile> getSignatureFilesByIdRequest(Long idRequest);

}
