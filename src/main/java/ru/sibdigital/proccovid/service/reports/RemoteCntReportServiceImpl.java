package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.report.RemoteCntEntityReport;
import ru.sibdigital.proccovid.model.report.RemoteCntEntityWithOkvedsReport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    private List<RemoteCntEntityWithOkvedsReport> getRemoteCntWithOkvedsForReport(List<String> okvedPaths) throws IOException {
        Query query = getQueryForRemoteCntWithOkvedReport(okvedPaths);
        List<RemoteCntEntityWithOkvedsReport> list = query.getResultList();

        return list;
    }

    private Query getQueryForRemoteCntWithOkvedReport(List<String> okvedPaths) throws IOException {
        String  queryString = getQueryString("classpath:reports/remote_cnt/remote_cnt_with_okveds.sql");
        Query query = entityManager.createNativeQuery(queryString, RemoteCntEntityWithOkvedsReport.class);
        query.setParameter("okved_paths", okvedPaths);

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

}
