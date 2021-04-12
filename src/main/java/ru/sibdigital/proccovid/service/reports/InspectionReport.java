package ru.sibdigital.proccovid.service.reports;

import javax.servlet.http.HttpServletResponse;

public interface InspectionReport {
    byte[] exportReport(String reportFormat, String pathNameWithoutExtension);

}
