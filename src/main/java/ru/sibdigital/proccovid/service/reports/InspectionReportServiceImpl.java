package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
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
import ru.sibdigital.proccovid.dto.Tuple;
import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;
import ru.sibdigital.proccovid.model.report.InspectionEntityReport;
import ru.sibdigital.proccovid.model.RegOrganizationOkved;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.repository.RegOrganizationInspectionRepo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Service
@Slf4j
public class InspectionReportServiceImpl implements InspectionReportService {

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    OkvedRepo okvedRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                               List<String> mainOkvedPaths, List<String> additionalOkvedPaths, Date defaultMinDate, Date defaultMaxDate) {
        try {
            List<InspectionEntityReport> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths);
            Long maxValueLong = (inspections.isEmpty() ? 0 : getMaxValueInspectionsByOrganAndAuthority(inspections));
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

            String hasOkvedFilter = "НЕТ";
            if (mainOkvedPaths != null && !mainOkvedPaths.isEmpty() || additionalOkvedPaths != null && !additionalOkvedPaths.isEmpty()) {
                hasOkvedFilter = "ДА";
            }
            parameters.put("hasOkvedFilter", hasOkvedFilter);

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

    public List<InspectionEntityReport> getInspectionEntitiesForReport(Date minDate, Date maxDate, Integer minCnt, List<String> mainOkvedPaths, List<String> additionalOkvedPaths) throws IOException {
        Query query = null;
        List<InspectionEntityReport> list = null;

        if (mainOkvedPaths != null && !mainOkvedPaths.isEmpty() || additionalOkvedPaths != null && !additionalOkvedPaths.isEmpty()) {
            query = getQueryWithOkvedFilter(minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths);
            list = query.getResultList();
        } else {
            query = getQueryWithoutOkvedFilter(minDate, maxDate, minCnt);
            list = query.getResultList();
        }

        return list;
    }


    private Query getQueryWithoutOkvedFilter(Date minDate, Date maxDate, Integer minCnt) throws IOException {
        String queryString = getQueryString("classpath:reports/inspection/inspection.sql");
        Query query = entityManager.createNativeQuery(queryString, InspectionEntityReport.class);

        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);

        return query;
    }

    private Query getQueryWithOkvedFilter(Date minDate, Date maxDate, Integer minCnt, List<String> mainOkvedPaths, List<String> additionalOkvedPaths) throws IOException {
        Boolean hasFilterByMainOkveds = false;
        Boolean hasFilterByAdditionalOkveds = false;

        // Отбор организации по основному оквэду
        Set<Long> orgIdsByMain = Collections.<Long>emptySet();
        if (mainOkvedPaths != null && !mainOkvedPaths.isEmpty()) {
            hasFilterByMainOkveds = true;
            List<RegOrganizationOkved> orgIdByMainOkveds = getIdOrganizationsByOkvedsPath(mainOkvedPaths, true);
            orgIdsByMain = orgIdByMainOkveds.stream()
                            .map(ctr -> ctr.getRegOrganizationOkvedId().getClsOrganization().getId())
                           .collect(Collectors.toSet());
        }

        // Отбор организации по доп оквэду
        Set<Long> orgIdsByAdditional = Collections.<Long>emptySet();
        if (additionalOkvedPaths != null && !additionalOkvedPaths.isEmpty()) {
            hasFilterByAdditionalOkveds = true;
            List<RegOrganizationOkved> orgIdByAdditionalOkveds = getIdOrganizationsByOkvedsPath(additionalOkvedPaths, false);
            orgIdsByAdditional = orgIdByAdditionalOkveds.stream()
                                .map(ctr -> ctr.getRegOrganizationOkvedId().getClsOrganization().getId())
                                 .collect(Collectors.toSet());
        }

        String  queryString = getQueryString("classpath:reports/inspection/inspection_filter_org_ids.sql");
        Query query = entityManager.createNativeQuery(queryString, InspectionEntityReport.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);
        if (hasFilterByAdditionalOkveds && hasFilterByMainOkveds) {
            Set<Long> orgIds = new HashSet<>(orgIdsByMain);
            orgIds.retainAll(orgIdsByAdditional); // в orgIds остается пересечение множеств

            query.setParameter("org_ids", orgIds);
        } else if (hasFilterByMainOkveds) {
            query.setParameter("org_ids", orgIdsByMain);
        } else if (hasFilterByAdditionalOkveds) {
            query.setParameter("org_ids", orgIdsByAdditional);
        }

        return query;
    }

    private List<RegOrganizationOkved> getIdOrganizationsByOkvedsPath(List<String> okveds, Boolean isMain) throws IOException {
        String queryString = getQueryString("classpath:reports/inspection/organizations_id_by_okveds_path.sql");
        Query query = entityManager.createNativeQuery(queryString, RegOrganizationOkved.class);

        query.setParameter("okved_paths", okveds);
        query.setParameter("is_main", isMain);
        return query.getResultList();
    }

    private String getQueryString(String path) throws IOException {
        File file = ResourceUtils.getFile(path);
        String query = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return query;
    }

    private Long getMaxValueInspectionsByOrganAndAuthority(List<InspectionEntityReport> rois) {

        if (rois != null && !rois.isEmpty()) {
            Map<Tuple<OrganizationShortDto, ControlAuthorityShortDto>, Long> map = rois.stream()
                    .collect(Collectors.groupingBy(ctr -> new Tuple(ctr.getOrganization(), ctr.getControlAuthority()), Collectors.counting()));
            Long maxValue = Collections.max(map.values());
            return maxValue;
        } else  {
            return Long.valueOf(1);
        }
    }



}


