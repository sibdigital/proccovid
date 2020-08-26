package ru.sibdigital.proccovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.ClsTypeRequest;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.DocRequest;
import ru.sibdigital.proccovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class MainController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationConstants applicationConstants;

    @GetMapping("/")
    public String index() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        if (clsUser.getAdmin() != null && clsUser.getAdmin()) {
            return "redirect:/admin";
        }

        return "redirect:/requests";
    }

    @GetMapping("/requests")
    public String requests(Map<String, Object> model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        //model.put("user", clsUser);
        model.put("id_department", clsUser.getIdDepartment().getId());
        model.put("department_name", clsUser.getIdDepartment().getName());
        model.put("user_lastname", clsUser.getLastname());
        model.put("user_firstname", clsUser.getFirstname());
        model.put("link_prefix", applicationConstants.getLinkPrefix());
        model.put("link_suffix", applicationConstants.getLinkSuffix());
        model.put("application_name", applicationConstants.getApplicationName());
        return "requests";
    }

    @GetMapping(value = "/download/{id}")
    public void downloadFile(HttpServletResponse response, @PathVariable("id") DocRequest docRequest) throws Exception {
        requestService.downloadFile(response, docRequest);
    }

    @GetMapping("/request/view")
    public String viewDocRequest(@RequestParam("id") Long id, Model model, HttpSession session) {
        model.addAttribute("doc_request_id", id);
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "view";
    }

    @GetMapping("/cls_type_requests")
    public @ResponseBody List<ClsTypeRequest> getClsTypeRequests() {
        return requestService.getClsTypeRequests();
    }

    @GetMapping("/cls_type_requests_short")
    public @ResponseBody List<KeyValue> getClsTypeRequestsShort() {
        List<KeyValue> list = requestService.getClsTypeRequests().stream()
                .map( ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getShortName()))
                .collect(Collectors.toList());
        return list;
    }
}
