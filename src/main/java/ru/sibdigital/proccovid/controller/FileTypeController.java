package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.proccovid.dto.subs.ClsSubsidyWithRequiredFilesDto;
import ru.sibdigital.proccovid.dto.subs.TpRequiredSubsidyFileDto;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.repository.ClsFileTypeRepo;
import ru.sibdigital.proccovid.service.subs.SubsidyService;

import java.util.List;

@Slf4j
@Controller
public class FileTypeController {
    //для работы с ClsFileType
    @Autowired
    SubsidyService subsidyService;

    @Autowired
    ClsFileTypeRepo clsFileTypeRepo;

    @PostMapping("/cls_file_types")
    public @ResponseBody
    List<ClsFileType> getClsFileTypes(@RequestBody ClsSubsidyWithRequiredFilesDto subsidyWithRequiredFilesDto) {
        List<ClsFileType> fileTypes;
        TpRequiredSubsidyFileDto[] requiredSubsidyFileDtos = subsidyWithRequiredFilesDto.getTpRequiredSubsidyFiles();
        Long[] fileTypesId = new Long[requiredSubsidyFileDtos.length - 1];

        for(int i = 0; i < requiredSubsidyFileDtos.length - 1; i++) {
            if (requiredSubsidyFileDtos[i].getClsFileType() != null) {
                fileTypesId[i] = requiredSubsidyFileDtos[i].getClsFileType().getId();
            }
        }

        if(fileTypesId.length != 0) {
            fileTypes = subsidyService.getAllWithoutExists(fileTypesId);
        } else {
            fileTypes = clsFileTypeRepo.findAllByIsDeleted(false);
        }

        return fileTypes;
    }
}
