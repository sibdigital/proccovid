package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.*;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private static boolean publicationPrescriptionInProgress = false;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private RequestService requestService;

    @Autowired
    private OkvedService okvedService;

    @Autowired
    private ClsDepartmentOkvedRepo clsDepartmentOkvedRepo;

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;

    @Autowired
    private ClsMailingListOkvedRepo clsMailingListOkvedRepo;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    @Autowired
    private ClsNewsRepo clsNewsRepo;

    @Autowired
    private RegNewsOkvedRepo regNewsOkvedRepo;

    @Autowired
    private RegNewsOrganizationRepo regNewsOrganizationRepo;

    @Autowired
    private RegNewsStatusRepo regNewsStatusRepo;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/admin")
    public String admin(Model model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        model.addAttribute("id_department", clsUser.getIdDepartment().getId());
        model.addAttribute("department_name", clsUser.getIdDepartment().getName());
        model.addAttribute("user_lastname", clsUser.getLastname());
        model.addAttribute("user_firstname", clsUser.getFirstname());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "admin";
    }

    @GetMapping("/cls_principals")
    public @ResponseBody Map<String, Object> principals(@RequestParam(value = "start", required = false) Integer start,
                                                        @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        Map<String, Object> result = new HashMap<>();
        Page<ClsPrincipal> principals = requestService.getPrincipalsByCriteria(page, size);

        result.put("data", principals.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", principals.getTotalElements());
        return result;
    }

    @GetMapping("/send_email")
    public @ResponseBody String sendEmail(@RequestParam(value = "type") String type) {
        requestService.sendMessageToPrincipals(type);
        return "OK";
    }

    @GetMapping("/send_email_message")
    public @ResponseBody
    String sendEmailMessage(@RequestParam(value = "type") String type) {
        requestService.sendMessageToOrganizations(type);
        return "OK";
    }

    @GetMapping("/send_email_message24")
    public @ResponseBody
    String sendEmailMessage24(@RequestParam(value = "type") String type) {
        requestService.sendMessageToOrganizations24(type);
        return "OK";
    }

    @GetMapping("/send_email_test_message")
    public @ResponseBody
    String sendEmailTestMessage(@RequestParam(value = "type") String type) {
        requestService.sendEmailTestMessage(type);
        return "OK";
    }

    @GetMapping("/cls_templates")
    public @ResponseBody Map<String, Object> getClsTemplates(@RequestParam(value = "start", required = false) Integer start,
                                                             @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        Map<String, Object> result = new HashMap<>();
        Page<ClsTemplate> templates = requestService.findAllClsTemplate(page, size);

        result.put("data", templates.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", templates.getTotalElements());
        return result;
    }

    @PostMapping("/save_cls_type_request")
    public @ResponseBody String saveClsTypeRequest(@RequestBody ClsTypeRequestDto clsTypeRequestDto) {
        try {
            prescriptionService.saveClsTypeRequest(clsTypeRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить предписание";
        }
        return "Предписание сохранено";
    }

    @PostMapping(value = "/upload_prescription_file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadPrescriptionFile(@RequestParam(value = "upload") MultipartFile file,
                                                         @RequestParam Long idTypeRequest,
                                                         @RequestParam Long idTypeRequestPrescription,
                                                         @RequestParam Short num) {
        RegTypeRequestPrescriptionFile regTypeRequestPrescriptionFile = prescriptionService.saveRegTypeRequestPrescriptionFile(file, idTypeRequest, idTypeRequestPrescription, num);
        if (regTypeRequestPrescriptionFile != null) {
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Файл успешно загружен\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + regTypeRequestPrescriptionFile.getOriginalFileName() + "\"}");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"status\": \"server\"," +
                        "\"cause\":\"Ошибка сохранения\"}");
    }

    @PostMapping("/save_cls_department")
    public @ResponseBody String saveClsDepartment(@RequestBody ClsDepartmentDto clsDepartmentDto) {
        try {
            requestService.saveClsDepartment(clsDepartmentDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить подразделение";
        }
        return "Подразделение сохранено";
    }

    @GetMapping("/cls_users")
    public @ResponseBody Map<String, Object> users(@RequestParam(value = "start", required = false) Integer start,
                                                   @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        Map<String, Object> result = new HashMap<>();
        Page<ClsUser> users = requestService.getUsersByCriteria(page, size);

        result.put("data", users.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", users.getTotalElements());
        return result;
    }

    @PostMapping("/save_cls_user")
    public @ResponseBody String saveClsUser(@RequestBody ClsUserDto clsUserDto) {
        try {
            ClsUser clsUser = requestService.findUserByLogin(clsUserDto.getLogin());
            if (clsUser != null && clsUserDto.getId() != clsUser.getId()) {
                return "Пользователь с таким логином уже существует";
            }
            requestService.saveClsUser(clsUserDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить пользователя";
        }
        return "Пользователь сохранен";
    }

    @GetMapping("/okveds")
    public @ResponseBody List<OkvedDto> getOkveds() {
        List<OkvedDto> list = okvedService.getOkveds()
                .stream().map(okved -> new OkvedDto(okved.getId(), okved.getKindCode(), okved.getKindName())).collect(Collectors.toList());
        return list;
    }

    @GetMapping("/department_okveds/{id_department}")
    public @ResponseBody List<ClsDepartmentOkved> getListOkvedsDto(@PathVariable("id_department") Long id_department){
        List<ClsDepartmentOkved> list = clsDepartmentOkvedRepo.findClsDepartmentOkvedByDepartment_Id(id_department);
        return list;
    }

    @GetMapping("/upload")
    public String upload(@RequestParam(value = "version") String version, Model model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getClsUser().getAdmin())
        {
            model.addAttribute("version", version);
            return "upload";
        }
        return "403";
    }

    @GetMapping("/cls_mailing_list")
    public @ResponseBody List<ClsMailingList> getListMailing() {
        return clsMailingListRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/mailing_list_okveds/{id_mailing}")
    public @ResponseBody List<ClsMailingListOkved> getListOkvedsDtoByMailing(@PathVariable("id_mailing") Long id_mailing){
        List<ClsMailingListOkved> list = clsMailingListOkvedRepo.findClsMailingListOkvedByClsMailingList_Id(id_mailing);
        return list;
    }

    @PostMapping("/save_cls_mailing_list")
    public @ResponseBody String saveClsMailingList(@RequestBody ClsMailingListDto clsMailingListDto) {
        try {
            requestService.saveClsMailingList(clsMailingListDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить рассылку";
        }
        return "Рассылка сохранена";
    }

    @GetMapping("/reg_mailing_message")
    public @ResponseBody List<RegMailingMessage> getListMailingMessages() {
        return regMailingMessageRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/reg_mailing_message/{id_message}")
    public @ResponseBody RegMailingMessage getMailingMessages(@PathVariable("id_message") Long id_message) {
        return regMailingMessageRepo.findById(id_message).orElse(null);
    }

    @GetMapping("/mailing_list_short")
    public @ResponseBody List<KeyValue> getMailingMessagesForRichselect() {
        List<KeyValue> list = requestService.getClsMailingList().stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/save_reg_mailing_message")
    public @ResponseBody String saveRegMailingMessage(@RequestBody RegMailingMessageDto regMailingMessageDto) {
        try {
            requestService.saveRegMailingMessage(regMailingMessageDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить сообщение";
        }
        return "Сообщение сохранено";
    }


    @GetMapping("/change_status")
    public @ResponseBody String changeStatusRegMailingMessage(@RequestParam("id") Long id_mailing_message, @RequestParam("status") Long status,
                                                              @RequestParam("sendingTime") String sendingTime) {
        try {
            requestService.setStatusToMailingMessage(id_mailing_message, status, sendingTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось изменить статус у сообщения (id: " + id_mailing_message + ")";
        }
        return "Статус изменен";
    }

    @GetMapping("/upload_fias")
    public String upload() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getClsUser().getAdmin())
        {
            return "upload_fias";
        }
        return "403";
    }

    @GetMapping("/news")
    public @ResponseBody List<ClsNews> getListNews() {
        return clsNewsRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/news/{id_news}")
    public @ResponseBody ClsNews getNews(@PathVariable("id_news") Long id_news) {
        return clsNewsRepo.findById(id_news).orElse(null);
    }

    @GetMapping("/news_okveds/{id_news}")
    public @ResponseBody List<RegNewsOkved> getListOkvedsDtoByNews(@PathVariable("id_news") Long id_news){
        List<RegNewsOkved> list = regNewsOkvedRepo.findClsNewsOkvedByNews_Id(id_news);
        return list;
    }

    @GetMapping("/news_inn/{id_news}")
    public @ResponseBody List<String> getListInnByNews(@PathVariable("id_news") Long id_news){
        List<String> list = regNewsOrganizationRepo.findInnByNews(id_news);
        return list;
    }

    @GetMapping("/news_statuses/{id_news}")
    public @ResponseBody List<CheckedReviewStatusDto> getListStatusesByNews(@PathVariable("id_news") Long id_news){
        List<CheckedReviewStatusDto> initList = CheckedReviewStatusDto.getInitList();

        if (id_news != Long.parseLong("-1")) {
            Long checkedValue = Long.parseLong("1");
            List<RegNewsStatus> list = regNewsStatusRepo.findRegNewsStatusByNews_Id(id_news);
            for (RegNewsStatus regNewsStatus : list) {
                int rowsId = Integer.parseInt("" + regNewsStatus.getStatusReview());
                initList.get(rowsId).setChecked(checkedValue);
            }
        }

        return initList;
    }

    @PostMapping("/save_news")
    public @ResponseBody String saveNews(@RequestBody ClsNewsDto clsNewsDto) {
        try {
            requestService.saveNews(clsNewsDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить новость";
        }
        return "Новость сохранена";
    }

    @GetMapping("/cls_restriction_types")
    public @ResponseBody List<ClsRestrictionType> getListRestrictionTypes() {
        return prescriptionService.getClsRestrictionTypes();
    }

    @PostMapping("/save_cls_restriction_type")
    public @ResponseBody String saveClsRestrictionType(@RequestBody ClsRestrictionTypeDto clsRestrictionTypeDto) {
        try {
            prescriptionService.saveClsRestrictionType(clsRestrictionTypeDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить тип ограничения";
        }
        return "Тип ограничения сохранен";
    }

    @PostMapping("/selected_organizations/count")
    public @ResponseBody Long getCountSelectedOrganizations(@RequestBody ClsTypeRequestDto clsTypeRequestDto) {
        return prescriptionService.getCountOrganizations(clsTypeRequestDto);
    }

    @PostMapping("/selected_organizations")
    public @ResponseBody List<ClsOrganization> getSelectedOrganizations(@RequestBody ClsTypeRequestDto clsTypeRequestDto) {
        return prescriptionService.findOrganizations(clsTypeRequestDto);
    }

    @GetMapping("/cls_organizations")
    public @ResponseBody Map<String, Object> getListOrganizations(@RequestParam(value = "inn", required = false) String inn,
                                           @RequestParam(value = "start", required = false) Integer start,
                                           @RequestParam(value = "count", required = false) Integer count) {

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
        searchCriteria.setInn(inn);

        Page<ClsOrganization> clsOrganizationPage = organizationService.getOrganizationsByCriteria(searchCriteria, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("data", clsOrganizationPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", clsOrganizationPage.getTotalElements());
        return result;
    }

    @GetMapping("publish_prescription")
    public @ResponseBody String publishPrescription(@RequestParam(value = "id") Long id) {
        publicationPrescriptionInProgress = true;
        try {
            prescriptionService.publishPrescription(id);
        } catch (Exception e) {
            publicationPrescriptionInProgress = false;
            log.error(e.getMessage(), e);
        }

        return "";
    }
}
