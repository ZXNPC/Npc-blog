package com.example.blognpc.utils;

import java.util.Calendar;

public class CalendarUtils {
    public static String welcome() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 6) {
            return "凌晨好！";
        } else if (hour < 9) {
            return "早上好！";
        } else if (hour < 12) {
            return "上午好！";
        } else if (hour < 14) {
            return "中午好！";
        } else if (hour < 17) {
            return "下午好！";
        } else if (hour < 19) {
            return "傍晚好！";
        } else if (hour < 22) {
            return "晚上好！";
        } else {
            return "夜里好！";
        }
    }

    public static String getCreateDay(Long gmtCreate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(gmtCreate);
        StringBuilder builder = new StringBuilder();
        builder.append(cal.get(Calendar.MONTH) + 1);
        builder.append(cal.get(Calendar.DATE));
        return builder.toString();
    }
}
