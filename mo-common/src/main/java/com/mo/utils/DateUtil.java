package com.mo.utils;

import java.text.SimpleDateFormat;
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


}
