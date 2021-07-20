package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.OrganizationTypes;
import ru.sibdigital.proccovid.model.ReviewStatuses;
import ru.sibdigital.proccovid.service.reports.RemoteCntReportService;
import ru.sibdigital.proccovid.service.reports.RequestSubsidyReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ReportController {

    @Autowired
    RemoteCntReportService remoteCntReportService;

    @Autowired
    RequestSubsidyReportService requestSubsidyReportService;

    @RequestMapping(
            value = {"/generate_remote_cnt_report","/outer/generate_remote_cnt_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody
    String generateRemoteCntReport(@RequestParam(value = "reportDate") String reportDateString) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date reportDate = dateFormat.parse(reportDateString);


        byte[] bytes = remoteCntReportService.exportRemoteCntReport("html", reportDate);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/remoteCntReport/{format}/params","/outer/remoteCntReport/{format}/params"}
    )
    public String downloadRemoteCntReport(@PathVariable String format,
                                 @RequestParam(value = "reportDate") String reportDateString,
                                 HttpServletResponse response) throws IOException, ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date reportDate = dateFormat.parse(reportDateString);

        byte[] bytes = remoteCntReportService.exportRemoteCntReport(format, reportDate);

        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=remoteCount.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }

    @RequestMapping(
            value = {"/generate_cnt_remote_with_okveds_report","/outer/generate_cnt_remote_with_okveds_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateCntRemoteWithOkvedsReport(@RequestParam(value = "okvedPaths") List<String> okvedPaths) throws ParseException {
        byte[] bytes = remoteCntReportService.exportRemoteCntWithOkvedFilterReport("html", okvedPaths);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/remoteCntReportByOkveds/{format}/params","/outer/remoteCntReportByOkveds/{format}/params"}
    )
    public String downloadRemoteCntReportByOkveds(@PathVariable String format,
                                          @RequestParam(value = "okvedPaths") List<String> okvedPaths,
                                          HttpServletResponse response) throws IOException, ParseException {

        byte[] bytes = remoteCntReportService.exportRemoteCntWithOkvedFilterReport(format, okvedPaths);

        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=remoteCount.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }

    @RequestMapping(
            value = {"/generate_org_count_by_okved_report","/outer/generate_org_count_by_okved_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateOrgCountByOkvedReport(@RequestParam(value = "okvedPaths") List<String> okvedPaths,
                                                              @RequestParam(value = "requestTimeCreate") String timeCreateString,
                                                              @RequestParam(value = "statusValue") Long statusValue) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeCreate = dateFormat.parse(timeCreateString);
        byte[] bytes = remoteCntReportService.exportOrgCountByOkvedReport("html", okvedPaths, timeCreate, statusValue);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/org_count_by_okved_report/{format}/params","/outer/org_count_by_okved_report/{format}/params"}
    )
    public String downloadOrgCountByOkvedReport(@PathVariable String format,
                                                @RequestParam(value = "okvedPaths") List<String> okvedPaths,
                                                @RequestParam(value = "requestTimeCreate") String timeCreateString,
                                                @RequestParam(value = "statusValue") Long statusValue,
                                                  HttpServletResponse response) throws IOException, ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date timeCreate = dateFormat.parse(timeCreateString);
        byte[] bytes = remoteCntReportService.exportOrgCountByOkvedReport(format, okvedPaths, timeCreate, statusValue);

        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=organizationCountByOkveds.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }

    @GetMapping("/review_statuses")
    public @ResponseBody List<KeyValue> getRequestReviewStatuses() {
        Map<Long, String> statusMap = remoteCntReportService.getReviewStatusMap();
        List<KeyValue> list = new ArrayList<>();

        for(Map.Entry<Long, String> item : statusMap.entrySet()){
            list.add(new KeyValue("ReviewStatuses", item.getKey(), item.getValue()));
        }

        return list;
    }



    @RequestMapping(
            value = {"/generate_request_subsidy_cnt_by_okveds_report","/outer/generate_request_subsidy_cnt_by_okveds_report"},
            method = RequestMethod.GET
    )
    public @ResponseBody String generateRequestSubsidyCntByOkvedsReport(@RequestParam(value = "okvedPaths") List<String> okvedPaths,
                                                              @RequestParam(value = "startDateReport") String startDateSendString,
                                                              @RequestParam(value = "endDateReport") String endDateSendString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDateSend = dateFormat.parse(startDateSendString);
        Date endDateSend = dateFormat.parse(endDateSendString);
        byte[] bytes = requestSubsidyReportService.exportRequestSubsidiesByOkvedsReport("html", startDateSend, endDateSend, okvedPaths);
        String template = new String(bytes);
        return template;
    }

    @RequestMapping(
            value = {"/request_subsidy_cnt_by_okveds/{format}/params","/outer/request_subsidy_cnt_by_okveds/{format}/params"}
    )
    public String downloadRequestSubsidyCntByOkveds(@PathVariable String format,
                                                @RequestParam(value = "okvedPaths") List<String> okvedPaths,
                                                @RequestParam(value = "startDateReport") String startDateSendString,
                                                @RequestParam(value = "endDateReport") String endDateSendString,
                                                HttpServletResponse response) throws IOException, ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date startDateSend = dateFormat.parse(startDateSendString);
        Date endDateSend = dateFormat.parse(endDateSendString);
        byte[] bytes = requestSubsidyReportService.exportRequestSubsidiesByOkvedsReport(format,  startDateSend, endDateSend, okvedPaths);

        if (format.equals("pdf")) {
            response.setContentType("application/pdf");
        } else if (format.equals("html")) {
            response.setContentType("text/html");
        } else if (format.equals("xlsx")){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=organizationCountByOkveds.xlsx");
        }

        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
        out.close();
        return null;
    }
}
