package com.duoyue.mod.ad.listener;

public interface SplashAdListener {

    void onShow(boolean showSkipView);

    void onDismiss();

    void onAdTick(long time);

    void onClick();

    void skipAd();
}
