package com.campus.trade.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间显示工具：服务端返回 ISO-8601（如 2026-09-07T13:17:40）
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static Date parse(String iso) {
        if (TextUtils.isEmpty(iso)) {
            return null;
        }
        String s = iso.replace('T', ' ');
        if (s.length() > 19) {
            s = s.substring(0, 19);
        }
        if (s.contains(".")) {
            s = s.substring(0, s.indexOf('.'));
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(s);
        } catch (ParseException ignored) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(s);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    /** 相对时间：刚刚 / n分钟前 / n小时前 / 昨天 / MM-dd / yyyy-MM-dd */
    public static String friendlyTime(String iso) {
        Date date = parse(iso);
        if (date == null) {
            return "";
        }
        long diff = System.currentTimeMillis() - date.getTime();
        long minute = 60 * 1000L;
        if (diff < minute) {
            return "刚刚";
        }
        if (diff < 60 * minute) {
            return (diff / minute) + "分钟前";
        }
        if (diff < 24 * 60 * minute) {
            return (diff / (60 * minute)) + "小时前";
        }
        Date now = new Date();
        if (isSameDay(now, date)) {
            return "今天 " + fmt(date, "HH:mm");
        }
        Date yesterday = new Date(now.getTime() - 24 * 60 * minute);
        if (isSameDay(yesterday, date)) {
            return "昨天 " + fmt(date, "HH:mm");
        }
        String y = fmt(date, "yyyy");
        if (y.equals(fmt(now, "yyyy"))) {
            return fmt(date, "MM-dd HH:mm");
        }
        return fmt(date, "yyyy-MM-dd");
    }

    public static String format(String iso, String pattern) {
        Date date = parse(iso);
        return date == null ? "" : fmt(date, pattern);
    }

    private static String fmt(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.US).format(date);
    }

    private static boolean isSameDay(Date a, Date b) {
        java.util.Calendar ca = java.util.Calendar.getInstance();
        java.util.Calendar cb = java.util.Calendar.getInstance();
        ca.setTime(a);
        cb.setTime(b);
        return ca.get(java.util.Calendar.YEAR) == cb.get(java.util.Calendar.YEAR)
                && ca.get(java.util.Calendar.DAY_OF_YEAR) == cb.get(java.util.Calendar.DAY_OF_YEAR);
    }
}
