package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.content.Context;
import com.duoyue.mod.ad.IAdPlatform;
import com.duoyue.mod.ad.IAdSource;


public class GDTAdPlatform implements IAdPlatform {

    public static final String APP_ID = "1101152570";
    public static final String SPLASH_ID = "8863364436303842593"; // 启动页广告
    public static final String BANNER_ID = "9079537218417626401"; // 横幅广告
    public static final String INTERACTION_ID = "8575134060152130849"; // 插屏广告
    public static final String NATIVE_LIST_ID = "6040749702835933"; // 原生信息流广告
    public static final String REWARDVIDEO_ID = "5040942242835423";// 广激励视频广告，不支持横屏

    @Override
    public void init(Context context, String appId) {

    }

    public void setAppId(String appId)
    {
    }

    @Override
    public IAdSource createSource(Activity activity) {
        return new GDTAdSource(activity);
    }

}
