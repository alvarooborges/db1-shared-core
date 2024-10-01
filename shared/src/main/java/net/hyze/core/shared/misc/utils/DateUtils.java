package net.hyze.core.shared.misc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat PRECISE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S z");

    public static String toString(Date date) {
        return PRECISE_FORMATTER.format(date);
    }

    public static Date fromString(String input) {
        try {
            return PRECISE_FORMATTER.parse(input);
        } catch (ParseException ex) {
            return null;
        }
    }

}
