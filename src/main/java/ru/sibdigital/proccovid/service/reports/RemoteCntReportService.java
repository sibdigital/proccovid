package ru.sibdigital.proccovid.service.reports;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RemoteCntReportService {
    byte[] exportRemoteCntReport(String reportFormat, Date reportDate);
    byte[] exportRemoteCntWithOkvedFilterReport(String reportFormat, List<String> okvedPaths);
    byte[] exportOrgCountByOkvedReport(String reportFormat, List<String> okvedPaths, Date requestTimeCreate, Long statusValue);
    Map<Long, String> getReviewStatusMap();
}
