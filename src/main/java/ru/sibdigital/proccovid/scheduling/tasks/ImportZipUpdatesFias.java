package ru.sibdigital.proccovid.scheduling.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sibdigital.proccovid.scheduling.ScheduleTasks;
import ru.sibdigital.proccovid.service.ImportFiasService;

@Component
public class ImportZipUpdatesFias implements Runnable {

    @Autowired
    private ImportFiasService importFiasService;

    public ImportZipUpdatesFias() {
    }

    @Override
    public void run() {
        importFiasService.importZipUpdatesFiasData();
    }
}
