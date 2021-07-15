package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;

@Repository
public interface RegVerificationSignatureFileRepo extends JpaRepository<RegVerificationSignatureFile, Long> {
}
