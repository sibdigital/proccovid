package ru.sibdigital.proccovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ru.sibdigital.proccovid.dto.OkvedDto;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.service.OkvedServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OkvedController {
    @Autowired
    private OkvedServiceImpl okvedServiceImpl;

    @Autowired
    private OkvedRepo okvedRepo;

    private static final Logger log = LoggerFactory.getLogger(OkvedController.class);

    @GetMapping(value = "/list_okved/{version}")
    public Map<String, Object> listRequest(@PathVariable("version") String version,
                                           @RequestParam(value = "searchText", required = false) String searchText,
                                           @RequestParam(value = "start", required = false) Integer start,
                                           @RequestParam(value = "count", required = false) Integer count) {

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;
        searchText = searchText == null ? "" : searchText;

        Page<Okved> okvedPage = okvedRepo.findAllBySearchTextAndVersion(searchText, version, PageRequest.of(page, size));

        Map<String, Object> result = new HashMap<>();
        result.put("data", okvedPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", okvedPage.getTotalElements());
        return result;
    }

    @PostMapping("/save_okved")
    public @ResponseBody String changeOkved(@RequestBody OkvedDto okvedDto) {
        String path = okvedDto.getVersion() + '.' + okvedDto.getKindCode().trim();
        try {
            if (okvedDto.getId() != null) {
                okvedServiceImpl.changeOkved(okvedDto);
            }
            else {
                if (okvedServiceImpl.findOkvedByPathCode(path) == null) {
                    okvedServiceImpl.createOkved(okvedDto);
                }
                else {
                    return "ОКВЭД с таким кодом уже существует!";
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить ОКВЭД";
        }
        return "ОКВЭД сохранен";

    }

    @PostMapping("/delete_okved")
    public @ResponseBody String deleteOkved(@RequestBody OkvedDto okvedDto) {
        try {
            okvedServiceImpl.deleteOkved(okvedDto.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось удалить ОКВЭД";
        }
        return "ОКВЭД удален";
    }

    @PostMapping("/process_file")
    public @ResponseBody
    String processFile(@RequestParam(name = "file") MultipartFile multipartFile, @RequestParam(name = "version") String version) {
        return okvedServiceImpl.processFile(multipartFile, version);
    }

    @GetMapping("/search")
    public @ResponseBody
    List<Okved> search(@RequestParam(name = "text") String text) {
        List<Okved> result = okvedServiceImpl.findOkvedsBySearchText(text);
        return result;
    }

}
