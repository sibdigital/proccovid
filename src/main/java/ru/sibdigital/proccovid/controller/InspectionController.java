package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;
import ru.sibdigital.proccovid.service.reports.InspectionReportService;
import ru.sibdigital.proccovid.service.reports.InspectionReportServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Controller
public class InspectionController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    InspectionReportService inspectionReportService;

    @GetMapping("/org_inspections")
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

    @GetMapping("/generate_inspection_report")
    public @ResponseBody String generateInspectionReport(@RequestParam(value = "minDate") String minDateString,
                                                         @RequestParam(value = "maxDate") String maxDateString,
                                                         @RequestParam(value = "minCnt") Integer minCnt) throws ParseException {

        Date minDate = null;
        Date maxDate = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!minDateString.equals("")) {
            minDate = dateFormat.parse(minDateString);
        }
        if (!maxDateString.equals("")) {
            maxDate = dateFormat.parse(maxDateString);
        }

        byte[] bytes = inspectionReportService.exportReport("html", minDate, maxDate, minCnt);
        String template = new String(bytes);
        return template;
    }
}
