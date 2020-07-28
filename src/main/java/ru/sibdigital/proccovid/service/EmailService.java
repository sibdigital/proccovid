package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.model.ClsPrincipal;
import ru.sibdigital.proccovid.model.ClsTemplate;
import ru.sibdigital.proccovid.model.DocRequest;

import java.util.List;
import java.util.Map;

public interface EmailService {

    void sendMessage(DocRequest docRequest);

    void sendMessage(List<ClsPrincipal> principals, ClsTemplate clsTemplate, Map<String, String> params);
}
