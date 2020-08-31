package com.duoyue.mod.ad.platform.url;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.zydm.base.utils.GlideUtils;

public class UrlFlowBallAdView extends AbstractAdView {

    private ImageView imageView;

    public UrlFlowBallAdView(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void init(ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        if (otherContainer instanceof ImageView) {
            imageView = (ImageView) otherContainer;
        } else {
            Logger.e("UrlFlowBallAdView", "悬浮球otherContainer传递错误");
        }
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void showAd() {
        pull();
        if (imageView != null) {
            GlideUtils.INSTANCE.loadImageWidthNoCorner(mActivity, mAdSiteBean.getPicUrl(), imageView);
            onShow();
        } else {
            onError("-1", "悬浮球otherContainer传递错误");
        }
    }

}
