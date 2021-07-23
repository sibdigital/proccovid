package ru.sibdigital.proccovid.service.reports;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface RequestSubsidyReportService {

    byte[] exportRequestSubsidiesByOkvedsReport(String reportFormat, Date minDate, Date maxDate, List<String> okveds);
    byte[] exportRequestSubsidiesByOkvedsReportDetail(String reportFormat, Date minDate, Date maxDate, List<String> okveds, UUID okvedId, Long statusId);
}
