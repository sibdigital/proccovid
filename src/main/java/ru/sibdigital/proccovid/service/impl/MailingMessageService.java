package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.RegMailingMessageDto;
import ru.sibdigital.proccovid.model.ClsMailingList;
import ru.sibdigital.proccovid.model.RegMailingMessage;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingMessageRepo;
import ru.sibdigital.proccovid.scheduling.ScheduleTasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class MailingMessageService {

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    @Autowired
    private ScheduleTasks scheduleTasks;

    public RegMailingMessage saveRegMailingMessage(RegMailingMessageDto regMailingMessageDto) throws ParseException {
        ClsMailingList clsMailing = clsMailingListRepo.findById(regMailingMessageDto.getMailingId()).orElse(null);
        Date time = null;
        if (regMailingMessageDto.getSendingTime() != null && regMailingMessageDto.getSendingTime().isBlank() == false) {
            time = new Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(regMailingMessageDto.getSendingTime()).getTime());
        }

        RegMailingMessage regMailingMessage = RegMailingMessage.builder()
                .id(regMailingMessageDto.getId())
                .clsMailingList(clsMailing)
                .message(regMailingMessageDto.getMessage())
                .sendingTime(time)
                .subject(regMailingMessageDto.getSubject())
                .status(regMailingMessageDto.getStatus())
                .build();

        scheduleTasks.removeTaskFromScheduler(regMailingMessageDto.getId());
        if (regMailingMessageDto.getStatus() == 1) {
            scheduleTasks.addTaskToScheduler(regMailingMessageDto.getId(), regMailingMessage, time);
        }

        regMailingMessageRepo.save(regMailingMessage);

        return regMailingMessage;
    }

    public RegMailingMessage setStatusToMailingMessage(Long id, Long status, String sendingTime) throws ParseException {
        RegMailingMessage regMailingMessage = regMailingMessageRepo.findById(id).orElse(null);
        regMailingMessage.setStatus(Short.parseShort("" + status));

        scheduleTasks.removeTaskFromScheduler(id);
        if (status == 1) {
            Date time = new Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(sendingTime).getTime());
            scheduleTasks.addTaskToScheduler(id, regMailingMessage, time);
        }

        regMailingMessageRepo.save(regMailingMessage);

        return regMailingMessage;
    }
}
