package ru.sibdigital.proccovid.service.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Service
public class InspectionReportServiceImpl implements InspectionReportService {

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt) {
        try {
//            List<InspectionEntityForReport> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt);
            List<RegOrganizationInspection> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt);

            // Load file and compile it
            File file = ResourceUtils.getFile("classpath:reports/inspection/inspection.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inspections);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == null ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == null ? "" : dateFormat.format(maxDate)));
            parameters.put("minCnt",  minCnt);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] bytes = null;
            Exporter exporter = null;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
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
            return null;
        }

    }

    public List<RegOrganizationInspection> getInspectionEntitiesForReport(Date minDate, Date maxDate, Integer minCnt) throws IOException {

        String queryString = getQueryString(minDate, maxDate);
        Query query = entityManager.createNativeQuery(queryString, RegOrganizationInspection.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);

        List<RegOrganizationInspection> list = query.getResultList();
        return list;
//        return null;
    }

    private String getQueryString(Date minDate, Date maxDate) throws IOException {
        File file = ResourceUtils.getFile("classpath:reports/inspection/inspection.sql");
        String query = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return query;
    }
}


//        Query query = entityManager.createNativeQuery(
//                "WITH\n" +
//                "    tbl as (\n" +
//                "        SELECT id, id_organization, id_control_authority\n" +
//                "        FROM reg_organization_inspection\n" +
//                "    ),\n" +
//                "    tbl_with_cnt as (\n" +
//                "        SELECT id_organization, id_control_authority, count(*) as cnt\n" +
//                "        FROM reg_organization_inspection\n" +
//                "        WHERE date_of_inspection > :min_date AND date_of_inspection < :max_date\n" +
//                "        GROUP BY id_organization, id_control_authority\n" +
//                "    )\n" +
//                "SELECT tbl.id, tbl.id_organization, tbl.id_control_authority\n" +
//                "FROM tbl\n" +
//                "         INNER JOIN tbl_with_cnt\n" +
//                "            ON tbl.id_organization = tbl_with_cnt.id_organization\n" +
//                "                AND tbl.id_control_authority = tbl_with_cnt.id_control_authority\n" +
//                "                AND tbl_with_cnt.cnt > :min_cnt", RegOrganizationInspection.class);
//        query.setParameter("min_date", minDate);
//        query.setParameter("max_date", maxDate);
//        query.setParameter("min_cnt", minCnt);
//        List<RegOrganizationInspection> list = query.getResultList();


//        Query queryOrgTotal = entityManager.createQuery(
//                "select roi.organization, count(roi.id) as totalOrganization from RegOrganizationInspection roi where roi in (:rois)", InspectionEntityForReport.class);
//        queryOrgTotal.setParameter("rois", list);
//        List<InspectionEntityForReport> orgTotalList = queryOrgTotal.getResultList();
//
//        Query queryAuthTotal = entityManager.createQuery(
//                "select roi.controlAuthority, count(roi.id) as totalAuthority from RegOrganizationInspection roi where roi in (:rois)", InspectionEntityForReport.class);
//        queryAuthTotal.setParameter("rois", list);
//        List<InspectionEntityForReport> authTotalList = queryAuthTotal.getResultList();
//
//        Query resQuery = entityManager.createQuery(
//                "select new InspectionEntityForReport(roi.id, roi.organization, roi.controlAuthority, totalOrg.totalOrganization, totalAuth.totalAuthority) from RegOrganizationInspection roi " +
//                        "inner join InspectionEntityForReport totalOrg on roi.organization = totalOrg.organization " +
//                        "inner join InspectionEntityForReport totalAuth on roi.controlAuthority = totalAuth.authority " +
//                        "where roi in (:rois) and totalOrg in (:totalOrgs) and totalAuth in (:totalAuths)", InspectionEntityForReport.class);
//        resQuery.setParameter("rois", list);
//        resQuery.setParameter("totalOrgs", orgTotalList);
//        resQuery.setParameter("totalAuths", authTotalList);
//        List<InspectionEntityForReport> resultList = resQuery.getResultList();

