package com.duoyue.mod.stats;

import android.app.Application;
import com.zhangyue.event.ZyEventApi;

/**
 * 掌阅统计
 * @author caoym
 * @data 2019/6/27  17:04
 */
public class ZyStatsApi
{
    /**
     * 日志Tag
     */
    //private static final String TAG = "Stats#ZyStatsApi";

    /**
     * 初始化.
     * @param application
     */
    public static void init(Application application)
    {
        //Logger.i(TAG, "init: {}", application);
        ZyEventApi.init(application);
    }
}
