package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;
import ru.sibdigital.proccovid.repository.classifier.ClsDepartmentRepo;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.proccovid.repository.subs.TpSubsidyOkvedRepo;
import ru.sibdigital.proccovid.service.subs.SubsidyService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class SubsidyController {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus
    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    SubsidyService subsidyService;

    @Autowired
    TpSubsidyOkvedRepo tpSubsidyOkvedRepo;

    @GetMapping("/subsidies")
    public @ResponseBody
    List<ClsSubsidy> getSubsidies() {
        return clsSubsidyRepo.findAllByIsDeleted(false);
    }

    @GetMapping("/subsidy/{id_subsidy}")
    public @ResponseBody ClsSubsidy getNews(@PathVariable("id_subsidy") Long id_subsidy) {
        return clsSubsidyRepo.findById(id_subsidy).orElse(null);
    }


    @GetMapping("/subsidy_okveds/{id_subsidy}")
    public @ResponseBody List<Okved> getSubsidyOkveds(@PathVariable("id_subsidy") Long id_subsidy){
        ClsSubsidy subsidy = clsSubsidyRepo.findById(id_subsidy).orElse(null);
        List<TpSubsidyOkved> tpSubsidyOkveds = tpSubsidyOkvedRepo.findAllBySubsidyAndIsDeleted(subsidy, false);
        List<Okved> okvedList = tpSubsidyOkveds.stream()
                                .map(ctr -> ctr.getOkved())
                                .collect(Collectors.toList());
        return okvedList;
    }

    @GetMapping("/cls_departments_short")
    public @ResponseBody List<KeyValue> getListDepartments() {
        List<ClsDepartment> departments = clsDepartmentRepo.findAllByIsReviewer(true, Sort.by("name"));
        List<KeyValue> list = departments.stream()
                                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                                .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/save_subsidy")
    public @ResponseBody Boolean saveClsSubsidy(@RequestBody ClsSubsidyDto dto) {
        try {
            subsidyService.saveSubsidy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
