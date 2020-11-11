package ru.sibdigital.proccovid.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sibdigital.proccovid.dto.OkvedDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    private static final String EMPTY_STRING = "";

    public static List<OkvedDto> parseFile(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        return readWorkbook(workbook);
    }

    public static List<OkvedDto> parseFile(String path) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(path);
        return readWorkbook(workbook);
    }

    private static List<OkvedDto> readWorkbook(Workbook workbook) {
        List<OkvedDto> list = null;

        Iterator sheetIterator = workbook.sheetIterator();
        list = new ArrayList<>();
        while (sheetIterator.hasNext()) {
            XSSFSheet sheet = (XSSFSheet) sheetIterator.next();
            Iterator rowIterator = sheet.rowIterator();
            OkvedDto okvedDto = null;
            while (rowIterator.hasNext()) {
                XSSFRow row = (XSSFRow) rowIterator.next();
                String cell0 = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : EMPTY_STRING;
                String cell1 = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : EMPTY_STRING;
                if (!cell0.isBlank() && cell0.matches("^[\\d\\d].*")) {
                    okvedDto = new OkvedDto();
                    okvedDto.setCode(cell0);
                    if (!cell1.isBlank()) {
                        okvedDto.setName(cell1);
                    }
                    list.add(okvedDto);
                } else {
                    if (!cell1.isBlank()) {
                        okvedDto.addToDescription(cell1);
                    }
                }
            }
        }

        return list;
    }

    public static void main(String[] args) {
//        List<OkvedDto> list = null;
//        try {
//            list = parseFile("/home/sergey/Projects/Java/okved/db/ОКВЭД по разделам.xlsx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        list.forEach(item -> {
//            if (item.getCode().length() > 8) {
//                System.out.println(item.toString());
//            }
//        });
//        System.out.println(list.size());
    }
}