//        StringBuilder stringBuilder = new StringBuilder("WITH\n" +
//                "tbl as (\n" +
//                "     SELECT id, id_organization, id_control_authority\n" +
//                "     FROM reg_organization_inspection\n");
//
//        if (minDate != null && maxDate != null) {
//            stringBuilder.append("     WHERE date_of_inspection > :min_date AND date_of_inspection < :max_date\n");
//        } else {
//            if (minDate != null) {
//                stringBuilder.append("     WHERE date_of_inspection > :min_date\n");
//            } else if (maxDate != null) {
//                stringBuilder.append("     WHERE date_of_inspection < :max_date\n");
//            }
//        }
//
//        stringBuilder.append(
//                " ),\n" +
//                "tbl_with_cnt as (\n" +
//                "    SELECT tbl.id_organization, tbl.id_control_authority, count(*) as cnt\n" +
//                "    FROM tbl\n" +
//                "    GROUP BY id_organization, id_control_authority\n" +
//                "),\n" +
//                "res_tbl as (\n" +
//                "    SELECT tbl.id, tbl.id_organization, tbl.id_control_authority\n" +
//                "    FROM tbl\n" +
//                "    INNER JOIN tbl_with_cnt\n" +
//                "    ON tbl.id_organization = tbl_with_cnt.id_organization\n" +
//                "        AND tbl.id_control_authority = tbl_with_cnt.id_control_authority\n" +
//                "        AND tbl_with_cnt.cnt > :min_cnt\n" +
//                "),\n" +
//                "total_organization as (\n" +
//                "    SELECT id_organization, count(*) as total\n" +
//                "    FROM res_tbl\n" +
//                "    GROUP BY id_organization\n" +
//                "),\n" +
//                "total_authority as (\n" +
//                "    SELECT id_control_authority, count(*) as total\n" +
//                "    FROM res_tbl\n" +
//                "    GROUP BY id_control_authority\n" +
//                ")\n" +
//                "SELECT res_tbl.id                            as id,\n" +
//                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
//                "       cca.name || ' (id: ' || cca.id || ')' as authority,\n" +
//                "       total_organization.total              as total_organization,\n" +
//                "       total_authority.total                 as total_authority\n" +
//                "FROM res_tbl\n" +
//                "         LEFT JOIN cls_organization co on res_tbl.id_organization = co.id\n" +
//                "            LEFT JOIN total_organization\n" +
//                "                ON co.id = total_organization.id_organization\n" +
//                "         LEFT JOIN cls_control_authority cca on res_tbl.id_control_authority = cca.id\n" +
//                "            LEFT JOIN total_authority\n" +
//                "                ON cca.id = total_authority.id_control_authority");
//        return stringBuilder.toString();

//        String query = "SELECT reg_organization_inspection.id        as id,\n" +
//                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
//                "       cca.name || ' (id: ' || cca.id || ')' as authority\n" +
//                "FROM reg_organization_inspection\n" +
//                "         LEFT JOIN cls_organization co on reg_organization_inspection.id_organization = co.id\n" +
//                "         LEFT JOIN cls_control_authority cca on reg_organization_inspection.id_control_authority = cca.id";

//        String query = "WITH\n" +
//                "tbl as (\n" +
//                "     SELECT id, id_organization, id_control_authority\n" +
//                "     FROM reg_organization_inspection\n" +
//                "     WHERE date_of_inspection > :min_date AND date_of_inspection < :max_date\n" +
//                " ),\n" +
//                "tbl_with_cnt as (\n" +
//                "    SELECT tbl.id_organization, tbl.id_control_authority, count(*) as cnt\n" +
//                "    FROM tbl\n" +
//                "    GROUP BY id_organization, id_control_authority\n" +
//                "),\n" +
//                "res_tbl as (\n" +
//                "    SELECT tbl.id, tbl.id_organization, tbl.id_control_authority\n" +
//                "    FROM tbl\n" +
//                "    INNER JOIN tbl_with_cnt\n" +
//                "    ON tbl.id_organization = tbl_with_cnt.id_organization\n" +
//                "        AND tbl.id_control_authority = tbl_with_cnt.id_control_authority\n" +
//                "        AND tbl_with_cnt.cnt > :min_cnt\n" +
//                "),\n" +
//                "total_organization as (\n" +
//                "    SELECT id_organization, count(*) as total\n" +
//                "    FROM res_tbl\n" +
//                "    GROUP BY id_organization\n" +
//                "),\n" +
//                "total_authority as (\n" +
//                "    SELECT id_control_authority, count(*) as total\n" +
//                "    FROM res_tbl\n" +
//                "    GROUP BY id_control_authority\n" +
//                ")\n" +
//                "SELECT res_tbl.id                            as id,\n" +
//                "       co.name || ' (id: ' || co.id || ')'   as organization,\n" +
//                "       cca.name || ' (id: ' || cca.id || ')' as authority,\n" +
//                "       total_organization.total              as total_organization,\n" +
//                "       total_authority.total                 as total_authority\n" +
//                "FROM res_tbl\n" +
//                "         LEFT JOIN cls_organization co on res_tbl.id_organization = co.id\n" +
//                "            LEFT JOIN total_organization\n" +
//                "                ON co.id = total_organization.id_organization\n" +
//                "         LEFT JOIN cls_control_authority cca on res_tbl.id_control_authority = cca.id\n" +
//                "            LEFT JOIN total_authority\n" +
//                "                ON cca.id = total_authority.id_control_authority";


