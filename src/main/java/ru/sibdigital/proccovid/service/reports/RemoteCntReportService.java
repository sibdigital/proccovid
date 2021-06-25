package ru.sibdigital.proccovid.service.reports;

import java.util.Date;
import java.util.List;

public interface RemoteCntReportService {
    byte[] exportRemoteCntReport(String reportFormat, Date reportDate);
    byte[] exportRemoteCntWithOkvedFilterReport(String reportFormat, List<String> okvedPaths);
}
