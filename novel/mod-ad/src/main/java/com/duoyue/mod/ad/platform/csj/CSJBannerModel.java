package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class CSJBannerModel extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";

    private TTAdNative mTTAdNative;
    private AdSlot adSlot;
    private TTNativeExpressAd mTTAd;
    private int refreshTime;
    private boolean countDownAd;
    private AdCallbackListener uiAdListener;

    public CSJBannerModel(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        //一定要在初始化后才能调用，否则为空
        try {
            CSJManager.getInstance().init(activity.getApplication(), mAdSiteBean.getAdAppId());
            TTAdManager manager = TTAdSdk.getAdManager();
            mTTAdNative = manager.createAdNative(activity);//baseContext建议为activity
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init(final ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        this.adContainerView = adContainer;
        addListener(adListener);
        this.uiAdListener = adListener;
        this.refreshTime = refreshTime;
        adSlot = new AdSlot.Builder()
                .setCodeId(mAdSiteBean.getAdId())
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setExpressViewAcceptedSize(ViewUtils.px2dp(PhoneUtil.getScreenSize(mActivity)[0]), 56)
                .setImageAcceptedSize(PhoneUtil.getScreenSize(mActivity)[0],  ViewUtils.dp2px(56))
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
//            adContainerView.removeAllViews();
            mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(final int code, final String message) {
                    if (countDownAd) {
                        CSJBannerModel.this.onError(" " + code, ", message: " + message);
                    } else {
                        uiAdListener.onError(mAdSiteBean, " " + code,   message);
                    }
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                    if (countDownAd && (ads == null || ads.size() == 0)){
                        CSJBannerModel.this.pullFailed(ERROR_CODE_NO_AD, "没有广告");
                        return;
                    }
                    mTTAd = ads.get(0);
//                    mTTAd.setSlideIntervalTime(refreshTime * 1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                    bindAdListener(mTTAd);
                    mTTAd.render();//调用render开始渲染广告
                }
            });
        }
    }

    //绑定广告行为
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                onClick();
            }

            @Override
            public void onAdShow(View view, int type) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (countDownAd) {
                            countDownAd = false;
                            onShow();
                        } else {
                            uiAdListener.onShow(mAdSiteBean);
                        }
                    }
                });
            }

            @Override
            public void onRenderFail(View view, final String msg, final int code) {
                if (countDownAd) {
                    onError("" + code, msg);
                } else {
                    uiAdListener.onError(mAdSiteBean, " " + code, msg);
                }
            }

            @Override
            public void onRenderSuccess(final View view, float width, float height) {
                //返回view的宽高 单位 dp
                //在渲染成功回调时展示广告，提升体验
                adContainerView.removeAllViews();
                adContainerView.addView(view);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
        Logger.e(TAG, "穿山甲Banner广告被销毁了");
    }


}
