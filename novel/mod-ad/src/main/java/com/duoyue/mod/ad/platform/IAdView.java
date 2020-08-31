package com.duoyue.mod.ad.platform;

import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mod.ad.listener.AdCallbackListener;

public interface IAdView {
    void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener);
    void setStatParams(String prePageId, String modelId, String source);
    void showAd();
    void destroy();
}
