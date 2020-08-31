package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.zydm.base.utils.ViewUtils;

public class CSJBanner extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private TTAdNative mTTAdNative;
    private AdSlot adParam;
    private boolean countDownAd;
    private AdCallbackListener uiAdListener;

    public CSJBanner(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        //一定要在初始化后才能调用，否则为空
        try {
            CSJManager.getInstance().init(activity.getApplication(), mAdSiteBean.getAdAppId());
            TTAdSdk.getAdManager();
            mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity);//baseContext建议为activity
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        this.adContainerView = adContainer;
        addListener(adListener);
        uiAdListener = adListener;
        adParam = new AdSlot.Builder()
                .setNativeAdType(AdSlot.TYPE_BANNER)
                .setCodeId(mAdSiteBean.getAdId())
                .setAdCount(Integer.MAX_VALUE)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 100)
                .build();
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
            countDownAd = true;
            mTTAdNative.loadBannerAd(adParam, bannerAdListener);
        }
    }

    private TTAdNative.BannerAdListener bannerAdListener = new TTAdNative.BannerAdListener() {

        @Override
        public void onError(int code, String message) {
            //加载失败的回调 详见3.1错误码说明
            if (countDownAd) {
                CSJBanner.this.onError("" + code, "csj banner load error : " + code + ", " + message);
                adContainerView.removeAllViews();
            }
        }

        @Override
        public void onBannerAdLoad(TTBannerAd ad) {
            if (countDownAd && (ad == null || mActivity == null)) {
                pullFailed(ERROR_CODE_NO_AD, "no Ad");
                return;
            }
            View bannerView = ad.getBannerView();
            if (bannerView == null && countDownAd) {
                pullFailed(ERROR_CODE_NO_AD, "no Ad");
                return;
            }
            //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
//                ad.setSlideIntervalTime(30 * 1000);
            adContainerView.removeAllViews();
            float scaneX = ViewUtils.dp2px(50) / 100f;
            Logger.e(TAG, "scanleX =  " + scaneX);
            adContainerView.setScaleX(scaneX);
            adContainerView.setScaleY(scaneX);
            adContainerView.addView(bannerView);
            if (countDownAd) {
                countDownAd = false;
                onShow();
            } else {
                uiAdListener.onShow(mAdSiteBean);
            }
            //设置广告互动监听回调
            ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    onClick();
                }

                @Override
                public void onAdShow(View view, int type) {

                }
            });
            //（可选）设置下载类广告的下载监听
//                bindDownloadListener(ad);
            //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
            /*ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    //用户选择不喜欢原因后，移除广告展示
                    adContainerView.removeAllViews();
                    onDismiss();
                }

                @Override
                public void onCancel() {

                }
            });*/
        }

    };

    private void bindDownloadListener(TTBannerAd ad) {
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
//                TToast.show(BannerActivity.this, "点击图片开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
//                    TToast.show(BannerActivity.this, "下载中，点击图片暂停", Toast.LENGTH_LONG);
//                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(BannerActivity.this, "下载暂停，点击图片继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(BannerActivity.this, "下载失败，点击图片重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                TToast.show(BannerActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                TToast.show(BannerActivity.this, "点击图片安装", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        Logger.e(TAG, "穿山甲Banner广告被销毁了");
    }


}
