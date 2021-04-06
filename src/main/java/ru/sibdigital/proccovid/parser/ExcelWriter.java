package ru.sibdigital.proccovid.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sibdigital.proccovid.model.RegOrganizationInspection;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ExcelWriter {

    final String[] SHEET_NAMES = {"Контрольно-надзорные мероприятия"};
    final int SHEET_INDEX = 0;
    final String[] ORG_INSPECTION_COLUMN_NAMES = {"Контрольно-надзорный орган", "Результат проверки", "Дата проверки", "Комментарий"};
    final String ORG_INSPECTION_FILENAME = "organization_inspection.xlsx";

    public void downloadOrgInspectionFile(List<RegOrganizationInspection> regOrganizationInspectionList, HttpServletResponse response) throws IOException {
    // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", ORG_INSPECTION_FILENAME);
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/octet-stream");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAMES[SHEET_INDEX]);

        Row firstRow = sheet.createRow(0);
        firstRow.setRowStyle(getFirstRowStyle(workbook));

        for (int i = 0; i < ORG_INSPECTION_COLUMN_NAMES.length; i++) {
            Cell cell = firstRow.createCell(i);
            cell.setCellValue(ORG_INSPECTION_COLUMN_NAMES[i]);
        }

        int rowCount = 1;
        for (RegOrganizationInspection regOrganizationInspection : regOrganizationInspectionList) {
            createEmployeeRow(sheet, regOrganizationInspection, rowCount++);
        }

        sheet.setDefaultColumnWidth(20);

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }


    private void createEmployeeRow(XSSFSheet sheet, RegOrganizationInspection regOrganizationInspection, int rowCount) {
        Row row = sheet.createRow(rowCount);

        Cell cellControlAuthorityName = row.createCell(0);
        Cell cellInspectionResult = row.createCell(1);
        Cell cellDateOfInspection = row.createCell(2);
        Cell cellComment = row.createCell(3);

        cellControlAuthorityName.setCellValue(regOrganizationInspection.getControlAuthority().getName());
        cellInspectionResult.setCellValue(regOrganizationInspection.getInspectionResult().getName());
        cellDateOfInspection.setCellValue(regOrganizationInspection.getDateOfInspection());
        cellComment.setCellValue(regOrganizationInspection.getComment());
    }

    private XSSFCellStyle getFirstRowStyle(XSSFWorkbook workbook ) {
        XSSFCellStyle style = workbook.createCellStyle();

        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);

        style.setFont(boldFont);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }
}
