package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.sibdigital.proccovid.config.ApplicationConstants;

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
}