package ru.sibdigital.proccovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sibdigital.proccovid.model.RequestTypes;
import ru.sibdigital.proccovid.service.StatisticService;

@Log4j2
@Controller
public class StatisticController {

    @Autowired
    StatisticService statisticService;

    @GetMapping(value = "/statistic")
    public String getStatisticPage(Model model){

        model.addAttribute("totalStatistic", statisticService.getTotalStatistic());
        model.addAttribute("departmentStatistic", statisticService.getDepartmentRequestStatistic());

        return "statistic";
    }

    @GetMapping(value = "/dacha/statistic")
    public String getDachaStatisticPage(Model model){

        model.addAttribute("totalStatistic", statisticService.getTotalDachaStatistic());
        model.addAttribute("nearestDaysStatistic", statisticService.getNearestDaysDachaRequestStatistic());

        return "dacha_statistic";
    }

    @GetMapping(value = "/barber/statistic")
    public String getBarberStatisticPage(Model model){
        Integer idTypeRequest = RequestTypes.BARBERSHOP.getValue();
        model.addAttribute("totalStatistic", statisticService.getTotalStatistic(idTypeRequest));
        model.addAttribute("departmentStatistic", statisticService.getDepartmentRequestStatistic(idTypeRequest));

        return "barber_statistic";
    }






}
