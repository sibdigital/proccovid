package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.DocRequest;

public interface EmailService {

    void sendMessage(DocRequest docRequest);
}
