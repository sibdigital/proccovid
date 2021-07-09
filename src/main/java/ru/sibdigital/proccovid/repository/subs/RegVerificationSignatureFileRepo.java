package ru.sibdigital.proccovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;

public interface RegVerificationSignatureFileRepo extends JpaRepository<RegVerificationSignatureFile, Long> {
}
