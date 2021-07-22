package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.ClsFileTypeDto;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.repository.ClsFileTypeRepo;
import ru.sibdigital.proccovid.utils.DataFormatUtils;

import java.sql.Timestamp;
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
    public ResponseEntity<String> saveFileType(@RequestBody ClsFileTypeDto fileTypeDto) {
        try {
            ClsFileType fileType = ClsFileType.builder()
                    .id(fileTypeDto.getId())
                    .name(fileTypeDto.getName())
                    .shortName(fileTypeDto.getShortName())
                    .isDeleted(false)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
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

    @PostMapping("/del_file_type")
    public ResponseEntity<String> delFileType(@RequestBody ClsFileTypeDto fileTypeDto) {
        try {
            ClsFileType fileType = ClsFileType.builder()
                    .id(fileTypeDto.getId())
                    .name(fileTypeDto.getName())
                    .shortName(fileTypeDto.getShortName())
                    .isDeleted(true)
                    .timeCreate(fileTypeDto.getTimeCreate())
                    .code(fileTypeDto.getCode())
                    .build();
            fileTypeRepo.save(fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return DataFormatUtils.buildInternalServerErrorResponse(Map.of(
                    "status","error","message","Не удалось удалить файл"));
        }
        return DataFormatUtils.buildOkResponse(Map.of(
                "status","success", "response",fileTypeDto,"message","Файл успешно удален"));
    }
}
