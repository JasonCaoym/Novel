package com.duoyue.mod.ad.utils;

public class AdConstants {
    public static final String PREFERENCE_NAME = "ad_preference";
    public static final String KEY_INTERVAL = "ad_config_interval";
    public static final String KEY_FREE_START_TIME = "ad_free_start_time";

    public static final String KEY_TODAY = "ad_today";
    public static final String KEY_AD_PARAMS = "ad_params";

    public static final String CURR_FREE_TIME = "current_free_time";

    /**
     * 1:开屏2:精选列表3:完结列表4:新书列表5:排行榜6:书籍详情7:分类列表8:书架9:阅读器章节末尾插屏
     */
    public static class Position {
        /**
         * 开屏
         */
        public static final int LAUNCHING = 1;
        /**
         * 精选列表
         */
        public static final int CHOICENESS = 2;
        /**
         * 完结列表
         */
        public static final int BOOK_FINISH = 3;
        /**
         * 新书列表
         */
        public static final int BOOK_NEWS = 4;
        /**
         * 排行榜
         */
        public static final int BOOK_RANK = 5;
        /**
         * 书籍详情
         */
        public static final int BOOK_DETAIL = 6;
        /**
         * 书分类列表
         */
        public static final int BOOK_CAEGORY = 7;
        /**
         * 书架
         */
        public static final int BOOKSHELF = 8;
        /**
         * 阅读器章节末尾
         */
        public static final int CHAPTER = 9;
        /**
         * 目录
         */
        public static final int CATALOGUE = 10;
        /**
         * 阅读几页后的插屏
         */
        public static final int CHAPTER_INTERACTION = 11;
        /**
         * 阅读器激励视频
         */
        public static final int REWARD_VIDEO = 12;
        /**
         * 任务激励视频
         */
        public static final int TASK_VIDEO = 13;
        /**
         * 签到激励视频
         */
        public static final int SIGNIN_VIDEO = 14;
    }

    public static class Source {
        /**
         * 广点通
         */
        public static final int GDT = 1;
        /**
         * 穿山甲
         */
        public static final int CSJ = 2;
        /**
         * 百度
         */
        public static final int BD = 3;

        /**
         * URL链接
         */
        public static final int URL = 4;
    }

    public static class Type {
        /**
         * 开屏
         */
        public static final int LAUNCHING = 1;
        /**
         * 横幅
         */
        public static final int BANNER = 2;
        /**
         * 插屏
         */
        public static final int INTERACTION = 3;
        /**
         * 信息流
         */
        public static final int INFORMATION_FLOW = 4;
        /**
         * 视频
         */
        public static final int VIDEO = 5;
    }

    /**
     * 渲染类型(1:模板;2:自渲染)
     */
    public static class RenderType
    {
        /**
         * 模版渲染.
         */
        public static final int TEMPLATE = 1;
    }

    public static class ReadParams {
        /**
         * 阅读器插屏广告间隔最小页码（首次）
         */
        public static final String PAGE_MIN_FIRST = "RD_PAGE_MIN1";
        /**
         * 阅读器插屏广告间隔最大页码（首次）
         */
        public static final String PAGE_MAX_FIRST = "RD_PAGE_MAX1";
        /**
         * 阅读器插屏广告间隔最小页码
         */
        public static final String PAGE_MIN_NOR = "RD_PAGE_MIN2";
        /**
         * 阅读器插屏广告间隔最大页码
         */
        public static final String PAGE_MAX_NOR = "RD_PAGE_MAX2";
        /**
         * 阅读器视频广告免广告时长（分钟）
         */
        public static final String FLOW_FREE_DURATION = "RD_ADFREE_TIME";
        /**
         * H5 白名单引导页链接
         */
        public static final String PAGE_URL_VIVO = "H5_WLIST_VIVO";
        public static final String PAGE_URL_OPPO = "H5_WLIST_OPPO";
        public static final String PAGE_URL_XIAOMI = "H5_WLIST_XIAOMI";
        public static final String PAGE_URL_HUAWEI = "H5_WLIST_HUAWEI";
        public static final String PAGE_URL_MEIZU = "H5_WLIST_MEIZU";
        public static final String PAGE_URL_GIONEE = "H5_WLIST_GIONEE";

        /**
         * 后台启动次数间隔阀值（分钟）
         */
        public static final String PAGE_STTCNB_TIME = "STTCNB_TIME";

        /**
         * 犹豫用户随机推书判断时间
         */
        public static final String RANDOM_PUSH_TIME = "HRMD_TIME";

        /**
         * 犹豫用户书城随机推书判断时间
         */
        public static final String RANDOM_PUSH_TIME_BOOK_CITY = "HESITATE_POP_TIME_BOOKSTORE";

        /**
         * 犹豫用户搜索随机推书判断时间
         */
        public static final String RANDOM_PUSH_TIME_SEARCH = "HESITATE_POP_TIME_SEARCH";

        /**
         * 自动加入书架的时间
         */
        public static final String AUTOADD_TIME = "AUTOADD_TIME";

        /**
         * H5书豆地址
         */
        public static final String H5_BOOK_BEANS = "H5_bookBean";

        /**
         * H5任务中心地址
         */
        public static final String H5_TASKCENTER = "H5_taskCenter";

        /**
         * 书单详情页
         */
        public static final String H5_BOOKLISTDETAIL = "H5_BOOKLIST_DETAIL";

        /**
         * 书单列表页
         */
        public static final String H5_BOOKLIST = "H5_BOOKLIST";

        /**
         * 向上预加载章节数
         */
        public static final String RD_PRELOAD_BACK = "RD_PRELOAD_BACK";

        /**
         * 向下预加载章节数
         */
        public static final String RD_PRELOAD = "RD_PRELOAD";

        public static final String RD_BANNER_FREE = "RD_BANNER_FREE";

        /**
         * 0关闭；1开启；后台目前还没做功能，设置为0
         */
        public static final String NOTIFY_ENABLE = "NOTIFY_ENABLE";
        /**
         * 书城轮播图是否显示  0 显示  1  隐藏
         */
        public static final String BANNER_SHOW = "BANNER_SHOW";

        /**
         * 疲劳弹框提醒，累计看书N分钟后弹框
         */
        public static final String RD_TIRED_TIME = "RD_TIRED_TIME";

        /**
         * 疲劳提醒激励视频免广告时间（分钟）
         */
        public static final String RD_TIRED_FREE_TIME = "RD_TIRED_FREE_TIME";

        public static final String AD_COMM_CLICK_INTERVAL = "AD_COMM_CLICK_INTERVAL";
        public static final String NOTIFICATION_BAR_REFRESH_INTERVAL = "NOTIFICATION_BAR_REFRESH_INTERVAL";
        public static final String NOTIFICATION_BAR_READING_INTERVAL = "NOTIFICATION_BAR_READING_INTERVAL";

        /**
         * 阅读器横幅广告重新请求间隔（分钟）
         */
        public static final String BANNER_INTERVAL = "BANNER_INTERVAL";

        /**
         * 阅读器横幅广告N次请求后，清除广告缓存
         */
        public static final String RD_BANNER_CLEAR = "RD_BANNER_CLEAR";

        /**
         * 发现-推荐小说
         */
        public static final String FIND_RECOM = "FIND_RECOM";

        /**
         * 用户协议地址.
         */
        public static final String USER_AGREEMENT = "XY";
    }

    /**
     * 新广告平台的广告类型
     */
    public static class LinkType {
        public static final int URL = 1;
        public static final int API = 2;
        public static final int APK = 3;
        public static final int JS = 4;
        public static final int SDK = 5;
    }
}
