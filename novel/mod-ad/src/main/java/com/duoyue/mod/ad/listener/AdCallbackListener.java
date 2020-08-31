package com.duoyue.mod.ad.listener;

import com.duoyue.mod.ad.bean.AdSiteBean;

public interface AdCallbackListener {

    void pull(AdSiteBean adSiteBean);

    void pullFailed(AdSiteBean adSiteBean, String code, String errorMsg);

    void onShow(AdSiteBean adSiteBean);

    void onClick(AdSiteBean adSiteBean);

    void onError(AdSiteBean adSiteBean, String code, String errorMsg);

    void onDismiss(AdSiteBean adSiteBean);

    void onAdTick(long time);

}
