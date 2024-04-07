package com.longfor.datav.common.utils;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * 时间工具类
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-30
 */

public class TimeUtil {

    private TimeUtil(){}

    /**
     * 获取当前年份
     * @return year
     */
    public static String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 获取当前年份的第一天
     * @return  local
     */
    public static LocalDateTime getCurrentFirstTimeByYear() {
        LocalDateTime currentDate = LocalDateTime.now();
        return currentDate.withDayOfYear(1);
    }
}
