package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.dto.ClsUserDto;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.repository.ClsUserRepo;
import ru.sibdigital.proccovid.service.EmailService;
import ru.sibdigital.proccovid.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Value("${spring.mail.from}")
    private String fromAddress;

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
    @GetMapping("/recovery")
    public String recovery(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
//        model.addAttribute("logo_path", "<img src = \"logo.png\">");
        return "recovery";
    }

    @RequestMapping(
            value = {"/recovery","/outer/recovery"},
            method = RequestMethod.POST
    )
    public @ResponseBody
    String recovery(@RequestBody ClsUserDto clsUserDto) {
        ClsUser clsUser = userService.findUserByLogin(clsUserDto.getLogin());
        if (clsUser != null) {
            String email = clsUser.getEmail();
            if (email != null && !email.isEmpty()) {
                String newPassword = userService.changeUserPassword(clsUser);
                try {
                    emailService.sendSimpleMessage(clsUser.getEmail(),
                            applicationConstants.getApplicationName() + ". Восстановление пароля",
                            "По логину " + clsUser.getLogin() + " произведена смена пароля. "+
                                    "Ваш новый пароль от личного кабинета на портале " + applicationConstants.getApplicationName() + ":" + newPassword, fromAddress);
                    return "Ок";
                } catch (Exception e) {
                    return "Не удалось отправить письмо";
                }
            } else {
                return "Адрес электронной почты не привязан к учетной записи";
            }
        } else {
            return "Пользователь с таким логином не найден";
        }
    }
}