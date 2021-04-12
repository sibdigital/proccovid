package ru.sibdigital.proccovid.service.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JasperReportSource;
import net.sf.jasperreports.engine.xml.JasperPrintFactory;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.InspectionEntityForReport;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;


@Service
public class InspectionReportImpl implements InspectionReport{

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportReport(String reportFormat, String pathNameWithoutExtension) {
        try {
            List<KeyValue> inspections = getInspectionDtoTest();

            // Load file and compile it
            File file = ResourceUtils.getFile("classpath:reports/inspection.jrxml");

//            JasperDesign jasperDesign;
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inspections);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] bytes = null;
            Exporter exporter = null;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean html = false;


            if (reportFormat.equalsIgnoreCase("pdf")) {
//                bytes = JasperRunManager.runReportToPdf(jasperReport, parameters);
                exporter = new JRPdfExporter();
            } else if (reportFormat.equalsIgnoreCase("html")) {
                exporter = new HtmlExporter();
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                html = true;
            } else if (reportFormat.equalsIgnoreCase("xlsx")) {
//                ByteArrayOutputStream os = new ByteArrayOutputStream();

                exporter = new JRXlsxExporter();
//                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//                File outputFile = new File(pathNameWithoutExtension + ".xlsx");
//                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
//                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
//                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
//                configuration.setDetectCellType(true);
//                configuration.setCollapseRowSpan(false);
//                exporter.setConfiguration(configuration);
//                exporter.exportReport();
//
//                bytes = os.toByteArray();
            }

            if (!html) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            }

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();

            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }

    }

    public List<KeyValue> getInspectionDtoTest() {
        String query = "SELECT reg_organization_inspection.id        as id,\n" +
                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
                "       cca.name || ' (id: ' || cca.id || ')' as authority\n" +
                "FROM reg_organization_inspection\n" +
                "         LEFT JOIN cls_organization co on reg_organization_inspection.id_organization = co.id\n" +
                "         LEFT JOIN cls_control_authority cca on reg_organization_inspection.id_control_authority = cca.id";
        List<KeyValue> list = entityManager.createNativeQuery(query, InspectionEntityForReport.class).getResultList();

        return list;
    }
}
