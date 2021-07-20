package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.subs.*;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRequestStatusRepo;
import ru.sibdigital.proccovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.proccovid.repository.subs.RegVerificationSignatureFileRepo;
import ru.sibdigital.proccovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.proccovid.service.subs.RequestSubsidyServiceImpl;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@Slf4j
public class RequestSubsidyController {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    ClsSubsidyRequestStatusRepo clsSubsidyRequestStatusRepo;

    @Autowired
    RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Autowired
    RequestSubsidyServiceImpl requestSubsidyService;

    @GetMapping("/doc_requests_subsidy/{id_request_subsidy}")
    public DocRequestSubsidy getDocRequestSubsidy(@PathVariable("id_request_subsidy") Long id_request_subsidy, HttpSession session) throws IllegalAccessException, InstantiationException {
        DocRequestSubsidy rest = docRequestSubsidyRepo.findById(id_request_subsidy).orElse(null);
        if (rest != null && rest.getSubsidy().getId() == 1) {
            ClsSubsidy clsSubsidy = new ClsSubsidy();
            clsSubsidy.setId(5L);
            clsSubsidy.setName(rest.getSubsidy().getName());
            rest.setSubsidy(clsSubsidy);
        }

        return rest;
    }

    @GetMapping("request_subsidy_files/{id_request_subsidy}")
    public List<TpRequestSubsidyFile> getTpRequestSubsidyFiles(@PathVariable("id_request_subsidy") Long id_request_subsidy, HttpSession session) {
        return tpRequestSubsidyFileRepo.getTpRequestSubsidyFilesByDocRequestId(id_request_subsidy);
    }

    @GetMapping("request_subsidy_files_verification/{id_request_subsidy}")
    public List<Map<String, String>> getVerificationTpRequestSubsidyFiles(@PathVariable("id_request_subsidy") Long id_request_subsidy, HttpSession session) {
//        -- 0 - проверка не проводилась
//        -- 1 - проверка прошла успешно
//        -- 2 - подпись не соответствует файлу
//        -- 3  в сертификате или цепочке сертификатов есть ошибки
//        -- 4 в подписи есть ошибки

        List<Map<String, String>> list = tpRequestSubsidyFileRepo.getSignatureVerificationTpRequestSubsidyFile(id_request_subsidy);

//        List<Map<String, String>> result = new ArrayList<>();
//
//        for (Map<String, String> elem : list) {
//            HashMap<String, String> hashMap = new HashMap<>();
//            String status = (String) elem.values().toArray()[1];
//            switch (status) {
//                case "1": hashMap.put("verify_status", "проверка прошла успешно"); break;
//                case "2": hashMap.put("verify_status", "подпись не соответствует файлу"); break;
//                case "3": hashMap.put("verify_status", "в сертификате или цепочке сертификатов есть ошибки"); break;
//                case "4": hashMap.put("verify_status", "в подписи есть ошибки"); break;
//                default: hashMap.put("verify_status", "проверка не проводилась"); break;
//            }
//            result.add(hashMap);
//        }

        return list;
    }

    @PostMapping(value = "change_request_subsidy_status/{id_request_subsidy}/{approve}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Map<String, String>> changeRequestSubsidyStatus(@PathVariable("id_request_subsidy") Long id,
                                                      @PathVariable("approve") Boolean approve,
                                                      @RequestBody DocRequestSubsidy newDocRequestSubsidy,
                                                      HttpSession session
    ){
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(id).orElse(null);
        Map<String, String> response = new HashMap<>();

        if (docRequestSubsidy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of( "success", "false"));
        } else if (newDocRequestSubsidy.getResolutionComment() != null && !newDocRequestSubsidy.getResolutionComment().equals(docRequestSubsidy.getResolutionComment())) {
            docRequestSubsidy.setResolutionComment(newDocRequestSubsidy.getResolutionComment());
            docRequestSubsidyRepo.save(docRequestSubsidy);
        }

        String code = approve ? "APPROVE" : "REJECT";
        ClsSubsidyRequestStatus clsApproveSubsidyRequestStatus = clsSubsidyRequestStatusRepo.getClsSubsidyRequestStatusByStatus(code);
        ClsSubsidyRequestStatus clsSubsidyRequestStatus = clsSubsidyRequestStatusRepo.findById(docRequestSubsidy.getSubsidyRequestStatus().getId()).orElse(null);

        if (clsApproveSubsidyRequestStatus == null || clsSubsidyRequestStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of( "success", "false", "message", "Не найден запрос"));
        }

        try {
            clsSubsidyRequestStatus.setCode(clsApproveSubsidyRequestStatus.getCode());
            clsSubsidyRequestStatus.setName(clsApproveSubsidyRequestStatus.getName());
            clsSubsidyRequestStatus.setShortName(clsApproveSubsidyRequestStatus.getShortName());
            clsSubsidyRequestStatusRepo.save(clsSubsidyRequestStatus);
            return ResponseEntity.ok().body(Map.of("success", "true", "status", clsApproveSubsidyRequestStatus.getName()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body(Map.of( "success", "false", "message", e.getMessage()));
        }
    }

    @GetMapping("find_verification_request_subsidy_signature_file/{idRequestSubsidyFile}")
    public RegVerificationSignatureFile getVerificationRequestSubsidySignatureFile(
            @PathVariable("idRequestSubsidyFile") Long idRequestSubsidyFile,
            @RequestParam("idUser") Long idUser,
            @RequestParam("idPrincipal") Long idPrincipal,
            HttpSession session) {
        if (idUser != null) {
            RegVerificationSignatureFile regVerificationSignatureFile =
                    regVerificationSignatureFileRepo.findRegVerificationSignatureFileByIdRequestSubsidyFileAndIdUser(idRequestSubsidyFile, idUser).orElse(null);
            return regVerificationSignatureFile;
        } else if (idPrincipal != null) {
            RegVerificationSignatureFile regVerificationSignatureFile =
                    regVerificationSignatureFileRepo.findRegVerificationSignatureFileByIdRequestSubsidyFileAndIdPrincipal(idRequestSubsidyFile, idPrincipal).orElse(null);
            return regVerificationSignatureFile;
        }
        return new RegVerificationSignatureFile();
    }

    @GetMapping("verification_request_subsidy_signature_files/{idRequestSubsidy}")
    public List<RegVerificationSignatureFile> verificationRequestSubsidySignatureFiles(
            @PathVariable("idRequestSubsidy") Long idRequestSubsidy,
            HttpSession session
    ) {
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(idRequestSubsidy).orElse(null);

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        if (docRequestSubsidy == null || clsUser == null) {
            return null;
        }

        List<RegVerificationSignatureFile> regVerificationSignatureFiles = requestSubsidyService.verifyRequestFiles(docRequestSubsidy, clsUser);

        return regVerificationSignatureFiles;
    }

    @GetMapping("check_signature_files_verify_progress")
    public HashMap<String, Object> checkSignatureFilesVerifyProgress(
            @RequestParam("id_request") Long idRequest
    ) {

        HashMap<String, Object> result = requestSubsidyService.checkSignatureFilesVerifyProgress(idRequest, 8095L);

        return result;
    }

    @GetMapping("check_request_subsidy_files_signatures")
    public ResponseEntity<String> checkProgress(
            @RequestParam("id_request") Long idRequest
    ) {

        ResponseEntity<String> responseEntity = requestSubsidyService.checkProgress(idRequest, 8095L);

        return responseEntity;
    }
}
