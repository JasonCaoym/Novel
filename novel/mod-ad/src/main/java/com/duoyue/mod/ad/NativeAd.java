package com.duoyue.mod.ad;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mod.ad.bean.AdOriginConfigBean;

import java.util.List;

public interface NativeAd {
    AdOriginConfigBean getAdParam();

    Bitmap getAdLogo();

    String getAdLogoUrl();

    String getTitle();

    String getDescription() ;

    String getSource();

    String getIcon();

    List<String> getImageList();

    int getInteractionType();

    /**
     * 1：下载；2：链接; 3:立即拨打
     * @return
     */
    int getImageMode();

    void registerViewForInteraction(ViewGroup viewGroup, View clickView, View creativeView);

    void registerViewForInteraction(ViewGroup viewGroup, List<View> var2, List<View> var3);

    View getAdView();

    void render();

    void destroy();

    int getAdSite();
}
