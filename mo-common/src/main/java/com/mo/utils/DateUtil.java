package com.mo.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2021/4/29
 */
public class DateUtil {

    /**
     * 格式化当前日期
     * 如yyyyMMdd
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatCurrentDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 时间毫秒数计算
     *
     * @param timeUnit
     * @param duration
     * @return
     */
    public static Long getMillis(TimeUnit timeUnit, int duration) {
        return timeUnit.toMillis(duration);
    }

    /**
     * 根据当前日期格式化
     *
     * @param pattern
     * @return
     */
    public static String getTime(String pattern) {

        //获取当前时间，JDK8时间日期处理类
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String format = formatter.format(now);

        return format;

    }


}
