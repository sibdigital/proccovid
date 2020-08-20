package ru.sibdigital.proccovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.repository.ClsUserRepo;
import ru.sibdigital.proccovid.repository.DocRequestRepo;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private ClsUserRepo clsUserRepo;

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
        ClsUser clsUser = (ClsUser) session.getAttribute("user");
        if(clsUser == null){
            return "404";
        }
        else {
            //model.put("user", clsUser);
            model.put("id_department", clsUser.getIdDepartment().getId());
            model.put("department_name", clsUser.getIdDepartment().getName());
            model.put("user_lastname", clsUser.getLastname());
            model.put("user_firstname", clsUser.getFirstname());
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
    public String authenticate(@ModelAttribute("log_form") ClsUser inputClsUser, Map<String, Object> model, HttpSession session) {

        if(inputClsUser == null){
            return "login";
        }

        ClsUser clsUser = clsUserRepo.findByLogin(inputClsUser.getLogin().toLowerCase());

        if ((clsUser == null) || (!clsUser.getPassword().equals(inputClsUser.getPassword()) ) ){
            //не прошли аутентификацию
            log.debug("LoginController. Аутентификация не пройдена.");

            model.put("message", "Аутентификация не пройдена.");
            return "login";
        }
        log.debug("LoginController. Аутентификация пройдена.");

        session.setAttribute("user", clsUser);
        session.setAttribute("token", clsUser.hashCode());
        session.setMaxInactiveInterval(120*60);

        if (clsUser.getAdmin() != null && clsUser.getAdmin()) {
            return "redirect:/admin";
        }

        return "redirect:/requests";
    }
}
