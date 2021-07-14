package ru.sibdigital.proccovid.service.subs;

import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;

import java.util.List;
import java.util.Map;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long tpRequestSubsidyFileId);
}
