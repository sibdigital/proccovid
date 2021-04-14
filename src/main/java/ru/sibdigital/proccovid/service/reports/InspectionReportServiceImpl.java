package ru.sibdigital.proccovid.service.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;


@Service
public class InspectionReportServiceImpl implements InspectionReportService {

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt, Date defaultMinDate, Date defaultMaxDate) {
        try {
            List<RegOrganizationInspection> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt);
            Long maxValueLong = getMaxValueInspectionsByOrganAndAuthority(inspections);
            Integer maxValue = maxValueLong.intValue();

            // Load file and compile it
            File file = ResourceUtils.getFile("classpath:reports/inspection/inspection.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inspections);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("minCnt",  minCnt);
            parameters.put("maxValue", (maxValue == 0 ? 1: maxValue));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            Exporter exporter = new HtmlExporter();
            exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();

            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }

    }

    public void exportReportXLS(String reportFormat, HttpServletResponse response, Date minDate, Date maxDate, Integer minCnt, Date defaultMinDate, Date defaultMaxDate) {
        try {
            List<RegOrganizationInspection> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt);
            Long maxValueLong = getMaxValueInspectionsByOrganAndAuthority(inspections);
            Integer maxValue = maxValueLong.intValue();

            // Load file and compile it
            File file = ResourceUtils.getFile("classpath:reports/inspection/inspection.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inspections);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("minCnt",  minCnt);
            parameters.put("maxValue", (maxValue == 0 ? 1: maxValue));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

//            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
//            configuration.setOnePagePerSheet(true);
//            configuration.setDetectCellType(true); // Detect cell types (date and etc.)
//            configuration.setWhitePageBackground(false); // No white background!
//            configuration.setFontSizeFixEnabled(false);

            // No spaces between rows and columns
//            configuration.setRemoveEmptySpaceBetweenRows(true);
//            configuration.setRemoveEmptySpaceBetweenColumns(true);
            final JRXlsExporter exporter = new JRXlsExporter();
//            exporter.setConfiguration(configuration);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

            try (ByteArrayOutputStream excelStream = new ByteArrayOutputStream();
                 FileOutputStream fos = new FileOutputStream("result.xlsx")) {
                 OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(excelStream);
                 exporter.setExporterOutput(exporterOutput);
                 exporter.exportReport();
                 excelStream.writeTo(fos);
            }

        } catch (Exception e) {

        }

    }


    public List<RegOrganizationInspection> getInspectionEntitiesForReport(Date minDate, Date maxDate, Integer minCnt) throws IOException {

        String queryString = getQueryString();
        Query query = entityManager.createNativeQuery(queryString, RegOrganizationInspection.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);

        List<RegOrganizationInspection> list = query.getResultList();
        return list;
    }

    private String getQueryString() throws IOException {
        File file = ResourceUtils.getFile("classpath:reports/inspection/inspection.sql");
        String query = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return query;
    }

    private Long getMaxValueInspectionsByOrganAndAuthority(List<RegOrganizationInspection> rois) {
        Query query = entityManager.createQuery(
    "select count(roi.id) as cnt " +
            "from RegOrganizationInspection roi " +
            "where roi in (:rois) " +
            "group by roi.organization, roi.controlAuthority " +
            "order by cnt desc");
        query.setParameter("rois", rois);
        List<Long> list = query.getResultList();
        if (list != null) {
            return list.get(0);
        } else {
            return Long.getLong("1");
        }
    }
}


