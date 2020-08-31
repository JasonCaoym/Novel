package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.ITransformAd;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.duoyue.mod.ad.view.InfoFlowNativeSmallView;
import com.duoyue.mod.ad.view.InfoFlowNativeView;

import java.util.List;

public class CSJInfoFlowNative extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private TTAdNative mTTAdNative;
    private AdCallbackListener adListener;
    private InfoFlowNativeView nativeImgView;
    private InfoFlowNativeSmallView nativeSmallView;
    private ITransformAd nativeAd;

    public CSJInfoFlowNative(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        this.mAdSiteBean = adSiteBean;
        try {
            CSJManager.getInstance().init(activity.getApplication(), mAdSiteBean.getAdAppId());
            TTAdManager manager = TTAdSdk.getAdManager();
            mTTAdNative = manager.createAdNative(activity);//baseContext建议为activity
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init(final ViewGroup adContainer, View otherContainer, int refreshTime, final AdCallbackListener adListener) {
        addListener(adListener);
        this.adContainerView = adContainer;
        this.adListener = adListener;
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
            mTTAdNative.loadFeedAd(new AdSlot.Builder()
                    .setCodeId(mAdSiteBean.getAdId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(600, 200)
                    .setAdCount(1) //请求广告数量为1到3条
                    .build(), new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int code, String message) {
                    CSJInfoFlowNative.this.onError("" + code,  message);
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> list) {
                    if (list == null || list.isEmpty() || mActivity == null) {
                        pullFailed(ERROR_CODE_NO_AD, "no ad");
                        return;
                    }
                    // 开屏不需要主动调用展示，需要SplashActivity中判断
                    if (mAdSiteBean.getChannelCode().equalsIgnoreCase(Constants.channalCodes[0])) {
                        adListener.onShow(mAdSiteBean);
                        Logger.e(TAG, "穿山甲开屏信息流--只回调splash监听");
                    } else {
                        onShow();
                    }
                    nativeAd = transform(mAdSiteBean, list.get(0), adContainerView);
                    if (mAdSiteBean.getChannelCode().equals(Constants.channalCodes[11])) {
                        nativeSmallView = new InfoFlowNativeSmallView(mActivity, adContainerView, mAdSiteBean, nativeAd);
                        nativeAd.registerViewForInteraction(adContainerView, nativeSmallView.getClickView(), nativeSmallView.getClickView());
                    } else {
                        nativeImgView = new InfoFlowNativeView(mActivity, adContainerView, mAdSiteBean, nativeAd);
                        nativeAd.registerViewForInteraction(adContainerView, nativeImgView.getClickView(), nativeImgView.getClickView());
                    }
                }
            });
        }
    }

    public void updateDayModel(boolean isNightMode) {
        if (nativeSmallView != null && mAdSiteBean.getChannelCode().equals(Constants.channalCodes[11])) {
            nativeSmallView.updateDayModel(isNightMode);
        } else if (nativeImgView != null) {
            nativeImgView.updateDayModel(isNightMode);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        Logger.e(TAG, "穿山甲信息流广告被销毁了");
    }
}
