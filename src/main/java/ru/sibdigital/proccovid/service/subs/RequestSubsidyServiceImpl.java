package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.proccovid.repository.subs.TpRequestSubsidyFileRepo;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Override
    public List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long tpRequestSubsidyFileId) {
        return tpRequestSubsidyFileRepo.getSignatureVerificationTpRequestSubsidyFile(tpRequestSubsidyFileId);
    }

}
