package com.duoyue.mod.ad.platform.gdt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.AdSourceProxy;
import com.duoyue.mod.ad.IAdSource;
import com.duoyue.mod.ad.NativeAd;
import com.duoyue.mod.ad.bean.AdOriginConfigBean;
import com.duoyue.mod.ad.listener.ADListener;
import com.duoyue.mod.ad.listener.AdCallback;
import com.duoyue.mod.ad.listener.SplashAdListener;
import com.duoyue.mod.ad.view.AdNativeImgView;
import com.duoyue.mod.ad.view.ListItemCommAd;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.nativ.*;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GDTAdSource implements IAdSource, ADListener {

    private static final String TAG = "ad#GDTAdSource";
    private Activity mActivity;
    private InterstitialAD iad;
    private AdOriginConfigBean mAdParam;
    private long preTime;
    private NativeExpressADView nativeExpressADView;

    public GDTAdSource(Activity activity) {
        mActivity = activity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }


    private HashSet<ADListener> mListenerSet = new HashSet();
    private RewardVideoAD rewardVideoAD;
    private BannerView bannerView;

    @Override
    public void addListener(ADListener adListener) {
        mListenerSet.add(adListener);
    }

    @Override
    public void loadBannerAd(final AdOriginConfigBean originBean, ViewGroup containerView, final ADListener adListener) {
        pull(originBean);
        mListenerSet.add(adListener);
        bannerView = new BannerView(mActivity, com.qq.e.ads.banner.ADSize.BANNER, originBean.getAdAppId(),
                originBean.getAdId());
        bannerView.setRefresh(30);
        bannerView.setShowClose(false);
        bannerView.setADListener(new AbstractBannerADListener() {

            @Override
            public void onADClosed() {
                onDismiss(originBean);
            }

            @Override
            public void onADClicked() {
                onClick(originBean);
            }

            @Override
            public void onNoAD(AdError error) {
                onError(originBean, String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                        error.getErrorMsg()));
            }

            @Override
            public void onADReceiv() {
                onShow(originBean);
            }
        });
        containerView.addView(bannerView);
        bannerView.loadAD();
    }

    @Override
    public void loadSplashAd(final AdOriginConfigBean adParam, ViewGroup containerView, View skipContainer, final SplashAdListener bannerListener) {
        pull(adParam);
        SplashAD splashAD = new SplashAD(mActivity, skipContainer, adParam.getAdAppId(), adParam.getAdId(),
                new SplashADListener() {
                    @Override
                    public void onADDismissed() {
                        bannerListener.onDismiss();
                    }

                    @Override
                    public void onNoAD(AdError adError) {
                        onError(adParam, adError == null ? "" : adError.getErrorMsg());
                        bannerListener.onDismiss();
                    }

                    @Override
                    public void onADPresent() {
                        onShow(adParam);
                        bannerListener.onShow(true);
                    }

                    @Override
                    public void onADClicked() {
                        onClick(adParam);
                        bannerListener.onClick();
                    }

                    /**
                     * 倒计时回调，返回广告还将被展示的剩余时间。
                     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
                     *
                     * @param millisUntilFinished 剩余毫秒数
                     */
                    @Override
                    public void onADTick(long millisUntilFinished) {
                        bannerListener.onAdTick(millisUntilFinished);
                    }

                    @Override
                    public void onADExposure() {
                    }
                }, 0);
        splashAD.fetchAndShowIn(containerView);
    }

    @Override
    public void loadInteractionAd(final AdOriginConfigBean adParam, final ADListener loadListener) {
        pull(adParam);
        mListenerSet.add(loadListener);
        getIAD(adParam).setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(AdError error) {
               onError(adParam, String.format("LoadInterstitialAd Fail, error code: %d, error msg: %s",
                                error.getErrorCode(), error.getErrorMsg()));
            }

            @Override
            public void onADReceive() {
                onShow(adParam);
                iad.showAsPopupWindow();
            }

            @Override
            public void onADClosed() {
                onDismiss(adParam);
            }
        });
        iad.loadAD();
    }

    @Override
    public void loadRewardVideoAD(final AdOriginConfigBean adParam, final ADListener adListener) {
        mAdParam = adParam;
        mListenerSet.add(adListener);
        pull(adParam);
        preTime = System.currentTimeMillis();
        rewardVideoAD = new RewardVideoAD(mActivity.getApplication(), adParam.getAdAppId(), adParam.getAdId(), new RewardVideoADListener() {

            @Override
            public void onADLoad() {
                if (System.currentTimeMillis() - preTime < AdSourceProxy.REWORD_VIDEO_TIMEOUT) {
                    try {
                        rewardVideoAD.showAD();
                    } catch (Exception ex) {
                        GDTAdSource.this.onError(mAdParam, "视频播放失败: " + ex.getMessage());
                    }
                } else {
                    rewardVideoAD.loadAD();
                }
            }

            @Override
            public void onVideoCached() {
            }

            @Override
            public void onADShow() {
            }

            @Override
            public void onADExpose() {
                Logger.e(TAG,"onADShow");
                onShow(mAdParam);
            }

            @Override
            public void onReward() {
                Logger.e(TAG,"onReward");
            }

            @Override
            public void onADClick() {
                Logger.e(TAG,"onADClick");
                onClick(mAdParam);
            }

            @Override
            public void onVideoComplete() {
            }

            @Override
            public void onADClose() {
                Logger.e(TAG,"onADClose");
                onDismiss(mAdParam);
            }

            @Override
            public void onError(AdError adError) {
                GDTAdSource.this.onError(mAdParam, TAG + ", ip" + mAdParam.getAdAppId() + ", site id : " + mAdParam.getAdId()
                        + ", code : " + adError.getErrorCode() + ", msg: " + adError.getErrorMsg());
            }
        });
        rewardVideoAD.loadAD();
    }

    @Override
    public void loadCommonAd(final AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height,
                             final AdCallback callback) {
        pull(adParam);
        NativeUnifiedAD nativeExpressAD = new NativeUnifiedAD(mActivity.getApplication(), adParam.getAdAppId(), adParam.getAdId(),
                new NativeADUnifiedListener() {
                    @Override
                    public void onADLoaded(List<NativeUnifiedADData> list) {
                        if (list == null || list.size() == 0 || mActivity == null) {
                            GDTAdSource.this.pullFailed(adParam);
                            return;
                        }
                        NativeUnifiedADData unifiedADData = list.get(0);
                        NativeAd nativeAd = transform(adParam, unifiedADData, containerView);
                        final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView, nativeAd, false);
                        // 自带广告字样
                        nativeImgView.hideAdLable();
//                        nativeImgView.setAdListener(getADListener(adParam));
                        nativeAd.registerViewForInteraction(containerView, nativeImgView.getRootView(), nativeImgView.getRootView());
                        List<View> clickView = new ArrayList<>();
                        clickView.add(nativeImgView.getRootView());
                        clickView.add(nativeImgView.getClickView());
                        unifiedADData.bindAdToView(mActivity.getApplication(), (NativeAdContainer) nativeImgView.getRootView(), null, clickView);
                        onShow(adParam);
                        unifiedADData.setNativeAdEventListener(new NativeADEventListener() {
                            @Override
                            public void onADExposed() {
                            }

                            @Override
                            public void onADClicked() {
                                onClick(adParam);
                            }

                            @Override
                            public void onADError(AdError adError) {
                                onError(adParam, "");
                            }

                            @Override
                            public void onADStatusChanged() {
                            }
                        });
                    }

                    @Override
                    public void onNoAD(AdError adError) {
                        onError(adParam, "gdt -- loadCommonAd code : " + adError.getErrorCode() + ", message : " + adError.getErrorMsg());
                    }
                });

        nativeExpressAD.loadData(1);
    }

    @Override
    public void loadCommonAdWithVideo(final AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height,
                                      final boolean showBigImg, final View.OnClickListener clickListener, ADListener adListener) {
        addListener(adListener);
        pull(adParam);
        NativeUnifiedAD nativeExpressAD = new NativeUnifiedAD(mActivity.getApplication(), adParam.getAdAppId(), adParam.getAdId(),
                new NativeADUnifiedListener() {
                    @Override
                    public void onADLoaded(List<NativeUnifiedADData> list) {
                        if (list == null || list.size() == 0 || mActivity == null) {
                            GDTAdSource.this.pullFailed(adParam);
                            return;
                        }
                        NativeUnifiedADData unifiedADData = list.get(0);
                        NativeAd nativeAd = transform(adParam, unifiedADData, containerView);
                        final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView, nativeAd, showBigImg);
                        // 自带广告字样
                        nativeImgView.hideAdLable();
//                        nativeImgView.setAdListener(getADListener(adParam));
                        nativeAd.registerViewForInteraction(containerView, nativeImgView.getRootView(), nativeImgView.getRootView());
                        List<View> clickView = new ArrayList<>();
                        clickView.add(nativeImgView.getRootView());
                        clickView.add(nativeImgView.getClickView());
                        unifiedADData.bindAdToView(mActivity.getApplication(), (NativeAdContainer) nativeImgView.getRootView(), null, clickView);
                        onShow(adParam);
                        unifiedADData.setNativeAdEventListener(new NativeADEventListener() {
                            @Override
                            public void onADExposed() {
                            }

                            @Override
                            public void onADClicked() {
                                onClick(adParam);
                            }

                            @Override
                            public void onADError(AdError adError) {
                                onError(adParam, "");
                            }

                            @Override
                            public void onADStatusChanged() {
                            }
                        });
                    }

                    @Override
                    public void onNoAD(AdError adError) {
                        onError(adParam, "gdt -- loadCommonAd code : " + adError.getErrorCode() + ", message : " + adError.getErrorMsg());
                    }
                });

        nativeExpressAD.loadData(1);
    }

    @Override
    public void loadReadNativeAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, ADListener adListener) {

    }

    @Override
    public Single<ArrayList<?>> loadListAd(final AdOriginConfigBean adParam, int width, int height) {
        pull(adParam);
        return Single.fromObservable(new ObservableSource<ArrayList<?>>() {
            @Override
            public void subscribe(final Observer<? super ArrayList<?>> observer) {
                NativeUnifiedAD nativeExpressAD = new NativeUnifiedAD(mActivity.getApplication(), adParam.getAdAppId(), adParam.getAdId(),
                        new NativeADUnifiedListener() {
                            @Override
                            public void onADLoaded(List<NativeUnifiedADData> list) {
                                if (list == null || list.size() == 0 ||  mActivity == null) {
                                    observer.onError(new Throwable("error : no ad"));
                                    pullFailed(adParam);
                                    return;
                                }
                                ArrayList<Object> dataLlist = new ArrayList();
                                for (NativeUnifiedADData unifiedADData : list) {
                                    NativeAd nativeAd = transform(adParam, unifiedADData, null);
                                    final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), null, nativeAd, false);
                                    // 自带广告字样
                                    nativeImgView.hideAdLable();
                                    // 重新生成一个带view的
                                    nativeAd = transform(adParam, unifiedADData, nativeImgView.getRootView());
//                                    nativeImgView.setAdListener(getADListener(adParam));
                                    nativeAd.registerViewForInteraction((ViewGroup) nativeImgView.getRootView(),
                                            nativeImgView.getRootView(), nativeImgView.getRootView());
                                    List<View> clickView = new ArrayList<>();
                                    clickView.add(nativeImgView.getRootView());
                                    clickView.add(nativeImgView.getClickView());
                                    unifiedADData.bindAdToView(mActivity.getApplication(), (NativeAdContainer) nativeImgView.getRootView(), null, clickView);
                                    dataLlist.add(new ListItemCommAd(nativeAd));
                                    onShow(adParam);
                                    unifiedADData.setNativeAdEventListener(new NativeADEventListener() {
                                        @Override
                                        public void onADExposed() {
                                        }

                                        @Override
                                        public void onADClicked() {
                                            onClick(adParam);
                                        }

                                        @Override
                                        public void onADError(AdError adError) {
                                            onError(adParam, "");
                                        }

                                        @Override
                                        public void onADStatusChanged() {
                                        }
                                    });
                                    break;
                                }
                                observer.onNext(dataLlist);
                                observer.onComplete();
                            }

                            @Override
                            public void onNoAD(AdError adError) {
                                observer.onError(new Throwable("ad error : code : " + adError.getErrorCode() + ", message : " + adError.getErrorMsg()));
                                onError(adParam, "gdt -- loadCommonAd code : " + adError.getErrorCode() + ", message : " + adError.getErrorMsg());
                            }
                        });

                nativeExpressAD.loadData(1);
            }
        }).timeout(8, TimeUnit.SECONDS);
    }

    private NativeAd transform(final AdOriginConfigBean adParam, final NativeUnifiedADData nativeAd, final View adView) {
        return new NativeAd() {

            @Override
            public AdOriginConfigBean getAdParam() {
                return adParam;
            }

            @Override
            public Bitmap getAdLogo() {
                return null;
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
                return nativeAd.getDesc();
            }

            @Override
            public String getSource() {
                return null;
            }

            @Override
            public String getIcon() {
                return nativeAd.getIconUrl();
            }

            @Override
            public List<String> getImageList() {
                List<String> imgList = new ArrayList<>();
                if (nativeAd.getImgList() != null && nativeAd.getImgList().size() > 0) {
                    for (String img : nativeAd.getImgList()) {
                        if (!TextUtils.isEmpty(img)) {
                            imgList.add(img);
                            break;
                        }
                    }
                } else {
                    imgList.add(nativeAd.getImgUrl());
                }
                return imgList;
            }

            @Override
            public int getInteractionType() {
                return 0;
            }

            @Override
            public int getImageMode() {
                return 0;
            }

            @Override
            public void registerViewForInteraction(ViewGroup viewGroup, View clickView, View creativeView) {
            }

            @Override
            public void registerViewForInteraction(ViewGroup viewGroup, List<View> var2, List<View> var3) {
            }

            @Override
            public View getAdView() {

                return adView;
            }

            @Override
            public void render() {
            }

            @Override
            public void destroy() {
            }

            @Override
            public int getAdSite() {
                return adParam.getAdSite();
            }
        };
    }

    private InterstitialAD getIAD(AdOriginConfigBean adParam) {
        if (this.iad != null) {
            iad.closePopupWindow();
            iad.destroy();
            iad = null;
        }
        if (iad == null) {
            iad = new InterstitialAD(mActivity, adParam.getAdAppId(), adParam.getAdId());
        }
        return iad;
    }

    // ---------------------------监听广告状态------------------------------------

    @Override
    public void pull(AdOriginConfigBean originBean) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.pull(originBean);
            }
        }
    }

    @Override
    public void pullFailed(AdOriginConfigBean originBean) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.pullFailed(originBean);
            }
        }
    }

    @Override
    public void onShow(AdOriginConfigBean originBean) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onShow(originBean);
            }
        }
    }

    @Override
    public void onClick(AdOriginConfigBean originBean) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onClick(originBean);
            }
        }
    }

    @Override
    public void onError(AdOriginConfigBean originBean, String msg) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onError(originBean, msg);
            }
        }
    }

    @Override
    public void onDismiss(AdOriginConfigBean originBean) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onDismiss(originBean);
            }
        }
    }

}
