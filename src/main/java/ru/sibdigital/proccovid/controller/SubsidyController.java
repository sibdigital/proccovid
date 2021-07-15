package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;
import ru.sibdigital.proccovid.repository.classifier.ClsDepartmentRepo;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySearchCriteria;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRequestStatusRepo;
import ru.sibdigital.proccovid.repository.subs.TpSubsidyOkvedRepo;
import ru.sibdigital.proccovid.service.subs.SubsidyService;

import java.util.List;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class SubsidyController {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus

    @Autowired
    private SubsidyService subsidyService;

    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    TpSubsidyOkvedRepo tpSubsidyOkvedRepo;

    @GetMapping("/list_request_subsidy")
    public Map<String, Object> listRequest(@RequestParam(value = "subsidy_request_status_short_name", required = false) String status,
                                           @RequestParam(value = "subsidy_type_id", required = false) Long subsidyTypeId,
                                           @RequestParam(value = "inn", required = false) String innOrName,
                                           @RequestParam(value = "start", required = false) Integer start,
                                           @RequestParam(value = "count", required = false) Integer count,
                                           @RequestParam(value = "bst", required = false) Timestamp beginSearchTime,
                                           @RequestParam(value = "est", required = false) Timestamp endSearchTime
    ) {

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        DocRequestSubsidySearchCriteria searchCriteria = new DocRequestSubsidySearchCriteria();
        searchCriteria.setIdDepartment(clsUser.getIdDepartment().getId());
        searchCriteria.setSubsidyRequestStatusShortName(status);
        searchCriteria.setSubsidyId(subsidyTypeId);
        searchCriteria.setInnOrName(innOrName);
        searchCriteria.setBeginSearchTime(beginSearchTime);
        searchCriteria.setEndSearchTime(endSearchTime);

        Page<DocRequestSubsidy> docRequestSubsidyPage = subsidyService.getRequestsByCriteria(searchCriteria, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("data", docRequestSubsidyPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", docRequestSubsidyPage.getTotalElements());
        return result;
    }


    @GetMapping("cls_subsidy_request_status_short")
    public @ResponseBody List<KeyValue> getClsSubsidyRequestStatusShort() {
        AtomicLong index = new AtomicLong(1);
        return subsidyService.getClsSubsidyRequestStatusShort().stream()
                .map( csrss -> new KeyValue(csrss.getClass().getSimpleName(), index.getAndIncrement(), csrss))
                .collect(Collectors.toList());
    }

    @GetMapping("cls_subsidy_short")
    public @ResponseBody List<KeyValue> getClsSubsidyShort() {
        AtomicLong index = new AtomicLong(0);
        return subsidyService.getClsSubsidyShort().stream()
                .map(clsSubsidy -> new KeyValue(clsSubsidy.getClass().getSimpleName(), clsSubsidy.getId(), clsSubsidy.getName()))
                .collect(Collectors.toList());
    }


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
