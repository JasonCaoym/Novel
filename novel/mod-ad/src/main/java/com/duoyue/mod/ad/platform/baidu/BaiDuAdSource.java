package com.duoyue.mod.ad.platform.baidu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.baidu.mobads.*;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
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
import com.duoyue.mod.ad.view.ReadNativeView;
import com.zydm.base.utils.ViewUtils;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BaiDuAdSource implements IAdSource, ADListener {

    private static final String TAG = "ad#BaiDuAdSource";
    private Activity mActivity;
    // Banner图
    private AdView adView;
    // 插图
    private InterstitialAd interAd;
    // 激励视频
    private RewardVideoAd mRewardVideoAd;
    private static final int RELOAD_MAX_COUNT = 4;
    // 激励视频加载失败后，尝试加载次数
    private int reLoadCnt = RELOAD_MAX_COUNT;
    private long preTime;
    private boolean isBannerRequest;

    public BaiDuAdSource(Activity activity) {
        mActivity = activity;
    }

    private HashSet<ADListener> mListenerSet = new HashSet();

    private AdOriginConfigBean mAdParam;

    @Override
    public void addListener(ADListener adListener) {
        mListenerSet.add(adListener);
    }

    @Override
    public void loadBannerAd(AdOriginConfigBean adParam, ViewGroup containerView, final ADListener adListener) {
        mAdParam = adParam;
        mListenerSet.add(adListener);
        pull(mAdParam);
        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_WHITE_THEME);
        adView = new AdView(mActivity.getApplication(), adParam.getAdId());
        adView.setScaleX(0.90f);
        adView.setScaleY(0.90f);
        isBannerRequest = true;
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
            }

            public void onAdShow(JSONObject info) {
                if (isBannerRequest) { // 会抽风自己回调多次
                    isBannerRequest = false;
                    onShow(mAdParam);
                }
            }

            public void onAdReady(AdView adView) {
            }

            public void onAdFailed(String reason) {
                onError(mAdParam, "reason : " + reason);
                adView.destroy();
            }

            public void onAdClick(JSONObject info) {
                onClick(mAdParam);
            }

            @Override
            public void onAdClose(JSONObject arg0) {
                onDismiss(mAdParam);
                adView.destroy();
            }
        });
        int winW = ViewUtils.getPhonePixels()[0];
        int winH = ViewUtils.getPhonePixels()[1];
        int width = Math.min(winW, winH);
        int height = width * 3 / 20;
        containerView.addView(adView, new FrameLayout.LayoutParams(width, height));
    }

    @Override
    public void loadSplashAd(AdOriginConfigBean adParam, ViewGroup containerView, View skipContainer, final SplashAdListener bannerListener) {
        mAdParam = adParam;
        pull(mAdParam);
        new SplashAd(mActivity.getApplication(), containerView, new SplashLpCloseListener() {
            @Override
            public void onLpClosed() {
                onDismiss(mAdParam);
            }

            @Override
            public void onAdDismissed() {
                onDismiss(mAdParam);
                bannerListener.onDismiss();
            }

            @Override
            public void onAdFailed(String arg0) {
                Logger.i(TAG, arg0);
                onError(mAdParam, arg0);
                bannerListener.onDismiss();
            }

            @Override
            public void onAdPresent() {
                onShow(mAdParam);
                bannerListener.onShow(true);
            }

            @Override
            public void onAdClick() {
                onClick(mAdParam);
            }
        }, adParam.getAdId(), true);
    }

    @Override
    public void loadInteractionAd(AdOriginConfigBean adParam, final ADListener loadListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mListenerSet.add(loadListener);
        interAd = new InterstitialAd(mActivity.getApplication(), adParam.getAdId());
        interAd.setListener(new InterstitialAdListener() {

            @Override
            public void onAdClick(InterstitialAd arg0) {
                onClick(mAdParam);
            }

            @Override
            public void onAdDismissed() {
                onDismiss(mAdParam);
            }

            @Override
            public void onAdFailed(String arg0) {
                onError(mAdParam, arg0);
            }

            @Override
            public void onAdPresent() {
                onShow(mAdParam);
            }

            @Override
            public void onAdReady() {
                if (mActivity != null) {
                    interAd.showAd(mActivity);
                } else {
                    pullFailed(mAdParam);
                }
            }

        });
        interAd.loadAd();
    }

    @Override
    public void loadRewardVideoAD(AdOriginConfigBean adParam, final ADListener adListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mListenerSet.add(adListener);
        reLoadCnt = RELOAD_MAX_COUNT;
        preTime = System.currentTimeMillis();
        mRewardVideoAd = new RewardVideoAd(mActivity, adParam.getAdId(), new RewardVideoAd.RewardVideoAdListener() {
            @Override
            public void onVideoDownloadSuccess() {
                // 视频缓存成功
                // 说明：如果想一定走本地播放，那么收到该回调之后，可以调用show
                if (System.currentTimeMillis() - preTime < AdSourceProxy.REWORD_VIDEO_TIMEOUT) {
                    try {
                        mRewardVideoAd.show();
                    } catch (Exception ex) {
                        onError(mAdParam, "onVideoDownloadSuccess -- 视频播放失败");
                    }
                }
            }

            @Override
            public void onVideoDownloadFailed() {
                // 视频缓存失败，如果想走本地播放，可以在这儿重新load下一条广告，最好限制load次数（4-5次即可）。
                onError(mAdParam, "RewardVideoAD -- 视频缓存失败");
            }

            @Override
            public void playCompletion() {
            }

            @Override
            public void onAdShow() {
                // 视频开始播放时候的回调
                onShow(mAdParam);
            }

            @Override
            public void onAdClick() {
                // 广告被点击的回调
                onClick(mAdParam);
            }

            @Override
            public void onAdClose(float playScale) {
                // 用户关闭了广告
                // 说明：关闭按钮在mssp上可以动态配置，媒体通过mssp配置，可以选择广告一开始就展示关闭按钮，还是播放结束展示关闭按钮
                // 建议：收到该回调之后，可以重新load下一条广告,最好限制load次数（4-5次即可）
                // playScale[0.0-1.0],1.0表示播放完成，媒体可以按照自己的设计给予奖励
                onDismiss(mAdParam);
            }

            @Override
            public void onAdFailed(String arg0) {
                // 广告失败回调 原因：广告内容填充为空；网络原因请求广告超时
                // 建议：收到该回调之后，可以重新load下一条广告，最好限制load次数（4-5次即可）
                onError(mAdParam, "百度--RewardVideoAD error :-- " + arg0);
//                if (reLoadCnt-- > 0) {
//                    mRewardVideoAd.load();
//                }
            }
        });
        mRewardVideoAd.load();
    }

    /**
     * 百度展示和点击都需要自己做处理
     * @param adParam
     * @param containerView
     * @param width
     * @param height
     * @param callback
     */
    @Override
    public void loadCommonAd(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height,
                              final AdCallback callback) {
        mAdParam = adParam;
        pull(mAdParam);
        BaiduNative baidu = new BaiduNative(mActivity.getApplication(), adParam.getAdId(), new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeFail(NativeErrorCode arg0) {
                BaiDuAdSource.this.onError(mAdParam, arg0.name());
            }

            @Override
            public void onNativeLoad(List<NativeResponse> arg0) {
                // 一个广告只允许展现一次，多次展现、点击只会计入一次
                if (arg0 != null && arg0.size() > 0 && mActivity != null) {
                    NativeResponse nativeResponse = null;
                    for (NativeResponse item : arg0) {
                        if (item != null && item.getMaterialType() == NativeResponse.MaterialType.NORMAL) {
                            nativeResponse = item;
                        }
                    }
                    if (nativeResponse == null) {
                        BaiDuAdSource.this.onError(mAdParam, "no ad !! ");
                        return;
                    }

                    final NativeResponse finalNativeResponse = nativeResponse;
                    AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView,
                            transform(mAdParam, nativeResponse, null), false, new ADListener() {
                        @Override
                        public void pull(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.pull(mAdParam);
                        }

                        @Override
                        public void pullFailed(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.pullFailed(mAdParam);
                        }

                        @Override
                        public void onShow(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.onShow(mAdParam);
                        }

                        @Override
                        public void onClick(AdOriginConfigBean originBean) {
                            finalNativeResponse.handleClick(containerView);
                            BaiDuAdSource.this.onClick(mAdParam);
                        }

                        @Override
                        public void onError(AdOriginConfigBean originBean, String msg) {
                            BaiDuAdSource.this.onError(mAdParam, msg);
                        }

                        @Override
                        public void onDismiss(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.onDismiss(mAdParam);
                        }
                    });
                    nativeResponse.recordImpression(nativeImgView.getClickView());
                } else {
                    pullFailed(mAdParam);
                }
            }
        });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
        RequestParameters requestParameters =
                new RequestParameters.Builder()
                        .downloadAppConfirmPolicy(
                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        baidu.makeRequest(requestParameters);
    }

    @Override
    public void loadCommonAdWithVideo(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height,
                                      final boolean showBigImg, final View.OnClickListener clickListener, ADListener adListener) {
        mAdParam = adParam;
        addListener(adListener);
        pull(mAdParam);
        BaiduNative baidu = new BaiduNative(mActivity.getApplication(), mAdParam.getAdId(), new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeFail(NativeErrorCode arg0) {
                BaiDuAdSource.this.onError(mAdParam, arg0.name());
            }

            @Override
            public void onNativeLoad(List<NativeResponse> arg0) {
                // 一个广告只允许展现一次，多次展现、点击只会计入一次
                if (arg0 != null && arg0.size() > 0 && mActivity != null) {
                    NativeResponse nativeResponse = null;
                    for (NativeResponse item : arg0) {
                        if (item != null && item.getMaterialType() == NativeResponse.MaterialType.NORMAL) {
                            nativeResponse = item;
                        }
                    }
                    if (nativeResponse == null) {
                        BaiDuAdSource.this.onError(mAdParam, "no ad !! ");
                        return;
                    }

                    final NativeResponse finalNativeResponse = nativeResponse;
                    AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView,
                            transform(mAdParam, nativeResponse, null), showBigImg, new ADListener() {
                        @Override
                        public void pull(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.pull(mAdParam);
                        }

                        @Override
                        public void pullFailed(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.pullFailed(mAdParam);
                        }

                        @Override
                        public void onShow(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.onShow(mAdParam);
                        }

                        @Override
                        public void onClick(AdOriginConfigBean originBean) {
                            finalNativeResponse.handleClick(containerView);
                            BaiDuAdSource.this.onClick(mAdParam);
                        }

                        @Override
                        public void onError(AdOriginConfigBean originBean, String msg) {
                            BaiDuAdSource.this.onError(mAdParam, msg);
                        }

                        @Override
                        public void onDismiss(AdOriginConfigBean originBean) {
                            BaiDuAdSource.this.onDismiss(mAdParam);
                        }
                    });
                    nativeResponse.recordImpression(nativeImgView.getClickView());
                } else {
                    pullFailed(mAdParam);
                }
            }
        });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
        RequestParameters requestParameters =
                new RequestParameters.Builder()
                        .downloadAppConfirmPolicy(
                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        baidu.makeRequest(requestParameters);
    }

    @Override
    public void loadReadNativeAd(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height, ADListener adListener) {
        mAdParam = adParam;
        addListener(adListener);
        pull(mAdParam);
        BaiduNative baidu = new BaiduNative(mActivity.getApplication(), adParam.getAdId(), new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeFail(NativeErrorCode arg0) {
                BaiDuAdSource.this.onError(mAdParam, arg0.name());
            }

            @Override
            public void onNativeLoad(List<NativeResponse> arg0) {
                // 一个广告只允许展现一次，多次展现、点击只会计入一次
                if (arg0 != null && arg0.size() > 0 && mActivity != null) {
                    NativeResponse nativeResponse = null;
                    for (NativeResponse item : arg0) {
                        if (item != null && item.getMaterialType() == NativeResponse.MaterialType.NORMAL) {
                            nativeResponse = item;
                        }
                    }
                    if (nativeResponse == null) {
                        BaiDuAdSource.this.onError(mAdParam, "no ad !! ");
                        return;
                    }

                    final NativeResponse finalNativeResponse = nativeResponse;
                    ReadNativeView nativeView = new ReadNativeView(mActivity.getApplication(), containerView, transform(mAdParam, nativeResponse, null));
                    nativeResponse.recordImpression(nativeView.getClickView());
                } else {
                    pullFailed(mAdParam);
                }
            }
        });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
        RequestParameters requestParameters =
                new RequestParameters.Builder()
                        .downloadAppConfirmPolicy(
                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        baidu.makeRequest(requestParameters);
    }

    @Override
    public Single<ArrayList<?>> loadListAd(AdOriginConfigBean adParam, int width, int height) {
        mAdParam = adParam;
        pull(mAdParam);
        return Single.fromObservable(new ObservableSource<ArrayList<?>>() {
            @Override
            public void subscribe(final Observer<? super ArrayList<?>> observer) {
                BaiduNative baidu = new BaiduNative(mActivity.getApplication(), mAdParam.getAdId(), new BaiduNative.BaiduNativeNetworkListener() {

                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        onError(mAdParam, "ad error : name = " + arg0.name() + ", arg0.str = " + arg0.toString());
                        observer.onError(new Throwable("ad error : name = " + arg0.name() + ", arg0.str = " + arg0.toString()));
                    }

                    @Override
                    public void onNativeLoad(List<NativeResponse> list) {
                        // 一个广告只允许展现一次，多次展现、点击只会计入一次
                        ArrayList<Object> dataLlist = new ArrayList();
                        if (list != null && list.size() > 0 && mActivity != null) {
                            for (final NativeResponse adView :  list) {
                                NativeAd nativeAd = transform(mAdParam, adView, null);
                                final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), null, nativeAd, false);
                                ADListener listener = new ADListener() {
                                    @Override
                                    public void pull(AdOriginConfigBean originBean) {
                                        BaiDuAdSource.this.pull(mAdParam);
                                    }

                                    @Override
                                    public void pullFailed(AdOriginConfigBean originBean) {
                                        BaiDuAdSource.this.pullFailed(mAdParam);
                                    }
                                    @Override
                                    public void onShow(AdOriginConfigBean originBean) {
                                        BaiDuAdSource.this.onShow(mAdParam);
                                    }

                                    @Override
                                    public void onClick(AdOriginConfigBean originBean) {
                                        adView.handleClick(nativeImgView.getRootView());
                                        BaiDuAdSource.this.onClick(mAdParam);
                                    }

                                    @Override
                                    public void onError(AdOriginConfigBean originBean, String msg) {
                                        BaiDuAdSource.this.onError(mAdParam, msg);
                                    }

                                    @Override
                                    public void onDismiss(AdOriginConfigBean originBean) {
                                        BaiDuAdSource.this.onDismiss(mAdParam);
                                    }
                                };
                                nativeImgView.setAdListener(listener);
                                // 重新生成一个带view的
                                nativeAd = transform(mAdParam, adView, nativeImgView.getRootView());
                                nativeImgView.setData(nativeAd);
                                dataLlist.add(new ListItemCommAd(nativeAd));
                                break;
                            }
                            observer.onNext(dataLlist);
                            observer.onComplete();
                        } else {
                            pullFailed(mAdParam);
                            observer.onError(new Throwable("ad error : no ad"));
                        }
                    }
                });

                /**
                 * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
                 */
                // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
                RequestParameters requestParameters = new RequestParameters.Builder().downloadAppConfirmPolicy(
                                        RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();

                baidu.makeRequest(requestParameters);
            }
        }).timeout(8, TimeUnit.SECONDS);
    }

    private NativeAd transform(final AdOriginConfigBean configBean, final NativeResponse nativeBean, final View adView) {
        return new NativeAd() {

            @Override
            public void destroy() {
            }

            @Override
            public int getAdSite() {
                return mAdParam.getAdSite();
            }

            @Override
            public void render() {

            }

            @Nullable
            @Override
            public View getAdView() {
                return adView;
            }

            @Override
            public void registerViewForInteraction(@NotNull ViewGroup var1, @NotNull View clickView, @NotNull View creativeView) {

            }

            @Override
            public void registerViewForInteraction(ViewGroup viewGroup, List<View> var2, List<View> var3) {

            }

            @Override
            public int getImageMode() {
                return 0;
            }

            @Override
            public int getInteractionType() {
                return 2;
            }

            @NotNull
            @Override
            public List<String> getImageList() {
                List<String> data = new ArrayList<>();
                data.add(nativeBean.getImageUrl());
                return data;
            }

            @NotNull
            @Override
            public String getIcon() {
                return nativeBean.getIconUrl();
            }

            @NotNull
            @Override
            public String getSource() {
                return nativeBean.getBrandName();
            }

            @NotNull
            @Override
            public String getDescription() {
                return nativeBean.getTitle();
            }

            @NotNull
            @Override
            public String getTitle() {
                return nativeBean.getDesc();
            }

            @Override
            public AdOriginConfigBean getAdParam() {
                return configBean;
            }

            @Nullable
            @Override
            public Bitmap getAdLogo() {
                return null;
            }

            @Override
            public String getAdLogoUrl() {
                return nativeBean.getBaiduLogoUrl();
            }

        };
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
