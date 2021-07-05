package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.ReviewStatuses;
import ru.sibdigital.proccovid.model.report.OrganizationCountByOkvedsReport;
import ru.sibdigital.proccovid.model.report.RemoteCntEntityReport;
import ru.sibdigital.proccovid.model.report.RemoteCntEntityWithOkvedsReport;
import ru.sibdigital.proccovid.repository.OkvedRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@PropertySource("classpath:reports")
public class RemoteCntReportServiceImpl implements RemoteCntReportService{

    @Autowired
    JasperReportService jasperReportService;

    @Autowired
    OkvedRepo okvedRepo;

    @Autowired
    ResourceLoader resourceLoader;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public byte[] exportRemoteCntReport(String reportFormat, Date reportDate) {
        try {
            List<RemoteCntEntityReport> remoteCntEntities = getRemoteCntEntitiesForReport(reportDate);

            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource remoteCntJRBean = new JRBeanCollectionDataSource(remoteCntEntities);
            parameters.put("RemoteCntDataSource", remoteCntJRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет о количестве работников на удаленке");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            parameters.put("reportDate", dateFormat.format(reportDate));

            String jrxmlPath = "classpath:reports/remote_cnt/remote_cnt.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, remoteCntEntities, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<RemoteCntEntityReport> getRemoteCntEntitiesForReport(Date reportDate) throws IOException {
        Query query = getQueryForRemoteCntReport(reportDate);
        List<RemoteCntEntityReport> list = query.getResultList();

        return list;
    }

    private Query getQueryForRemoteCntReport(Date reportDate) throws IOException {
        String  queryString = getQueryString("classpath:reports/remote_cnt/remote_cnt.sql");
        Query query = entityManager.createNativeQuery(queryString, RemoteCntEntityReport.class);
        query.setParameter("report_date", reportDate);

        return query;
    }


    @Override
    public byte[] exportRemoteCntWithOkvedFilterReport(String reportFormat, List<String> okvedPaths) {
        try {
            List<RemoteCntEntityWithOkvedsReport> remoteCntEntities = getRemoteCntWithOkvedsForReport(okvedPaths);

            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource remoteCntJRBean = new JRBeanCollectionDataSource(remoteCntEntities);
            parameters.put("RemoteCntDataSource", remoteCntJRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет о количестве сотрудников в организации");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            parameters.put("okvedPaths", okvedPaths.toString());

            String jrxmlPath = "classpath:reports/remote_cnt/remote_cnt_with_okveds.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, remoteCntEntities, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] exportOrgCountByOkvedReport(String reportFormat, List<String> okvedPaths, Date requestTimeCreate, Long statusValue) {
        try {
            Map<Long, String> statusMap = getReviewStatusMap();
            String requestStatus = statusMap.get(statusValue);
            if (statusValue.equals(-100L)) {
                statusValue = 0L; // Из-за webix, который не может принять 0, передаем 1001 вместо 0
            }

            List<OrganizationCountByOkvedsReport> organizationCnts = getOrganizationCntByOkvedsForReport(okvedPaths, requestTimeCreate, statusValue);

            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource reportJRBean = new JRBeanCollectionDataSource(organizationCnts);
            parameters.put("ReportDataSource", reportJRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет по количествам в разрезе ОКВЭД\n");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            parameters.put("requestTimeCreate", dateFormat.format(requestTimeCreate));
            parameters.put("requestStatus", requestStatus);

            String jrxmlPath = "classpath:reports/remote_cnt/org_count_by_okved.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, organizationCnts, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<OrganizationCountByOkvedsReport> getOrganizationCntByOkvedsForReport(List<String> okvedPaths, Date requestTimeCreate, Long statusValue) throws IOException {
        Set<String> set = okvedPaths.stream().collect(Collectors.toSet());
        if (set.contains("2001")) {
            set.addAll(getAllOkvedPathsByVersion("2001"));
        }
        if (set.contains("2014")) {
            set.addAll(getAllOkvedPathsByVersion("2014"));
        }
        Query query = getQueryForOrgCntByOkvedsReport(set, requestTimeCreate, statusValue);
        List<OrganizationCountByOkvedsReport> list = query.getResultList();

        return list;
    }

    private List<RemoteCntEntityWithOkvedsReport> getRemoteCntWithOkvedsForReport(List<String> okvedPaths) throws IOException {
        Set<String> set = okvedPaths.stream().collect(Collectors.toSet());
        if (set.contains("2001")) {
            set.addAll(getAllOkvedPathsByVersion("2001"));
        }
        if (set.contains("2014")) {
            set.addAll(getAllOkvedPathsByVersion("2014"));
        }
        Query query = getQueryForRemoteCntWithOkvedReport(set);
        List<RemoteCntEntityWithOkvedsReport> list = query.getResultList();

        return list;
    }

    private Set<String> getAllOkvedPathsByVersion(String version) {
        List<Okved> okveds = okvedRepo.findAllByVersion(version);
        Set<String> okvedPaths = okveds.stream().map(ctr -> ctr.getPath()).collect(Collectors.toSet());
        return okvedPaths;
    }

    private Query getQueryForRemoteCntWithOkvedReport(Set<String> okvedPaths) throws IOException {
        String  queryString = getQueryString("classpath:reports/remote_cnt/remote_cnt_with_okveds.sql");
        Query query = entityManager.createNativeQuery(queryString, RemoteCntEntityWithOkvedsReport.class);
        query.setParameter("okved_paths", okvedPaths);

        return query;
    }

    private Query getQueryForOrgCntByOkvedsReport(Set<String> okvedPaths, Date requestTimeCreate, Long statusValue) throws IOException {
        String  queryString = getQueryString("classpath:reports/remote_cnt/org_count_by_okved.sql");
        Query query = entityManager.createNativeQuery(queryString, OrganizationCountByOkvedsReport.class);
        query.setParameter("okved_paths", okvedPaths);
        query.setParameter("status_review", statusValue);
        query.setParameter("time_create", requestTimeCreate);

        return query;
    }

    private String getQueryString(String path) throws IOException {
        InputStream inputStream = resourceLoader.getResource(path).getInputStream();
        String query = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        return query;
    }


    @Override
    public Map<Long, String> getReviewStatusMap() {
        Map<Long, String> map = new HashMap<>();
        map.put(Long.valueOf(ReviewStatuses.OPENED.getValue() - 100), "На рассмотрении"); // webix.richselect не может принять 0
        map.put(Long.valueOf(ReviewStatuses.CONFIRMED.getValue()), "Одобрена");
//        map.put(ReviewStatuses.REJECTED.getValue(), "Отклонена");
//        map.put(ReviewStatuses.UPDATED.getValue(), "UPDATED");
//        map.put(ReviewStatuses.ACCEPTED.getValue(), "ACCEPTED");
        map.put(Long.valueOf(ReviewStatuses.NEW.getValue()), "Новая");
//        map.put(ReviewStatuses.EXPIRED.getValue(), "Просрочена");
        return map;
    }
}
