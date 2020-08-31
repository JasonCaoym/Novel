package com.duoyue.mod.ad.platform.csj;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.sdk.openadsdk.*;
import com.duoyue.lib.base.devices.PhoneUtil;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CSJAdSource implements IAdSource, ADListener {

    private static final String TAG = "ad#CSJAdSource";
    private Activity mActivity;
    private TTAdManager ttAdManager;
    private TTAdNative mTTAdNative;
    private AdOriginConfigBean mAdParam;
    private TTRewardVideoAd rewardVideoAd;
    private long preTime;

    public CSJAdSource(Activity activity) {
        mActivity = activity;
        initAd();
    }

    private void initAd() {
        //一定要在初始化后才能调用，否则为空
        try {
            ttAdManager = TTAdSdk.getAdManager();
            mTTAdNative = TTAdSdk.getAdManager().createAdNative(mActivity);//baseContext建议为activity
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private HashSet<ADListener> mListenerSet = new HashSet();

    @Override
    public void addListener(ADListener adListener) {
        mListenerSet.add(adListener);
    }

    @Override
    public void loadBannerAd(final AdOriginConfigBean adParam, final ViewGroup containerView, final ADListener adListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mListenerSet.add(adListener);
        mTTAdNative.loadBannerAd(new AdSlot.Builder()
                .setCodeId(adParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 100)
                .build(), new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                //加载失败的回调 详见3.1错误码说明
                CSJAdSource.this.onError(adParam, "csj banner load error : " + code + ", " + message);
                containerView.removeAllViews();
                mListenerSet.remove(adListener);
            }

            @Override
            public void onBannerAdLoad(TTBannerAd ad) {
                if (ad == null || mActivity == null) {
                    CSJAdSource.this.pullFailed(adParam);
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    CSJAdSource.this.pullFailed(adParam);
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
//                ad.setSlideIntervalTime(30 * 1000);
                containerView.removeAllViews();
                float scaneX = ViewUtils.dp2px(50) / 100f;
                Logger.e(TAG, "scanleX =  " + scaneX);
                containerView.setScaleX(scaneX);
                containerView.setScaleY(scaneX);
                containerView.addView(bannerView);
                onShow(adParam);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        onClick(adParam);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
//                        onShow(adParam);
                    }
                });
                //（可选）设置下载类广告的下载监听
//                bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
//                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
//                    @Override
//                    public void onSelected(int position, String value) {
//                        //用户选择不喜欢原因后，移除广告展示
//                        containerView.removeAllViews();
//                        CSJAdSource.this.onDismiss(adParam);
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
            }
        });
    }

    @Override
    public void loadSplashAd(final AdOriginConfigBean adParam, final ViewGroup containerView, View skipContainer, final SplashAdListener splashListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mTTAdNative.loadSplashAd(new AdSlot.Builder()
                .setCodeId(adParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(PhoneUtil.getScreenSize(mActivity.getApplication())[0],
                        PhoneUtil.getScreenSize(mActivity.getApplication())[1])
                .build(), new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Logger.e(TAG, message);
                splashListener.onDismiss();
                CSJAdSource.this.onError(adParam, "code : " + code + ", message : " + message);
                mListenerSet.remove(splashListener);
            }

            @Override
            @MainThread
            public void onTimeout() {
                splashListener.onDismiss();
                CSJAdSource.this.onError(adParam, "timeout");
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Logger.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    CSJAdSource.this.pullFailed(adParam);
                    return;
                }
                onShow(adParam);
                splashListener.onShow(false);
                View view = ad.getSplashView();
                containerView.removeAllViews();
                //把SplashView 添加到ViewGroup中
                containerView.addView(view);
                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        onClick(adParam);
                        splashListener.onClick();
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                    }

                    @Override
                    public void onAdSkip() {
                        onDismiss(adParam);
                        splashListener.skipAd();
                        splashListener.onDismiss();
                    }

                    @Override
                    public void onAdTimeOver() {
                        splashListener.onDismiss();
                    }
                });
            }
        }, 2000);

    }

    @Override
    public void loadInteractionAd(final AdOriginConfigBean adParam, final ADListener loadListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mListenerSet.add(loadListener);
        mTTAdNative.loadInteractionAd(new AdSlot.Builder()
                .setCodeId(adParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 600) //根据广告平台选择的尺寸，传入同比例尺寸
                .build(), new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                CSJAdSource.this.onError(adParam, "loadInteractionAd error - code : " + code + ", msg: " + message);
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        onClick(adParam);
                    }

                    @Override
                    public void onAdShow() {
                        onShow(adParam);
                    }

                    @Override
                    public void onAdDismiss() {
                        onDismiss(adParam);
                    }
                });
                //如果是下载类型的广告，可以注册下载状态回调监听
                if (ttInteractionAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ttInteractionAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                        }
                    });
                }
                //弹出插屏广告
                if (mActivity != null) {
                    ttInteractionAd.showInteractionAd(mActivity);
                } else {
                    CSJAdSource.this.pullFailed(adParam);
                }
            }
        });
    }

    @Override
    public void loadRewardVideoAD(final AdOriginConfigBean adParam, final ADListener adListener) {
        mAdParam = adParam;
        pull(mAdParam);
        mListenerSet.add(adListener);
        preTime = System.currentTimeMillis();
        mTTAdNative.loadRewardVideoAd(new AdSlot.Builder()
                .setCodeId(adParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(ViewUtils.getPhonePixels()[0], ViewUtils.getPhonePixels()[1])
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build(), new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                CSJAdSource.this.onError(mAdParam, "穿山甲视频播放失败");
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                if (System.currentTimeMillis() - preTime < AdSourceProxy.REWORD_VIDEO_TIMEOUT && mActivity != null) {
                    try {
                        rewardVideoAd.showRewardVideoAd(mActivity);
                    } catch (Exception ex) {
                        CSJAdSource.this.pullFailed(mAdParam);
                    }
                } else {
                    CSJAdSource.this.pullFailed(mAdParam);
                }
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd mttRewardVideoAd) {
                rewardVideoAd = mttRewardVideoAd;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        onShow(adParam);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        onClick(adParam);
                    }

                    @Override
                    public void onAdClose() {
                        onDismiss(adParam);
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                    }

                    @Override
                    public void onVideoError() {
                        CSJAdSource.this.onError(adParam, "csj : rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                    }

                    @Override
                    public void onSkippedVideo() {

                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                        FuncPageStatsApi.vidioAPPDownload();
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                        FuncPageStatsApi.vidioAPPDownloadPause();
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                        FuncPageStatsApi.vidioAPPDownloadFail();
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
//                        FuncPageStatsApi.vidioAPPInstall();
                    }
                });
            }
        });
    }

    @Override
    public void loadCommonAd(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height, final AdCallback callback) {
        mAdParam = adParam;
        pull(mAdParam);
        mTTAdNative.loadFeedAd(new AdSlot.Builder()
                .setCodeId(mAdParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(width, height)
                .setAdCount(1) //请求广告数量为1到3条
                .build(), new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                CSJAdSource.this.onError(mAdParam, "code : " + code + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list == null || list.isEmpty() || mActivity == null) {
                    CSJAdSource.this.pullFailed(mAdParam);
                    return;
                }
                onShow(mAdParam);
                NativeAd nativeAd = transform(mAdParam, list.get(0), containerView);
                final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView, nativeAd, false);
                nativeAd.registerViewForInteraction(containerView, nativeImgView.getClickView(), nativeImgView.getClickView());
            }
        });
    }

    @Override
    public void loadCommonAdWithVideo(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height,
                                      final boolean showBigImg, final View.OnClickListener clickListener, ADListener adListener) {
        mAdParam = adParam;
        addListener(adListener);
        pull(mAdParam);
        mTTAdNative.loadFeedAd(new AdSlot.Builder()
                .setCodeId(mAdParam.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(width, height)
                .setAdCount(1) //请求广告数量为1到3条
                .build(), new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                CSJAdSource.this.onError(mAdParam, "code : " + code + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list == null || list.isEmpty() || mActivity == null) {
                    CSJAdSource.this.pullFailed(mAdParam);
                    return;
                }
                onShow(mAdParam);
                NativeAd nativeAd = transform(mAdParam, list.get(0), containerView);
                final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), containerView, nativeAd, showBigImg);
                nativeAd.registerViewForInteraction(containerView, nativeImgView.getClickView(), nativeImgView.getClickView());
            }
        });
    }

    @Override
    public void loadReadNativeAd(AdOriginConfigBean adParam, final ViewGroup containerView, int width, int height, ADListener adListener) {
        mAdParam = adParam;
        addListener(adListener);
        pull(mAdParam);
        mTTAdNative.loadFeedAd(new AdSlot.Builder()
                .setCodeId("926423020"/*mAdParam.getAdId()*/)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(width, height)
                .setAdCount(1) //请求广告数量为1到3条
                .build(), new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                CSJAdSource.this.onError(mAdParam, "code : " + code + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list == null || list.isEmpty() || mActivity == null) {
                    CSJAdSource.this.pullFailed(mAdParam);
                    return;
                }
                onShow(mAdParam);
                NativeAd nativeAd = transform(mAdParam, list.get(0), containerView);
                final ReadNativeView nativeImgView = new ReadNativeView(mActivity, containerView, nativeAd);
                nativeAd.registerViewForInteraction(containerView, nativeImgView.getClickView(), nativeImgView.getClickView());
            }
        });
    }

    @Override
    public Single<ArrayList<?>> loadListAd(AdOriginConfigBean adParam, final int width, final int height) {
        mAdParam = adParam;
        pull(mAdParam);
        return Single.fromObservable(new ObservableSource<ArrayList<?>>() {
            @Override
            public void subscribe(final Observer<? super ArrayList<?>> observer) {
                mTTAdNative.loadFeedAd(new AdSlot.Builder()
                        .setCodeId(mAdParam.getAdId())
                        .setSupportDeepLink(true)
                        .setImageAcceptedSize(width, height)
                        .setAdCount(1) //请求广告数量为1到3条
                        .build(), new TTAdNative.FeedAdListener() {
                    @Override
                    public void onError(int code, String message) {
                        CSJAdSource.this.onError(mAdParam, "code : " + code + message);
                        observer.onError(new Throwable("ad error : code = " + code + ", message = " + message));
                    }

                    @Override
                    public void onFeedAdLoad(List<TTFeedAd> list) {
                        if (list == null || list.isEmpty() || mActivity == null) {
                            CSJAdSource.this.pullFailed(mAdParam);
                            observer.onError(new Throwable("no ad "));
                            return;
                        }
                        ArrayList<Object> dataLlist = new ArrayList();
                        for (TTFeedAd ad : list) {
                            NativeAd nativeAd = transform(mAdParam, list.get(0), null);
                            final AdNativeImgView nativeImgView = new AdNativeImgView(mActivity.getApplication(), null, nativeAd, false);
                            ADListener listener = new ADListener() {
                                @Override
                                public void pull(AdOriginConfigBean originBean) {
//                                    CSJAdSource.this.pull(mAdParam);
                                }

                                @Override
                                public void pullFailed(AdOriginConfigBean originBean) {
//                                    CSJAdSource.this.pullFailed(mAdParam);
                                }

                                @Override
                                public void onShow(AdOriginConfigBean originBean) {
//                                    CSJAdSource.this.onShow(mAdParam);
                                }

                                @Override
                                public void onClick(AdOriginConfigBean originBean) {
//                                    CSJAdSource.this.onClick(mAdParam);
                                }

                                @Override
                                public void onError(AdOriginConfigBean originBean, String msg) {
//                                    CSJAdSource.this.onError(mAdParam, msg);
                                }

                                @Override
                                public void onDismiss(AdOriginConfigBean originBean) {
                                    CSJAdSource.this.onDismiss(mAdParam);
                                }
                            };
                            nativeImgView.setAdListener(listener);
                            onShow(mAdParam);
                            // 重新生成一个带view的
                            nativeAd = transform(mAdParam, ad, nativeImgView.getRootView());
                            nativeImgView.setData(nativeAd);
                            nativeAd.registerViewForInteraction((ViewGroup) nativeImgView.getClickView(), nativeImgView.getClickView(), nativeImgView.getClickView());
                            dataLlist.add(new ListItemCommAd(nativeAd));
                            break;
                        }

                        observer.onNext(dataLlist);
                        observer.onComplete();
                    }
                });
            }
        }).timeout(5, TimeUnit.SECONDS);
    }

    private NativeAd transform(final AdOriginConfigBean adParam, final TTFeedAd nativeAd, final View adView) {
        return new NativeAd() {

            @Override
            public AdOriginConfigBean getAdParam() {
                return mAdParam;
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
                        onClick(mAdParam);
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        onClick(mAdParam);
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {
//                        onShow(mAdParam);
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
            public int getAdSite() {
                return adParam.getAdSite();
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
    public void pullFailed(AdOriginConfigBean adParam) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.pullFailed(adParam);
            }
        }
    }

    @Override
    public void onShow(AdOriginConfigBean adParam) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onShow(adParam);
            }
        }
    }

    @Override
    public void onClick(AdOriginConfigBean adParam) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onClick(adParam);
            }
        }
    }

    @Override
    public void onError(AdOriginConfigBean adParam, String msg) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onError(adParam, msg);
            }
        }
    }

    @Override
    public void onDismiss(AdOriginConfigBean adParam) {
        for (ADListener listener : mListenerSet) {
            if (listener != null) {
                listener.onDismiss(adParam);
            }
        }
    }

}
