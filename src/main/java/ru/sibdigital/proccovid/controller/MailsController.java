package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.service.StatisticService;

import java.util.List;
import java.util.Map;

@RestController
public class MailsController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping(value = "/numberOfMailsSent/all")
    public List<Map<String, Object>> getNumberOfMailsSentStatistic(@RequestParam(value = "dateStart", required = false) String dateStart,
                                                             @RequestParam(value = "dateEnd", required = false) String dateEnd) {
        List<Map<String, Object>> result = statisticService.getNumberOfMailSentForEachMailing(dateStart, dateEnd);
        return result;
    }

    @GetMapping(value = "/numberOfMailsSent/sent/{status}")
    public List<Map<String, Object>> getNumberOfMailsSentStatistic(@PathVariable("status") Integer status,
                                                                             @RequestParam(value = "dateStart", required = false) String dateStart,
                                                                             @RequestParam(value = "dateEnd", required = false) String dateEnd) {
        List<Map<String, Object>> result = statisticService.getNumberOfMailSentForEachMailing(status, dateStart, dateEnd);
        return result;
    }
}
