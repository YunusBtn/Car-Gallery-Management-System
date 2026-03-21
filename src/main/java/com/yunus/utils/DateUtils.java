package com.yunus.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter TCMB_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    private DateUtils() {

    }

    public static String getCurrentyDate() {

        return LocalDate.now().format(TCMB_FORMAT);
    }

    public static String format(LocalDate date) {
        return date.format(TCMB_FORMAT);
    }


}
