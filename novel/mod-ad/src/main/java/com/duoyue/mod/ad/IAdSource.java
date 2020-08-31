package com.duoyue.mod.ad;

import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mod.ad.bean.AdOriginConfigBean;
import com.duoyue.mod.ad.listener.ADListener;
import com.duoyue.mod.ad.listener.AdCallback;
import com.duoyue.mod.ad.listener.SplashAdListener;
import io.reactivex.Single;

import java.util.ArrayList;

public interface IAdSource {

    void addListener(ADListener adListener);

    void loadBannerAd(AdOriginConfigBean adParam, ViewGroup containerView, ADListener loadListener);

    void loadSplashAd(AdOriginConfigBean adParam, ViewGroup containerView, View skipView, SplashAdListener bannerListener);

    void loadInteractionAd(AdOriginConfigBean adParam, ADListener loadListener);

    /**
     *
     */
    void loadRewardVideoAD(AdOriginConfigBean adParam, ADListener loadListener);

    /**
     * 通用广告显示：书架、排行榜、新书列表、精选列表
     * @param adParam
     * @param containerView
     */
    void loadCommonAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, AdCallback callback);

    /**
     * listview显示原生广告
     * @param adParam
     * @param width
     * @param height
     * @return
     */
    Single<ArrayList<?>> loadListAd(AdOriginConfigBean adParam, int width, int height);

    void loadCommonAdWithVideo(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height,
                               boolean showBigImg, View.OnClickListener clickListener, ADListener adListener);

    void loadReadNativeAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, ADListener adListener);

//    void loadFlowModelAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, ADListener adListener);
}
