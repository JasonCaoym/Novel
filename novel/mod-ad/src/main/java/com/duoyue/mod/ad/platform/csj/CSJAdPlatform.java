package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.IAdPlatform;
import com.duoyue.mod.ad.IAdSource;

public class CSJAdPlatform implements IAdPlatform {

    public static final String APP_ID = "5001121";
    public static final String SPLASH_ID = "801121648"; // 启动页广告
    public static final String BANNER_ID = "901121895"; // 横幅广告
    public static final String INTERACTION_ID = "901121725"; // 插屏广告
    public static final String REWARDVIDEO_ID = "901121365"; // 激励视频
    public static final String NATIVE_LIST_ID = "901121737"; // 原生信息流广告

    private TTAdConfig mTTAdConfig;

    @Override
    public void init(Context context, String appId) {
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        if (TextUtils.isEmpty(appId)) {
            return;
        }
        try {
            mTTAdConfig = new TTAdConfig.Builder()
                    .appId("5026423")
                    .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                    .appName("APP测试媒体")
                    .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                    .allowShowNotify(true) //是否允许sdk展示通知栏提示
                    .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                    .debug(Logger.isDebug()) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                    .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                    .supportMultiProcess(false) //是否支持多进程，true支持
                    .build();
            TTAdSdk.init(context.getApplicationContext(), mTTAdConfig);
        } catch (Exception ex) {
            Logger.e("ad#", "csj init error : " + ex.getMessage());
        }
    }

    /**
     * 设置AppId.
     * @param appId
     */
    public void setAppId(String appId)
    {
        if (mTTAdConfig != null)
        {
            mTTAdConfig.setAppId(appId);
        }
    }

    @Override
    public IAdSource createSource(Activity context) {
        return new CSJAdSource(context);
    }
}
