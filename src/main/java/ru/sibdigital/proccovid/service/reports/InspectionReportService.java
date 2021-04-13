package ru.sibdigital.proccovid.service.reports;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public interface InspectionReportService {
    byte[] exportReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt);

}
