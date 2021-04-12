package ru.sibdigital.proccovid.service.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JasperReportSource;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
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
import java.sql.Date;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;


@Service
public class InspectionReportImpl implements InspectionReport{

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportReport(String reportFormat, String pathNameWithoutExtension) {
        try {
            Date minDate = new Date(Long.valueOf("946656000000")); // 2000 год
            Date maxDate = new Date(Long.valueOf("32472115200000")); //2999 год
            List<InspectionEntityForReport> inspections = getInspectionDtoTest(minDate, maxDate, 0);

            // Load file and compile it
            File file = ResourceUtils.getFile("classpath:reports/inspection.jrxml");

//            JasperDesign jasperDesign;
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inspections);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);


            // sort
//            List<JRSortField> sortList = new ArrayList<JRSortField>();
//            JRDesignSortField sortField = new JRDesignSortField();
//            sortField.setName("totalOrganization");
//            sortField.setOrder(SortOrderEnum.DESCENDING);
//            sortField.setType(SortFieldTypeEnum.FIELD);
//            sortList.add(sortField);
//            parameters.put(JRParameter.SORT_FIELDS, sortList);

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

    public List<InspectionEntityForReport> getInspectionDtoTest(Date minDate, Date maxDate, Integer minCnt) {
//        String query = "SELECT reg_organization_inspection.id        as id,\n" +
//                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
//                "       cca.name || ' (id: ' || cca.id || ')' as authority\n" +
//                "FROM reg_organization_inspection\n" +
//                "         LEFT JOIN cls_organization co on reg_organization_inspection.id_organization = co.id\n" +
//                "         LEFT JOIN cls_control_authority cca on reg_organization_inspection.id_control_authority = cca.id";

        String query = "WITH\n" +
                "tbl as (\n" +
                "     SELECT id, id_organization, id_control_authority\n" +
                "     FROM reg_organization_inspection\n" +
                "     WHERE date_of_inspection > :min_date AND date_of_inspection < :max_date\n" +
                " ),\n" +
                "tbl_with_cnt as (\n" +
                "    SELECT tbl.id_organization, tbl.id_control_authority, count(*) as cnt\n" +
                "    FROM tbl\n" +
                "    GROUP BY id_organization, id_control_authority\n" +
                "),\n" +
                "res_tbl as (\n" +
                "    SELECT tbl.id, tbl.id_organization, tbl.id_control_authority\n" +
                "    FROM tbl\n" +
                "    INNER JOIN tbl_with_cnt\n" +
                "    ON tbl.id_organization = tbl_with_cnt.id_organization\n" +
                "        AND tbl.id_control_authority = tbl_with_cnt.id_control_authority\n" +
                "        AND tbl_with_cnt.cnt > :min_cnt\n" +
                "),\n" +
                "total_organization as (\n" +
                "    SELECT id_organization, count(*) as total\n" +
                "    FROM res_tbl\n" +
                "    GROUP BY id_organization\n" +
                "),\n" +
                "total_authority as (\n" +
                "    SELECT id_control_authority, count(*) as total\n" +
                "    FROM res_tbl\n" +
                "    GROUP BY id_control_authority\n" +
                ")\n" +
                "SELECT res_tbl.id                            as id,\n" +
                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
                "       cca.name || ' (id: ' || cca.id || ')' as authority,\n" +
                "       total_organization.total              as total_organization,\n" +
                "       total_authority.total                 as total_authority\n" +
                "FROM res_tbl\n" +
                "         LEFT JOIN cls_organization co on res_tbl.id_organization = co.id\n" +
                "            LEFT JOIN total_organization\n" +
                "                ON co.id = total_organization.id_organization\n" +
                "         LEFT JOIN cls_control_authority cca on res_tbl.id_control_authority = cca.id\n" +
                "            LEFT JOIN total_authority\n" +
                "                ON cca.id = total_authority.id_control_authority";
        List<InspectionEntityForReport> list = entityManager.createNativeQuery(query, InspectionEntityForReport.class)
                                                .setParameter("min_date", minDate)
                                                .setParameter("max_date", maxDate)
                                                .setParameter("min_cnt", minCnt)
                                                .getResultList();

        return list;
    }
}



