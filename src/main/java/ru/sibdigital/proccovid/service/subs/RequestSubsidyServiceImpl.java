package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.proccovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.proccovid.repository.subs.RegVerificationSignatureFileRepo;
import ru.sibdigital.proccovid.repository.subs.TpRequestSubsidyFileRepo;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Value("${addcovid.baseurl}")
    private String addCovidBaseUrl;

    @Override
    public List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long tpRequestSubsidyFileId) {
        return tpRequestSubsidyFileRepo.getSignatureVerificationTpRequestSubsidyFile(tpRequestSubsidyFileId);
    }

    @Override
    public List<RegVerificationSignatureFile> verifyRequestFiles(DocRequestSubsidy docRequestSubsidy, ClsUser user){
        List<RegVerificationSignatureFile> list = new ArrayList<>();

        List<TpRequestSubsidyFile> signatureFiles = tpRequestSubsidyFileRepo.getSignatureFilesByIdRequest(docRequestSubsidy.getId());
        List<VerifiedData> verifiedDataList = new ArrayList<>();
        for (TpRequestSubsidyFile signatureFile : signatureFiles) {
            TpRequestSubsidyFile docFile = signatureFile.getRequestSubsidyFile();

            VerifiedData verifiedData = new VerifiedData(
                    signatureFile.getAttachmentPath(),
                    docFile.getAttachmentPath(),
                    docFile.getId(),
                    signatureFile.getId()
            );

            List<RegVerificationSignatureFile> previous = regVerificationSignatureFileRepo
                    .getTpRequestSubsidyFilesPrevisiousVerified(user.getId(),
                            signatureFile.getRequestSubsidy().getId(),
                            docFile.getId(), signatureFile.getId());
            if (previous.isEmpty()) {
                RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
                        .requestSubsidy(docFile.getRequestSubsidy())
                        .requestSubsidyFile(docFile)
                        .requestSubsidySubsidySignatureFile(signatureFile)
                        .isDeleted(false)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .verifyStatus(0)
                        .user(user)
                        .build();
                previous.add(regVerificationSignatureFile);
            } else {
                previous.stream().forEach(p -> {
                    p.setTimeCreate(new Timestamp(System.currentTimeMillis()));
                    p.setVerifyStatus(0);
                    p.setVerifyResult("");
                });
            }
            List<RegVerificationSignatureFile> prevList = regVerificationSignatureFileRepo.saveAll(previous);
            for (RegVerificationSignatureFile prev : prevList) {
                System.out.println(prev);
                if (verifiedData.getIdentificator().equals(prev.getRequestSubsidyFile().getId().toString()) && verifiedData.getSignatureIdentificator().equals(prev.getRequestSubsidySubsidySignatureFile().getId().toString())) {
                    verifiedData.setGroup(prev.getId().toString());
                }
            }
            verifiedDataList.add(verifiedData);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<VerifiedData>> newRequest = new HttpEntity<>(verifiedDataList, headers);

            String url = String.format("%s/verify/subsidy/list", addCovidBaseUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, newRequest, String.class);

            HttpStatus responseFromAddCovid = response.getStatusCode();
        }
        return list;
    }

    public HashMap<String, Object> checkSignatureFilesVerifyProgress(Long idRequest, Long idUser) {
        HashMap<String, Object> result = new HashMap<>();

        List<RegVerificationSignatureFile> regVerificationSignatureFiles =
                regVerificationSignatureFileRepo.findRegVerificationSignatureFileByIdRequestSubsidyAndIdUser(idRequest, idUser).orElse(null);

        if (regVerificationSignatureFiles == null) {
            result.put("notFound", "Файлы не найдены");
        } else {
            long countVerifySignatures = regVerificationSignatureFiles.stream()
                    .filter(rvsf -> rvsf.getVerifyStatus() != 0)
                    .count();
            long countNumberOfFiles = regVerificationSignatureFiles.size();

            result.put("files", regVerificationSignatureFiles);
            result.put("verified", countVerifySignatures);
            result.put("numberOfFiles", countNumberOfFiles);
        }
        return result;
    }
}
