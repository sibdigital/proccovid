package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.RegMailingMessageDto;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.ClsPrescription;
import ru.sibdigital.proccovid.model.ClsPrincipal;
import ru.sibdigital.proccovid.model.RegMailingMessage;
import ru.sibdigital.proccovid.repository.RegMailingMessageRepo;
import ru.sibdigital.proccovid.service.EmailServiceImpl;
import ru.sibdigital.proccovid.service.MailingMessageService;
import ru.sibdigital.proccovid.service.StatisticService;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class MailsController {
    @Autowired
    private StatisticService statisticService;

    @Autowired
    private MailingMessageService mailingMessageService;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping(value = "/numberOfMailsSent/all")
    public List<Map<String, Object>> getNumberOfMailsSentStatistic(@RequestParam(value = "dateStart", required = false) String dateStart,
                                                             @RequestParam(value = "dateEnd", required = false) String dateEnd) {
        List<Map<String, Object>> result = statisticService.getNumberOfMailSentForEachMailing(dateStart, dateEnd);
        return result;
    }

    @GetMapping(value = "/numberOfMailsSent/sent/{status}")
    public List<Map<String, Object>> getNumberOfMailsSentStatistic(@PathVariable("status") Integer status,
                                                                             @RequestParam(value = "dateStart", required = false) String dateStart,
                                                                             @RequestParam(value = "dateEnd", required = false) String dateEnd) {
        List<Map<String, Object>> result = statisticService.getNumberOfMailSentForEachMailing(status, dateStart, dateEnd);
        return result;
    }

    @PostMapping("/save_reg_mailing_message")
    public @ResponseBody String saveRegMailingMessage(@RequestBody RegMailingMessageDto regMailingMessageDto) {
        try {
            mailingMessageService.saveRegMailingMessage(regMailingMessageDto);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return "Не указано время отправки сообщения!";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить сообщения!";
        }
        return "Сообщение сохранено";
    }

    @PostMapping("/send_test_message")
    public @ResponseBody Map sendTestMessage(@RequestBody Map<String,String> map) {
        try {
            RegMailingMessage rmm = RegMailingMessage.builder()
                    .message(map.get("message"))
                    .subject("TECT - " + map.get("subject"))
                    .status((short) 1)
                    .build();
            ClsOrganization co = ClsOrganization.builder()
                    .email(map.get("address"))
                    .build();
            ClsPrincipal cp = ClsPrincipal.builder()
                    .organization(co)
                    .build();
            emailService.sendMessage(List.of(cp), rmm, map);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Map.of("message", e.getMessage(), "success", false);
        }
        return Map.of("message", "Сообщение отправлено", "success", true);
    }


    @GetMapping("/change_status")
    public @ResponseBody String changeStatusRegMailingMessage(@RequestParam("id") Long id_mailing_message, @RequestParam("status") Long status,
                                                              @RequestParam("sendingTime") String sendingTime) {
        try {
            mailingMessageService.setStatusToMailingMessage(id_mailing_message, status, sendingTime);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return "Не указано время отправки для сообщения сообщения (id: " + id_mailing_message + ")";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось изменить статус у сообщения (id: " + id_mailing_message + ")";
        }
        return "Статус изменен";
    }

    @GetMapping("/reg_mailing_message")
    public @ResponseBody List<RegMailingMessage> getListMailingMessages() {
        return regMailingMessageRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/reg_mailing_message/{id_message}")
    public @ResponseBody RegMailingMessage getMailingMessages(@PathVariable("id_message") Long id_message) {
        return regMailingMessageRepo.findById(id_message).orElse(null);
    }
}
