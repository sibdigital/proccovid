package ru.sibdigital.proccovid.service.reports;

import java.util.Date;
import java.util.List;

public interface RequestSubsidyReportService {

    byte[] exportRequestSubsidiesByOkvedsReport(String reportFormat, Date minDate, Date maxDate, List<String> okveds);

}
