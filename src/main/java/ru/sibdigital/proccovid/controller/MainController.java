package ru.sibdigital.proccovid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.EgripResponse;
import ru.sibdigital.proccovid.dto.EgrulResponse;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.dto.egrip.EGRIP;
import ru.sibdigital.proccovid.dto.egrul.EGRUL;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.ClsMigrationRepo;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.service.EgrulService;
import ru.sibdigital.proccovid.service.PrescriptionService;
import ru.sibdigital.proccovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
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

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EgrulService egrulService;

    @Autowired
    private ClsMigrationRepo clsMigrationRepo;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @GetMapping("/")
    public String index(HttpSession session) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

//        if (clsUser.getAdmin() != null && clsUser.getAdmin()) {
//            return "redirect:/admin";
//        }

        Object lastPage = session.getAttribute("lastPage");
        if(lastPage != null && lastPage.equals("outer")) {
            return "redirect:/403";
        }

        Collection<GrantedAuthority> userAuthorities = currentUser.getAuthorities();
        if (userAuthorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }

        return "redirect:/cabinet";
    }

    @GetMapping("/403")
    public String deniedPage(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "403";
    }

    @GetMapping("/outer")
    public String outerRoot(Model model, HttpSession session) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        session.setAttribute("lastPage", "outer");

        model.addAttribute("id_department", clsUser.getIdDepartment().getId());
        model.addAttribute("department_name", clsUser.getIdDepartment().getName());
        if (clsUser.getDistrict() != null) {
            model.addAttribute("id_district", clsUser.getDistrict().getId());
        }
        model.addAttribute("user_lastname", clsUser.getLastname());
        model.addAttribute("user_firstname", clsUser.getFirstname());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "outer/ouser";
    }

    @GetMapping("/cabinet")
    public String cabinet(Model model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        model.addAttribute("id_department", clsUser.getIdDepartment().getId());
        model.addAttribute("department_name", clsUser.getIdDepartment().getName());
        if (clsUser.getDistrict() != null) {
            model.addAttribute("id_district", clsUser.getDistrict().getId());
        }
        model.addAttribute("user_lastname", clsUser.getLastname());
        model.addAttribute("user_firstname", clsUser.getFirstname());
        model.addAttribute("application_name", applicationConstants.getApplicationName());

        return "user";
    }

    @GetMapping("/violation/view")
    public String violationView(
            Model model,
            @RequestParam(value = "id_violation", required = false) Long idViolation,
            @RequestParam(value = "id_person_violation", required = false) Long idPersonViolation
    ) {
        model.addAttribute("id_violation", idViolation);
        model.addAttribute("id_person_violation", idPersonViolation);
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "violation";
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
    public String viewDocRequest(@RequestParam("id") DocRequest docRequest, Model model, HttpSession session) {
        model.addAttribute("doc_request_id", docRequest.getId());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());

        if (docRequest.getDocRequestPrescriptions() != null && docRequest.getDocRequestPrescriptions().size() > 0) {
            return "request";
        }
        return "view";
    }

    @GetMapping("/organization/view")
    public String viewDocRequest(@RequestParam("id") Long organizationId, Model model, HttpSession session) {
        model.addAttribute("organization_id", organizationId);
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());

        return "organization";
    }

    @GetMapping("/cls_organization/{id_organization}")
    public @ResponseBody ClsOrganization getClsOrganization(@PathVariable("id_organization") Long organizationId){
        ClsOrganization organization = clsOrganizationRepo.findById(organizationId).orElse(null);
        return organization;
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

    @GetMapping("/cls_type_request")
    public @ResponseBody ClsTypeRequest getClsTypeRequest(@RequestParam Long id) {
        return requestService.getClsTypeRequest(id);
    }

    @GetMapping("/migration_data")
    public @ResponseBody  List<ClsMigration> getMigrationData() {
        return clsMigrationRepo.findAll(Sort.by("loadDate"));
    }

    @GetMapping("/cls_prescriptions_short")
    public @ResponseBody List<KeyValue> getClsPrescriptionsShort() {
        List<KeyValue> list = prescriptionService.getClsPrescriptions().stream().filter(cp -> cp.getStatus() == PrescriptionStatuses.PUBLISHED.getValue())
                .map(cp -> new KeyValue(cp.getClass().getSimpleName(), cp.getId(), cp.getName()))
                .collect(Collectors.toList());
        return list;
    }

    @RequestMapping(
            value = {"/get_user_status", "/outer/get_user_status"},
            method = RequestMethod.GET
    )
    public @ResponseBody Map<String, Integer> getUserStatus() {
        CurrentUser currentUser =  (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer status = currentUser.getClsUser().getStatus();
        Map<String, Integer> map = new HashMap<>();
        map.put("status", status);

        return map;
    }
}
