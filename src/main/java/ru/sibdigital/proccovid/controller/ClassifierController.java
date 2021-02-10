package ru.sibdigital.proccovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sibdigital.proccovid.dto.EgripResponse;
import ru.sibdigital.proccovid.dto.EgrulResponse;
import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.service.EgrulService;

import java.util.List;

@Log4j2
@RestController
public class ClassifierController {

    @Autowired
    private EgrulService egrulService;

    //@CrossOrigin
    @GetMapping("/egrul")
    public EgrulResponse getEgrul(@RequestParam(name = "inn") String inn) {
        EgrulResponse response = new EgrulResponse();
        RegEgrul egrul = egrulService.getEgrul(inn);
        if (egrul != null) {
            response.build(egrul);
        }else{
            response.empty("По введенному ИНН не найдено юр. лицо");
        }
        return response;
    }

    //@CrossOrigin
    @GetMapping("/egrip")
    public EgripResponse getEgrip(@RequestParam(name = "inn") String inn) {
        EgripResponse response = new EgripResponse();
        List<RegEgrip> egrips = egrulService.getEgrip(inn);
        if (egrips != null && egrips.size() > 0) {
            response.build(egrips);
        }else{
            response.setPossiblySelfEmployed(true);
            response.empty("Если вы являетесь самозанятым, заполните информацию о себе");
        }
        return response;
    }
}
