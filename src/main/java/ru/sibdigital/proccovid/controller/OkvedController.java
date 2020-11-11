package ru.sibdigital.proccovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.ClsDepartmentDto;
import ru.sibdigital.proccovid.dto.IdValue;
import ru.sibdigital.proccovid.dto.OkvedDto;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.service.OkvedServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        try {
            if (okvedDto.getId() != null) {
                okvedServiceImpl.changeOkved(okvedDto);
            }
            else {
                okvedServiceImpl.createOkved(okvedDto);
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

    @GetMapping("/upload")
    public String upload() {
        return "upload";
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

    @GetMapping("/new_okved")
    public String newOkved(@RequestParam(name = "okved_name") String kind_name, Model model) {
        model.addAttribute("kind_name", kind_name);
        model.addAttribute("version", "Синтетический ОКВЭД");
        return "new_okved";
    }

    @GetMapping("/okvedform")
    public String okvedForm(@RequestParam(name = "id") String id, Model model) {
        Okved okved = okvedServiceImpl.findOkvedById(UUID.fromString(id));
        model.addAttribute("kind_code", okved.getKindCode());
        model.addAttribute("kind_name", okved.getKindName());
        model.addAttribute("version", okved.getVersion().equals("synt") ? "Синтетический ОКВЭД": okved.getVersion());
        model.addAttribute("status", okved.getStatus());
        model.addAttribute("description", (okved.getDescription() != null) ? okved.getDescription() : "");
        model.addAttribute("okved_id", id);
        return "okvedform";
    }

    @PostMapping("/create_okved")
    public @ResponseBody String addOkved(@RequestParam(name = "kind_name") String kind_name, @RequestParam(name = "kind_code") String kind_code,
                                         @RequestParam(name = "description") String description, @RequestParam(name = "status") Short status) {
        return okvedServiceImpl.createOkved(kind_name, kind_code, description, status);
    }


}
