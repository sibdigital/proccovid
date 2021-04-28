package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.ClsControlAuthorityRepo;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;
import ru.sibdigital.proccovid.service.reports.InspectionReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(
            value = {"/org_inspections","/organization/org_inspections"},
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

        List<RegOrganizationInspection> inspections = regOrganizationInspectionRepo.findRegOrganizationInspectionsByOrganization(organization).orElse(null);

        return inspections;
    }

    @GetMapping("/control_authorities_list_short")
    public @ResponseBody List<KeyValue> getControlAuthoritiesForRichselect() {
        List<KeyValue> list = clsControlAuthorityRepo.findAllByIsDeleted(false).stream()
                .sorted(Comparator.comparing(ClsControlAuthority::getWeight, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());

        return list;
    }

    @GetMapping("/generate_inspection_report")
    public @ResponseBody String generateInspectionReport(@RequestParam(value = "minDate") String minDateString,
                                                         @RequestParam(value = "maxDate") String maxDateString,
                                                         @RequestParam(value = "minCnt") Integer minCnt,
                                                         @RequestParam(value = "mainOkveds") List<String> mainOkvedPaths,
                                                         @RequestParam(value = "additionalOkveds") List<String> additionalOkvedPaths) throws ParseException {

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

        byte[] bytes = inspectionReportService.exportInspectionReport("html", minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths, defaultMinDate, defaultMaxDate);
        String template = new String(bytes);
        return template;
    }

    @GetMapping("/generate_count_inspection_report")
    public @ResponseBody String generateCountInspectionReport(@RequestParam(value = "minDate") String minDateString,
                                                         @RequestParam(value = "maxDate") String maxDateString,
                                                         @RequestParam(value = "minCnt") Integer minCnt,
                                                         @RequestParam(value = "idOrganization") Long idOrganization,
                                                         @RequestParam(value = "idAuthority") Long idAuthority,
                                                         @RequestParam(value = "typeRecord") String typeRecord) throws ParseException {

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

        byte[] bytes = inspectionReportService.exportInspectionCountReport("html", minDate, maxDate, minCnt,
                        idOrganization, idAuthority, Integer.valueOf(typeRecord), defaultMinDate, defaultMaxDate);
        String template = new String(bytes);
        return template;
    }

    @GetMapping("/generate_inspection_report_details")
    public @ResponseBody String generateInspectionReportDetails(@RequestParam(value = "minDate") String minDateString,
                                                              @RequestParam(value = "maxDate") String maxDateString,
                                                              @RequestParam(value = "idOrganization") Long idOrganization,
                                                              @RequestParam(value = "idAuthority") Long idAuthority) throws ParseException {
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
                idOrganization, idAuthority, defaultMinDate, defaultMaxDate);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(value = "/inspectionReport/{format}/params")
    public String downloadReport(@PathVariable String format,
                                 @RequestParam(value = "minDate") String minDateString,
                                 @RequestParam(value = "maxDate") String maxDateString,
                                 @RequestParam(value = "minCnt") Integer minCnt,
                                 @RequestParam(value = "mainOkveds") List<String> mainOkvedPaths,
                                 @RequestParam(value = "additionalOkveds") List<String> additionalOkvedPaths,
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

        byte[] bytes = inspectionReportService.exportInspectionReport(format, minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths, defaultMinDate, defaultMaxDate);

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

    @RequestMapping(value = "/inspectionCountReport/{format}/params")
    public String downloadReport(@PathVariable String format,
                                 @RequestParam(value = "minDate") String minDateString,
                                 @RequestParam(value = "maxDate") String maxDateString,
                                 @RequestParam(value = "minCnt") Integer minCnt,
                                 @RequestParam(value = "idOrganization") Long idOrganization,
                                 @RequestParam(value = "idAuthority") Long idAuthority,
                                 @RequestParam(value = "typeRecord") Integer typeRecord,
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

        byte[] bytes = inspectionReportService.exportInspectionCountReport(format, minDate, maxDate, minCnt,
                idOrganization, idAuthority, typeRecord, defaultMinDate, defaultMaxDate);
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
}
