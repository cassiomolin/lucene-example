package com.cassiomolin.example.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for date operations.
 *
 * @author cassiomolin
 */
public class DateUtils {

    private DateUtils() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Convert a {@link LocalDate} instance into a {@link Date} instance.
     *
     * @param localDate
     * @return
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a {@link Date} instance into a {@link LocalDate} instance.
     *
     * @param date
     * @return
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
