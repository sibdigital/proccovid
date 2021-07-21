package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.report.RequestSubsidyCntByOkvedsEntityReport;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRequestStatusRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    ClsSubsidyRequestStatusRepo clsSubsidyRequestStatusRepo;

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
            List<RequestSubsidyCntByOkvedsEntityReport> rscboEntities = getRequestSubsidyCntByOkvedsForReport(okveds, minDate, maxDate);

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет о количестве заявок на получение субсидии в разрезе ОКВЭД");
            parameters.put("okvedFilterDesc", okveds.toString().replace("[", "").replace("]", ""));
            parameters.put("okvedPaths", okveds);
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));

            String jrxmlPath = "classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, rscboEntities, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<RequestSubsidyCntByOkvedsEntityReport> getRequestSubsidyCntByOkvedsForReport(List<String> okvedPaths, Date minDate, Date maxDate) throws IOException {
        Set<String> set = okvedPaths.stream().collect(Collectors.toSet());
        if (set.contains("2001")) {
            set.addAll(getAllOkvedPathsByVersion("2001"));
        }
        if (set.contains("2014")) {
            set.addAll(getAllOkvedPathsByVersion("2014"));
        }
        Query query = getQueryForRequestSubsidyCntByOkvedsReport(set, minDate, maxDate);
        List<RequestSubsidyCntByOkvedsEntityReport> list = query.getResultList();

        return list;
    }

    private Query getQueryForRequestSubsidyCntByOkvedsReport(Set<String> okvedPaths, Date minDate, Date maxDate) throws IOException {
        String  queryString = getQueryString("classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds.sql");
        Query query = entityManager.createNativeQuery(queryString, RequestSubsidyCntByOkvedsEntityReport.class);
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

    @Override
    public byte[] exportRequestSubsidiesByOkvedsReportDetail(String reportFormat, Date minDate, Date maxDate, List<String> okveds, UUID okvedId, Long statusId) {
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
            List<RequestSubsidyCntByOkvedsEntityReport> rscboEntities = getRequestSubsidyCntByOkvedsForReportDetails(okveds, minDate, maxDate, okvedId, statusId);
            Okved okved = okvedRepo.findOkvedById(okvedId);
            ClsSubsidyRequestStatus status = clsSubsidyRequestStatusRepo.findById(statusId).orElse(null);

            Map<String, Object> parameters = new HashMap<>();

            JRBeanCollectionDataSource jRBean = new JRBeanCollectionDataSource(rscboEntities);
            parameters.put("DataSource", jRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Отчет о количестве заявок на получение субсидии в разрезе ОКВЭД (детализация)");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("okvedName", okved.getKindCode() + " " + okved.getKindName());
            parameters.put("statusName", status.getShortName());

            String jrxmlPath = "classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds_details.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, rscboEntities, parameters, reportFormat);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<RequestSubsidyCntByOkvedsEntityReport> getRequestSubsidyCntByOkvedsForReportDetails(List<String> okvedPaths, Date minDate, Date maxDate, UUID okvedId, Long statusId) throws IOException {
        Set<String> set = okvedPaths.stream().collect(Collectors.toSet());
        if (set.contains("2001")) {
            set.addAll(getAllOkvedPathsByVersion("2001"));
        }
        if (set.contains("2014")) {
            set.addAll(getAllOkvedPathsByVersion("2014"));
        }
        Query query = getQueryForRequestSubsidyCntByOkvedsReportDetails(set, minDate, maxDate, okvedId, statusId);
        List<RequestSubsidyCntByOkvedsEntityReport> list = query.getResultList();

        return list;
    }

    private Query getQueryForRequestSubsidyCntByOkvedsReportDetails(Set<String> okvedPaths, Date minDate, Date maxDate, UUID okvedId, Long statusId) throws IOException {
        String  queryString = getQueryString("classpath:reports/request_subsidy/request_subsidy_cnt_by_okveds_details.sql");
        Query query = entityManager.createNativeQuery(queryString, RequestSubsidyCntByOkvedsEntityReport.class);
        query.setParameter("okved_paths", okvedPaths);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("id_okved", okvedId);
        query.setParameter("id_status", statusId);

        return query;
    }
}
