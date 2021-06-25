package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
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
import ru.sibdigital.proccovid.repository.classifier.ClsDepartmentOkvedRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListOkvedRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsNewsRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegUserRoleRepo;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegPersonViolationSearchSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegViolationSearchSearchCriteria;
import ru.sibdigital.proccovid.service.*;
import ru.sibdigital.proccovid.utils.DataFormatUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class AdminController {

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
    private ClsNewsRepo clsNewsRepo;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ViolationService violationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ControlAuthorityService controlAuthorityService;

    @Autowired
    private UserRolesEntityRepo userRolesEntityRepo;

    @Autowired
    private RegUserRoleRepo regUserRoleRepo;

    @Autowired
    private MailingListService mailingListService;

    @Value("${spring.mail.from}")
    private String fromAddress;

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
            requestService.saveClsTypeRequest(clsTypeRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить тип заявки";
        }
        return "Тип заявки сохранен";
    }

    @GetMapping("/cls_prescriptions")
    public @ResponseBody List<ClsPrescription> getClsPrescriptions() {
        return prescriptionService.getClsPrescriptions();
    }

    @PostMapping("/save_cls_prescription")
    public @ResponseBody ClsPrescription saveClsTypeRequest(@RequestBody ClsPrescriptionDto clsPrescriptionDto) {
        ClsPrescription clsPrescription;
        try {
            clsPrescription = prescriptionService.savePrescription(clsPrescriptionDto);
        } catch (Exception e) {
            clsPrescription = new ClsPrescription();
            log.error(e.getMessage());
        }
        return clsPrescription;
    }

    @PostMapping(value = "/upload_prescription_file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadPrescriptionFile(@RequestParam(value = "upload") MultipartFile file,
                                                         @RequestParam Long idPrescriptionText) {
        RegPrescriptionTextFile prescriptionTextFile = prescriptionService.savePrescriptionTextFile(file, idPrescriptionText);
        if (prescriptionTextFile != null) {
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Файл успешно загружен\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + prescriptionTextFile.getOriginalFileName() + "\"}");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"status\": \"server\"," +
                        "\"cause\":\"Ошибка сохранения\"}" +
                        "\"sname\": \"" + file.getOriginalFilename() + "\"}");
    }

    @GetMapping("/delete_prescription_file")
    public @ResponseBody String deletePrescriptionFile(@RequestParam Long id) {
        boolean deleted = prescriptionService.deletePrescriptionTextFile(id);
        if (deleted) {
            return "Файл удален";
        }
        return "Не удалось удалить файл";
    }

    @GetMapping("/cls_prescription")
    public @ResponseBody ClsPrescription getClsPrescription(@RequestParam Long id) {
        return prescriptionService.getClsPrescription(id);
    }

    @GetMapping("/publish_prescription")
    public @ResponseBody String publishPrescription(@RequestParam Long id) {
        boolean published = prescriptionService.publishPrescription(id);
        if (published) {
//            prescriptionService.createRequestsByPrescription(id);
        } else {
            return "Не удалось опубликовать предписание";
        }
        return "Предписание опубликовано";
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

    @GetMapping("all_cls_users")
    public @ResponseBody List<KeyValue> allUsers() {
        List<KeyValue> list = requestService.getClsUsers().stream()
                .map(o -> new KeyValue(o.getClass().getSimpleName(), o.getId(), o.getFullName()))
                .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/save_cls_user")
    public @ResponseBody String saveClsUser(@RequestBody ClsUserDto clsUserDto) {
        try {
            ClsUser userByLogin = requestService.findUserByLogin(clsUserDto.getLogin());
            if (userByLogin != null && clsUserDto != null && clsUserDto.getId() != null && userByLogin.getId() != null
                    && ((long)clsUserDto.getId()) != ((long)userByLogin.getId())) {
                return "Пользователь с таким логином уже существует";
            }

            String emailText = null;
            if ((clsUserDto.getNewPassword() != null && !clsUserDto.getNewPassword().isBlank())
                    || (userByLogin == null && clsUserDto.getId() == null)) {
                emailText = "Логин и пароль от личного кабинета на портале " + applicationConstants.getApplicationName() + ":\n"
                        + clsUserDto.getLogin() + "\n"
                        + clsUserDto.getNewPassword();

            } else if (userByLogin == null && clsUserDto.getId() != null) {
                emailText = "Логин от личного кабинета на портале " + applicationConstants.getApplicationName() + ":\n"
                        + clsUserDto.getLogin();
            }

            if (userByLogin != null) {
                clsUserDto.setStatus(userByLogin.getStatus());
            } else {
                clsUserDto.setStatus(UserStatuses.NOT_ACTIVE.getValue());
            }

            ClsUser clsUser = requestService.saveClsUser(clsUserDto);
            // отправим логин и пароль на почту
            if (emailText != null) {
                emailService.sendSimpleMessage(clsUserDto.getEmail(), applicationConstants.getApplicationName(), emailText, fromAddress);
            }
        } catch (MailException e) {
            log.error(e.getMessage(), e);
            return "Не удалось отправить письмо";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить пользователя";
        }
        return "Пользователь сохранен";
    }

    //@PostMapping("/save_user_password")
    @RequestMapping(
            value = {"/save_user_password","/outer/save_user_password"},
            method = RequestMethod.POST
    )
    public @ResponseBody String saveUserPassword(@RequestParam(value = "password", required = true) String newPassword) {
        try {
            CurrentUser currentUser =  (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ClsUser user = currentUser.getClsUser();
            user = requestService.saveUserPassword(user, newPassword);
            user = requestService.setStatus(user, UserStatuses.ACTIVE.getValue());

            // отправим логин и пароль на почту
            String emailText = "Логин и пароль от личного кабинета на портале " + applicationConstants.getApplicationName() + ":\n"
                    + user.getLogin() + "\n"
                    + newPassword;
            emailService.sendSimpleMessage(user.getEmail(), applicationConstants.getApplicationName(), emailText, fromAddress);

        } catch (MailException e) {
            log.error(e.getMessage(), e);
            return "Не удалось отправить письмо с новым паролем";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить пароль";
        }
        return "Пароль сохранен";
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

    @PostMapping(value = "/save_cls_mailing_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity saveClsMailingList(@RequestBody ClsMailingListDto clsMailingListDto) {
        String message = "Рассылка сохранена";
        Boolean success = false;
        try {
            mailingListService.saveClsMailingList(clsMailingListDto);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message =  "Не удалось сохранить рассылку";
        }
        return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                Map.of("message", message,"success", success));
    }

    @GetMapping("/mailing_list_short")
    public @ResponseBody List<KeyValue> getMailingMessagesForRichselect() {
        List<KeyValue> list = mailingListService.getClsMailingList().stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());
        return list;
    }

    @GetMapping("/news")
    public @ResponseBody List<ClsNews> getListNews() {
        return clsNewsRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/news/{id_news}")
    public @ResponseBody ClsNews getNews(@PathVariable("id_news") Long id_news) {
        return clsNewsRepo.findById(id_news).orElse(null);
    }

    @GetMapping("/init_news_statuses")
    public @ResponseBody List<CheckedReviewStatusDto> getInitListStatuses() {
        return CheckedReviewStatusDto.getInitList();
    }

    @GetMapping("/news_tables/{id_news}")
    public @ResponseBody Map<String, List> getNewsTables(@PathVariable("id_news") Long id_news) {
       return newsService.getNewsTables(id_news);
    }

    @PostMapping(value = "/upload_news_file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadNewsFile(@RequestParam(value = "upload") MultipartFile file,
                                                         @RequestParam Long idNews) {
        RegNewsFile regNewsFile = newsService.saveRegNewsFile(file, idNews);
        if (regNewsFile != null) {
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Файл успешно загружен\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + regNewsFile.getOriginalFileName() + "\"}");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"status\": \"server\"," +
                        "\"cause\":\"Ошибка сохранения\"}" +
                        "\"sname\": \"" + file.getOriginalFilename() + "\"}");
    }

    @GetMapping("/delete_news_file")
    public @ResponseBody String deleteNewsFile(@RequestParam(value = "id") Long id) {
        boolean deleted = newsService.deleteRegNewsFile(id);
        if (deleted) {
            return "Файл удален";
        }
        return "Не удалось удалить файл";
    }

    @PostMapping("/save_news")
    public @ResponseBody ClsNews saveNews(@RequestBody ClsNewsDto clsNewsDto) {
        ClsNews news = newsService.saveNews(clsNewsDto);
        return news;
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
                                           @RequestParam(value = "id_prescription", required = false) Long idPrescription,
                                           @RequestParam(value = "start", required = false) Integer start,
                                           @RequestParam(value = "count", required = false) Integer count) {

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
        searchCriteria.setInn(inn);
        searchCriteria.setIdPrescription(idPrescription);

        Page<ClsOrganization> clsOrganizationPage = organizationService.getOrganizationsByCriteria(searchCriteria, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("data", clsOrganizationPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", clsOrganizationPage.getTotalElements());
        return result;
    }

    @GetMapping("/subdomainWork")
    public @ResponseBody String getSubdomainWork(){
        return applicationConstants.getSubdomainWork();
    }

    @GetMapping("/dep_contacts/{id}")
    public @ResponseBody List<ClsDepartmentContact> getDepContacts(@PathVariable("id") Long departmentId){
        List<ClsDepartmentContact> departmentContacts = requestService.getAllClsDepartmentContactByDepartmentId(departmentId);
        return departmentContacts;
    }

    @GetMapping("/mark_organizations")
    public @ResponseBody String markOrganizationAsDeleted() {
        organizationService.markOrganizationAsDeleted();
        return "Логическое удаление организаций выполнено";
    }

    @GetMapping("/actualize_organizations")
    public @ResponseBody String actualizeOrganizations() {
        organizationService.actualizeOrganizations();
        return "Актуализация организаций запущена. Смотрите лог-файл actualization-organizations.log";
    }

    @GetMapping("/actualize_files")
    public @ResponseBody String actualizeFiles() {
        organizationService.actualizeFiles();
        return "Актуализация файлов заявок запущена. Смотрите лог-файл actualization-files.log";
    }

    @GetMapping("/create_prescriptions")
    public @ResponseBody String createPrescriptions() {
        organizationService.createPrescriptions();
        return "Создание предписаний выполнено";
    }

    @RequestMapping(
            value = {"/type_violations","/outer/type_violations", "/violation/type_violations"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<ClsTypeViolationDto> getClsTypeViolations() {
        return violationService.getClsTypeViolations().stream()
                .map(o -> new ClsTypeViolationDto(o.getId(), o.getName(), o.getDescription())).collect(Collectors.toList());
    }

    @PostMapping("/save_type_violation")
    public @ResponseBody String saveClsTypeViolation(@RequestBody ClsTypeViolationDto dto) {
        try {
            violationService.saveClsTypeViolation(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить вид нарушения";
        }
        return "Вид нарушения сохранен";
    }

    @GetMapping("/violation_search_queries")
    public @ResponseBody Map<String, Object> getViolationSearchQueries(@RequestParam(value = "bst", required = false) Timestamp beginSearchTime,
                                                                       @RequestParam(value = "est", required = false) Timestamp endSearchTime,
                                                                       @RequestParam(value = "u", required = false) Long idUser,
                                                                       @RequestParam(value = "start", required = false) Integer start,
                                                                       @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegViolationSearchSearchCriteria searchCriteria = new RegViolationSearchSearchCriteria(beginSearchTime, endSearchTime, idUser);

        Page<RegViolationSearch> regViolationSearchPage = violationService.getViolationSearchQueriesByCriteria(searchCriteria, page, size);

        List<ViolationSearchDto> violationSearchDtos = regViolationSearchPage.getContent().stream()
                .map(o -> new ViolationSearchDto(o))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", violationSearchDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regViolationSearchPage.getTotalElements());
        return result;
    }

    @GetMapping("/violation_search_query")
    public @ResponseBody ViolationSearchDto getViolation(@RequestParam Long id) {
        RegViolationSearch regViolationSearch = violationService.getRegViolationSearch(id);
        ViolationSearchDto dto = new ViolationSearchDto(regViolationSearch);
        return dto;
    }

    @GetMapping("/person_violation_search_queries")
    public @ResponseBody Map<String, Object> getPersonViolationSearchQueries(@RequestParam(value = "bst", required = false) Timestamp beginSearchTime,
                                                                             @RequestParam(value = "est", required = false) Timestamp endSearchTime,
                                                                             @RequestParam(value = "u", required = false) Long idUser,
                                                                             @RequestParam(value = "start", required = false) Integer start,
                                                                             @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegPersonViolationSearchSearchCriteria searchCriteria = new RegPersonViolationSearchSearchCriteria(beginSearchTime, endSearchTime, idUser);

        Page<RegPersonViolationSearch> regPersonViolationSearchPage = violationService.getPersonViolationSearchQueriesByCriteria(searchCriteria, page, size);

        List<PersonViolationSearchDto> personViolationSearchDtos = regPersonViolationSearchPage.getContent().stream()
                .map(o -> new PersonViolationSearchDto(o))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", personViolationSearchDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regPersonViolationSearchPage.getTotalElements());
        return result;
    }

    @GetMapping("/person_violation_search_query")
    public @ResponseBody PersonViolationSearchDto getPersonViolation(@RequestParam Long id) {
        RegPersonViolationSearch regPersonViolationSearch = violationService.getRegPersonViolationSearch(id);
        PersonViolationSearchDto dto = new PersonViolationSearchDto(regPersonViolationSearch);
        return dto;
    }

    @GetMapping("/control_authorities")
    public @ResponseBody Map<String, Object> getControlAuthoritiesList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "shortName", required = false) String shortName,
            @RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "count", required = false) Integer count
    ) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        Page<ClsControlAuthority> clsControlAuthoritiesPage = controlAuthorityService.getControlAuthorities(page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("data", clsControlAuthoritiesPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", clsControlAuthoritiesPage.getTotalElements());
        return result;
    }

    @GetMapping("/delete_control_authority")
    public @ResponseBody Boolean deleteControlAuthority(@RequestParam(value = "id") Long id) {
        boolean deleted = controlAuthorityService.deleteControlAuthority(id);

        return deleted;
    }

    @PostMapping("/save_control_authority")
    public @ResponseBody Boolean saveClsTypeViolation(@RequestBody ClsControlAuthorityDto dto) {
        try {
            controlAuthorityService.saveControlAuthority(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @GetMapping("/cls_control_authority_parents")
    public @ResponseBody List<KeyValue> getClsPrescriptionsShort() {
        List<KeyValue> list = controlAuthorityService.getControlAuthorityParentsList().stream()
                .map(cap -> new KeyValue(cap.getClass().getSimpleName(), cap.getId(), cap.getName()))
                .collect(Collectors.toList());
        return list;
    }


    @GetMapping("/user_roles/{id_dep_user}")
    public @ResponseBody List<UserRolesEntity> getRolesByUserId(@PathVariable("id_dep_user") Long idDepUser){
        List<UserRolesEntity> list = userRolesEntityRepo.getRolesByUserId(idDepUser);
        list.sort(Comparator.comparing(UserRolesEntity::getName));
        return list;
    }

    @RequestMapping(
            value = {"/current_roles", "/outer/current_roles"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<String> getCurrentRoleCodes(){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        List<RegUserRole> rurs = regUserRoleRepo.findAllByUser(clsUser);
        List<String> roleCodes = rurs.stream()
                                .map(ctr -> ctr.getRole().getCode())
                                .collect(Collectors.toList());
        return roleCodes;
    }

    @RequestMapping(
            value = {"/user_roles", "/outer/user_roles"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<UserRolesEntity> getCurrentUserRoles(){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        List<UserRolesEntity> list = userRolesEntityRepo.getRolesByUserId(clsUser.getId());
        list.sort(Comparator.comparing(UserRolesEntity::getName));
        return list;
    }

    @GetMapping("/organization_types")
    public @ResponseBody List<KeyValue> getOrganizationTypesForRichselect() {
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.JURIDICAL.getValue()), "Юр. лицо"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.PHYSICAL.getValue()), "Физ. лицо"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.SELF_EMPLOYED.getValue()), "Самозанятый"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.FILIATION.getValue()), "Филиал"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.REPRESENTATION.getValue()), "Представительство"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.DETACHED.getValue()), "Обособленное подразделение"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.IP.getValue()), "ИП"));
        list.add(new KeyValue("OrganizationTypes", Long.valueOf(""+OrganizationTypes.KFH.getValue()), "КФХ"));

        return list;
    }

    @PostMapping("/save_organization")
    public @ResponseBody ResponseEntity<String> saveOrganization(@RequestBody ClsOrganizationDto clsOrganizationDto) {
        try {
            organizationService.saveOrganization(clsOrganizationDto);
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Сохранено\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + clsOrganizationDto.getName() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\": \"server\"," +
                            "\"cause\":\"Не удалось сохранить\"}" +
                            "\"sname\": \"" + clsOrganizationDto.getName() + "\"}");
        }
    }

}
