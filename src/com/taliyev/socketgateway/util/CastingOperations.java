package com.taliyev.socketgateway.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CastingOperations {

    public static final String MYSQL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat sdf = new SimpleDateFormat(MYSQL_DATE_TIME_FORMAT);

    public static Integer parseInt(String v) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return null;
        }
    }

    // Convert java.util.Date to java.sql.Date
    public static java.sql.Date convertToSqlDate(Date startTime) {
        if (startTime == null) {
            return null;
        }
        return new java.sql.Date(startTime.getTime());
    }

    // Convert date to MySQL date
    public static String convertToMySQLDate(Date date) {
        return date == null ? null : sdf.format(date);
    }

}
