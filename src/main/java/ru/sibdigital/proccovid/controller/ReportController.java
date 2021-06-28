package ru.sibdigital.proccovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.service.reports.RemoteCntReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class ReportController {

    @Autowired
    RemoteCntReportService remoteCntReportService;

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
}
