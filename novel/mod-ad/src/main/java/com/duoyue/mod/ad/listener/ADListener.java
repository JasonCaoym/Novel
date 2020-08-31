package com.duoyue.mod.ad.listener;


import com.duoyue.mod.ad.bean.AdOriginConfigBean;

public interface ADListener {

    void pull(AdOriginConfigBean originBean);

    void pullFailed(AdOriginConfigBean originBean);

    void onShow(AdOriginConfigBean originBean);

    void onClick(AdOriginConfigBean originBean);

    void onError(AdOriginConfigBean originBean, String msg);

    void onDismiss(AdOriginConfigBean originBean);
}
