package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import java.util.List;

@Slf4j
@Controller
public class InspectionController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private RegOrganizationInspectionRepo regOrganizationInspectionRepo;

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
}
