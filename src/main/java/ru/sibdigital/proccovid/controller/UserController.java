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
import ru.sibdigital.proccovid.model.RegPersonViolation;
import ru.sibdigital.proccovid.model.RegViolation;
import ru.sibdigital.proccovid.repository.specification.RegPersonViolationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegViolationSearchCriteria;
import ru.sibdigital.proccovid.service.ViolationService;

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

    @GetMapping("/violations")
    public @ResponseBody Map<String, Object> getRegViolations(@RequestParam(value = "inn", required = false) String inn,
                                                             @RequestParam(value = "start", required = false) Integer start,
                                                             @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegViolationSearchCriteria searchCriteria = new RegViolationSearchCriteria();
        searchCriteria.setInn(inn);

        Page<RegViolation> regViolationPage = violationService.getViolationsByCriteria(searchCriteria, page, size);

        List<ViolationDto> violationDtos = regViolationPage.getContent().stream()
                .map(o -> ViolationDto.builder()
                        .id(o.getId())
                        .idTypeViolation(o.getTypeViolation().getId())
                        .nameTypeViolation(o.getTypeViolation().getName())
                        .nameAddedUser(o.getAddedUser().getFullName())
                        .nameUpdatedUser(o.getUpdatedUser().getFullName())
                        .timeCreate(o.getTimeCreate())
                        .timeUpdate(o.getTimeCreate())
                        .nameOrg(o.getNameOrg())
                        .opfOrg(o.getOpfOrg())
                        .innOrg(o.getInnOrg())
                        .ogrnOrg(o.getOgrnOrg())
                        .kppOrg(o.getKppOrg())
                        .dateRegOrg(o.getDateRegOrg())
                        .numberFile(o.getNumberFile())
                        .dateFile(o.getDateFile())
                        .isDeleted(o.getDeleted())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", violationDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regViolationPage.getTotalElements());
        return result;
    }

    @PostMapping("/save_violation")
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

    @GetMapping("/person_violations")
    public @ResponseBody Map<String, Object> getRegPersonViolations(@RequestParam(value = "l", required = false) String lastname,
                                                                    @RequestParam(value = "f", required = false) String firstname,
                                                                    @RequestParam(value = "p", required = false) String patronymic,
                                                                    @RequestParam(value = "start", required = false) Integer start,
                                                                    @RequestParam(value = "count", required = false) Integer count) {
        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        RegPersonViolationSearchCriteria searchCriteria = new RegPersonViolationSearchCriteria();
        searchCriteria.setLastname(lastname);
        searchCriteria.setFirstname(firstname);
        searchCriteria.setPatronymic(patronymic);

        Page<RegPersonViolation> regViolationPage = violationService.getPersonViolationsByCriteria(searchCriteria, page, size);

        List<PersonViolationDto> violationDtos = regViolationPage.getContent().stream()
                .map(o -> new PersonViolationDto(o))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", violationDtos);
        result.put("pos", (long) page * size);
        result.put("total_count", regViolationPage.getTotalElements());
        return result;
    }

    @PostMapping("/save_person_violation")
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

    @GetMapping("/person_violation")
    public @ResponseBody PersonViolationDto getPersonViolation(@RequestParam Long id) {
        RegPersonViolation regPersonViolation = violationService.getRegPersonViolation(id);
        PersonViolationDto dto = new PersonViolationDto(regPersonViolation);
        return dto;
    }
}
