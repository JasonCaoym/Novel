package com.duoyue.mod.ad.listener;

public interface StatisticsListener {

    void pull();

    void pullFailed(String code, String errorMsg);

    void onShow();

    void onClick();

    void onError(String code, String errorMsg);

    void onDismiss();

}
