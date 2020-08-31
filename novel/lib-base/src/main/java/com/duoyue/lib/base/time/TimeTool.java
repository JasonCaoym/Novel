package com.duoyue.lib.base.time;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author caoym
 * @data 2019/3/25  22:26
 */
public class TimeTool
{
    //=====================日期格式====================
    /**
     * 完整日期格式01
     */
    public static final String DATE_FORMAT_FULL_01 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 完整日期格式02
     */
    public static final String DATE_FORMAT_FULL_02 = "yyyy-MM-dd HH:mm";

    /**
     * 简洁日期格式01
     */
    public static final String DATE_FORMAT_SMALL_01 = "yyyy-MM-dd";

    /**
     * 简洁日期格式02
     */
    public static final String DATE_FORMAT_SMALL_02 = "yyyyMMdd";

    /**
     * 简洁日期格式03
     */
    public static final String DATE_FORMAT_SMALL_03 = "yyyyMM";

    /**
     * 获取当前小时(24小时制)
     *
     * @return
     */
    public static int getCurrentHour()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTimeMillis()));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前系统日期
     *
     * @param dateFormat 日期字符串格式
     * @return
     */
    public static String getCurrentDate(String dateFormat)
    {
        if (TextUtils.isEmpty(dateFormat))
        {
            dateFormat = DATE_FORMAT_FULL_01;
        }
        String date = new SimpleDateFormat(dateFormat, Locale.CHINA).format(new Date(currentTimeMillis()));
        return date;
    }

    /**
     * 获取当前时间(毫秒数)
     * @return
     */
    public static long currentTimeMillis()
    {
        return System.currentTimeMillis();
    }

    /**
     * 获取当日开始时间(毫秒数).
     * @return
     */
    public static long getCurrDayBeginTime()
    {
        Calendar todayBegin = Calendar.getInstance();
        todayBegin.setTime(new Date(currentTimeMillis()));
        todayBegin.set(Calendar.HOUR_OF_DAY, 0);
        todayBegin.set(Calendar.MINUTE, 0);
        todayBegin.set(Calendar.SECOND, 0);
        todayBegin.set(Calendar.MILLISECOND, 0);
        return todayBegin.getTimeInMillis();
    }

    /**
     * 获取当日结束时间(毫秒数).
     * @return
     */
    public static long getCurrDayEndTime()
    {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(new Date(currentTimeMillis()));
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis();
    }

    /**
     * 将毫秒数转换为指定日期格式
     *
     * @param time       毫秒数
     * @param dataFormat 日期格式
     * @return
     */
    public static String timeToData(long time, String dataFormat)
    {
        if (time <= 0)
        {
            return "";
        }
        if (TextUtils.isEmpty(dataFormat))
        {
            dataFormat = DATE_FORMAT_FULL_01;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
        return sdf.format(new Date(time));
    }

    /**
     * 判断是否超过指定时间.
     *
     * @param lastTime 最近一次执行时间(毫秒数).
     * @param duration 有效时长(毫秒数).
     * @return
     */
    public static boolean isTimeOut(long lastTime, long duration)
    {
        if (lastTime <= 0 || duration <= 0 || Math.abs(currentTimeMillis() - lastTime) >= duration)
        {
            return true;
        }
        return false;
    }
}
