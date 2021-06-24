package ru.sibdigital.proccovid.service.reports;

import java.util.Date;

public interface RemoteCntReportService {

    byte[] exportRemoteCntReport(String reportFormat, Date reportDate);
}
