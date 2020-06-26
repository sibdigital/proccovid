package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.DocRequest;
import ru.sibdigital.proccovid.model.ReviewStatuses;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

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
}
