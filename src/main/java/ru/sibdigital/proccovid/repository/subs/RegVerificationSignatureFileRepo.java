package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;

import java.util.List;

@Repository
public interface RegVerificationSignatureFileRepo extends JpaRepository<RegVerificationSignatureFile, Long> {

    @Query(
            value = "select rvsf.*\n " +
                    "from reg_verification_signature_file as rvsf\n " +
                    "where rvsf.id_user = :id_user and rvsf.id_request = :id_request\n " +
                    "and rvsf.id_request_subsidy_file = :id_request_subsidy_file\n" +
                    "and rvsf.id_request_subsidy_signature_file = :id_request_subsidy_signature_file\n",
            nativeQuery = true
    )
    List<RegVerificationSignatureFile> getTpRequestSubsidyFilesPrevisiousVerified(
            @Param("id_user") Long idUser,  @Param("id_request") Long idRequest,
            @Param("id_request_subsidy_file") Long idRequestSubsidyFile,
            @Param("id_request_subsidy_signature_file") Long idRequestSubsidySignatureFile);
}
