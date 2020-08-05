package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.ClsPrincipal;
import ru.sibdigital.proccovid.model.ClsTemplate;
import ru.sibdigital.proccovid.model.DepUser;
import ru.sibdigital.proccovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class AdminController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/admin")
    public String admin(HttpSession session) {
        DepUser depUser = (DepUser) session.getAttribute("user");
        if (depUser == null) {
            return "404";
        } else {
            if (!depUser.getAdmin()) {
                return "403";
            }
        }
        return "admin";
    }

    @GetMapping("/cls_principals")
    public @ResponseBody Map<String, Object> principals(HttpSession session,
                                                   @RequestParam(value = "start", required = false) Integer start,
                                                   @RequestParam(value = "count", required = false) Integer count) {
        DepUser depUser = (DepUser) session.getAttribute("user");
        if (depUser == null) {
            return null;
        } else {
            if (!depUser.getAdmin()) {
                return null;
            }
        }

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
    public @ResponseBody String sendEmail(HttpSession session,
                                          @RequestParam(value = "type") String type) {
        DepUser depUser = (DepUser) session.getAttribute("user");
        if (depUser == null) {
            return "Не пройдена аутентификация";
        } else {
            if (!depUser.getAdmin()) {
                return "Пользователь не авторизован";
            }
        }
        requestService.sendMessageToPrincipals(type);
        return "OK";
    }

    @GetMapping("/cls_templates")
    public @ResponseBody Map<String, Object> getClsTemplates(HttpSession session,
                                                           @RequestParam(value = "start", required = false) Integer start,
                                                           @RequestParam(value = "count", required = false) Integer count) {
        DepUser depUser = (DepUser) session.getAttribute("user");
        if (depUser == null) {
            return null;
        } else {
            if (!depUser.getAdmin()) {
                return null;
            }
        }

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
        } catch(Exception e){
            log.error(e.getMessage(), e);
            return "Не удалось сохранить тип заявки";
        }
        return "Тип заявки сохранен";
    }
}
