package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.model.RegOrganizationInspectionFile;
import ru.sibdigital.proccovid.repository.ClsControlAuthorityRepo;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionFileRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;
import ru.sibdigital.proccovid.service.reports.InspectionReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class InspectionController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    private InspectionReportService inspectionReportService;

    @Autowired
    private ClsControlAuthorityRepo clsControlAuthorityRepo;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private RegOrganizationInspectionFileRepo regOrganizationInspectionFileRepo;

    @RequestMapping(
            value = {"/org_inspections","/organization/org_inspections",
                    "/outer/org_inspections","/outer/organization/org_inspections"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<RegOrganizationInspection> getInspections(@RequestParam(value = "id") Long id) {
        if (id == null) {
            return null;
        }

        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization == null) {
            return null;
        }

//        List<RegOrganizationInspection> inspections = regOrganizationInspectionRepo.findRegOrganizationInspectionsByOrganization(organization).orElse(null);
        List<RegOrganizationInspection> inspections = regOrganizationInspectionRepo.findRegOrganizationInspectionsByOrganizationAndControlAuthority_IsDeleted(organization, false).orElse(null);
        if (inspections != null) {
            inspections.sort(Comparator.comparing(RegOrganizationInspection::getDateOfInspection));
        }

        return inspections;
    }

    @RequestMapping(
            value = {"/control_authorities_list_short","/outer/control_authorities_list_short"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<KeyValue> getControlAuthoritiesForRichselect() {
        List<KeyValue> list = clsControlAuthorityRepo.findAllByIsDeleted(false).stream()
                .sorted(Comparator.comparing(ClsControlAuthority::getWeight, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());

        return list;
    }

    @RequestMapping(
            value = {"/generate_inspection_report","/outer/generate_inspection_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateInspectionReport(@RequestParam(value = "minDate") String minDateString,
                                                         @RequestParam(value = "maxDate") String maxDateString,
                                                         @RequestParam(value = "minCnt") Integer minCnt,
                                                         @RequestParam(value = "mainOkveds") List<String> mainOkvedPaths,
                                                         @RequestParam(value = "additionalOkveds") List<String> additionalOkvedPaths,
                                                         @RequestParam(value = "currentUrl") String currentUrl) throws ParseException {

        Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
        Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год
        Date minDate = defaultMinDate;
        Date maxDate = defaultMaxDate;

        minCnt = (minCnt == null ? 0 :minCnt);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!minDateString.equals("")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        String outerPrefix = getOuterPrefix(currentUrl);

        byte[] bytes = inspectionReportService.exportInspectionReport("html", minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths, defaultMinDate, defaultMaxDate, outerPrefix);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/generate_count_inspection_report","/outer/generate_count_inspection_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateCountInspectionReport(@RequestParam(value = "minDate") String minDateString,
                                                         @RequestParam(value = "maxDate") String maxDateString,
                                                         @RequestParam(value = "minCnt") Integer minCnt,
                                                         @RequestParam(value = "idOrganization") Long idOrganization,
                                                         @RequestParam(value = "idAuthority") Long idAuthority,
                                                         @RequestParam(value = "typeRecord") String typeRecord,
                                                         @RequestParam(value = "currentUrl") String currentUrl) throws ParseException {

        Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
        Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год
        Date minDate = defaultMinDate;
        Date maxDate = defaultMaxDate;

        minCnt = (minCnt == null ? 0 :minCnt);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!minDateString.equals("")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        String outerPrefix = getOuterPrefix(currentUrl);

        byte[] bytes = inspectionReportService.exportInspectionCountReport("html", minDate, maxDate, minCnt,
                        idOrganization, idAuthority, Integer.valueOf(typeRecord), defaultMinDate, defaultMaxDate, outerPrefix);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/generate_inspection_report_details","/outer/generate_inspection_report_details"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateInspectionReportDetails(@RequestParam(value = "minDate") String minDateString,
                                                              @RequestParam(value = "maxDate") String maxDateString,
                                                              @RequestParam(value = "idOrganization") Long idOrganization,
                                                              @RequestParam(value = "idAuthority") Long idAuthority,
                                                              @RequestParam(value = "prefix") String prefix) throws ParseException {
        Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
        Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год
        Date minDate = defaultMinDate;
        Date maxDate = defaultMaxDate;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (!minDateString.equals("")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        byte[] bytes = inspectionReportService.exportInspectionReportDetail( minDate, maxDate,
                idOrganization, idAuthority, defaultMinDate, defaultMaxDate, prefix);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/inspectionReport/{format}/params","/outer/inspectionReport/{format}/params"}
    )
    public String downloadReport(@PathVariable String format,
                                 @RequestParam(value = "minDate") String minDateString,
                                 @RequestParam(value = "maxDate") String maxDateString,
                                 @RequestParam(value = "minCnt") Integer minCnt,
                                 @RequestParam(value = "mainOkveds") List<String> mainOkvedPaths,
                                 @RequestParam(value = "additionalOkveds") List<String> additionalOkvedPaths,
                                 @RequestParam(value = "currentUrl") String currentUrl,
                                 HttpServletResponse response) throws IOException, ParseException {

        Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
        Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год
        Date minDate = defaultMinDate;
        Date maxDate = defaultMaxDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!minDateString.equals("") && !minDateString.equals("null")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("") && !minDateString.equals("null")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        minCnt = (minCnt == null ? 0 :minCnt);

        String outerPrefix = getOuterPrefix(currentUrl);

        byte[] bytes = inspectionReportService.exportInspectionReport(format, minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths, defaultMinDate, defaultMaxDate, outerPrefix);

        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=inspection.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }

    @RequestMapping(value = {"/inspectionCountReport/{format}/params", "/outer/inspectionCountReport/{format}/params"})
    public String downloadReport(@PathVariable String format,
                                 @RequestParam(value = "minDate") String minDateString,
                                 @RequestParam(value = "maxDate") String maxDateString,
                                 @RequestParam(value = "minCnt") Integer minCnt,
                                 @RequestParam(value = "idOrganization") Long idOrganization,
                                 @RequestParam(value = "idAuthority") Long idAuthority,
                                 @RequestParam(value = "typeRecord") Integer typeRecord,
                                 @RequestParam(value = "currentUrl") String currentUrl,
                                 HttpServletResponse response) throws IOException, ParseException {

        Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
        Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год
        Date minDate = defaultMinDate;
        Date maxDate = defaultMaxDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!minDateString.equals("") && !minDateString.equals("null")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("") && !minDateString.equals("null")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        minCnt = (minCnt == null ? 0 :minCnt);

        String prefix = getOuterPrefix(currentUrl);

        byte[] bytes = inspectionReportService.exportInspectionCountReport(format, minDate, maxDate, minCnt,
                idOrganization, idAuthority, typeRecord, defaultMinDate, defaultMaxDate, prefix);
        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=inspection.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }

    @RequestMapping(
            value = {"/inspection/view","/outer/inspection/view",
                    "/organization/inspection/view","/outer/organization/inspection/view"},
            method = RequestMethod.GET
    )
    public String viewInspection(@RequestParam("id") Long inspectionId, Model model, HttpSession session) {
        model.addAttribute("inspection_id", inspectionId);
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());

        return "inspection";
    }

    @RequestMapping(
            value = {"/inspection/{inspection_id}","/outer/inspection/{inspection_id}",
                    "/organization/inspection/{inspection_id}","/outer/organization/inspection/{inspection_id}"},
            method = RequestMethod.GET
    )
    public @ResponseBody RegOrganizationInspection getRegOrganizationInspection(@PathVariable("inspection_id") Long inspectionId){
        RegOrganizationInspection inspection = regOrganizationInspectionRepo.findById(inspectionId).orElse(null);
        return inspection;
    }

    @RequestMapping(
            value = {"/inspection_files/{id_inspection}","/outer/inspection_files/{id_inspection}",
                    "/inspection/inspection_files/{id_inspection}","/outer/inspection/inspection_files/{id_inspection}",
                    "/organization/inspection/inspection_files/{id_inspection}","/outer/organization/inspection/inspection_files/{id_inspection}"},
            method = RequestMethod.GET
    )
    public @ResponseBody List<RegOrganizationInspectionFile> getRegOrgInspectionFiles(@PathVariable("id_inspection") Long inspectionId) {
        if (inspectionId != -1) {
            RegOrganizationInspection inspection = regOrganizationInspectionRepo.findById(inspectionId).orElse(null);
            List<RegOrganizationInspectionFile> list = regOrganizationInspectionFileRepo.findRegOrganizationInspectionFilesByOrganizationInspectionAndIsDeleted(inspection, false).orElse(null);
            return list;
        } else
            return null;
    }

    private String getOuterPrefix(String currentUrl) {
        String url = currentUrl + '/';
        String outer = applicationConstants.getOuterUrlPrefix();
        Boolean isOuter = false;
        if (!outer.isBlank() && url.contains("/" + outer)) {
                isOuter = true;
        }

        if (isOuter) {
            return outer;
        } else {
            return "";
        }
    }
}
