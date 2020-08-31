package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.duoyue.mod.stats.ErrorStatsApi;
import com.zydm.base.utils.ViewUtils;
import java.util.List;

/**
 * 穿山甲模版信息流广告.
 */
public class CSJExpressNative extends AbstractAdView {
    private static final String TAG = "ad#CSJExpressNative";

    private TTAdNative mTTAdNative;

    /**
     * 广告信息对象.
     */
    private TTNativeExpressAd mTTNativeExpressAd;

    private boolean mHasShowDownloadActive;

    private AdCallbackListener adListener;

    /**
     * 是否渲染失败.
     */
    private boolean isRenderFail = false;

    public CSJExpressNative(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
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
            try
            {
                //设置广告参数
                AdSlot adSlot = new AdSlot.Builder()
                        .setCodeId(mAdSiteBean.getAdId()) //广告位id
                        .setSupportDeepLink(true)
                        .setAdCount(1) //请求广告数量为1到3条
                        .setExpressViewAcceptedSize(ViewUtils.px2dp(PhoneUtil.getScreenSize(mActivity)[0]) - ViewUtils.dp2px(12f), 0) //期望个性化模板广告view的size,单位dp
                        .setImageAcceptedSize(600, 200) //这个参数设置即可，不影响个性化模板广告的size
                        .build();
                //加载广告
                mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                    @Override
                    public void onError(int code, String message) {
                        Logger.e(TAG, "穿上甲信息流模版广告加载失败:{}, {}", code, message);
                        CSJExpressNative.this.onError("" + code,  message);
                        if (adContainerView != null)
                        {
                            adContainerView.removeAllViews();
                        }
                    }
                    @Override
                    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                        if (ads == null || ads.size() == 0){
                            pullFailed(ERROR_CODE_NO_AD, "no ad");
                            return;
                        }
                        mTTNativeExpressAd = ads.get(0);
                        bindAdListener(mTTNativeExpressAd);
                        //调用render开始渲染广告.
                        mTTNativeExpressAd.render();
                    }
                });
            } catch (Throwable throwable)
            {
                ErrorStatsApi.addError(ErrorStatsApi.TT_NATIVE_TEMP_FAIL, "loadNativeExpressAd, Throwable:" + Logger.getStackTraceString(throwable));
                Logger.e(TAG, "loadNativeExpressAd Throwable:{}", throwable);
            }
        }
    }

    /**
     * 绑定广告行为
     * @param ad
     */
    private void bindAdListener(TTNativeExpressAd ad)
    {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener()
        {
            @Override
            public void onAdClicked(View view, int type)
            {
                onClick();
            }
            @Override
            public void onAdShow(View view, int type)
            {
            }
            @Override
            public void onRenderFail(View view, String msg, int code)
            {
                Logger.e(TAG, "穿上甲信息流模版广告渲染失败:{}, {}", code, msg);
                ErrorStatsApi.addError(ErrorStatsApi.TT_NATIVE_TEMP_FAIL, "onRenderFail, code:" + code + ", msg:" + msg);
                //记录渲染失败标识.
                isRenderFail = true;
                onError(String.valueOf(code), msg);
            }
            @Override
            public void onRenderSuccess(View view, float width, float height)
            {
                //返回view的宽高, 单位dp
                Logger.i(TAG, "穿山甲模版信息流广告渲染成功:{}, {}, {}", view, width, height);
                //渲染成功标识.
                isRenderFail = false;
                // 开屏不需要主动调用展示，需要SplashActivity中判断
                if (mAdSiteBean.getChannelCode().equalsIgnoreCase(Constants.channalCodes[0])) {
                    adListener.onShow(mAdSiteBean);
                    Logger.e(TAG, "穿山甲开屏模版信息流--只回调splash监听");
                } else {
                    onShow();
                }
                //在渲染成功回调时展示广告，提升体验
                if (adContainerView != null)
                {
                    adContainerView.removeAllViews();
                    adContainerView.addView(view);
                }
            }
        });
        //dislike设置
        //bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        //可选，下载监听设置
        ad.setDownloadListener(new TTAppDownloadListener()
        {
            @Override
            public void onIdle()
            {
                Logger.i(TAG, "穿山甲模版信息流广告开始下载.");
            }
            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName)
            {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Logger.i(TAG, "穿山甲模版信息流广告下载中:{}, {}, {}, {}", totalBytes, currBytes, fileName, appName);
                }
            }
            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName)
            {
                Logger.i(TAG, "穿山甲模版信息流广告暂停下载:{}, {}, {}, {}", totalBytes, currBytes, fileName, appName);
            }
            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName)
            {
                Logger.e(TAG, "穿山甲模版信息流广告下载失败:{}, {}, {}, {}", totalBytes, currBytes, fileName, appName);
            }
            @Override
            public void onInstalled(String fileName, String appName) {
                Logger.i(TAG, "穿山甲模版信息流广告安装完成:{}, {}", fileName, appName);
            }
            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName)
            {
                Logger.i(TAG, "穿山甲模版信息流广告下载完成:{}, {}, {}", totalBytes, fileName, appName);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        //非渲染失败的情况下才回收, 防止渲染失败回收资源, 导致异常:java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.bytedance.sdk.openadsdk.AdSlot.getCodeId()' on a null object reference
        if (mTTNativeExpressAd != null && !isRenderFail) {
            mTTNativeExpressAd.destroy();
        }
        Logger.e(TAG, "穿山甲模版信息流广告被销毁了:{}", isRenderFail);
    }
}
