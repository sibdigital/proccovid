package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.RegMailingHistoryRepo;

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
            sendSimpleMessage(organization.getEmail(), "Работающая Бурятия", text, "rabota@govrb.ru");
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

    private void sendSimpleMessage(String to, String subject, String text, String from) {
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

                RegMailingHistory history = new RegMailingHistory();
                history.setClsPrincipal(principal);
                history.setTimeSend(new Timestamp(System.currentTimeMillis()));
                history.setStatus(exception);
                history.setClsTemplate(clsTemplate);
                history.setEmail(organization.getEmail());
                histories.put(code, history);
            }
        }

        if (!messages.isEmpty()) {
            try {
                javaMailSender.send(messages.toArray(new MimeMessage[0]));
            } catch (MailSendException mailSendException) {
                Map<Object, Exception> failedMessages = mailSendException.getFailedMessages();
                for (Map.Entry<Object, Exception> failedMessage : failedMessages.entrySet()) {
                    MimeMessage message = (MimeMessage) failedMessage.getKey();
                    try {
                        RegMailingHistory history = histories.get(Integer.valueOf(message.getDescription()));
                        history.setStatus(MailingStatuses.EMAIL_NOT_SENT.value());
                    } catch (MessagingException messagingException) {
                        log.error(messagingException.getMessage());
                    }
                }
            }

            regMailingHistoryRepo.saveAll(histories.values());
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

    public void sendMessage(List<ClsPrincipal> principals, RegMailingMessage regMailingMessage, Map<String, String> params) {
        Map<Integer, RegMailingHistory> histories = new HashMap<>();

        List<MimeMessage> messages = new ArrayList<>();
        for (ClsPrincipal principal : principals) {
            ClsOrganization organization = principal.getOrganization();
            if (organization != null) {
                int code = principal.hashCode();

                Short status = MailingStatuses.EMAIL_SENT.value();
                try {
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

                RegMailingHistory history = new RegMailingHistory();
                history.setClsMailingList(regMailingMessage.getClsMailingList());
                history.setRegMailingMessage(regMailingMessage);
                history.setClsPrincipal(principal);
                history.setTimeSend(new Timestamp(System.currentTimeMillis()));
                history.setStatus(status);
                history.setEmail(organization.getEmail());
                histories.put(code, history);
            }
        }

        if (!messages.isEmpty()) {
            try {
                javaMailSender.send(messages.toArray(new MimeMessage[0]));
            } catch (MailSendException mailSendException) {
                Map<Object, Exception> failedMessages = mailSendException.getFailedMessages();
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
                log.info("Рассылка закончилась ошибками:");
                log.error(e.getMessage(), e);
            }

            regMailingHistoryRepo.saveAll(histories.values());
        }
    }

    private MimeMessage prepareMimeMessage(InternetAddress address, ClsTemplate clsTemplate, Map<String, String> params) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(address.getAddress());
        String subject = params.get("subject") == null ? "Работающая Бурятия" : params.get("subject");
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
        String subject = params.get("subject") == null ? "Работающая Бурятия" : params.get("subject");
        helper.setSubject(subject);
        helper.setFrom(fromAdress);

        String text = regMailingMessage.getMessage();
        helper.setText(text, true);

        return message;
    }

}
