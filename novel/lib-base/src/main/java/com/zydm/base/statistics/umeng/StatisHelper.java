package com.zydm.base.statistics.umeng;

import android.content.Context;
import com.duoyue.lib.base.app.Constants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.utils.StringUtils;
import com.duoyue.lib.base.log.Logger;

import java.util.Map;

public class StatisHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#StatisHelper";

    private static final String UM_APP_KEY_PRODUCTION = Constants.UM_APP_KEY;
    private static final String UM_APP_KEY_TEST = Constants.UM_APP_KEY;

    public static void init(Context context) {
        boolean testEnv = BaseApplication.context.isTestEnv();
        String umAppKey = testEnv ? UM_APP_KEY_TEST : UM_APP_KEY_PRODUCTION;
        String channel = PhoneStatusManager.getInstance().getAppChannel();
        Logger.i(TAG, "init: umAppKey: {}, channel: {}, isDebug: {}", umAppKey, channel, testEnv);
        /*UMGameAgent.startWithConfigure(new UMGameAgent.UMAnalyticsConfig(context, umAppKey, channel));
        UMGameAgent.openActivityDurationTrack(false);
        UMGameAgent.setDebugMode(testEnv);
        UMGameAgent.init(context);*/
        //注意:即使您已经在AndroidManifest.xml中配置过appkey和channel值, 也需要在App代码中调用初始化接口(如需要使用AndroidManifest.xml中配置好的appkey和channel值, UMConfigure.init调用中appkey和channel参数请置为null
        //参数1:上下文, 不能为空;参数2:AppKey;参数3:Channel;参数4:设备类型, UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机;参数5:Push推送业务的secret
        UMConfigure.init(context, umAppKey, channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        //设置组件化的Log开关(默认为false, 如需查看LOG设置为true)
        UMConfigure.setLogEnabled(testEnv);
        //设置日志加密, 默认为false(不加密)
        UMConfigure.setEncryptEnabled(false);
        //选用AUTO页面采集模式(在Android4.0以上设备中, 推荐使用系统自动监控机制进行页面及基础指标自动埋点[AUTO模式下SDK会自动调用MobclickAgent.onResume/MobclickAgent.onPause接口，用户无须手动调用这两个接口]).
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        //是否上报异常(false:关闭错误统计功能;true:打开错误统计功能), 默认打开.
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    public static void onResume(Context context) {
        Logger.i(TAG, "onResume: {}", context);
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        Logger.i(TAG, "onPause: {}", context);
        MobclickAgent.onPause(context);
    }

    public static void onPageStart(String pageName, Object object) {
        Logger.i(TAG, "onPageStart: {}, {}", pageName, object);
        if (StringUtils.isBlank(pageName)) {
            return;
        }
        MobclickAgent.onPageStart(pageName);
    }

    public static void onPageEnd(String pageName, Object object) {
        Logger.i(TAG, "onPageEnd: {}, {}", pageName, object);
        if (StringUtils.isBlank(pageName)) {
            return;
        }
        MobclickAgent.onPageEnd(pageName);
    }

    public static void reportError(Throwable throwable) {
        Logger.e(TAG, "reportError: {}", throwable);
        //UMGameAgent.reportError(BaseApplication.context.globalContext, throwable);
        MobclickAgent.reportError(BaseApplication.context.globalContext, throwable);
    }

    public static void onProfileSignIn(String userId) {
        Logger.i(TAG, "onProfileSignIn: {}", userId);
        //UMGameAgent.onProfileSignIn(userId);
        MobclickAgent.onProfileSignIn(userId);
    }

    public static void onProfileSignIn(String channel, String userId) {
        Logger.i(TAG, "onProfileSignIn: {}, {}", channel, userId);
        //UMGameAgent.onProfileSignIn(channel, userId);
        MobclickAgent.onProfileSignIn(channel, userId);
    }

    public static void onProfileSignOff() {
        Logger.i(TAG, "onProfileSignOff");
        //UMGameAgent.onProfileSignOff();
        MobclickAgent.onProfileSignOff();
    }

    /**
     * 上报自定义节点.
     * @param eventName 节点名称.
     * @param paramMap
     */
    public static void onEvent(Context context, String eventName, Map<String, String> paramMap)
    {
        Logger.i(TAG, "onEvent: {}, {}", eventName, paramMap);
        MobclickAgent.onEvent(context, eventName, paramMap);
    }

    public static EventMethods onEvent() {
        return CustomEventMgr.getInstance().onEvent();
    }

    public static void bookUV(final String bookId, final MtMap<String, String> params) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("bookUV").append(bookId);
//        String eventId = builder.toString();
//        MobclickAgent.onEvent(BaseApplication.context.globalContext, eventId, params);
    }

    public static void onKillProcess(Context context)
    {
        Logger.i(TAG, "onKillProcess: {}", context);
        MobclickAgent.onKillProcess(context);
    }
}
