package ru.sibdigital.proccovid.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    public static String dateToStr(Timestamp date) {
        return dateFormat.format(date);
    }
}
