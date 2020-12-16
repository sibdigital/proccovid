package ru.sibdigital.proccovid.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsNewsDto;
import ru.sibdigital.proccovid.model.ClsNews;
import ru.sibdigital.proccovid.model.RegNewsFile;

import java.util.List;
import java.util.Map;

public interface NewsService {
    ClsNews saveNews(ClsNewsDto clsNewsDto);
    RegNewsFile saveRegNewsFile(MultipartFile file, Long idNews);
    boolean deleteRegNewsFile(Long id);
    Map<String, List> getNewsTables(Long id_news);
}
