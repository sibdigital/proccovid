package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.parser.ExcelWriter;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class FileDownloadController {
    @Autowired
    ExcelWriter excelWriter;

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @RequestMapping("download_file/{id}")
    public void downloadFile(HttpServletResponse response, @PathVariable Long id) throws IOException {
        if (id != null) {

            ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);

            List<RegOrganizationInspection> organizationInspections = regOrganizationInspectionRepo.findRegOrganizationInspectionsByOrganization(organization).orElse(null);

            try {
                excelWriter.downloadOrgInspectionFile(organizationInspections, response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
