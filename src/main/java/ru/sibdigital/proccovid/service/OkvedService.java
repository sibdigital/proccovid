package ru.sibdigital.proccovid.service;


import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.OkvedDto;
import ru.sibdigital.proccovid.model.Okved;

import java.util.List;
import java.util.UUID;

public interface OkvedService {
    String processFile(MultipartFile multipartFile, String version);

    List<Okved> findOkvedsBySearchText(String text);

    Okved findOkvedById(UUID id);

    String createOkved(String kind_name, String kind_code, String description, Short status);

    Okved changeOkved(OkvedDto okvedDto);

    String deleteOkved(UUID id);

}
