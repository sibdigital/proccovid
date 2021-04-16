package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.sibdigital.proccovid.dto.Tuple;
import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;
import ru.sibdigital.proccovid.model.RegOrganizationOkved;
import ru.sibdigital.proccovid.model.report.InspectionEntityReport;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class JasperReportServiceImpl implements JasperReportService {

    @Override
    public  <T> byte[] exportJasperReport(String jrxmlPath, List<T> dataSourceList, Map<String, Object> parameters, String reportFormat) {
        try {
            // Load file and compile it
            File file = ResourceUtils.getFile(jrxmlPath);

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataSourceList);


            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] bytes = null;
            Exporter exporter = null;
            boolean html = false;

            if (reportFormat.equalsIgnoreCase("pdf")) {
                exporter = new JRPdfExporter();
            } else if (reportFormat.equalsIgnoreCase("html")) {
                exporter = new HtmlExporter();
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                html = true;
            } else if (reportFormat.equalsIgnoreCase("xlsx")) {
                exporter = new JRXlsxExporter();
            }

            if (!html) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            }
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();

            return out.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

}


