package com.middleware.org.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatNow() {
        return format(new Date(), DEFAULT_FORMAT);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getCurrentDateTime() {
        return formatNow();
    }
}
