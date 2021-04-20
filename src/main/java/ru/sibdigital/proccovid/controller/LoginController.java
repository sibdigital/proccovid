package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @ModelAttribute("application_name")
    public String getVersion() {
        return applicationConstants.getApplicationName();
    }

    @GetMapping("/login")
    public String login(Model model) {
        //model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "login";
    }

    @GetMapping("/outer/ologin")
    public String loginOuter(Model model) {
        //model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "outer/ologin";
    }

    @RequestMapping(
            value = {"/recovery","/outer/recovery"},
            method = RequestMethod.GET
    )
//    @GetMapping("/recovery")
    public String recovery(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "recovery";
    }

//    @PostMapping("/recovery")
//    public @ResponseBody
//    String recovery(@RequestBody OrganizationDto organizationDto) {
//        List<ClsOrganization> clsOrganizations = findOrganizationsByInn(organizationDto.getOrganizationInn()).stream()
//                .filter(org -> org.getActivated()).collect(Collectors.toList());
//        if (clsOrganizations != null && clsOrganizations.size() > 0) {
//            ClsOrganization organization = null;
//            for (ClsOrganization clsOrganization: clsOrganizations) {
//                boolean emailLinked = false;
//                try {
//                    emailLinked = isEmailLinked(clsOrganization, organizationDto.getOrganizationEmail());
//                } catch (Exception e) {
//                    log.info(e.getMessage(), e);
//                }
//                if (emailLinked) {
//                    organization = clsOrganization;
//                    break;
//                }
//            }
//            if (organization != null) {
//                String newPassword = requestService.changeOrganizationPassword(organization);
//                boolean emailSent = emailService.sendSimpleMessageNoAsync(organization.getEmail(),
//                        applicationConstants.getApplicationName() + ". Восстановление пароля",
//                        "По ИНН " + organization.getInn() + " произведена смена пароля. " +
//                                "Ваш новый пароль от личного кабинета на портале " + applicationConstants.getApplicationName() + ":" + newPassword);
//                if (!emailSent) {
//                    return "Не удалось отправить письмо";
//                }
//                return "Ок";
//            } else {
//                return "Адрес электронной почты не привязан к учетной записи";
//            }
//        }
//        return "Организация не найдена";
//    }
}