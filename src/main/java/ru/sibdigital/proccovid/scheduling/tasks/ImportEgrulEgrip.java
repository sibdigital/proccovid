package ru.sibdigital.proccovid.scheduling.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.service.ImportEgrulEgripService;

@Component
public class ImportEgrulEgrip implements Runnable {

    private boolean isEgrul;
    private boolean isEgrip;

    @Autowired
    private ImportEgrulEgripService importEgrulEgripService;

    @Override
    public void run() {
        importEgrulEgripService.importData(isEgrul(), isEgrip());
    }

    public boolean isEgrul() {
        return isEgrul;
    }

    public void setEgrul(boolean egrul) {
        isEgrul = egrul;
    }

    public boolean isEgrip() {
        return isEgrip;
    }

    public void setEgrip(boolean egrip) {
        isEgrip = egrip;
    }
}
