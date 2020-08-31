package com.duoyue.mod.ad.platform.baidu;

import android.app.Activity;
import android.content.Context;
import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.duoyue.mod.ad.IAdPlatform;
import com.duoyue.mod.ad.IAdSource;


public class BaiDuAdPlatform implements IAdPlatform {

    public static final String APP_ID = "e866cfb0";
    public static final String SPLASH_ID = "2058622"; // 启动页广告
    public static final String BANNER_ID = "2015351"; // 横幅广告
    public static final String InterteristalPosID = "2403633"; // 插屏广告
    public static final String NATIVE_LIST_ID = "2058628"; // 原生信息流
    public static final String REWARDVIDEO_ID = "5925490";//激励视频广告

    @Override
    public void init(Context context, String appId) {
        // 无法设置，只能使用AndroidManirfest进行配置id
        AdSettings.setSupportHttps(false);
        //设置AppId.
        setAppId(context, appId);
    }

    /**
     * 设置AppId.
     * @param context
     * @param appId
     */
    public void setAppId(Context context, String appId)
    {
        AdView.setAppSid(context, appId);
    }

    @Override
    public IAdSource createSource(Activity activity) {
        return new BaiDuAdSource(activity);
    }

}
