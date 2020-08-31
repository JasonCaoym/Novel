package com.duoyue.mod.stats;

import com.duoyue.mod.stats.common.StatisticsMgr;

/**
 * 广告模块统计接口类
 * @author caoym
 * @data 2019/3/22  11:27
 */
public class AdStatsApi
{
    /**
     * 拉取广告.
     */
    private static final String PULL = "START";

    /**
     * 拉取广告成功.
     */
    private static final String PULL_SUCC = "PULLED";

    /**
     * 拉取广告失败.
     */
    private static final String PULL_FAIL = "PULLFAIL";

    /**
     * 展示广告成功.
     */
    private static final String SHOW_SUCC = "SHOWED";

    /**
     * 展示广告失败.
     */
    private static final String SHOW_FAIL = "SHOWFAIL";

    /**
     * 点击广告.
     */
    private static final String CLICK = "CLICK";

    /**
     * 拉取广告
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void pull(String adSoltId, int adSite, int adType, int origin)
    {
       StatisticsMgr.addStatsForAd(PULL, adSoltId, adSite, adType, origin);
    }

    /**
     * 拉取广告成功
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void pullSucc(String adSoltId, int adSite, int adType, int origin)
    {
        StatisticsMgr.addStatsForAd(PULL_SUCC, adSoltId, adSite, adType, origin);
    }

    /**
     * 拉取广告失败
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void pullFail(String adSoltId, int adSite, int adType, int origin)
    {
        StatisticsMgr.addStatsForAd(PULL_FAIL, adSoltId, adSite, adType, origin);
    }

    /**
     * 展示广告成功
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void showSucc(String adSoltId, int adSite, int adType, int origin)
    {
        StatisticsMgr.addStatsForAd(SHOW_SUCC, adSoltId, adSite, adType, origin);
    }

    /**
     * 展示广告失败
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void showFail(String adSoltId, int adSite, int adType, int origin)
    {
        StatisticsMgr.addStatsForAd(SHOW_FAIL, adSoltId, adSite, adType, origin);
    }

    /**
     * 点击广告
     * @param adSoltId 广告位Id
     * @param adSite 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     * @param adType 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     * @param origin 广告源(1:广点通2:穿山甲 3:百度)
     */
    public static void click(String adSoltId, int adSite, int adType, int origin)
    {
        StatisticsMgr.addStatsForAd(CLICK, adSoltId, adSite, adType, origin);
    }
}
