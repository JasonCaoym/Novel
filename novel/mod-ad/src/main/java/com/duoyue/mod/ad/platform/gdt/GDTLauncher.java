package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

public class GDTLauncher extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private SplashAD splashAD;

    public GDTLauncher(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, final AdCallbackListener adListener) {
        addListener(adListener);
        this.adContainerView = adContainer;
        splashAD = new SplashAD(mActivity, mAdSiteBean.getAdAppId(), mAdSiteBean.getAdId(),
                new SplashADListener() {
                    @Override
                    public void onADDismissed() {
                        onDismiss();
                    }

                    @Override
                    public void onNoAD(AdError adError) {
                        if (adError != null) {
                            onError("" + adError.getErrorCode(), adError.getErrorMsg());
                        } else {
                            onError("", "onNoAd");
                        }
                        onDismiss();
                    }

                    @Override
                    public void onADPresent() {
                        // 在开屏中处理，两个开屏只报其中一个
                        adListener.onShow(mAdSiteBean);
                    }

                    @Override
                    public void onADClicked() {
                        onClick();
                    }

                    /**
                     * 倒计时回调，返回广告还将被展示的剩余时间。
                     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
                     *
                     * @param millisUntilFinished 剩余毫秒数
                     */
                    @Override
                    public void onADTick(long millisUntilFinished) {
                        adListener.onAdTick(millisUntilFinished);
                    }

                    @Override
                    public void onADExposure() {
                    }
                }, 2000);

    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (splashAD != null) {
            pull();
            splashAD.fetchAndShowIn(adContainerView);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (splashAD != null) {
            splashAD.preLoad();
            splashAD = null;
        }
        Logger.e(TAG, "广点通Launcher广告被销毁了");
    }
}
