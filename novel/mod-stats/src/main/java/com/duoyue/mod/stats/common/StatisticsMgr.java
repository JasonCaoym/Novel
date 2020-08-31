package com.duoyue.mod.stats.common;

import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.data.helper.FunctionStatsHelper;
import org.json.JSONObject;

/**
 * 统计管理类对象
 * @author caoym
 * @data 2019/3/23  14:13
 */
public class StatisticsMgr
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#StatisticsMgr";

    /**
     * 当前类对象.
     */
    private static volatile StatisticsMgr sInstance;

    /**
     * 构造方法
     */
    private StatisticsMgr()
    {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (StatisticsMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new StatisticsMgr();
                }
            }
        }
    }

    /**
     * 添加广告统计数据.
     * @param nodeName 节点名称.
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void addStatsForAd(String nodeName, String adSoltId, int adSite, int adType, int origin)
    {
        /*Logger.i(TAG, "addStatsForAd: {}, AdSoltId:{}, AdSite:{}, AdType:{}, Origin:{}", nodeName, adSoltId, adSite, adType, origin);
        //创建统计节点数据对象.
        AdStatsEntity statsEntity = new AdStatsEntity();
        //广告位Id.
        statsEntity.setAdSoltId(adSoltId);
        //广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
        statsEntity.setAdSite(adSite);
        //广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
        statsEntity.setAdType(adType);
        //广告源(1:广点通2:穿山甲 3:百度)
        statsEntity.setOrigin(origin);
        //设置节点名称.
        statsEntity.setNodeName(nodeName);
        //保存时间.
        statsEntity.setSaveTime(System.currentTimeMillis());
        //保存到数据库.
        AdStatsHelper.getInstance().saveAdStatsInfo(statsEntity, false);*/
    }

    /**
     * 添加功能统计数据.
     * @param nodeName 节点名称
     */
    public static void addStatsForFunc(String nodeName)
    {
        addStatsForFunc(nodeName, 0);
    }

    /**
     * 添加功能统计数据.
     * @param nodeName 节点名称
     */
    public static void addStatsForFunc(String nodeName, long bookId)
    {
        /*Logger.i(TAG, "addStatsForFunc: {}, {}", nodeName, bookId);
        //创建统计节点数据对象.
        FunctionStatsEntity statsEntity = new FunctionStatsEntity();
        //设置节点名称.
        statsEntity.setNodeName(nodeName);
        //设置书籍Id.
        statsEntity.setBookId(bookId);
        //设置当前日期.
        statsEntity.setNodeDate(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01));
        //保存时间.
        statsEntity.setSaveTime(TimeTool.currentTimeMillis());
        //保存到数据库.
        FunctionStatsHelper.getInstance().saveFuncStatsInfo(statsEntity, false);*/
    }

    /**
     * 添加功能统计数据.
     * @param nodeName 节点名称
     * @param paramJSONObj 参数集合
     * @param bookId 书籍Id
     *
     */
    public static void addStatsForFunc(String nodeName, JSONObject paramJSONObj, long bookId)
    {
        addStatsForFunc(nodeName, paramJSONObj, bookId, "");
    }

    /**
     * 添加功能统计数据.
     * @param nodeName 节点名称
     * @param paramJSONObj 参数集合
     * @param bookId 书籍Id
     * @param bookName 书籍名称
     */
    public static void addStatsForFunc(String nodeName, JSONObject paramJSONObj, long bookId, String bookName)
    {
        /*Logger.i(TAG, "addStatsForFunc: {}, {}, {}, {}", nodeName, paramJSONObj, bookId, bookName);
        //创建统计节点数据对象.
        FunctionStatsEntity statsEntity = new FunctionStatsEntity();
        //设置节点名称.
        statsEntity.setNodeName(nodeName);
        //设置书籍Id.
        statsEntity.setBookId(bookId);
        //设置扩展信息.
        if (paramJSONObj != null)
        {
            statsEntity.setExtInfo(paramJSONObj.toString());
        }
        //设置当前日期.
        statsEntity.setNodeDate(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01));
        //保存时间.
        statsEntity.setSaveTime(TimeTool.currentTimeMillis());
        //保存到数据库.
        FunctionStatsHelper.getInstance().saveFuncStatsInfo(statsEntity, false);*/
    }

    /**
     * 创建Target扩展参数(categoryId)
     * @param paramJSONObj
     * @param target
     * @return
     */
    public static JSONObject createParamTarget(JSONObject paramJSONObj, String target)
    {
        if (!StringFormat.isEmpty(target))
        {
            try
            {
                if (paramJSONObj == null)
                {
                    paramJSONObj = new JSONObject();
                }
                paramJSONObj.put("TARGET", target);
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "createParamTarget: {}", throwable);
            }
        }
        return paramJSONObj;
    }

    /**
     * 创建Target扩展参数(阅读器停留时长)
     * @param paramJSONObj
     * @param time
     * @return
     */
    public static JSONObject createParamTime(JSONObject paramJSONObj, long time)
    {
        if (time > 0)
        {
            try
            {
                if (paramJSONObj == null)
                {
                    paramJSONObj = new JSONObject();
                }
                paramJSONObj.put("TIME", time);
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "createParamTime: {}", throwable);
            }
        }
        return paramJSONObj;
    }

    /**
     * 读取当日未上报阅读时长.
     * @return
     */
    public static int getCurrDayReadingTime()
    {
        return FunctionStatsHelper.getInstance().findCurrDayReadingTime();
    }

    /**
     * 读取所有未上报阅读时长.
     * @return
     */
    public static int getTotalReadingTime()
    {
        return FunctionStatsHelper.getInstance().findTotalReadingTime();
    }
}
