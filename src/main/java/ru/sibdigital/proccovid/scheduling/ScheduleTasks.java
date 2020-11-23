package ru.sibdigital.proccovid.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.model.RegMailingMessage;
import ru.sibdigital.proccovid.repository.ClsPrincipalRepo;
import ru.sibdigital.proccovid.repository.RegMailingMessageRepo;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ScheduleTasks {
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public void addTaskToScheduler(Long id, RegMailingMessage regMailingMessage, Date date) {
        Runnable task = new SendingMailingMessageTask(regMailingMessage);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, date);
        jobsMap.put(id, scheduledTask);
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
        // Проверка на время: sendingTime > currentTime,
        // иначе старые сообщения, если у них статус = 1, отправляются.
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
            List<String> emailList = null;

            if (message.getClsMailingList().getStatus() == 1) {  // Если рассылка действует
                if (message.getClsMailingList().getId() == 1) { // Системная рассылка
                    emailList = clsPrincipalRepo.findAllEmails();
                }
                else {
                    emailList = regMailingMessageRepo.getFollowersEmailsByMessage_Id(message.getId());
                }
                for (String email : emailList) {
                    System.out.println("Send message to " + email + ": " + message.getMessage());
//                emailService.sendSimpleMessage(email, message.getClsMailingList().getName(), message.getMessage());
                }

                message.setStatus(Short.parseShort("2")); // Статус = отправлено
                regMailingMessageRepo.save(message);
            }
        }
    }

}
