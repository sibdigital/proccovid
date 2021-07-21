package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.proccovid.dto.ClsFileTypeDto;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.repository.ClsFileTypeRepo;
import ru.sibdigital.proccovid.utils.DataFormatUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class FileTypeController {
    //для работы с ClsFileType
    @Autowired
    ClsFileTypeRepo fileTypeRepo;

    @GetMapping("/file_types")
    public @ResponseBody List<ClsFileType> getFileTypes() {
        return fileTypeRepo.getClsFileTypesByIsDeleted(false);
    }

    @PostMapping("/save_file_type")
    public ResponseEntity<String> saveFileType(@RequestParam("fileType") ClsFileTypeDto fileTypeDto) {
        try {
            ClsFileType fileType = ClsFileType.builder()
                    .name(fileTypeDto.getName())
                    .shortName(fileTypeDto.getShortName())
                    .isDeleted(false)
                    .code(fileTypeDto.getCode())
                    .build();
            fileTypeRepo.save(fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return DataFormatUtils.buildInternalServerErrorResponse(Map.of(
                    "status","error","message","Не удалось добавить файл"));
        }
        return DataFormatUtils.buildOkResponse(Map.of(
                "status","success", "response",fileTypeDto,"message","Файл успешно добавлен"));
    }
}
