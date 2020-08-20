package ru.sibdigital.proccovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.ClsTypeRequest;
import ru.sibdigital.proccovid.model.DepUser;
import ru.sibdigital.proccovid.model.DocRequest;
import ru.sibdigital.proccovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class MainController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationConstants applicationConstants;



    @GetMapping(value = "/download/{id}")
    public void downloadFile(HttpServletResponse response, @PathVariable("id") DocRequest docRequest) throws Exception {
        requestService.downloadFile(response, docRequest);
    }

    @GetMapping("/request/view")
    public String viewDocRequest(@RequestParam("id") Long id, Model model, HttpSession session) {
        DepUser depUser = (DepUser) session.getAttribute("user");
        if (depUser == null) {
            return "404";
        }
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

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "view";
    }
}
