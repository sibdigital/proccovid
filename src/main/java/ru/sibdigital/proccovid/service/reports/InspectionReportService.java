package ru.sibdigital.proccovid.service.reports;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface InspectionReportService {

    byte[] exportReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                        List<String> mainOkveds, List<String> additionalOkveds, Date defaultMinDate, Date defaultMaxDate);
}
