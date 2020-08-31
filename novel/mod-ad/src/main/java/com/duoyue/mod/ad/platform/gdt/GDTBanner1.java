package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

public class GDTBanner1 extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";
    private BannerView bannerView;
    private boolean countDownAd;
    private AdCallbackListener uiAdCallbackListener;

    public GDTBanner1(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        addListener(adListener);
        uiAdCallbackListener = adListener;
        bannerView = new BannerView(mActivity, com.qq.e.ads.banner.ADSize.BANNER, mAdSiteBean.getAdAppId(), mAdSiteBean.getAdId());
        bannerView.setRefresh(refreshTime);
        bannerView.setShowClose(false);
        bannerView.setADListener(new AbstractBannerADListener() {

            @Override
            public void onADClosed() {
                onDismiss();
            }

            @Override
            public void onADClicked() {
                onClick();
            }

            @Override
            public void onNoAD(AdError error) {
                if (countDownAd) {
                    if (error != null) {
                        onError("" + error.getErrorCode(), error.getErrorMsg());
                    } else {
                        onError("", "onNoAd");
                    }
                }
            }

            @Override
            public void onADReceiv() {
                if (countDownAd) {
                    onShow();
                } else {
                    uiAdCallbackListener.onShow(mAdSiteBean);
                }
            }
        });
        adContainer.addView(bannerView);
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (bannerView != null) {
            pull();
            countDownAd = true;
            bannerView.loadAD();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
        Logger.e(TAG, "广点通Banner广告被销毁了");
    }
}
