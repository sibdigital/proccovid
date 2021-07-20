package ru.sibdigital.proccovid.scheduling.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.classifier.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsPrincipalRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingHistoryRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingMessageRepo;
import ru.sibdigital.proccovid.service.impl.EmailServiceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class SendingMailingMessageTask implements Runnable {

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private RegMailingHistoryRepo regMailingHistoryRepo;

    @Autowired
    private RegMailingMessageRepo regMailingMessageRepo;

    private RegMailingMessage message;
    private final int granula = 250;


    @Override
    public void run() {

        log.info("Старт рассылки: " + getMessage().getClsMailingList().getName());

        try {
            if (getMessage().getClsMailingList().getStatus() == MailingListStatuses.VALID.value()) {
                if (getMessage().getClsMailingList().getForPrincipal() == true){
                    sendForPrincipals();
                }else{
                    sendForOrganization();
                }
            } else {
                RegMailingHistory history = new RegMailingHistory();
                history.setClsMailingList(getMessage().getClsMailingList());
                history.setRegMailingMessage(getMessage());
                history.setTimeSend(new Timestamp(System.currentTimeMillis()));
                history.setStatus(MailingStatuses.MAILING_LIST_NOT_VALID.value());
                regMailingHistoryRepo.save(history);
            }

            log.info("Конец рассылки: " + getMessage().getClsMailingList().getName());
        } catch (Exception e) {
            log.info("Рассылка закончилась ошибками:");
            log.error(e.getMessage(), e);
        }
    }

    private void sendForPrincipals(){
        List<ClsPrincipal> principals = new ArrayList<>();
        if (getMessage().getClsMailingList().getId() == 1) { // Системная рассылка
            principals = clsPrincipalRepo.findAll();
        } else {
            principals = clsPrincipalRepo.getClsPrincipalsByMessage_Id(getMessage().getId());
        }
        log.info("Записей для обработки: " + principals.size());
        sendMessagesToPrincipals(principals);

        getMessage().setStatus(MailingMessageStatuses.IS_SENT.value());
        regMailingMessageRepo.save(getMessage());
    }

    private void sendForOrganization(){
        List<ClsOrganization> organizations = new ArrayList<>();
        if (getMessage().getClsMailingList().getId() == 1) { // Системная рассылка
            organizations = clsOrganizationRepo.findAll();
        } else {
            organizations = clsOrganizationRepo.getClsOrganizationByMessage_Id(getMessage().getId());
        }
        log.info("Записей для обработки: " + organizations.size());
        sendMessagesToOrganization(organizations);

        getMessage().setStatus(MailingMessageStatuses.IS_SENT.value());
        regMailingMessageRepo.save(getMessage());
    }

    private void sendMessagesToPrincipals(List<ClsPrincipal> principals) {
        List<ClsPrincipal> granulas = new ArrayList<>();
        int granulaSum = 0;
        for (ClsPrincipal principal : principals) {
            granulas.add(principal);
            if (granulas.size() >= granula) {
                emailService.sendMessage(granulas, getMessage(), new HashMap<>());
                granulaSum += granulas.size();
                log.info("Обработано: " + granulaSum + " из " + principals.size());
                granulas.clear();
            }
        }
        if (granulas.isEmpty() == false) {
            emailService.sendMessage(granulas, getMessage(), new HashMap<>());
            granulaSum += granulas.size();
            log.info("Обработано: " + granulaSum + " из " + principals.size());
            granulas.clear();
        }
    }

    private void sendMessagesToOrganization(List<ClsOrganization> organizations) {
        List<ClsOrganization> granulas = new ArrayList<>();
        for (ClsOrganization organization : organizations) {
            granulas.add(organization);
            if (granulas.size() >= granula) {
                emailService.sendMessageToOrganizations(granulas, getMessage(), new HashMap<>());
                granulas.clear();
            }
        }
        if (granulas.isEmpty() == false) {
            emailService.sendMessageToOrganizations(granulas, getMessage(), new HashMap<>());
            granulas.clear();
        }
    }

    public RegMailingMessage getMessage() {
        return message;
    }

    public void setMessage(RegMailingMessage message) {
        this.message = message;
    }
}
