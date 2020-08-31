package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import com.zydm.base.utils.SPUtils;

public class GDTRewardVideo extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";


    private long startTime;
    private RewardVideoAD rewardVideoAD;

    public GDTRewardVideo(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        addListener(adListener);

        rewardVideoAD = new RewardVideoAD(mActivity.getApplication(), mAdSiteBean.getAdAppId(), mAdSiteBean.getAdId(), new RewardVideoADListener() {

            @Override
            public void onADLoad() {
                if (mActivity != null) {
                    try {
                        rewardVideoAD.showAD();
                    } catch (Exception ex) {
                        GDTRewardVideo.this.onError("", ex.getMessage());
                        AdHttpUtil.vidioPlayFail(mAdSiteBean);
                    }
                } else {

                }
            }

            @Override
            public void onVideoCached() {
                AdHttpUtil.vidioCacheSuccess(mAdSiteBean, System.currentTimeMillis() - startTime);
            }

            @Override
            public void onADShow() {
            }

            @Override
            public void onADExpose() {
                onShow();
            }

            @Override
            public void onReward() {
                AdHttpUtil.vidioTask(mAdSiteBean);
            }

            @Override
            public void onADClick() {
                SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getREWARD_CLICKED(), true);
                onClick();
            }

            @Override
            public void onVideoComplete() {
                SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getREWARD_COMPLETE(), true);
                AdHttpUtil.vidioPlaySuccess(mAdSiteBean);
            }

            @Override
            public void onADClose() {
                onDismiss();
            }

            @Override
            public void onError(AdError adError) {
                if (adError != null) {
                    GDTRewardVideo.this.onError("" + adError.getErrorCode(), adError.getErrorMsg());
                } else {
                    GDTRewardVideo.this.onError("", "onNoAd");
                }
                AdHttpUtil.vidioPlayFail(mAdSiteBean);
            }
        });
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (rewardVideoAD != null) {
            pull();
            startTime = System.currentTimeMillis();
            rewardVideoAD.loadAD();
        }
    }


    @Override
    public void destroy() {
        super.destroy();
        if (rewardVideoAD != null) {
            rewardVideoAD = null;
        }
        Logger.e(TAG, "广点通激励视频广告被销毁了");
    }


}
