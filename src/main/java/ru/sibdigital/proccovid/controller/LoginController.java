package ru.sibdigital.proccovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.model.DepUser;
import ru.sibdigital.proccovid.repository.DepUserRepo;
import ru.sibdigital.proccovid.repository.DocRequestRepo;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private DepUserRepo depUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private ApplicationConstants applicationConstants;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(Model model) {

        model.addAttribute("application_name", applicationConstants.getApplicationName());

        return "login";
    }

    @GetMapping("/logout")
    public String login(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }

    @GetMapping("/requests")
    public String requests(Map<String, Object> model, HttpSession session) {
        //model.put();
        DepUser depUser = (DepUser) session.getAttribute("user");
        if(depUser == null){
            return "404";
        }
        else {
            //model.put("user", depUser);
            model.put("id_department", depUser.getIdDepartment().getId());
            model.put("department_name", depUser.getIdDepartment().getName());
            model.put("user_lastname", depUser.getLastname());
            model.put("user_firstname", depUser.getFirstname());
            model.put("link_prefix", applicationConstants.getLinkPrefix());
            model.put("link_suffix", applicationConstants.getLinkSuffix());
            model.put("token", session.getAttribute("token"));
            model.put("application_name", applicationConstants.getApplicationName());
            return "requests";
        }
    }

    @GetMapping("/authenticate")
    public String authenticateGet(Map<String, Object> model){
        model.put("application_name", applicationConstants.getApplicationName());
        return "404";
    }

    @PostMapping("/authenticate")
    //public String login(Model model, String error, String logout) {
    public String authenticate(@ModelAttribute("log_form") DepUser inputDepUser, Map<String, Object> model, HttpSession session) {

        if(inputDepUser == null){
            return "login";
        }

        DepUser depUser = depUserRepo.findByLogin(inputDepUser.getLogin().toLowerCase());

        if ((depUser == null) || (!depUser.getPassword().equals(inputDepUser.getPassword()) ) ){
            //не прошли аутентификацию
            log.debug("LoginController. Аутентификация не пройдена.");

            model.put("message", "Аутентификация не пройдена.");
            return "login";
        }
        log.debug("LoginController. Аутентификация пройдена.");

        session.setAttribute("user", depUser);
        session.setAttribute("token", depUser.hashCode());
        session.setMaxInactiveInterval(120*60);

        if (depUser.getAdmin() != null && depUser.getAdmin()) {
            return "redirect:/admin";
        }

        return "redirect:/requests";
    }
}
