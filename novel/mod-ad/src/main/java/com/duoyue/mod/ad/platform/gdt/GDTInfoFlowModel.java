package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class GDTInfoFlowModel extends AbstractAdView {
    private static final String TAG = "ad#AbstractBannerView";
    private NativeExpressAD nativeExpressAD;

    public GDTInfoFlowModel(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void init(final ViewGroup adContainer, View otherContainer, int refreshTime, final AdCallbackListener adListener) {
        addListener(adListener);
        nativeExpressAD = new NativeExpressAD(mActivity.getApplication(),
                new ADSize(ADSize.FULL_WIDTH - ViewUtils.dp2px(32f), mAdSiteBean.getAspectRatio() == 1 ? ((int) (ViewUtils.px2dp(PhoneUtil.getScreenSize(mActivity)[1]) * 0.6)) : (ADSize.AUTO_HEIGHT - ViewUtils.dp2px(32f))),
                mAdSiteBean.getAdAppId(), mAdSiteBean.getAdId(), new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                if (adContainer.getVisibility() != View.VISIBLE) {
                    adContainer.setVisibility(View.VISIBLE);
                }
                if (adContainer.getChildCount() > 0) {
                    adContainer.removeAllViews();
                }
                if (list.size() > 0) {
                    NativeExpressADView adView = list.get(0);
                    adContainer.addView(adView);
                    adView.render();
                } else {
                    pullFailed(ERROR_CODE_NO_AD, "no ad");
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                onError("", "onRenderFail");
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                // 开屏不需要主动调用展示，需要SplashActivity中判断
                if (mAdSiteBean.getChannelCode().equalsIgnoreCase(Constants.channalCodes[0])) {
                    adListener.onShow(mAdSiteBean);
                    Logger.e(TAG, "穿山甲开屏信息流--只回调splash监听");
                } else {
                    onShow();
                }
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                onClick();
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                onDismiss();
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onNoAD(AdError adError) {
                onDismiss();
                if (adError != null) {
                    onError("" + adError.getErrorCode(), adError.getErrorMsg());
                } else {
                    onError("", "onNoAd");
                }
            }
        }); // 这里的Context必须为Activity
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS) // 设置什么网络环境下可以自动播放视频
                .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
                .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置

    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        if (nativeExpressAD != null) {
            pull();
            nativeExpressAD.loadAD(1);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (nativeExpressAD != null) {
            nativeExpressAD = null;
        }
        Logger.e(TAG, "广点通信息流广告被销毁了");
    }
}
