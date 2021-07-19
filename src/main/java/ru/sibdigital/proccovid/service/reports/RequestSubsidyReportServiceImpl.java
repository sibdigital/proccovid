package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.Okved;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestSubsidyReportServiceImpl implements RequestSubsidyReportService {

    @Autowired
    JasperReportService jasperReportService;

    @Autowired
    OkvedRepo okvedRepo;

    @Autowired
    ResourceLoader resourceLoader;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public byte[] exportRequestSubsidiesByOkvedsReport(String reportFormat, Date minDate, Date maxDate, List<String> okveds) {
        try {
            Date defaultMinDate = new Date(Long.valueOf("943891200000")); // 2000 год
            Date defaultMaxDate = new Date(Long.valueOf("4099651200000")); // 2100 год

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (minDate == null) {
                minDate = defaultMinDate;
            }
            if (maxDate == null) {
                maxDate = defaultMaxDate;
            }
            List<RemoteCntEntityWithOkvedsReport> remoteCntEntities = getRequestSubsidyCntByOkvedsForReport(okveds, minDate, maxDate);

            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource remoteCntJRBean = new JRBeanCollectionDataSource(remoteCntEntities);
            parameters.put("RemoteCntDataSource", remoteCntJRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет о количестве субсидий в разрезе ОКВЭД");
            parameters.put("okvedPaths", okveds.toString());
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));

            String jrxmlPath = "classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, remoteCntEntities, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<RemoteCntEntityWithOkvedsReport> getRequestSubsidyCntByOkvedsForReport(List<String> okvedPaths, Date minDate, Date maxDate) throws IOException {
        Set<String> set = okvedPaths.stream().collect(Collectors.toSet());
        if (set.contains("2001")) {
            set.addAll(getAllOkvedPathsByVersion("2001"));
        }
        if (set.contains("2014")) {
            set.addAll(getAllOkvedPathsByVersion("2014"));
        }
        Query query = getQueryForRequestSubsidyCntByOkvedsReport(set, minDate, maxDate);
        List<RemoteCntEntityWithOkvedsReport> list = query.getResultList();

        return list;
    }


    private Query getQueryForRequestSubsidyCntByOkvedsReport(Set<String> okvedPaths, Date minDate, Date maxDate) throws IOException {
        String  queryString = getQueryString("classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds.sql");
        Query query = entityManager.createNativeQuery(queryString, RemoteCntEntityWithOkvedsReport.class);
        query.setParameter("okved_paths", okvedPaths);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);


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

    private Set<String> getAllOkvedPathsByVersion(String version) {
        List<Okved> okveds = okvedRepo.findAllByVersion(version);
        Set<String> okvedPaths = okveds.stream().map(ctr -> ctr.getPath()).collect(Collectors.toSet());
        return okvedPaths;
    }
}
