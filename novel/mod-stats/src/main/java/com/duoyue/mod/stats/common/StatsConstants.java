package com.duoyue.mod.stats.common;

/**
 * @author caoym
 * @data 2019/3/26  10:22
 */
public class StatsConstants
{
    //============================缓存路径=========================
    /**
     * 活跃用户日期
     */
    public static final String ACTIVE_USER_TIME_PATH = "novel/stats/act_user_time.dat";

    /**
     * 上报功能统计数据时间.
     */
    public static final String UPLOAD_FUNC_STATS_TIME_PATH = "novel/stats/upload_func_stats_time.dat";

    /**
     * 上报广告统计数据时间.
     */
    public static final String UPLOAD_AD_STATS_TIME_PATH = "novel/stats/upload_ad_stats_time.dat";

    //==============================接口================================
    /**
     * 协议版本
     */
    public static final String PROTOCOL_VERSION = "1.0.0";

    /**
     * 功能统计数据上报接口.
     */
    public static final String FUNC_STATS_ACTION = "/app/stats/v1/count";



    //==============================间隔时间===============================
    /**
     * 上报功能统计数据间隔.
     */
    public static final long UPLOAD_FUNC_STATS_DELAY = 5 * 60 * 1000L;

    /**
     * 上报广告统计数据间隔.
     */
    public static final long UPLOAD_AD_STATS_DELAY = 5 * 60 * 1000L;

}
