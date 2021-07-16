package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.proccovid.repository.subs.RegVerificationSignatureFileRepo;
import ru.sibdigital.proccovid.repository.subs.TpRequestSubsidyFileRepo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Override
    public List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long tpRequestSubsidyFileId) {
        return tpRequestSubsidyFileRepo.getSignatureVerificationTpRequestSubsidyFile(tpRequestSubsidyFileId);
    }

    @Override
    public List<RegVerificationSignatureFile> verifyRequestFiles(DocRequestSubsidy docRequestSubsidy, ClsUser user){
        List<RegVerificationSignatureFile> list = new ArrayList<>();

        List<TpRequestSubsidyFile> signatureFiles = tpRequestSubsidyFileRepo.getSignatureFilesByIdRequest(docRequestSubsidy.getId());
        List<VerifiedData> verifiedDataList = new ArrayList<>();
        for(TpRequestSubsidyFile signatureFile : signatureFiles){
            TpRequestSubsidyFile docFile = signatureFile.getRequestSubsidyFile();

            VerifiedData verifiedData = new VerifiedData(
                    signatureFile.getAttachmentPath(),
                    docFile.getAttachmentPath(),
                    docFile.getId(),
                    signatureFile.getId(),
                    docFile.getRequestSubsidy().getId()
            );

            List<RegVerificationSignatureFile> previsious = regVerificationSignatureFileRepo
                    .getTpRequestSubsidyFilesPrevisiousVerified(user.getId(),
                            signatureFile.getRequestSubsidy().getId(),
                            signatureFile.getId(), docFile.getId());
            if(previsious.isEmpty()){
                RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
                        .requestSubsidy(docFile.getRequestSubsidy())
                        .requestSubsidyFile(docFile)
                        .requestSubsidySubsidySignatureFile(signatureFile)
                        .isDeleted(false)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .verifyStatus(0)
                        .user(user)
                        .build();
                previsious.add(regVerificationSignatureFile);
            }else{
                previsious.stream().forEach(p -> {
                    p.setTimeCreate(new Timestamp(System.currentTimeMillis()));
                    p.setVerifyStatus(0);
                    p.setVerifyResult("");
                });
            }
            regVerificationSignatureFileRepo.saveAll(previsious);
            verifiedDataList.add(verifiedData);

        }
        return list;
    }

}
