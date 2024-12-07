package com.secinfostore.secureinfostore.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeFormatter {
    public static String formatNumTime(Date date) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return localDateTime.format(formatter);
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
        return sdf.format(date);
    }

    public static String formatTime(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return localDateTime.format(formatter);
    }

    public static String getFormattedDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
        return sdf.format(timestamp);
    }
}
