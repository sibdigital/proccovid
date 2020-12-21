package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.scheduling.ScheduleTasks;
import ru.sibdigital.proccovid.service.ImportEgrulEgripService;
import ru.sibdigital.proccovid.service.ImportFiasService;

import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import java.io.File;
import java.io.IOException;

@Controller
public class ImportController {

    @Autowired
    private ImportFiasService importFiasService;

    @Autowired
    private ImportEgrulEgripService imortEgrulEgripService;

    @Autowired
    private ScheduleTasks scheduleTasks;


    @GetMapping("/process_egrip_files")
    @ResponseBody String processFilesEgrip() {
        try {
            scheduleTasks.startImportEgrulEgrip(false, true);
//            imortEgrulEgripService.importData();
        } catch (Exception e) {
            return "Не удалось запустить загрузку";
        }
        return "Ok";
    }

    @GetMapping("/process_egrul_files")
    @ResponseBody String processFilesEgrul() {
        try {
            scheduleTasks.startImportEgrulEgrip(true, false);
//            imortEgrulEgripService.importData();
        } catch (Exception e) {
            return "Не удалось запустить загрузку";
        }
        return "Ok";
    }


    @GetMapping("/process_fias_zip_full")
    @ResponseBody String processZipFullFias() {
        try {
            scheduleTasks.startImportZipFullFias();
        } catch (Exception e) {
            return "Не удалось запустить загрузку";
        }
        return "Ok";
    }

    @GetMapping("/process_fias_zip_update")
    @ResponseBody String processZipUpdatesFias() {
        try {
            scheduleTasks.startImportZipUpdatesFias();
        } catch (Exception e) {
            return "Не удалось запустить загрузку";
        }
        return "Ok";
    }
}
