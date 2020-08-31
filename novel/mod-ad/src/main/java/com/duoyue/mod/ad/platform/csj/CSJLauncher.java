package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;

public class CSJLauncher extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private TTAdNative mTTAdNative;
    private AdCallbackListener adListener;

    public CSJLauncher(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        try {
            CSJManager.getInstance().init(activity.getApplication(), mAdSiteBean.getAdAppId());
            TTAdSdk.getAdManager();
            mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity);//baseContext建议为activity
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init(final ViewGroup adContainer, View otherContainer, int refreshTime, final AdCallbackListener adListener) {
        addListener(adListener);
        this.adListener = adListener;
        this.adContainerView = adContainer;
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (mTTAdNative != null) {
            pull();
            mTTAdNative.loadSplashAd(new AdSlot.Builder()
                    .setCodeId(mAdSiteBean.getAdId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(PhoneUtil.getScreenSize(mActivity.getApplication())[0],
                            PhoneUtil.getScreenSize(mActivity.getApplication())[1])
                    .build(), new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    Logger.e(TAG, message);
                    CSJLauncher.this.onError("" + code,  message);
                    onDismiss();
                }

                @Override
                @MainThread
                public void onTimeout() {
                    CSJLauncher.this.onError(ERROR_CODE_TIMEOUT, "timeout");
                    onDismiss();
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
                    if (ad == null) {
                        pullFailed(ERROR_CODE_NO_AD, "no ad");
                        return;
                    }
                    // 只回调Splash的回调
                    adListener.onShow(mAdSiteBean);
                    View view = ad.getSplashView();
                    adContainerView.removeAllViews();
                    //把SplashView 添加到ViewGroup中
                    adContainerView.addView(view);
                    //设置SplashView的交互监听器
                    ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            onClick();
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                        }

                        @Override
                        public void onAdSkip() {
                            onDismiss();
                        }

                        @Override
                        public void onAdTimeOver() {
                            onDismiss();
                        }
                    });
                }
            }, 2000);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        Logger.e(TAG, "穿山甲Launcher广告被销毁了");
    }
}
