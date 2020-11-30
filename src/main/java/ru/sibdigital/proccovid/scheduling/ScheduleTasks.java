package ru.sibdigital.proccovid.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.ClsPrincipalRepo;
import ru.sibdigital.proccovid.repository.RegMailingHistoryRepo;
import ru.sibdigital.proccovid.repository.RegMailingMessageRepo;
import ru.sibdigital.proccovid.service.EmailServiceImpl;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class ScheduleTasks {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private RegMailingHistoryRepo regMailingHistoryRepo;

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public void addTaskToScheduler(Long id, RegMailingMessage regMailingMessage, Date date) {
        if (date.compareTo(new Date()) >= 0) {
            Runnable task = new SendingMailingMessageTask(regMailingMessage);
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, date);
            jobsMap.put(id, scheduledTask);
        }
    }

    public void removeTaskFromScheduler(Long id) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if(scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(id, null);
        }
    }

    @EventListener({ ContextRefreshedEvent.class })
    void contextRefreshedEvent() {
        Date currentTime = new Date();
        jobsMap.clear();
        List<RegMailingMessage> listMailingMessage = regMailingMessageRepo.findAllByStatusAndCurrentTime(Short.parseShort("1"), currentTime);
        for (RegMailingMessage regMailingMessage : listMailingMessage) {
            addTaskToScheduler(regMailingMessage.getId(),
               regMailingMessage, regMailingMessage.getSendingTime());
        }
    }


    class SendingMailingMessageTask implements Runnable {

        private RegMailingMessage message;

        public SendingMailingMessageTask(RegMailingMessage message) {
            this.message = message;
        }

        @Override
        public void run() {
            List<ClsPrincipal> principals = null;
            log.info("Старт рассылки: " + message.getClsMailingList().getName());

            try {
                if (message.getClsMailingList().getStatus() == MailingListStatuses.VALID.value()) {
                    if (message.getClsMailingList().getId() == 1) { // Системная рассылка
                        principals = clsPrincipalRepo.findAll();
                    } else {
                        principals = clsPrincipalRepo.getClsPrincipalsByMessage_Id(message.getId());
                    }

                    emailService.sendMessage(principals, message, new HashMap<>());

                    message.setStatus(MailingMessageStatuses.IS_SENT.value());
                    regMailingMessageRepo.save(message);
                }
                else {
                    RegMailingHistory history = new RegMailingHistory();
                    history.setClsMailingList(message.getClsMailingList());
                    history.setRegMailingMessage(message);
                    history.setTimeSend(new Timestamp(System.currentTimeMillis()));
                    history.setStatus(MailingStatuses.MAILING_LIST_NOT_VALID.value());
                    regMailingHistoryRepo.save(history);
                }

                log.info("Конец рассылки: " + message.getClsMailingList().getName());
            } catch (Exception e) {
                log.info("Рассылка закончилась ошибками:");
                log.error(e.getMessage(), e);
            }
        }
    }

}
