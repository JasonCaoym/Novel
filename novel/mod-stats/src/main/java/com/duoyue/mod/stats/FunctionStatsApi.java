package com.duoyue.mod.stats;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.common.StatisticsMgr;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.utils.SPUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能统计
 * @author caoym
 * @data 2019/4/28  14:08
 */
public class FunctionStatsApi
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#FunctionStatsApi";

    /**
     * 书架页面-书架tab点击
     */
    private static final String BS_TAB_CLICK = "J1";

    /**
     * 书架页面-收藏书籍点击
     */
    private static final String BS_BOOK_CLICK = "J2";

    /**
     * 书架页面-推荐书籍点击
     */
    private static final String BS_RECOMM_BOOK_CLICK = "J3";

    /**
     * 书架页面-加号点击
     */
    private static final String BS_ADD_CLICK = "J4";

    /**
     * 书架页面-书架tab点击
     */
    private static final String BS_LONG_EDIT_CLICK = "J5";

    /**
     * 书架页面-搜索
     */
    private static final String BS_SEARCH_CLICK = "J6";

    /**
     * 书架页面-更多
     */
    private static final String BS_MORE_CLICK = "J7";

    /**
     * 书架-文字轮播曝光
     */
    private static final String BS_TEXT_CAROUSEL_EXPOSURE = "EPJ8";

    /**
     * 书架-文字轮播点击
     */
    private static final String BS_TEXT_CAROUSEL_CLICK = "J8";

    /**
     * 书架-文字展开曝光
     */
    private static final String BS_TEXT_EXPAND_EXPOSURE = "EPJ9";

    /**
     * 书架-文字展开点击
     */
    private static final String BS_TEXT_EXPAND_CLICK = "J9";

    /**
     * 书架页面-阅读历史
     */
    private static final String BS_READ_HISTORY_CLICK = "J10";

    /**
     * 书城页面-书城Tab点击
     */
    private static final String BC_TAB_CLICK = "C1";

    /**
     * 书城页面-搜索
     */
    private static final String BC_SEARCH_CLICK = "C2";

    /**
     * 书城页面-精选Tab点击
     */
    private static final String BC_FEATURED_TAB_CLICK = "C3";

    /**
     * 书城页面-书城女生Tab点击
     */
    private static final String BC_GIRL_TAB_CLICK = "C4";

    /**
     * 书城页面-书城男生Tab点击
     */
    private static final String BC_BOY_TAB_CLICK = "C5";

    /**
     * 书城页面-榜单Icon点击
     */
    private static final String BC_RANK_ICON_CLICK = "C6";

    /**
     * 书城页面-精品Icon点击
     */
    private static final String BC_FEATURED_ICON_CLICK = "C7";

    /**
     * 书城页面-新书Icon点击
     */
    private static final String BC_NEW_ICON_CLICK = "C8";

    /**
     * 书城页面-完结Icon点击
     */
    private static final String BC_COMPLETE_ICON_CLICK = "C9";

    /**
     * 书城页面-分类Icon点击
     */
    private static final String BC_CATEGORY_ICON_CLICK = "C10";

    /**
     * 书城页面-分栏书籍点击(上报分栏ID  -- CFxx)
     */
    private static final String BC_C_BOOK_CLICK = "CF";

    /**
     * 书城页面-分栏列表页书籍点击(上报分栏ID  -- CLxx)
     */
    private static final String BC_C_LIST_BOOK_CLICK = "CL";

    /**
     * 书城页面-分栏更多点击(上报分栏ID  -- CMxx)
     */
    private static final String BC_C_MORE_CLICK = "CM";

    /**
     * 书城页面-书城悬浮点击
     */
    private static final String BC_CITY_FLOAT_CLICK = "C11";

    /**
     * 书籍详情页-点击书籍(书城-精选-点击书籍)
     */
    private static final String BD_FEATURED_BOOK_CLICK = "D1";

    /**
     * 书籍详情页-点击书籍(书城-男生-点击书籍)
     */
    private static final String BD_BOY_BOOK_CLICK = "D2";

    /**
     * 书籍详情页-点击书籍(书城-女生-点击书籍)
     */
    private static final String BD_GIRL_BOOK_CLICK = "D3";

    /**
     * 书籍详情页-点击书籍(女生排行榜-点击书籍， 上报榜单Id, 既D5GXX, 带上BookId)
     */
    private static final String BD_GIRL_LEADERBOARD_BOOK_CLICK = "D4G";

    /**
     * 书籍详情页-点击书籍(男生排行榜-点击书籍， 上报榜单Id, 既D5BXX, 带上BookId)
     */
    private static final String BD_BOY_LEADERBOARD_BOOK_CLICK = "D4B";

    /**
     * 书籍详情页-点击书籍(分类列表-点击书籍)
     */
    private static final String BD_CATEGORY_LIST_BOOK_CLICK = "D5";

    /**
     * 书籍详情页-推送来源(D6XXX, xx表示推送的类型[xx=1:追书推送;2:沉睡用户唤醒推送)
     */
    private static final String BD_PUSH_BOOK_CLICK = "D6";

    /**
     * 书籍详情页-点击书籍(目录页入口-点击书籍)
     */
    private static final String BD_DIR_PAGE_BOOK_CLICK = "B1";

    /**
     * 书籍详情页-点击书籍( 更多书评页入口-点击书籍)
     */
    private static final String BD_MORE_BOOK_REVIEW_BOOK_CLICK = "B2";

    /**
     * 分类-Tab点击
     */
    private static final String C_TAB_CLICK = "F1";

    /**
     * 分类-女生Tab点击
     */
    private static final String C_GIRL_TAB_CLICK = "F2";

    /**
     * 分类-男生Tab点击
     */
    private static final String C_BOY_TAB_CLICK = "F3";

    /**
     * 分类-搜索点击
     */
    private static final String C_SEARCH_CLICK = "F4";

    /**
     * 分类-分类点击(FD + 分类Id)
     */
    private static final String C_CATEGORY_CLICK = "FD";

    /**
     * 我的-Tab点击
     */
    private static final String M_TAB_CLICK = "M1";

    /**
     * 我的-登录入口点击
     */
    private static final String M_LOGIN_CLICK = "M2";

    /**
     * 我的-阅读口味入口点击
     */
    private static final String M_READ_TASTE_CLICK = "M3";

    /**
     * 我的-阅读历史入口点击
     */
    private static final String M_READ_HISTORY_CLICK = "M4";

    /**
     * 我的-退出登录点击
     */
    private static final String M_SIGN_OUT_CLICK = "M5";

    /**
     * 阅读器-书籍翻页次数(每向前翻一页即记录一次PV, 向后翻页不计PV, 需要关联到BookID)
     */
    private static final String READ_NEXT_PAGE = "RD_PV";

    /**
     * 阅读器-书籍真实翻页次数(每翻一页, 并停留5秒才统计一次真实PV, 可以排除一些无效的快速误触操作, 需要关联到BookID)
     */
    private static final String READ_VALID_NEXT_PAGE = "RD_PVT";

    /**
     * 阅读器-书籍章节阅读量(阅读到章节末尾, 切换到新章节时上报, 目录中直接切换不上报, 需要关联到BookID)
     */
    private static final String READ_CHAPTER_READING = "RD_CV";

    /**
     * 阅读器-阅读器目录点击(阅读器内, 点击目录次数, 需要关联到BookID)
     */
    private static final String READ_CATALOG_CLICK = "RD_CC";

    /**
     * 阅读器-阅读器目录章节切换(阅读器内目录章节列表点击切换章节时上报, 需要关联到BookID)
     */
    private static final String READ_CATALOG_CHAPTER_CLICK = "RD_CS";

    /**
     * 阅读器-切换到夜间模式(日间状态下, 点击按钮切换到夜间模式的次数, 需要关联到BookID)
     */
    private static final String READ_NIGHT_MODE_CLICK = "RD_NM";

    /**
     * 阅读器-切换到日间模式(夜间状态下, 点击按钮切换到日间模式的次数, 需要关联到BookID)
     */
    private static final String READ_DAY_MODE_CLICK = "RD_DM";

    /**
     * 阅读器-设置菜单点击次数(需要关联到BookID)
     */
    private static final String READ_MENU_CLICK = "RD_AA";

    /**
     * 阅读器-字号加大点击次数(需要关联到BookID)
     */
    private static final String READ_INCREASE_FONT_CLICK = "RD_AL";

    /**
     * 阅读器-字号缩小点击次数(需要关联到BookID)
     */
    private static final String READ_REDUCE_FONT_CLICK = "RD_AS";

    /**
     * 阅读器-亮度调节次数(每次通过设置菜单修改屏幕亮度生效时上报, 需要关联到BookID)
     */
    private static final String READ_BRIGHTNESS_CLICK = "RD_BN";

    /**
     * 阅读器-系统按钮亮度调节次数(每次通过设置菜单修改屏幕亮度生效时上报, 需要关联到BookID)
     */
    private static final String READ_SYS_BRIGHTNESS_CLICK = "RD_SBN";

    /**
     * 阅读器-背景1切换次数(点击切换到1号背景的次数, 需要关联到BookID)
     */
    private static final String READ_BACKGROUND1_CLICK = "RD_BG1";

    /**
     * 阅读器-背景2切换次数(点击切换到2号背景的次数, 需要关联到BookID)
     */
    private static final String READ_BACKGROUND2_CLICK = "RD_BG2";

    /**
     * 阅读器-背景3切换次数(点击切换到3号背景的次数, 需要关联到BookID)
     */
    private static final String READ_BACKGROUND3_CLICK = "RD_BG3";

    /**
     * 阅读器-背景4切换次数(点击切换到4号背景的次数, 需要关联到BookID)
     */
    private static final String READ_BACKGROUND4_CLICK = "RD_BG4";

    /**
     * 分类列表-筛选>全部(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_FILTER_ALL_CLICK = "C_F1";

    /**
     * 分类列表-筛选>连载中(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_FILTER_LOADING_CLICK = "C_F2";

    /**
     * 分类列表-筛选>已完结(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_FILTER_FINISHED_CLICK = "C_F3";

    /**
     * 分类列表-筛选>不限字数(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_UNLIMITED_WORD_COUNT_CLICK = "CC1";

    /**
     * 分类列表-筛选>30万以下(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_30WAN_FOLLOWING_CLICK = "CC2";

    /**
     * 分类列表-筛选>30-80万(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_30_80WAN_CLICK = "CC3";

    /**
     * 分类列表-筛选>80万以下(点击筛选项时触发, 默认选中时不触发上报)
     */
    private static final String CL_80WAN_ABOVE_CLICK = "CC4";

    /**
     * 分类列表-排序>按人气(点击排序项时触发, 默认选中时不触发上报)
     */
    private static final String CL_SORT_POPULARITY_CLICK = "C_S1";

    /**
     * 分类列表-排序>按更新(点击排序项时触发, 默认选中时不触发上报)
     */
    private static final String CL_SORT_UPDATE_CLICK = "C_S2";

    /**
     * 分类列表-排序>按评分(点击排序项时触发, 默认选中时不触发上报)
     */
    private static final String CL_SORT_RATING_CLICK = "C_S3";

    /**
     * 搜索页-推荐来源点击(点击搜索页推荐[飙升热门大作]的书籍进入书籍详情页, 需要关联到BookID)
     */
    private static final String SEARCH_RECOMMEND_CLICK = "D7";

    /**
     * 搜索页-热搜词点击(热搜词列表的任意一项, 点击即上报)
     */
    private static final String SEARCH_HOT_WORD_CLICK = "S1";

    /**
     * 搜索页-历史搜索点击(搜索历史列表的任意一项, 点击即上报)
     */
    private static final String SEARCH_HISTORY_CLICK = "S2";

    /**
     * 搜索页-清空历史点击(清空历史操作, 点击即上报)
     */
    private static final String SEARCH_CLEAR_HISTORY_CLICK = "S3";

    /**
     * 搜索页-关键词联想列表点击(搜索关键字联想列表, 点击任意一项即上报)
     */
    private static final String SEARCH_KEYWORDS_LENOVO_CLICK = "S4";

    /**
     * 搜索页-搜索按钮点击(点击键盘上的搜索按钮触发搜索请求时上报)
     */
    private static final String SEARCH_BUTTON_CLICK = "S5";

    /**
     * 搜索页-搜索空状态来源(点击搜索结果为空状态下的推荐书籍时上报)
     */
    private static final String SEARCH_EMPTY_STATE_BOOK_CLICK = "D8";

    /**
     * 书城Banner曝光(上报频道[1:精选男;2:精选女;3:男生;4:女生]、BookId)
     */
    private static final String CITY_BANNER_EXPOSURE = "EPCB";

    /**
     * 书城Banner点击(上报频道[1:精选男;2:精选女;3:男生;4:女生]、BookId)
     */
    private static final String CITY_BANNER_CLICK = "CB";

    /**
     * 书城分栏书籍曝光(上报分栏Id、BookId)
     */
    private static final String CITY_BOOK_EXPOSURE = "EPCF";

    /**
     * 书城分栏列表(更多)页书籍曝光(上报分栏Id、BookId)
     */
    private static final String CITY_MORE_BOOK_EXPOSURE = "EPCL";

    /**
     * 二级分类列表页书籍曝光(上报分类Id、BookId)
     */
    private static final String CATEGORY_BOOK_EXPOSURE = "EPCGL";

    /**
     * 详情页-写书籍评论按钮点击(上报来源:1-书籍详情;2-书评列表)
     */
    private static final String BD_WRITE_BOOK_REVIEW = "BPA";

    /**
     * 详情页-发送书评按钮点击
     */
    private static final String BD_SEND_BOOK_REVIEW = "BPS";

    /**
     * 详情页-加入书架按钮点击(上报来源:1-方案A;2-方案B)
     */
    private static final String BD_ADD_BOOK_SHELF = "ADD";

    /**
     * 详情页-点击阅读按钮(上报来源:1-方案A;2-方案B)
     */
    private static final String BD_CONTINUE_READ_CLICK = "ERD";

    /**
     * 详情页-B方案抢先阅读第一章按钮点击
     */
    private static final String BD_READ_FIRST_CHAPTER_B = "AB1";

    /**
     * 详情页-B方案继续阅读第二章按钮点击
     */
    private static final String BD_CONTINUE_READ_2_CHAPTER_B = "AB2";

    /**
     * 详情页-读者都在看, 书籍曝光.
     */
    private static final String BD_READERS_LOOKING_EXPOSURE = "EPRP";

    /**
     * 详情页-读者都在看, 书籍点击.
     */
    private static final String BD_READERS_LOOKING_CLICK = "RP";

    /**
     * 详情页-同类热门书, 书籍曝光.
     */
    private static final String BD_SIMILAR_POPULAR_EXPOSURE = "EPRS";

    /**
     * 详情页-同类热门书, 书籍点击.
     */
    private static final String BD_SIMILAR_POPULAR_CLICK = "RS";

    /**
     * 阅读器-音量加(上一页)
     */
    private static final String READ_VOLUME_UP = "RD_PU";

    /**
     * 阅读器-音量减(下一页)
     */
    private static final String READ_VOLUME_DOWN= "RD_PD";

    /**
     * 阅读器-去书城按钮点击(阅读器末尾页)
     */
    private static final String READ_GO_BOOK_CITY = "RD_EH";

    /**
     * 阅读器-推荐书籍点击(阅读器末尾页)
     */
    private static final String READ_RECOMMEND_BOOK_CLICK = "D9";

    /**
     * 阅读器-退出阅读器(通过弹框中的取消/加入书架按钮退出, 渲染状态:1-渲染成功;2-渲染失败)
     */
    public static final String READ_QUIT = "RD_ESC";

    /**
     * 授权成功.
     */
    private static final String AUTH_SUCC = "ACTVA";

    /**
     * 启动次数.
     */
    private static final String START_APP = "STTCN";

    /**
     * 活跃用户.
     */
    private static final String ACTIVE_USER = "ACTVU";

    /**
     * 在线时长
     */
    private static final String ONLINE_TIME = "EOLNL";

    /**
     * 阅读器-阅读时长(阅读器每个页面用户停留45秒后停止阅读时长的计数, 锁屏后停止阅读时长的计数, 需要关联到BookID)
     */
    public static final String READING_TIME = "ERENL";

    /**
     * 搜索书籍点击
     */
    private static final String CLICK_SEARCH_BOOK = "BSCLK";

    /**
     * 进入阅读器阅读(需带上BookId).
     */
    private static final String ENTER_READ = "READ";

    /**
     * 进入阅读器且真实阅读完成10页(一本书一次阅读只记录一次)
     */
    private static final String ENTER_REAL_READ = "DREAD";

    /**
     * 进入书籍的详情页次数(需带上BookId).
     */
    private static final String ENTER_DETAILS_PAGE = "PREAD";

    /**
     * 书籍曝光.
     */
    ///private static final String BOOK_EXPOSURE = "BKEXPO";

    /**
     * 已统计活跃用户节点缓存对象{"日期": ["UID1", "UID2"]}.
     */
    private static RamCache mActiveUserCache;

    /**
     * 书架页面-书架tab点击
     */
    public static void bsTabClick()
    {
        StatisticsMgr.addStatsForFunc(BS_TAB_CLICK);
    }

    /**
     * 书架页面-收藏书籍点击
     */
    public static void bsBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_BOOK_CLICK, bookId);
    }

    /**
     * 书架页面-推荐书籍点击
     */
    public static void bsRecommBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_RECOMM_BOOK_CLICK, bookId);
    }

    /**
     * 书架页面-加号点击
     */
    public static void bsAddClick()
    {
        StatisticsMgr.addStatsForFunc(BS_ADD_CLICK);
    }

    /**
     * 书架页面-书架tab点击
     */
    public static void bsLongEditClick()
    {
        StatisticsMgr.addStatsForFunc(BS_LONG_EDIT_CLICK);
    }

    /**
     * 书架页面-搜索
     */
    public static void bsSearchClick()
    {
        StatisticsMgr.addStatsForFunc(BS_SEARCH_CLICK);
    }

    /**
     * 书架页面-更多
     */
    public static void bsMoreClick()
    {
        StatisticsMgr.addStatsForFunc(BS_MORE_CLICK);
    }

    /**
     * 书架-文字轮播曝光
     */
    public static void bsTextCarouselExposure(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_TEXT_CAROUSEL_EXPOSURE, bookId);
    }

    /**
     * 书架-文字轮播点击
     */
    public static void bsTextCarouselClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_TEXT_CAROUSEL_CLICK, bookId);
    }

    /**
     * 书架-文字展开曝光
     */
    public static void bsTextExpandExposure(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_TEXT_EXPAND_EXPOSURE, bookId);
    }

    /**
     * 书架-文字展开点击
     */
    public static void bsTextExpandClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BS_TEXT_EXPAND_CLICK, bookId);
    }

    /**
     * 书架页面-阅读历史
     */
    public static void bsReadHistoryClick()
    {
        StatisticsMgr.addStatsForFunc(BS_READ_HISTORY_CLICK);
    }

    /**
     * 书城页面-书城Tab点击
     */
    public static void bcTabClick()
    {
        StatisticsMgr.addStatsForFunc(BC_TAB_CLICK);
    }

    /**
     * 书城页面-搜索
     */
    public static void bcSearchClick()
    {
        StatisticsMgr.addStatsForFunc(BC_SEARCH_CLICK);
    }

    /**
     * 书城页面-精选Tab点击
     */
    public static void bcFeaturedTabClick()
    {
        StatisticsMgr.addStatsForFunc(BC_FEATURED_TAB_CLICK);
    }

    /**
     * 书城页面-书城女生Tab点击
     */
    public static void bcGirlTabClick()
    {
        StatisticsMgr.addStatsForFunc(BC_GIRL_TAB_CLICK);
    }

    /**
     * 书城页面-书城男生Tab点击
     */
    public static void bcBoyTabClick()
    {
        StatisticsMgr.addStatsForFunc(BC_BOY_TAB_CLICK);
    }

    /**
     * 书城页面-榜单Icon点击
     */
    public static void bcRankIconClick()
    {
        StatisticsMgr.addStatsForFunc(BC_RANK_ICON_CLICK);
    }

    /**
     * 书城页面-精品Icon点击
     */
    public static void bcFeaturedIconClick()
    {
        StatisticsMgr.addStatsForFunc(BC_FEATURED_ICON_CLICK);
    }

    /**
     * 书城页面-新书Icon点击
     */
    public static void bcNewIconClick()
    {
        StatisticsMgr.addStatsForFunc(BC_NEW_ICON_CLICK);
    }

    /**
     * 书城页面-完结Icon点击
     */
    public static void bcCompleteIconClick()
    {
        StatisticsMgr.addStatsForFunc(BC_COMPLETE_ICON_CLICK);
    }

    /**
     * 书城页面-分类Icon点击
     */
    public static void bcCategoryIconClick()
    {
        StatisticsMgr.addStatsForFunc(BC_CATEGORY_ICON_CLICK);
    }

    /**
     * 书城页面-分栏书籍点击(上报分栏ID  -- CFxx)
     */
    public static void bcCBookClick(String subCategoryId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(BC_C_BOOK_CLICK + subCategoryId, bookId);
    }

    /**
     * 书城页面-分栏列表页书籍点击(上报分栏ID  -- CLxx)
     */
    public static void bcCListBookClick(String subCategoryId, long bookId)
    {
        //TODO:等列表页对接新接口再接入
        StatisticsMgr.addStatsForFunc(BC_C_LIST_BOOK_CLICK + subCategoryId, bookId);
    }

    /**
     * 书城页面-分栏更多点击(上报分栏ID  -- CMxx)
     */
    public static void bcCMoreClick(String subCategoryId)
    {
        StatisticsMgr.addStatsForFunc(BC_C_MORE_CLICK + subCategoryId);
    }

    /**
     * 书城页面-书城悬浮点击
     */
    public static void bcCityFloatClick()
    {
        StatisticsMgr.addStatsForFunc(BC_CITY_FLOAT_CLICK);
    }

    /**
     * 书籍详情页-点击书籍(书城-精选-点击书籍)
     */
    public static void bdFeaturedBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_FEATURED_BOOK_CLICK, bookId);
    }

    /**
     * 书籍详情页-点击书籍(书城-男生-点击书籍)
     */
    public static void bdBoyBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_BOY_BOOK_CLICK, bookId);
    }

    /**
     * 书籍详情页-点击书籍(书城-女生-点击书籍)
     */
    public static void bdGirlBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_GIRL_BOOK_CLICK, bookId);
    }

    /**
     * 书籍详情页-点击书籍(女生排行榜-点击书籍，上报榜单Id, 既D5GXX, 带上BookId)
     */
    public static void bdGirlLeaderboardBookClick(String rankId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_GIRL_LEADERBOARD_BOOK_CLICK + rankId, bookId);
    }

    /**
     * 书籍详情页-点击书籍(男生排行榜-点击书籍，上报榜单Id, 既D5BXX, 带上BookId)
     */
    public static void bdBoyLeaderboardBookClick(String rankId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_BOY_LEADERBOARD_BOOK_CLICK + rankId, bookId);
    }

    /**
     * 书籍详情页-点击书籍(分类列表-点击书籍)
     */
    public static void bdCategoryListBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_CATEGORY_LIST_BOOK_CLICK, bookId);
    }

    /**
     * 书籍详情页-推送来源(D6XXX, xx表示推送的类型[xx=1:追书推送;2:沉睡用户唤醒推送)
     */
    public static void bdPushBookClick(int pushType, String bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_PUSH_BOOK_CLICK + pushType, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 书籍详情页-点击书籍(目录页入口-点击书籍)
     */
    public static void bdDirPageBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_DIR_PAGE_BOOK_CLICK, bookId);
    }

    /**
     * 书籍详情页-点击书籍( 更多书评页入口-点击书籍)
     */
    public static void bdMoreBookReviewBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_MORE_BOOK_REVIEW_BOOK_CLICK, bookId);
    }

    /**
     * 分类-Tab点击
     */
    public static void cTabClick()
    {
        StatisticsMgr.addStatsForFunc(C_TAB_CLICK);
    }

    /**
     * 分类-女生Tab点击
     */
    public static void cGirlTabClick()
    {
        StatisticsMgr.addStatsForFunc(C_GIRL_TAB_CLICK);
    }

    /**
     * 分类-男生Tab点击
     */
    public static void cBoyTabClick()
    {
        StatisticsMgr.addStatsForFunc(C_BOY_TAB_CLICK);
    }

    /**
     * 分类-搜索点击
     */
    public static void cSearchClick()
    {
        StatisticsMgr.addStatsForFunc(C_SEARCH_CLICK);
    }

    /**
     * 分类-分类点击(FD + 分类Id)
     */
    public static void cCategoryClick(String categoryId)
    {
        StatisticsMgr.addStatsForFunc(C_CATEGORY_CLICK + categoryId);
    }

    /**
     * 我的-Tab点击
     */
    public static void mTabClick()
    {
        StatisticsMgr.addStatsForFunc(M_TAB_CLICK);
    }

    /**
     * 我的-登录入口点击
     */
    public static void mLoginClick()
    {
        StatisticsMgr.addStatsForFunc(M_LOGIN_CLICK);
    }

    /**
     * 我的-阅读口味入口点击
     */
    public static void mReadTasteClick()
    {
        StatisticsMgr.addStatsForFunc(M_READ_TASTE_CLICK);
    }

    /**
     * 我的-阅读历史入口点击
     */
    public static void mReadHistoryClick()
    {
        StatisticsMgr.addStatsForFunc(M_READ_HISTORY_CLICK);
    }

    /**
     * 我的-退出登录点击
     */
    public static void mSignOutClick()
    {
        StatisticsMgr.addStatsForFunc(M_SIGN_OUT_CLICK);
    }

    /**
     * 阅读器-书籍翻页次数(每向前翻一页即记录一次PV, 向后翻页不计PV, 需要关联到BookID)
     */
    public static void rNextPage(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_NEXT_PAGE, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-书籍真实翻页次数(每翻一页, 并停留5秒才统计一次真实PV, 可以排除一些无效的快速误触操作, 需要关联到BookID)
     */
    public static void rValidNextPage(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_VALID_NEXT_PAGE, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-书籍章节阅读量(阅读到章节末尾, 切换到新章节时上报, 目录中直接切换不上报, 需要关联到BookID)
     */
    public static void rChapterReading(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_CHAPTER_READING, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-阅读器目录点击(阅读器内, 点击目录次数, 需要关联到BookID)
     */
    public static void rCatalogClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_CATALOG_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-阅读器目录章节切换(阅读器内目录章节列表点击切换章节时上报, 需要关联到BookID)
     */
    public static void rCatalogChapterClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_CATALOG_CHAPTER_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-切换到夜间模式(日间状态下, 点击按钮切换到夜间模式的次数, 需要关联到BookID)
     */
    public static void rNightModeClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_NIGHT_MODE_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-切换到日间模式(夜间状态下, 点击按钮切换到日间模式的次数, 需要关联到BookID)
     */
    public static void rDayModeClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_DAY_MODE_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-设置菜单点击次数(需要关联到BookID)
     */
    public static void rMenuClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_MENU_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-字号加大点击次数
     */
    public static void rIncreaseFontClick()
    {
        StatisticsMgr.addStatsForFunc(READ_INCREASE_FONT_CLICK);
    }

    /**
     * 阅读器-字号缩小点击次数
     */
    public static void rReduceFontClick()
    {
        StatisticsMgr.addStatsForFunc(READ_REDUCE_FONT_CLICK);
    }

    /**
     * 阅读器-亮度调节次数(每次通过设置菜单修改屏幕亮度生效时上报)
     */
    public static void rBrightnessClick()
    {
        StatisticsMgr.addStatsForFunc(READ_BRIGHTNESS_CLICK);
    }

    /**
     * 阅读器-系统按钮亮度调节次数(每次通过设置菜单修改屏幕亮度生效时上报, 需要关联到BookID)
     */
    public static void rBrightnessSysClick()
    {
        StatisticsMgr.addStatsForFunc(READ_SYS_BRIGHTNESS_CLICK);
    }

    /**
     * 阅读器-背景1切换次数(点击切换到1号背景的次数)
     */
    public static void rBackground1Click()
    {
        StatisticsMgr.addStatsForFunc(READ_BACKGROUND1_CLICK);
    }

    /**
     * 阅读器-背景2切换次数(点击切换到2号背景的次数)
     */
    public static void rBackground2Click()
    {
        StatisticsMgr.addStatsForFunc(READ_BACKGROUND2_CLICK);
    }

    /**
     * 阅读器-背景3切换次数(点击切换到3号背景的次数)
     */
    public static void rBackground3Click()
    {
        StatisticsMgr.addStatsForFunc(READ_BACKGROUND3_CLICK);
    }

    /**
     * 阅读器-背景4切换次数(点击切换到4号背景的次数)
     */
    public static void rBackground4Click()
    {
        StatisticsMgr.addStatsForFunc(READ_BACKGROUND4_CLICK);
    }

    /**
     * 分类列表-筛选>全部(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilterAllClick()
    {
        StatisticsMgr.addStatsForFunc(CL_FILTER_ALL_CLICK);
    }

    /**
     * 分类列表-筛选>连载中(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilterLoadingClick()
    {
        StatisticsMgr.addStatsForFunc(CL_FILTER_LOADING_CLICK);
    }

    /**
     * 分类列表-筛选>已完结(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilterFinishedClick()
    {
        StatisticsMgr.addStatsForFunc(CL_FILTER_FINISHED_CLICK);
    }

    /**
     * 分类列表-筛选>不限字数(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilterUnlimitedWordCountClick()
    {
        StatisticsMgr.addStatsForFunc(CL_UNLIMITED_WORD_COUNT_CLICK);
    }

    /**
     * 分类列表-筛选>30万以下(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilter30WanFollowingClick()
    {
        StatisticsMgr.addStatsForFunc(CL_30WAN_FOLLOWING_CLICK);
    }

    /**
     * 分类列表-筛选>30-80万(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilter30To80WanClick()
    {
        StatisticsMgr.addStatsForFunc(CL_30_80WAN_CLICK);
    }

    /**
     * 分类列表-筛选>80万以上(点击筛选项时触发, 默认选中时不触发上报)
     */
    public static void clFilter80WanAboveClick()
    {
        StatisticsMgr.addStatsForFunc(CL_80WAN_ABOVE_CLICK);
    }

    /**
     * 分类列表-排序>按人气(点击排序项时触发, 默认选中时不触发上报)
     */
    public static void clSortPopularityClick()
    {
        StatisticsMgr.addStatsForFunc(CL_SORT_POPULARITY_CLICK);
    }

    /**
     * 分类列表-排序>按更新(点击排序项时触发, 默认选中时不触发上报)
     */
    public static void clSortUpdateClick()
    {
        StatisticsMgr.addStatsForFunc(CL_SORT_UPDATE_CLICK);
    }

    /**
     * 分类列表-排序>按评分(点击排序项时触发, 默认选中时不触发上报)
     */
    public static void clSortRatingClick()
    {
        StatisticsMgr.addStatsForFunc(CL_SORT_RATING_CLICK);
    }

    /**
     * 搜索页-推荐来源点击(点击搜索页推荐[飙升热门大作]的书籍进入书籍详情页, 需要关联到BookID)
     */
    public static void sRecommBookClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(SEARCH_RECOMMEND_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 搜索页-热搜词点击(热搜词列表的任意一项, 点击即上报)
     */
    public static void sHotWordClick()
    {
        StatisticsMgr.addStatsForFunc(SEARCH_HOT_WORD_CLICK);
    }

    /**
     * 搜索页-历史搜索点击(搜索历史列表的任意一项, 点击即上报)
     */
    public static void sHistoryClick()
    {
        StatisticsMgr.addStatsForFunc(SEARCH_HISTORY_CLICK);
    }

    /**
     * 搜索页-清空历史点击(清空历史操作, 点击即上报)
     */
    public static void sClearHistoryClick()
    {
        StatisticsMgr.addStatsForFunc(SEARCH_CLEAR_HISTORY_CLICK);
    }

    /**
     * 搜索页-关键词联想列表点击(搜索关键字联想列表, 点击任意一项即上报)
     */
    public static void sKeywordsLenovoClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(SEARCH_KEYWORDS_LENOVO_CLICK, bookId);
    }

    /**
     * 搜索页-搜索按钮点击(点击键盘上的搜索按钮触发搜索请求时上报)
     */
    public static void sButtonClick()
    {
        StatisticsMgr.addStatsForFunc(SEARCH_BUTTON_CLICK);
    }

    /**
     * 搜索页-搜索空状态来源(点击搜索结果为空状态下的推荐书籍时上报)
     */
    public static void sEmptyStateBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(SEARCH_EMPTY_STATE_BOOK_CLICK, bookId);
    }

    /**
     * 书城Banner曝光(上报频道[1:精选男;2:精选女;3:男生;4:女生]、BookId)
     */
    public static void cBannerExposure(String categoryId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(CITY_BANNER_EXPOSURE, StatisticsMgr.createParamTarget(null, categoryId), bookId);
    }

    /**
     * 书城Banner点击(上报频道[1:精选男;2:精选女;3:男生;4:女生]、BookId)
     */
    public static void cBannerClick(String categoryId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(CITY_BANNER_CLICK, StatisticsMgr.createParamTarget(null, categoryId), bookId);
    }

    /**
     * 书城分栏书籍曝光(上报分栏Id、BookId)
     */
    public static void cBookExposure(String categoryId, long bookId, String bookName)
    {
        StatisticsMgr.addStatsForFunc(CITY_BOOK_EXPOSURE, StatisticsMgr.createParamTarget(null, categoryId), bookId, bookName);
    }

    /**
     * 书城分栏列表(更多)页书籍曝光(上报分栏Id、BookId)
     */
    public static void cMoreBookExposure(String categoryId, long bookId, String bookName)
    {
        StatisticsMgr.addStatsForFunc(CITY_MORE_BOOK_EXPOSURE, StatisticsMgr.createParamTarget(null, categoryId), bookId, bookName);
    }

    /**
     * 二级分类列表页书籍曝光(上报分类Id、BookId)
     */
    public static void categoryBookExposure(String categoryId, long bookId, String bookName)
    {
        StatisticsMgr.addStatsForFunc(CATEGORY_BOOK_EXPOSURE, StatisticsMgr.createParamTarget(null, categoryId), bookId, bookName);
    }

    /**
     * 详情页-写书籍评论按钮点击(上报来源:1-书籍详情;2-书评列表)
     */
    public static void bdWriteBookReview(String categoryId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_WRITE_BOOK_REVIEW, StatisticsMgr.createParamTarget(null, categoryId), bookId);
    }

    /**
     * 详情页-发送书评按钮点击
     */
    public static void bdSendBookReviewClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_SEND_BOOK_REVIEW, bookId);
    }

    /**
     * 详情页-加入书架按钮点击(上报来源:1-方案A;2-方案B)
     */
    public static void bdAddBookShelf(String categoryId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_ADD_BOOK_SHELF, StatisticsMgr.createParamTarget(null, categoryId), bookId);
    }

    /**
     * 详情页-B方案点击阅读按钮(上报来源:1-方案A;2-方案B)
     */
    public static void bdContinueReadClick(String categoryId, String bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_CONTINUE_READ_CLICK, StatisticsMgr.createParamTarget(null, categoryId), StringFormat.parseLong(bookId, -1));
    }

    /**
     * 详情页-B方案抢先阅读第一章按钮点击
     */
    public static void bdReadFirstChapterB(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_READ_FIRST_CHAPTER_B, bookId);
    }

    /**
     * 详情页-B方案继续阅读第二章按钮点击
     */
    public static void bdContinueRead2ChapterB(long bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_CONTINUE_READ_2_CHAPTER_B, bookId);
    }

    /**
     * 详情页-读者都在看, 书籍曝光.
     */
    public static void bdReadersLookingExposure(long bookId, String bookName)
    {
        StatisticsMgr.addStatsForFunc(BD_READERS_LOOKING_EXPOSURE, null, bookId, bookName);
    }

    /**
     * 详情页-读者都在看, 书籍点击.
     */
    public static void bdReadersLookingClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_READERS_LOOKING_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 详情页-同类热门书, 书籍曝光.
     */
    public static void bdSimilarPopularExposure(long bookId, String bookName)
    {
        StatisticsMgr.addStatsForFunc(BD_SIMILAR_POPULAR_EXPOSURE, null, bookId, bookName);
    }

    /**
     * 详情页-同类热门书, 书籍点击.
     */
    public static void bdSimilarPopularClick(String bookId)
    {
        StatisticsMgr.addStatsForFunc(BD_SIMILAR_POPULAR_CLICK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-音量加(上一页)
     */
    public static void readVolumeUp(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_VOLUME_UP, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-音量减(下一页)
     */
    public static void readVolumeDown(String bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_VOLUME_DOWN, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 阅读器-去书城按钮点击(阅读器末尾页)
     */
    public static void readGoBookCity(long bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_GO_BOOK_CITY, bookId);
    }

    /**
     * 阅读器-推荐书籍点击(阅读器末尾页)
     */
    public static void readRecommendBookClick(long bookId)
    {
        StatisticsMgr.addStatsForFunc(READ_RECOMMEND_BOOK_CLICK, bookId);
    }

    /**
     * 阅读器-退出阅读器(通过弹框中的取消/加入书架按钮退出, 渲染状态:0-渲染中;1-渲染成功;2-渲染失败)
     */
    public static void readQuit(int renderingState, String bookId, long duration)
    {
        //创建Target
        JSONObject paramJSONObj = StatisticsMgr.createParamTarget(null, String.valueOf(renderingState));
        StatisticsMgr.addStatsForFunc(READ_QUIT, StatisticsMgr.createParamTime(paramJSONObj, duration), StringFormat.parseLong(bookId, -1));
    }

    /**
     * 授权成功(实时上报).
     */
    public synchronized static void authSucc()
    {
        try
        {
            //判断UID是否为空.
            if (UserManager.getInstance().getUserInfo() == null || StringFormat.isEmpty(UserManager.getInstance().getUserInfo().uid))
            {
                //UID为空, 不上报.
                return;
            }
            //判断是否已上报.
            if (SPUtils.INSTANCE.getBoolean("AUTH_SUCC", false))
            {
                //已上报.
                return;
            }
            //实时上报授权成功节点.
//            UploadStatsMgr.getInstance().uploadFuncStatsForTimely(AUTH_SUCC, 0);
            PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", "", "0", AUTH_SUCC, "");
            //设置已上报授权成功标识.
            SPUtils.INSTANCE.putBoolean("AUTH_SUCC", true);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "authSucc: {}", throwable);
        }
    }

    /**
     * 应用启动.
     */
    public static void startApp()
    {
        //实时上报应用启动节点.
//        UploadStatsMgr.getInstance().uploadFuncStatsForTimely(START_APP, 0);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", "", 0, START_APP, "");
        try
        {
            //获取用户信息.
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            //添加启动应用统计.
            Map<String, String> paramMap = new HashMap<>();
            //MID
            paramMap.put("MID", UserManager.getInstance().getMid());
            //UID
            paramMap.put("UID", userInfo != null ? userInfo.uid : "NULL");
            //用户类型(1.游客,2.微信,3.QQ,4.微博,5手机).
            paramMap.put("TYPE", String.valueOf(userInfo != null ? userInfo.type : -1));
            StatisHelper.onEvent(BaseContext.getContext(), START_APP, paramMap);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "startApp: {}", throwable);
        }
    }

    /**
     * 活跃用户(用户登录并开启阅读器，视为活跃用户).
     */
    public static void activeUser()
    {
        //获取当前用户UID.
        /*UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null || StringFormat.isEmpty(userInfo.uid))
        {
            Logger.e(TAG, "activeUser: 用户UID为空: {}", (userInfo != null ? userInfo.uid : "NULL"));
            return;
        }
        //判断当日是否已统计.
        if (mActiveUserCache == null)
        {
            mActiveUserCache = new RamCache(new File(BaseContext.getContext().getFilesDir() + StatsConstants.ACTIVE_USER_TIME_PATH), new StringParser());
        }
        //获取当前日期.
        String currentData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01);
        //获取缓存时间.
        String activeUserInfo = TextTool.toString(mActiveUserCache.get(""));
        JSONObject activeUserJSONObj = null;
        if (!StringFormat.isEmpty(activeUserInfo))
        {
            try {
                activeUserJSONObj = new JSONObject(activeUserInfo);
                //获取当日已上报的UID.
                JSONArray jsonArray = activeUserJSONObj.optJSONArray(currentData);
                for (int index = 0; index < jsonArray.length(); index++)
                {
                    if (userInfo.uid.equalsIgnoreCase(jsonArray.optString(index, "")))
                    {
                        //当日已上报.
                        return;
                    }
                }
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "activeUser: {}, {}", activeUserInfo, throwable);
            }
        }
        try
        {
            if (activeUserJSONObj == null)
            {
                activeUserJSONObj = new JSONObject();
            }
            //获取当前日志已上报的UID.
            JSONArray jsonArray = activeUserJSONObj.has(currentData) ? activeUserJSONObj.optJSONArray(currentData) : new JSONArray();
            jsonArray.put(userInfo.uid);
            activeUserJSONObj.put(currentData, jsonArray);
            //记录当日已上报的UID.
            mActiveUserCache.set(activeUserJSONObj.toString());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "activeUser: {}", throwable);
        }
        //添加统计数据到DB.
        StatisticsMgr.addStatsForFunc(ACTIVE_USER);*/
    }

    /**
     * 在线时长(即每个用户每日开启应用并停留的时长)
     * (注:间隔一分钟调用一次)
     * NOW_X
     */
    public static void onlineTime()
    {
        //累计单日在线时长.
        StatisticsMgr.addStatsForFunc(ONLINE_TIME);
    }

    /**
     * 阅读时长(阅读器每个页面用户停留45秒后停止阅读时长的计数, 锁屏后停止阅读时长的计数, 需要关联到BookID).
     */
    public static void readingTime(String bookId)
    {
        //累计阅读时长.
        StatisticsMgr.addStatsForFunc(READING_TIME, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 点击搜索书籍.
     * @param bookId
     */
    public static void clickSearchBook(String bookId)
    {
        //点击搜索书籍.
        StatisticsMgr.addStatsForFunc(CLICK_SEARCH_BOOK, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 进入阅读器阅读(需带上BookId).
     * @param bookId
     */
    public static void enterRead(String bookId)
    {
        //实时上报进入阅读器阅读节点.
//        UploadStatsMgr.getInstance().uploadFuncStatsForTimely(ENTER_READ, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 进入阅读器且真实阅读完成10页(一本书一次阅读只记录一次)
     * @param bookId
     *  NOW_X
     */
    public static void enterRealRead(String bookId)
    {
        //实时上报进入阅读器且真实阅读完成10页节点.
//        UploadStatsMgr.getInstance().uploadFuncStatsForTimely(ENTER_REAL_READ, StringFormat.parseLong(bookId, -1));
    }

    /**
     * 进入书籍的详情页次数(需带上BookId).
     */
    public static void enterDetailsPage(long bookId)
    {
        //点击搜索书籍.
        StatisticsMgr.addStatsForFunc(ENTER_DETAILS_PAGE, bookId);
    }

    /**
     * 添加统计数据(实时上报失败的情况下, 再通过离线方式上报)
     * @param eventId
     */
    public static void addStats(String eventId, long bookId)
    {
        StatisticsMgr.addStatsForFunc(eventId, bookId);
    }

    /**
     * 添加错误日志
     * @param errorType
     */
    public static void addError(String errorType)
    {
        StatisticsMgr.addStatsForFunc(errorType);
    }

    /**
     * 读取当日未上报阅读时长.
     * @return
     */
    public static int getCurrDayReadingTime()
    {
        return StatisticsMgr.getCurrDayReadingTime();
    }

    /**
     * 读取所有未上报阅读时长.
     * @return
     */
    public static int getTotalReadingTime()
    {
        return StatisticsMgr.getTotalReadingTime();
    }
}
