package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.dto.PersonViolationDto;
import ru.sibdigital.proccovid.dto.ViolationDto;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.RegPersonViolation;
import ru.sibdigital.proccovid.model.RegViolation;
import ru.sibdigital.proccovid.repository.classifier.ClsUserRepo;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClsUserRepo clsUserRepo;

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
            method = RequestMethod.GET
    )
    public @ResponseBody ViolationDto getViolation(@RequestParam Long id) {
        RegViolation regViolation = violationService.getRegViolation(id);
        ViolationDto dto = new ViolationDto(regViolation);
        return dto;
    }

    @RequestMapping(
            value = {"/view_violation","/outer/view_violation"},
            method = RequestMethod.POST
    )
    public @ResponseBody void onViewViolation(@RequestBody ViolationDto violationDto) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        violationService.viewViolation(clsUser.getId(), violationDto);
    }


    @RequestMapping(
            value = {"/person_violations","/outer/person_violations"},
            method = RequestMethod.GET
    )
    public @ResponseBody Map<String, Object> getRegPersonViolations(@RequestParam(value = "fio", required = false) String fio,
                                                                    @RequestParam(value = "pd", required = false) String passportData,
                                                                    @RequestParam(value = "nf", required = false) String numberFile,
                                                                    @RequestParam(value = "d", required = false) Long idDistrict,
                                                                    @RequestParam(value = "start", required = false) Integer start,
                                                                    @RequestParam(value = "count", required = false) Integer count) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegPersonViolationSearchCriteria searchCriteria = new RegPersonViolationSearchCriteria(fio, passportData, numberFile, idDistrict);

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

    @RequestMapping(
            value = {"/view_person_violation","/outer/view_person_violation"},
            method = RequestMethod.POST
    )
    public @ResponseBody void onViewPersonViolation(@RequestBody PersonViolationDto personViolationDto) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        violationService.viewPersonViolation(clsUser.getId(), personViolationDto);
    }

    @RequestMapping(
            value = {"/profile", "/outer/profile"},
            method = RequestMethod.GET
    )
    public @ResponseBody ClsUser getProfile() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getClsUser();
    }

    @RequestMapping(
            value = {"/check_current_pass","/outer/check_current_pass"},
            method = RequestMethod.POST
    )
    public @ResponseBody String checkCurrentPass(@RequestBody String incomingPass){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentPass = currentUser.getClsUser().getPassword();
        if(passwordEncoder.matches(incomingPass,currentPass)){
            return "Пароли совпадают";
        }else{
            return "Пароли не совпадают";
        }
    }

    @RequestMapping(
            value = {"/edit_user_pass","/outer/edit_user_pass"},
            method = RequestMethod.POST
    )
    public @ResponseBody ResponseEntity<Object>  editUserPass(@RequestParam(value = "new_pass", required = true) String newPass){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();
        clsUser.setPassword(passwordEncoder.encode(newPass));
        clsUserRepo.save(clsUser);

        ResponseEntity<Object>  responseEntity = ResponseEntity.ok()
                .body("{\"cause\": \"Пароль успешно обновлен\"," +
                        "\"status\": \"server\"}");
        return responseEntity;
    }
}
