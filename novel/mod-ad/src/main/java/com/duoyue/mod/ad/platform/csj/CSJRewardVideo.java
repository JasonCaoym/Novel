package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.ViewUtils;

public class CSJRewardVideo extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private TTAdNative mTTAdNative;
    private AdSlot adSlot;
    private TTRewardVideoAd rewardVideoAd;
    private long startTime;
    private boolean isDownloading;

    public CSJRewardVideo(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
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

        adSlot = new AdSlot.Builder()
                .setCodeId(mAdSiteBean.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(ViewUtils.getPhonePixels()[0], ViewUtils.getPhonePixels()[1])
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
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
            startTime = System.currentTimeMillis();
            mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
                @Override
                public void onError(int code, String message) {
                    AdHttpUtil.vidioPlayFail(mAdSiteBean);
                    CSJRewardVideo.this.onError(""+ code, "穿山甲视频播放失败");
                }

                //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
                @Override
                public void onRewardVideoCached() {
                    if (mActivity != null) {
                        try {
                            AdHttpUtil.vidioCacheSuccess(mAdSiteBean, System.currentTimeMillis() - startTime);
                            rewardVideoAd.showRewardVideoAd(mActivity);
                        } catch (Exception ex) {
                            pullFailed(ERROR_CODE_EXCEPTION, ex.getMessage());
                        }
                    } else {
                        pullFailed(ERROR_CODE_UI_DESTROY,"activity not exist");
                    }
                }

                //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
                @Override
                public void onRewardVideoAdLoad(TTRewardVideoAd mttRewardVideoAd) {
                    rewardVideoAd = mttRewardVideoAd;
                    mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                        @Override
                        public void onAdShow() {
                            onShow();
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getREWARD_CLICKED(), true);
                            onClick();
                        }

                        @Override
                        public void onAdClose() {
                            onDismiss();
                        }

                        //视频播放完成回调
                        @Override
                        public void onVideoComplete() {
                            SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getREWARD_COMPLETE(), true);
                            AdHttpUtil.vidioPlaySuccess(mAdSiteBean);
                        }

                        @Override
                        public void onVideoError() {
                            CSJRewardVideo.this.onError(ERROR_CODE_EXCEPTION, "rewardVideoAd: onVideoError");
                            AdHttpUtil.vidioPlayFail(mAdSiteBean);
                        }

                        //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                        @Override
                        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                            AdHttpUtil.vidioTask(mAdSiteBean);
                        }

                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                    rewardVideoAd.setDownloadListener(new TTAppDownloadListener() {

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!isDownloading) {
                                isDownloading = true;
                                AdHttpUtil.vidioAPPDownload(mAdSiteBean);
                            }
                        }

                        @Override
                        public void onDownloadPaused(long l, long l1, String s, String s1) {
                            AdHttpUtil.vidioAPPDownloadPause(mAdSiteBean);
                        }

                        @Override
                        public void onDownloadFailed(long l, long l1, String s, String s1) {
                            AdHttpUtil.vidioAPPDownloadFail(mAdSiteBean);
                        }

                        @Override
                        public void onDownloadFinished(long l, String s, String s1) {

                        }

                        @Override
                        public void onInstalled(String s, String s1) {
                            AdHttpUtil.vidioAPPInstall(mAdSiteBean);
                        }
                    });
                }
            });
        }
    }


    @Override
    public void destroy() {
        super.destroy();
        Logger.e(TAG, "穿山甲激励视频广告被销毁了");
    }


}
