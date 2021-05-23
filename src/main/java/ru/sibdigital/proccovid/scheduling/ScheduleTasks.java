package ru.sibdigital.proccovid.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingMessageRepo;
import ru.sibdigital.proccovid.scheduling.tasks.ImportEgrulEgrip;
import ru.sibdigital.proccovid.scheduling.tasks.ImportZipFullFias;
import ru.sibdigital.proccovid.scheduling.tasks.ImportZipUpdatesFias;
import ru.sibdigital.proccovid.scheduling.tasks.SendingMailingMessageTask;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class ScheduleTasks {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public void addTaskToScheduler(Long id, RegMailingMessage regMailingMessage, Date date) {
        if (date.compareTo(new Date()) >= 0) {
            SendingMailingMessageTask task = new SendingMailingMessageTask();
            beanFactory.autowireBean(task);
            task.setMessage(regMailingMessage);
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

    public void startImportEgrulEgrip(boolean isEgrul, boolean isEgrip) {
        ImportEgrulEgrip task = new ImportEgrulEgrip();
        beanFactory.autowireBean(task);
        task.setEgrul(isEgrul);
        task.setEgrip(isEgrip);
        taskScheduler.schedule(task, new Date());
    }

    public void startImportZipFullFias(){
        ImportZipFullFias task = new ImportZipFullFias();
        beanFactory.autowireBean(task);
        taskScheduler.schedule(task, new Date());
    }

    public void startImportZipUpdatesFias(){
        ImportZipUpdatesFias task = new ImportZipUpdatesFias();
        beanFactory.autowireBean(task);
        taskScheduler.schedule(task, new Date());
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


}
