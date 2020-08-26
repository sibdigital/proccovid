package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sibdigital.proccovid.config.ApplicationConstants;

@Controller
public class LoginController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "login";
    }
}