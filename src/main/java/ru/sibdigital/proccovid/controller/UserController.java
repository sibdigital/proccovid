package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.PersonViolationDto;
import ru.sibdigital.proccovid.dto.ViolationDto;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.RegPersonViolation;
import ru.sibdigital.proccovid.model.RegViolation;
import ru.sibdigital.proccovid.repository.specification.RegPersonViolationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegViolationSearchCriteria;
import ru.sibdigital.proccovid.service.ViolationService;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private ViolationService violationService;

    @RequestMapping(
            value = {"/violations","/outer/violations"},
            method = RequestMethod.GET
    )
    public @ResponseBody Map<String, Object> getRegViolations(@RequestParam(value = "inn", required = false) String inn,
                                                              @RequestParam(value = "name", required = false) String nameOrg,
                                                              @RequestParam(value = "nf", required = false) String numberFile,
                                                              @RequestParam(value = "bdr", required = false) Date beginDateRegOrg,
                                                              @RequestParam(value = "edr", required = false) Date endDateRegOrg,
                                                              @RequestParam(value = "d", required = false) Long idDistrict,
                                                              @RequestParam(value = "start", required = false) Integer start,
                                                              @RequestParam(value = "count", required = false) Integer count) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegViolationSearchCriteria searchCriteria = new RegViolationSearchCriteria(inn, nameOrg, numberFile, beginDateRegOrg, endDateRegOrg, idDistrict);

        Page<RegViolation> regViolationPage = violationService.getViolationsByCriteria(searchCriteria, page, size, clsUser.getId());

        List<ViolationDto> violationDtos = regViolationPage.getContent().stream()
                .map(o -> new ViolationDto(o))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", violationDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regViolationPage.getTotalElements());
        return result;
    }

    @RequestMapping(
            value = {"/save_violation","/outer/save_violation"},
            method = RequestMethod.POST
    )
    public @ResponseBody String saveViolation(@RequestBody ViolationDto dto) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setIdAddedUser(currentUser.getClsUser().getId());
            dto.setIdUpdatedUser(currentUser.getClsUser().getId());
            violationService.saveRegViolation(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить нарушение";
        }
        return "Нарушение сохранено";
    }

    @RequestMapping(
            value = {"/violation","/outer/violation"},
            method = RequestMethod.POST
    )
    public @ResponseBody ViolationDto getViolation(@RequestParam Long id) {
        RegViolation regViolation = violationService.getRegViolation(id);
        ViolationDto dto = new ViolationDto(regViolation);
        return dto;
    }

    @RequestMapping(
            value = {"/person_violations","/outer/person_violations"},
            method = RequestMethod.GET
    )
    public @ResponseBody Map<String, Object> getRegPersonViolations(@RequestParam(value = "l", required = false) String lastname,
                                                                    @RequestParam(value = "f", required = false) String firstname,
                                                                    @RequestParam(value = "p", required = false) String patronymic,
                                                                    @RequestParam(value = "pd", required = false) String passportData,
                                                                    @RequestParam(value = "nf", required = false) String numberFile,
                                                                    @RequestParam(value = "d", required = false) Long idDistrict,
                                                                    @RequestParam(value = "start", required = false) Integer start,
                                                                    @RequestParam(value = "count", required = false) Integer count) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegPersonViolationSearchCriteria searchCriteria = new RegPersonViolationSearchCriteria(lastname, firstname, patronymic, passportData, numberFile, idDistrict);

        Page<RegPersonViolation> regViolationPage = violationService.getPersonViolationsByCriteria(searchCriteria, page, size, clsUser.getId());

        List<PersonViolationDto> violationDtos = regViolationPage.getContent().stream()
                .map(o -> new PersonViolationDto(o))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", violationDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regViolationPage.getTotalElements());
        return result;
    }

    @RequestMapping(
            value = {"/save_person_violation","/outer/save_person_violation"},
            method = RequestMethod.POST
    )
    public @ResponseBody String savePersonViolation(@RequestBody PersonViolationDto dto) {
        try {
            CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setIdAddedUser(currentUser.getClsUser().getId());
            dto.setIdUpdatedUser(currentUser.getClsUser().getId());
            violationService.saveRegPersonViolation(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить нарушение";
        }
        return "Нарушение сохранено";
    }

    @RequestMapping(
            value = {"/person_violation","/outer/person_violation"},
            method = RequestMethod.GET
    )
    public @ResponseBody PersonViolationDto getPersonViolation(@RequestParam Long id) {
        RegPersonViolation regPersonViolation = violationService.getRegPersonViolation(id);
        PersonViolationDto dto = new PersonViolationDto(regPersonViolation);
        return dto;
    }
}
