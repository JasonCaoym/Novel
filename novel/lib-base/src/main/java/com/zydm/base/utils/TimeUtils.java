package com.zydm.base.utils;

import android.os.SystemClock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtils {

    private static final int DAY_TIME_BEGIN = 6;

    private static final int NIGHT_TIME_BEGIN = 18;

    public static final int SECOND_1 = 1000;

    public static final int MINUTE_1 = SECOND_1 * 60;

    public static final int HOUR_1 = MINUTE_1 * 60;

    public static final long DAY_1 = HOUR_1 * 24;

    public static final long YEAR_1 = DAY_1 * 365;

    private static final SimpleDateFormat M_FORMAT = new SimpleDateFormat("M", Locale.CHINA);

    public static final SimpleDateFormat Y_FORMAT = new SimpleDateFormat("yyyy", Locale.CHINA);

    public static final SimpleDateFormat YM_CHARACTER_FORMAT = new SimpleDateFormat("yyyy年M月", Locale.CHINA);

    private static final SimpleDateFormat YMD_CHARACTER_FORMAT = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

    private static final SimpleDateFormat MD_FORMAT = new SimpleDateFormat("MM-dd", Locale.CHINA);

    private static final SimpleDateFormat YMD_NUM_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

    public static final SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private static final SimpleDateFormat YMD_POINT_FORMAT = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);

    private static final SimpleDateFormat YMDHM_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private static final SimpleDateFormat MD_DOT_FORMAT = new SimpleDateFormat("MM.dd", Locale.CHINA);

    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("EEE", Locale.CHINA);

    public static final SimpleDateFormat MD_FORMAT_X = new SimpleDateFormat("HH:mm", Locale.CHINA);

    public static boolean isTheSameDay(long earlier, long later) {

        return Math.abs(later - earlier) < DAY_1;
    }

    public static boolean isDaytime() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= DAY_TIME_BEGIN && hour < NIGHT_TIME_BEGIN;
    }

    public static synchronized String formatDotData(long timeStamp) {

        Date date = new Date(timeStamp);
        return MD_DOT_FORMAT.format(date);
    }

    public static synchronized String formatDate(long timeStamp) {

        Date date = new Date(timeStamp * SECOND_1);
        return YMD_FORMAT.format(date);
    }

    public static synchronized String formatDateStylePoint(long timeStampSeconds) {

        Date date = new Date(timeStampSeconds * SECOND_1);
        return YMD_POINT_FORMAT.format(date);
    }

    public static synchronized String formatDateStyleCharacter(String timeStamp) {
        long time = parseTime(timeStamp);
        Date date = new Date(time * SECOND_1);
        return YMD_CHARACTER_FORMAT.format(date);
    }

    public static long parseTime(String timeStamp) {
        if (null == timeStamp) {
            return 0;
        } else {
            try {
                return Long.parseLong(timeStamp);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public static String formatDateTime(long timeStamp) {

        Date date = new Date(timeStamp);
        return formatDateTime(date);
    }

    public static synchronized String formatDateTime(Date date) {

        return YMDHM_FORMAT.format(date);
    }

    public static String formatDateTime(String format, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(time);
    }

    public static long getMillis(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getMillis(int year, int month, int day, int hour) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static synchronized String formatDateWeek(long timeStamp) {

        Date date = new Date(timeStamp);
        return WEEK_FORMAT.format(date);
    }

    /*获取星期几*/
    public static String getWeekCharacter(int dayNum) {
        switch (dayNum) {
            case 1:
                return "日";
            case 2:
                return "一";
            case 3:
                return "二";
            case 4:
                return "三";
            case 5:
                return "四";
            case 6:
                return "五";
            case 7:
                return "六";
            default:
                return "日";
        }
    }

    public static String getTodayWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        return getWeekCharacter(i);
    }

    public static int getMonth(String unixTime) {
        long time = parseTime(unixTime);
        long millisTimeStamp = time * SECOND_1;

        return Integer.parseInt(M_FORMAT.format(new Date(millisTimeStamp)));
    }

    public static int getCurYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public static int getCurDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static long getGapSecond(long oldMillis) {
        return (getCurTime() - oldMillis) / 1000;
    }

    public static long getGapDay(long endTimes) {
        long curMillis = System.currentTimeMillis();
        long gap = (endTimes * 1000 - curMillis) / (1000 * 60 * 60 * 24);
        if (gap < 0) {
            return 0;
        }
        return gap;
    }

    public static long getCurTime() {
        return System.currentTimeMillis();
    }

    public static int getSysCurMonth() {

        return Integer.parseInt(M_FORMAT.format(new Date(System.currentTimeMillis())));
    }

    /**
     * 过去几天的日期
     *
     * @param intervals
     * @return
     */
    public static ArrayList<Integer> getPastDateList(int intervals) {
        ArrayList<Integer> pastDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            pastDaysList.add(getPastDate(i));
        }
        Collections.reverse(pastDaysList);
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static int getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        String result = YMD_NUM_FORMAT.format(today);
        return Integer.parseInt(result);
    }

    public static boolean isPastDay(long oldTime) {
        int curTimeNum = getYMDNum(System.currentTimeMillis());
        int oldTimeNum = getYMDNum(oldTime);
        return curTimeNum > oldTimeNum;
    }

    public static boolean isPassDesignatedDay(long oldTime, int days) {
        int curTimeNum = getYMDNum(System.currentTimeMillis());
        int oldTimeNum = getYMDNum(oldTime);
        LogUtils.d("today", "------ curTime = " + curTimeNum + ", oldTime = " + oldTime);
        return curTimeNum - oldTimeNum >= days;
    }

    public static int dayPass(long oldTime) {
        int curTimeNum = getYMDNum(System.currentTimeMillis());
        int oldTimeNum = getYMDNum(oldTime);
        LogUtils.d("today", "------ curTime = " + curTimeNum + ", oldTime = " + oldTime);
        return curTimeNum - oldTimeNum;
    }

    /**
     * 是否过了指定的时间
     *
     * @param oldTime   离现在最近的旧时间点
     * @param timeStamp 指定的时间间隔
     * @return
     */
    public static boolean isPastDesignatedTime(long oldTime, long timeStamp) {
        return System.currentTimeMillis() - oldTime > timeStamp;
    }

    public static String preDay() {
        return YMD_NUM_FORMAT.format(new Date(System.currentTimeMillis() - DAY_1));
    }

    public static String today() {
        return YMD_NUM_FORMAT.format(new Date());
    }

    public static int getYMDNum(long time) {
        return Integer.parseInt(YMD_NUM_FORMAT.format(new Date(time)));
    }

    public static boolean isCurrTimeBelongRegion(String startTime, String endTime) {

        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            return false;
        } else {
            long curTimeNum = System.currentTimeMillis();
            long startTimeNum = parseTime(startTime) * SECOND_1;
            long endTimeNum = parseTime(endTime) * SECOND_1;

            return curTimeNum >= startTimeNum && curTimeNum <= endTimeNum;
        }
    }

    public static long[] getGapTime(long mss) {

        long days = mss / (1000 * 60 * 60 * 24);
        long hours = mss / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        long[] longs = new long[]{days, hours, minutes, seconds};

        return longs;
    }

    public static int getGapTimeDayNum(String startTime, String endTime) {
        long startMills = parseTime(startTime) * SECOND_1;
        long endMills = parseTime(endTime) * SECOND_1;

        Date startDate = new Date(startMills);
        Date endDate = new Date(endMills);
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));

        return days;
    }

    public static int getGapDayNum(String startTime, String endTime) {
        long start = parseTime(startTime);
        long end = parseTime(endTime);
        Date date1 = new Date(start);
        Date date2 = new Date(end);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else {
            return day2 - day1;
        }
    }

    public static long getElapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getTodayEnd() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTimeInMillis();
    }

    public static long[] getHMS(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return new long[]{calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)};
    }

    public static int[] getDayAndHour(long endTime) {
        long stamp = endTime * 1000 - System.currentTimeMillis();
        if (stamp <= 0) {
            return null;
        }
        int[] times = new int[2];
        times[0] = (int) (stamp / (1000 * 60 * 60 * 24));
        times[1] = (int) Math.ceil((double) stamp / (1000 * 60 * 60) - times[0] * 24);
        return times;
    }

    public static int getDifferentDay(String time) {
        Date date = new Date(parseTime(time) * 1000);
        Date date2 = new Date(getCurTime());
        int day = (int) ((date2.getTime() - date.getTime()) / (24 * 60 * 60 * 1000));
        return day;
    }

    /**
     * 获得指定时间明天凌晨零点的时间毫秒值
     *
     * @return
     */
    public static long getTomorowMilinSeconds(long milins) {
        long zero = milins / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long twelve = zero + 24 * 60 * 60 * 1000;
        return twelve;
    }

    /**
     * <li>功能描述：时间相减得到天数
     *
     * @param beginDateStr
     * @param endDateStr
     * @return long
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        Date beginDate;
        Date endDate;
        try {
            beginDate = YMD_FORMAT.parse(beginDateStr);
            endDate = YMD_FORMAT.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }
}

