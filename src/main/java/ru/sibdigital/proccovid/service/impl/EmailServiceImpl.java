package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingHistoryRepo;
import ru.sibdigital.proccovid.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RegMailingHistoryRepo regMailingHistoryRepo;

    @Value("${spring.mail.from:/rabota@govrb.ru}")
    protected String fromAdress;

    private final static String sep = "|";

    private final static Logger emailLog = LoggerFactory.getLogger(EmailServiceImpl.class);

    public void sendMessage(DocRequest docRequest) {
        if (docRequest == null) {
            return;
        }
        ClsOrganization organization = docRequest.getOrganization();
        String textLog = sep + docRequest.getId() + sep + organization.getId() + sep
                + organization.getInn() + sep + organization.getEmail() + sep;
        try {
            String text = "";
            if (docRequest.getStatusReview() == ReviewStatuses.CONFIRMED.getValue()) {
                String cause = docRequest.getRejectComment().isBlank() == false
                        ? " Примечание: " + docRequest.getRejectComment() : "";
                text = "Ваше заявление рассмотрено " + docRequest.getDepartment().getName() + " и одобрено." + cause;
            } else if (docRequest.getStatusReview() == ReviewStatuses.REJECTED.getValue()) {
                text = "Ваша заявка рассмотрена " + docRequest.getDepartment().getName() + " и отклонена по причине: " + docRequest.getRejectComment();
            }
            sendSimpleMessage(organization.getEmail(), applicationConstants.getApplicationName(), text, "rabota@govrb.ru");
            textLog += "0";
        } catch (MailException e) {
            textLog += "1" + sep + e.getMessage();
            log.error(e.getMessage());
        } catch (Exception e) {
            textLog += "2" + sep + e.getMessage();
            log.error(e.getMessage());
        } finally {
            emailLog.info(textLog);
        }
    }

    public void sendSimpleMessage(String to, String subject, String text, String from) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(from);

        javaMailSender.send(message);
    }

    public void sendMessage(List<ClsPrincipal> principals, ClsTemplate clsTemplate, Map<String, String> params) {
        Map<Integer, RegMailingHistory> histories = new HashMap<>();

        List<MimeMessage> messages = new ArrayList<>();

        for (ClsPrincipal principal : principals) {
            ClsOrganization organization = principal.getOrganization();
            if (organization != null) {
                int code = principal.hashCode();

                Short exception = MailingStatuses.EMAIL_SENT.value();
                try {
                    if (organization.getEmail() == null){
                        throw new AddressException(organization.getId() + " email is null");
                    }
                    InternetAddress address = new InternetAddress(organization.getEmail()); // validate

                    params.put("organizationName", organization.getName() == null ? "" : organization.getName());
                    params.put("inn", organization.getInn() == null ? "" : organization.getInn());

                    MimeMessage message = prepareMimeMessage(address, clsTemplate, params);
                    message.setDescription(String.valueOf(code));
                    messages.add(message);
                } catch (AddressException e) {
                    exception = MailingStatuses.INVALID_ADDRESS.value();
                } catch (MessagingException messagingException) {
                    exception = MailingStatuses.EMAIL_NOT_CREATED.value();
                }

                RegMailingHistory history = constructHistory(principal, null, exception);
                histories.put(code, history);
            }
        }
        try {
            log.info("Обработка");
            process(messages, histories);
            regMailingHistoryRepo.saveAll(histories.values());
        }catch (Exception e) {
            log.info("Ошибка сохранения истории"); // если это была тестовая отправка. то нормальная ситуация
            log.error(e.getMessage(), e);
        }
    }

    public void sendMessage(String email, ClsTemplate clsTemplate, Map<String, String> params) {
        Map<Integer, RegMailingHistory> histories = new HashMap<>();

        List<MimeMessage> messages = new ArrayList<>();
        RegMailingHistory history;
//        for (ClsPrincipal principal : principals) {
//            ClsOrganization organization = principal.getOrganization();
//            if (organization != null) {

//                int code = principal.hashCode();
                int code = email.hashCode();

                Short exception = MailingStatuses.EMAIL_SENT.value();
                try {
                    if (email == null){
                        throw new AddressException( " email is null");
                    }
                    InternetAddress address = new InternetAddress(email); // validate

//                    params.put("organizationName", organization.getName() == null ? "" : organization.getName());
//                    params.put("inn", organization.getInn() == null ? "" : organization.getInn());

                    MimeMessage message = prepareMimeMessage(address, clsTemplate, params);
                    message.setDescription(String.valueOf(code));
                    messages.add(message);
                } catch (AddressException e) {
                    exception = MailingStatuses.INVALID_ADDRESS.value();
                } catch (MessagingException messagingException) {
                    exception = MailingStatuses.EMAIL_NOT_CREATED.value();
                }catch (Exception ex){
                    exception = MailingStatuses.UNKNOWN_ERROR.value();
                    log.error(ex.getMessage());
                }

                history = new RegMailingHistory();
//                history.setClsPrincipal(principal);
                history.setTimeSend(new Timestamp(System.currentTimeMillis()));
                history.setStatus(exception);
                history.setClsTemplate(clsTemplate);
                history.setEmail(email);
                histories.put(code, history);
//            }
//        }

        if (!messages.isEmpty()) {
            try {
                javaMailSender.send(messages.toArray(new MimeMessage[0]));
            } catch (MailSendException mailSendException) {
                Map<Object, Exception> failedMessages = mailSendException.getFailedMessages();
                for (Map.Entry<Object, Exception> failedMessage : failedMessages.entrySet()) {
                    MimeMessage message = (MimeMessage) failedMessage.getKey();
                    try {

                        history = histories.get(Integer.valueOf(message.getDescription()));
                        history.setStatus(MailingStatuses.EMAIL_NOT_SENT.value());
                    } catch (MessagingException messagingException) {
                        log.error(messagingException.getMessage());
                    }
                }
            } catch (Exception ex){
                log.error(ex.getMessage());
            }

            regMailingHistoryRepo.saveAll(histories.values());
        }
    }

    private RegMailingHistory constructHistory(ClsOrganization organization, RegMailingMessage regMailingMessage, Short status){
        RegMailingHistory history = new RegMailingHistory();
        if (regMailingMessage != null) {
            history.setClsMailingList(regMailingMessage.getClsMailingList());
            history.setRegMailingMessage(regMailingMessage);
        }
        //history.setClsPrincipal(principal);
        history.setTimeSend(new Timestamp(System.currentTimeMillis()));
        history.setStatus(status);
        //ClsOrganization organization = principal.getOrganization();
        if (organization != null) {
            history.setEmail(organization.getEmail());
        }
        return history;
    }

    private RegMailingHistory constructHistory(ClsPrincipal principal, RegMailingMessage regMailingMessage, Short status){
        RegMailingHistory history = new RegMailingHistory();
        if (regMailingMessage != null) {
            history.setClsMailingList(regMailingMessage.getClsMailingList());
            history.setRegMailingMessage(regMailingMessage);
        }
        history.setClsPrincipal(principal);
        history.setTimeSend(new Timestamp(System.currentTimeMillis()));
        history.setStatus(status);
        ClsOrganization organization = principal.getOrganization();
        if (organization != null) {
            history.setEmail(organization.getEmail());
        }
        return history;
    }

    private void process(List<MimeMessage> messages, Map<Integer, RegMailingHistory> histories){
        if (!messages.isEmpty()) {
            try {
                javaMailSender.send(messages.toArray(new MimeMessage[0]));
            } catch (MailSendException mailSendException) {
                Map<Object, Exception> failedMessages = mailSendException.getFailedMessages();
                log.info("Ошибок при отправке: " + failedMessages.size());
                for (Map.Entry<Object, Exception> failedMessage : failedMessages.entrySet()) {
                    MimeMessage message = (MimeMessage) failedMessage.getKey();
                    try {
                        RegMailingHistory history = histories.get(Integer.valueOf(message.getDescription()));
                        history.setStatus(MailingStatuses.EMAIL_NOT_SENT.value());
                    } catch (MessagingException messagingException) {
                        log.error(messagingException.getMessage());
                    }
                }
            } catch (Exception e) {
                histories.entrySet().stream().forEach(h -> h.getValue().setStatus(MailingStatuses.EMAIL_NOT_SENT.value()));
                log.info("Гранула рассылки завершилась ошибками:");
                log.error(e.getMessage(), e);
            }
        }
    }

    public void sendMessage(List<ClsPrincipal> principals, RegMailingMessage regMailingMessage, Map<String, String> params) {
        Map<Integer, RegMailingHistory> histories = new HashMap<>();
        List<MimeMessage> messages = new ArrayList<>();

        for (ClsPrincipal principal : principals) {
            ClsOrganization organization = principal.getOrganization();
            if (organization != null) {
                int code = principal.hashCode();
                Short status = MailingStatuses.EMAIL_SENT.value();

                try {
                    if (organization.getEmail() == null){
                        throw new AddressException(organization.getId() + " email is null");
                    }
                    InternetAddress address = new InternetAddress(organization.getEmail()); // validate

                    params.put("organizationName", organization.getName() == null ? "" : organization.getName());
                    params.put("inn", organization.getInn() == null ? "" : organization.getInn());
                    params.put("subject", regMailingMessage.getSubject());

                    MimeMessage message = prepareMimeMessage(address, regMailingMessage, params);
                    message.setDescription(String.valueOf(code));
                    messages.add(message);
                } catch (AddressException e) {
                    status = MailingStatuses.INVALID_ADDRESS.value();
                } catch (MessagingException messagingException) {
                    status = MailingStatuses.EMAIL_NOT_CREATED.value();
                }

                RegMailingHistory history = constructHistory(principal, regMailingMessage, status);
                histories.put(code, history);
            }
        }
        if (messages.isEmpty() == false){
            try {
                log.info("Обработка " + messages.size());
                process(messages, histories);
                regMailingHistoryRepo.saveAll(histories.values());
            }catch (Exception e) {
                log.info("Ошибка сохранения истории"); // если это была тестовая отправка. то нормальная ситуация
                log.error(e.getMessage(), e);
            }
        }
    }

    public void sendMessageToOrganizations(List<ClsOrganization> organizations, RegMailingMessage regMailingMessage, Map<String, String> params) {
        Map<Integer, RegMailingHistory> histories = new HashMap<>();
        List<MimeMessage> messages = new ArrayList<>();

        for (ClsOrganization organization : organizations) {
            if (organization != null) {
                int code = organization.hashCode();
                Short status = MailingStatuses.EMAIL_SENT.value();

                try {
                    if (organization.getEmail() == null){
                        throw new AddressException(organization.getId() + " email is null");
                    }
                    InternetAddress address = new InternetAddress(organization.getEmail()); // validate

                    params.put("organizationName", organization.getName() == null ? "" : organization.getName());
                    params.put("inn", organization.getInn() == null ? "" : organization.getInn());
                    params.put("subject", regMailingMessage.getSubject());

                    MimeMessage message = prepareMimeMessage(address, regMailingMessage, params);
                    message.setDescription(String.valueOf(code));
                    messages.add(message);
                } catch (AddressException e) {
                    status = MailingStatuses.INVALID_ADDRESS.value();
                } catch (MessagingException messagingException) {
                    status = MailingStatuses.EMAIL_NOT_CREATED.value();
                }

                RegMailingHistory history = constructHistory(organization, regMailingMessage, status);
                histories.put(code, history);
            }
        }
        if (messages.isEmpty() == false){
            try {
                log.info("Обработка " + messages.size());
                process(messages, histories);
                regMailingHistoryRepo.saveAll(histories.values());
            }catch (Exception e) {
                log.info("Ошибка сохранения истории"); // если это была тестовая отправка. то нормальная ситуация
                log.error(e.getMessage(), e);
            }
        }
    }


    private MimeMessage prepareMimeMessage(InternetAddress address, ClsTemplate clsTemplate, Map<String, String> params) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(address.getAddress());
        String subject = params.get("subject") == null ? applicationConstants.getApplicationName() : params.get("subject");
        helper.setSubject(subject);
        helper.setFrom(fromAdress);

        String text = clsTemplate.getValue();
        for (Map.Entry<String, String> param : params.entrySet()) {
            text = text.replaceAll(param.getKey(), param.getValue());
        }
        helper.setText(text, true);

        return message;
    }

    private MimeMessage prepareMimeMessage(InternetAddress address, RegMailingMessage regMailingMessage, Map<String, String> params) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(address.getAddress());
        String subject = params.get("subject") == null ? applicationConstants.getApplicationName() : params.get("subject");
        helper.setSubject(subject);
        helper.setFrom(fromAdress);

        String text = regMailingMessage.getMessage();
        helper.setText(text, true);

        return message;
    }

}
