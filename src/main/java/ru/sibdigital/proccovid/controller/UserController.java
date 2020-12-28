package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.sibdigital.proccovid.config.ApplicationConstants;

@Controller
public class UserController {

    @Autowired
    private ApplicationConstants applicationConstants;

}
