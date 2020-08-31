package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;
import com.zydm.base.utils.ViewUtils;

public class GDTBanner2 extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";
    private UnifiedBannerView bv;
    private boolean countDownAd;
    private AdCallbackListener uiAdListener;

    public GDTBanner2(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     *
     * @return
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams(Activity activity) {
        int[] screenSize = PhoneUtil.getScreenSize(activity);
        return new FrameLayout.LayoutParams(screenSize[0], ViewUtils.dp2px(56));
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        addListener(adListener);
        uiAdListener = adListener;
        if (this.bv != null) {
            adContainer.removeView(bv);
            bv.destroy();
        }

        this.bv = new UnifiedBannerView(mActivity, mAdSiteBean.getAdAppId(), mAdSiteBean.getAdId(), new UnifiedBannerADListener() {

            @Override
            public void onNoAD(final AdError adError) {
                if (countDownAd) {
                    if (adError != null) {
                        onError("" + adError.getErrorCode(), adError.getErrorMsg());
                    } else {
                        onError("", "onNoAd");
                    }
                } else {
                    if (adError != null) {
                        uiAdListener.onError(mAdSiteBean, "" + adError.getErrorCode(), adError.getErrorMsg());
                    } else {
                        uiAdListener.onError(mAdSiteBean, "", "onNoAD error");
                    }
                }
            }

            @Override
            public void onADReceive() {
            }

            @Override
            public void onADExposure() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bv != null) {
                            if (countDownAd) {
                                countDownAd = false;
                                onShow();
                            } else {
                                uiAdListener.onShow(mAdSiteBean);
                            }
                        }
                    }
                });
            }

            @Override
            public void onADClosed() {
                onDismiss();
            }

            @Override
            public void onADClicked() {
                onClick();
            }

            @Override
            public void onADLeftApplication() {

            }

            @Override
            public void onADOpenOverlay() {

            }

            @Override
            public void onADCloseOverlay() {

            }
        });
        this.bv.setRefresh(refreshTime);
        // 不需要传递tags使用下面构造函数
        // this.bv = new UnifiedBannerView(this, Constants.APPID, posId, this);
        adContainer.removeAllViews();
        adContainer.addView(bv, getUnifiedBannerLayoutParams(mActivity));
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (bv != null) {
            pull();
            countDownAd = true;
            bv.loadAD();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (bv != null) {
            bv.destroy();
            bv = null;
        }
        Logger.e(TAG, "广点通Banner广告被销毁了");
    }
}
