package ru.sibdigital.proccovid.service.reports;

import java.util.List;
import java.util.Map;

public interface JasperReportService {

    <T> byte[] exportJasperReport(String jrxmlPath, List<T> dataSourceList, Map<String, Object> parameters, String reportFormat);
}
