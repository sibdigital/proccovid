package ru.sibdigital.proccovid.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;
import ru.sibdigital.proccovid.model.report.InspectionEntityReport;
import ru.sibdigital.proccovid.model.RegOrganizationOkved;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegOrganizationInspectionRepo;

import java.io.*;
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
@PropertySource("classpath:reports")
public class InspectionReportServiceImpl implements InspectionReportService {

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    OkvedRepo okvedRepo;

    @Autowired
    JasperReportService jasperReportService;

    @Autowired
    ResourceLoader resourceLoader;

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportInspectionReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                               List<String> mainOkvedPaths, List<String> additionalOkvedPaths, Date defaultMinDate, Date defaultMaxDate,
                                         String prefix) {
        try {
            List<InspectionEntityReport> inspections = getInspectionEntitiesForReport(minDate, maxDate, minCnt, mainOkvedPaths, additionalOkvedPaths);
            Long maxValueLong = (inspections.isEmpty() ? 0 : getMaxValueInspectionsByOrganAndAuthority(inspections));
            Integer maxValue = maxValueLong.intValue();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
//            ResourceBundle bundle = ResourceBundle.getBundle("labels/russia/labels", new Locale("ru", "RU"));
//            parameters.put("REPORT_RESOURCE_BUNDLE", bundle);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("minCnt",  minCnt);
            parameters.put("maxValue", (maxValue == 0 ? 1: maxValue));
            parameters.put("reportTitle", "Отчет по контрольно-надзорным мероприятиям");
            parameters.put("prefix", prefix);

            String hasOkvedFilter = "НЕТ";
            if (mainOkvedPaths != null && !mainOkvedPaths.isEmpty() || additionalOkvedPaths != null && !additionalOkvedPaths.isEmpty()) {
                hasOkvedFilter = "ДА";
            }
            parameters.put("okvedFilterDesc", "Фильтр по ОКВЭД: " + hasOkvedFilter);

            return jasperReportService.exportJasperReport("classpath:reports/inspection/inspection.jrxml",
                    inspections, parameters, reportFormat);

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    @Override
    public byte[] exportInspectionCountReport(String reportFormat, Date minDate, Date maxDate, Integer minCnt,
                                    Long idOrganization, Long idAuthority, Integer typeRecord, Date defaultMinDate, Date defaultMaxDate,
                                              String prefix) {
        try {
            List<InspectionEntityReport> inspections = getInspectionEntitiesForReportCount(minDate, maxDate, minCnt, idOrganization, idAuthority, typeRecord);
            Long maxValueLong = (inspections.isEmpty() ? 0 : getMaxValueInspectionsByOrganAndAuthority(inspections));
            Integer maxValue = maxValueLong.intValue();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));
            parameters.put("prefix", prefix);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("minCnt",  minCnt);
            parameters.put("maxValue", (maxValue == 0 ? 1: maxValue));
            parameters.put("okvedFilterDesc", "");

            String jrxmlPath = null;
            if (typeRecord == 1) {
                jrxmlPath = "classpath:reports/inspection/inspection_T.jrxml";
                parameters.put("reportTitle", "Отчет о количестве проверок по организации");
            } else  {
                jrxmlPath = "classpath:reports/inspection/inspection.jrxml";
                parameters.put("reportTitle", "Отчет о количестве проверок по проверяющему органу");
            }

            return jasperReportService.exportJasperReport(jrxmlPath, inspections, parameters, reportFormat);

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] exportInspectionReportDetail(Date minDate, Date maxDate, Long idOrganization, Long idAuthority,
                                               Date defaultMinDate, Date defaultMaxDate, String prefix) {
        try {
            List<InspectionEntityReport> inspections = getInspectionsForReportDetail(minDate, maxDate, idOrganization, idAuthority);

            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource inspectionJRBean = new JRBeanCollectionDataSource(inspections);
            parameters.put("InspectionDataSource", inspectionJRBean);

            parameters.put("net.sf.jasperreports.print.keep.full.text", true);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

            parameters.put("reportTitle", "Список проверок");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameters.put("minDate", (minDate == defaultMinDate ? "" : dateFormat.format(minDate)));
            parameters.put("maxDate", (maxDate == defaultMaxDate ? "" : dateFormat.format(maxDate)));
            parameters.put("prefix", prefix);

            String jrxmlPath = "classpath:reports/inspection/inspection_details.jrxml";

            return jasperReportService.exportJasperReport(jrxmlPath, inspections, parameters, "html");

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    private List<InspectionEntityReport> getInspectionEntitiesForReport(Date minDate, Date maxDate, Integer minCnt, List<String> mainOkvedPaths, List<String> additionalOkvedPaths) throws IOException {
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

    private List<InspectionEntityReport> getInspectionEntitiesForReportCount(Date minDate, Date maxDate, Integer minCnt,
                                                                            Long idOrganization, Long idAuthority, Integer typeRecord) throws IOException {
        Query query = null;
        List<InspectionEntityReport> list = null;

        if (typeRecord == 1) {
            query = getQueryWithFilterByOrgId(minDate, maxDate, minCnt, Set.of(idOrganization));
        } else {
            query = getQueryWithFilterByAuthorityId(minDate, maxDate, minCnt, Set.of(idAuthority));
        }

        list = query.getResultList();

        return list;
    }


    private List<InspectionEntityReport> getInspectionsForReportDetail(Date minDate, Date maxDate,
                                                                                 Long idOrganization, Long idAuthority) throws IOException {
        Query query = getQueryByOrganizationIdAndAuthorityId(minDate, maxDate, idOrganization, idAuthority);
        List<InspectionEntityReport> list = query.getResultList();

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

        Query query = null;
        Set<Long> orgIds = new HashSet<>();

        if (hasFilterByAdditionalOkveds && hasFilterByMainOkveds) {
            orgIds = new HashSet<>(orgIdsByMain);
            orgIds.retainAll(orgIdsByAdditional); // в orgIds остается пересечение множеств
        } else if (hasFilterByMainOkveds) {
            orgIds = orgIdsByMain;
        } else if (hasFilterByAdditionalOkveds) {
            orgIds = orgIdsByAdditional;
        }

        query = getQueryWithFilterByOrgId(minDate, maxDate, minCnt, orgIds);
        return query;
    }

    private Query getQueryWithFilterByOrgId(Date minDate, Date maxDate, Integer minCnt, Set<Long> orgIds) throws IOException {
        String  queryString = getQueryString("classpath:reports/inspection/inspection_filter_org_ids.sql");
        Query query = entityManager.createNativeQuery(queryString, InspectionEntityReport.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);
        query.setParameter("org_ids", orgIds);

        return query;
    }

    private Query getQueryWithFilterByAuthorityId(Date minDate, Date maxDate, Integer minCnt, Set<Long> authorityIds) throws IOException {
        String  queryString = getQueryString("classpath:reports/inspection/inspection_filter_authority_ids.sql");
        Query query = entityManager.createNativeQuery(queryString, InspectionEntityReport.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("min_cnt", minCnt);
        query.setParameter("authority_ids", authorityIds);

        return query;
    }

    private Query getQueryByOrganizationIdAndAuthorityId(Date minDate, Date maxDate, Long organizationId,  Long authorityId) throws IOException {
        String  queryString = getQueryString("classpath:reports/inspection/inspection_by_organization_and_authority.sql");
        Query query = entityManager.createNativeQuery(queryString, InspectionEntityReport.class);
        query.setParameter("min_date", minDate);
        query.setParameter("max_date", maxDate);
        query.setParameter("org_id", organizationId);
        query.setParameter("auth_id", authorityId);

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
//        File file = ResourceUtils.getFile(path);
//        String query = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        InputStream inputStream = resourceLoader.getResource(path).getInputStream();
        String query = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        return query;
    }

    private Long getMaxValueInspectionsByOrganAndAuthority(List<InspectionEntityReport> rois) {

        if (rois != null && !rois.isEmpty()) {
            Map<Pair<OrganizationShortDto, ControlAuthorityShortDto>, Long> map = rois.stream()
                    .collect(Collectors.groupingBy(ctr -> new Pair(ctr.getOrganization(), ctr.getControlAuthority()), Collectors.counting()));

            Long maxValue = Collections.max(map.values());
            return maxValue;
        } else  {
            return Long.valueOf(1);
        }
    }



}


