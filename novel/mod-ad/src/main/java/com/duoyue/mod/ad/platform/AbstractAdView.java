package com.duoyue.mod.ad.platform;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.ITransformAd;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.listener.StatisticsListener;
import com.zydm.base.common.BaseApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractAdView implements IAdView, StatisticsListener {

    public static final String ERROR_CODE_TIMEOUT = "-990";
    public static final String ERROR_CODE_NO_AD = "-991";
    public static final String ERROR_CODE_UI_DESTROY = "-992";
    public static final String ERROR_CODE_EXCEPTION = "-993";

    private String TAG = "ad#AbstractBannerView";
    private static final long TIMEOUT_VALUE = 15_000;

    protected HashSet<AdCallbackListener> mListenerSet = new HashSet();
    protected Activity mActivity;
    protected AdSiteBean mAdSiteBean;
    protected String prePageId, modelId, source;
    protected Handler mHandler;
    protected ViewGroup adContainerView;
    private boolean hasProcess;

    protected Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasProcess) {
                onError(ERROR_CODE_TIMEOUT, "timeout");
            }
        }
    };

    public AbstractAdView(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        this.mActivity = activity;
        this.mAdSiteBean = adSiteBean;
        mListenerSet.add(adListener);
        mHandler = BaseApplication.handler;
    }

    public void addListener(AdCallbackListener listener) {
        if (listener != null) {
            mListenerSet.add(listener);
        }
    }

    // ---------------------------监听广告状态------------------------------------

    @Override
    public void pull() {
        hasProcess = false;
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.pull(mAdSiteBean);
            }
        }
        if (mHandler != null) {
            mHandler.postDelayed(timeoutRunnable, TIMEOUT_VALUE);
        }
    }

    @Override
    public void pullFailed(String code, String errorMsg) {
        hasProcess = true;
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.pullFailed(mAdSiteBean, code, errorMsg);
            }
        }
    }

    @Override
    public void onShow() {
        hasProcess = true;
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.onShow(mAdSiteBean);
            }
        }
    }

    @Override
    public void onClick() {
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.onClick(mAdSiteBean);
            }
        }
    }

    @Override
    public void onError(String code, String errorMsg) {
        hasProcess = true;
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.onError(mAdSiteBean, code, errorMsg);
            }
        }
    }

    @Override
    public void onDismiss() {
        for (AdCallbackListener listener : mListenerSet) {
            if (listener != null) {
                listener.onDismiss(mAdSiteBean);
            }
        }
    }

    @Override
    public void destroy() {
        hasProcess = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    protected ITransformAd transform(final AdSiteBean adParam, final TTFeedAd nativeAd, final View adView) {
        return new ITransformAd() {

            @Override
            public AdSiteBean getAdParam() {
                return adParam;
            }

            @Override
            public Bitmap getAdLogo() {
                return nativeAd.getAdLogo();
            }

            @Override
            public String getAdLogoUrl() {
                return null;
            }

            @Override
            public String getTitle() {
                return nativeAd.getTitle();
            }

            @Override
            public String getDescription() {
                return nativeAd.getDescription();
            }

            @Override
            public String getSource() {
                return nativeAd.getSource();
            }

            @Override
            public String getIcon() {
                return nativeAd.getIcon().getImageUrl();
            }

            @Override
            public List<String> getImageList() {
                List<String> imgList = new ArrayList<>();
                if (nativeAd.getImageList() != null && nativeAd.getImageList().size() > 0) {
                    for (TTImage img : nativeAd.getImageList()) {
                        if (img != null && !TextUtils.isEmpty(img.getImageUrl())) {
                            imgList.add(img.getImageUrl());
                        }
                    }
                }
                return imgList;
            }

            @Override
            public int getInteractionType() {
                int adType = 0;
                switch (nativeAd.getInteractionType()) {
                    case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                        adType = 1;
                        break;
                    case TTAdConstant.INTERACTION_TYPE_BROWSER:
                        adType = 2;
                        break;
                    case TTAdConstant.INTERACTION_TYPE_DIAL:
                        //setText("立即拨打");
                        adType = 3;
                        break;
                }
                return adType;
            }

            @Override
            public int getImageMode() {
//                TTAdConstant.IMAGE_MODE_SMALL_IMG
//                TTAdConstant.IMAGE_MODE_LARGE_IMG
//                TTAdConstant.IMAGE_MODE_GROUP_IMG
//                TTAdConstant.IMAGE_MODE_VIDEO
                return nativeAd.getImageMode();
            }

            @Override
            public void registerViewForInteraction(ViewGroup viewGroup, View clickView, View creativeView) {
                //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
                List<View> creativeViewList = new ArrayList<>();
                creativeViewList.add(creativeView);
                nativeAd.registerViewForInteraction((ViewGroup) viewGroup, creativeViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, TTNativeAd ad) {
                        onClick();
                        Logger.e("ad#cjs", "onAdClicked穿山甲信息流被点击了");
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        onClick();
                        Logger.e("ad#cjs", "onAdCreativeClick穿山甲信息流被点击了");
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {

                    }
                });
            }

            @Override
            public void registerViewForInteraction(ViewGroup viewGroup, List<View> var2, List<View> var3) {

            }

            @Override
            public View getAdView() {
                if (nativeAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
                    return nativeAd.getAdView();
                } else {
                    return adView;
                }
            }

            @Override
            public void render() {
            }

            @Override
            public void destroy() {
            }

            @Override
            public String getChannalCode() {
                return adParam.getChannelCode();
            }
        };
    }

}
