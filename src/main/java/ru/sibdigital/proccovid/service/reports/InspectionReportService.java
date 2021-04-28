package ru.sibdigital.proccovid.service.reports;

import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface InspectionReportService {

    byte[] exportInspectionReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                        List<String> mainOkveds, List<String> additionalOkveds, Date defaultMinDate, Date defaultMaxDate,
                                  String prefix);

    byte[] exportInspectionCountReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                             Long idOrganization, Long idAuthority, Integer typeRecord, Date defaultMinDate, Date defaultMaxDate,
                                       String prefix);

    byte[] exportInspectionReportDetail(Date minDate, Date maxDate, Long idOrganization, Long idAuthority, Date defaultMinDate, Date defaultMaxDate,
                                        String prefix);
}
