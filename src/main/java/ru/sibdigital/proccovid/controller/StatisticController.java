package ru.sibdigital.proccovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.RequestTypes;
import ru.sibdigital.proccovid.service.impl.RequestService;
import ru.sibdigital.proccovid.service.StatisticService;
import ru.sibdigital.proccovid.service.reports.InspectionReportService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
@Controller
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private RequestService requestService;

    @Autowired
    private InspectionReportService inspectionReportService;

    @GetMapping(value = "/statistic")
    public String getStatisticPage(
            @RequestParam(value = "dateBegin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "dateEnd", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd,
            Model model){

        LocalDateTime localDateBegin =  dateBegin != null ?
                dateBegin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate().atStartOfDay()
                : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime localDateEnd =  dateEnd != null ?
                dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)
                : LocalDateTime.of(2199, 12, 31, 23, 59);

        model.addAttribute("totalStatistic", statisticService.getTotalStatistic(localDateBegin, localDateEnd));
        model.addAttribute("departmentStatistic", statisticService.getDepartmentRequestStatistic(localDateBegin, localDateEnd));
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "statistic";
    }

    @GetMapping(value = "/dacha/statistic")
    public String getDachaStatisticPage(Model model){

        model.addAttribute("totalStatistic", statisticService.getTotalDachaStatistic());
        model.addAttribute("nearestDaysStatistic", statisticService.getNearestDaysDachaRequestStatistic());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "dacha_statistic";
    }

    @GetMapping(value = "/barber/statistic")
    public String getBarberStatisticPage(Model model){
        Integer idTypeRequest = RequestTypes.BARBERSHOP.getValue();
        model.addAttribute("totalStatistic", statisticService.getTotalStatistic(idTypeRequest));
        model.addAttribute("departmentStatistic", statisticService.getDepartmentRequestStatistic(idTypeRequest));
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "barber_statistic";
    }

    @GetMapping(value = "/actualOrganizations/statistic")
    public String getActualOrganizationsStatisticPage(Model model) {
        model.addAttribute("actualOrganizationsStatistic", statisticService.getActualRequestStatisticForEeachOrganization());
        model.addAttribute("actualNumberWorkerOrganizationsStatistic", statisticService.getActualRequestNumberWorkerStatisticForEeachOrganization());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "actual_organizations_statistic";
    }

    @GetMapping(value = "/actualDepartments/statistic")
    public String getActualDepartmentsStatisticPage(Model model) {
        model.addAttribute("actualDepartmentsStatistic", statisticService.getActualRequestStatisticForEeachDepartment());
        model.addAttribute("actualNumberWorkerDepartmentStatistic", statisticService.getActualRequestNumberWorkerStatisticForEeachDepartment());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "actual_departments_statistic";
    }

    @GetMapping(value = "/numberOfSubscribersForEachMailing/statistic")
    public String getNumberOfSubscribersForEachMailingStatisticPage(Model model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        model.addAttribute("user_lastname", clsUser.getLastname());
        model.addAttribute("user_firstname", clsUser.getFirstname());
        model.addAttribute("numberOfSubscribersForEachMailing", statisticService.getNumberOfSubscribersStatisticForEachMailing());
        model.addAttribute("countOfSubscribers", statisticService.getCountOfSubscribers());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "numberOfSubscribersForEachMailing_statistic";
    }

    @GetMapping(value = "/numberOfMailsSent/statistic")
    public String getNumberOfMailsSentStatisticPage(Model model) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        model.addAttribute("user_lastname", clsUser.getLastname());
        model.addAttribute("user_firstname", clsUser.getFirstname());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "numberOfMailsSent_statistic";
    }

}
