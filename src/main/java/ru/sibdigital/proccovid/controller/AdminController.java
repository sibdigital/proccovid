package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.ClsDepartmentDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.dto.ClsUserDto;
import ru.sibdigital.proccovid.model.ClsPrincipal;
import ru.sibdigital.proccovid.model.ClsTemplate;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.dto.ClsOkvedDto;
import ru.sibdigital.proccovid.repository.ClsDepartmentOkvedRepo;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.service.OkvedServiceImpl;
import ru.sibdigital.proccovid.service.RequestService;

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
    private OkvedServiceImpl okvedServiceImpl;

    @Autowired
    private ClsDepartmentOkvedRepo clsDepartmentOkvedRepo;



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
    public @ResponseBody List<ClsOkvedDto> getOkveds() {
        List<ClsOkvedDto> list = okvedServiceImpl.getOkveds().stream()
                .map( ctr -> new ClsOkvedDto(ctr.getPath(), ctr.getKindCode() + " " + ctr.getKindName()))
                .collect(Collectors.toList());
        return list;
    }

    @GetMapping("/dep_okveds/{id_department}")
    public @ResponseBody List<ClsOkvedDto> getListOkvedsDto(@PathVariable("id_department") Long id_department){
        List<ClsOkvedDto> list = clsDepartmentOkvedRepo.findClsDepartmentOkvedByDepartment_Id(id_department).stream()
                                .map(ctr -> new ClsOkvedDto(ctr.getOkved().getPath(), ctr.getOkved().getKindCode()+ " " + ctr.getOkved().getKindName()))
                                .collect(Collectors.toList());
        return list;
    }
}
