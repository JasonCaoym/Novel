package com.duoyue.mod.stats;

import com.duoyue.mod.stats.error.ErrorLogPresenter;

/**
 * 错误日志统计类
 * @author caoym
 * @data 2019/5/15  16:30
 */
public class ErrorStatsApi
{
    /**
     * 启动开屏页成功.
     */
    public static final String SPLASH_SUCC = "SP_SU";

    /**
     * 启动开屏页失败.
     */
    public static final String SPLASH_FAIL = "SP_FA";

    /**
     * 加载开屏页广告.
     */
    public static final String SPLASH_AD = "SP_AD";

    /**
     * 无开屏页广告.
     */
    public static final String SPLASH_NO_AD = "SP_N_AD";

    /**
     * 开屏页面点击Home键退出.
     */
    public static final String SPLASH_HOME_KEY = "SP_HM_K";

    /**
     * 启动Home页
     */
    public static final String GO_HOME = "GO_HM";

    /**
     * 启动Home失败
     */
    public static final String GO_HOME_FAIL = "GO_HM_FA";

    /**
     * 展示开屏广告.
     */
    public static final String SHOW_SPLASH_AD = "S_SP_AD";

    /**
     * 关闭开屏广告.
     */
    public static final String CLOSE_SPLASH_AD = "C_SP_AD";

    /**
     * 开屏页销毁.
     */
    public static final String DESTORY_SPLASH = "SP_DEST";

    /**
     * 主页初始化.
     */
    public static final String HOME_INIT = "HM_INIT";

    /**
     * 启动主页成功.
     */
    public static final String HOME_SUCC = "HM_SU";

    /**
     * 登录开始.
     */
    public static final String LOGIN_START = "LG_ST";

    /**
     * 登录成功.
     */
    public static final String LOGIN_SUCC = "LG_SU";

    /**
     * 登录失败.
     */
    public static final String LOGIN_FAIL = "LG_FA";

    /**
     * 加载章节内容失败.
     */
    public static final String LOAD_RD_FAIL = "LD_RD_FA";

    /**
     * 加载书籍详情失败.
     */
    public static final String LOAD_DETAIL_FAIL = "LD_DT_FA";

    /**
     * 加载章节列表失败.
     */
    public static final String LOAD_CHAPTER_FAIL = "LD_CR_FA";

    /**
     * 穿上甲模版信息流广告失败.
     */
    public static final String TT_NATIVE_TEMP_FAIL = "TT_NA_T_FA";

    /**
     * 添加错误日志.
     * @param errorType
     */
    public static void addError(String errorType)
    {
        addError(errorType, null);
    }

    /**
     * 添加错误日志.
     * @param errorType
     * @param errorMsg
     */
    public static void addError(String errorType, String errorMsg)
    {
        ErrorLogPresenter.addError(errorType, errorMsg);
    }
}
