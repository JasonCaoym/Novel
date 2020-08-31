package com.duoyue.mod.ad.platform.url;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.zydm.base.utils.GlideUtils;

public class UrlBannerAdView extends BaseUrlAdView {

    private static final String TAG = "ad#UrlInfoFlowAdView";

    public UrlBannerAdView(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
    }

    @Override
    public void initData() {
        if (mAdSiteBean != null) {
            if (!TextUtils.isEmpty(mAdSiteBean.getPicUrl()) && mActivity != null && !mActivity.isFinishing()) {
                mWebView.setVisibility(View.GONE);
                GlideUtils.INSTANCE.loadImageWidthNoCorner(mActivity, mAdSiteBean.getPicUrl(), ivUrl);
                ivUrl.setVisibility(View.VISIBLE);
                onShow();
            } else {
                ivUrl.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }
            rootView.setOnClickListener(this);
            if (PhoneUtil.isNetworkAvailable(mActivity)) {
                if (TextUtils.isEmpty(mAdSiteBean.getPicUrl())) {
                    mWebView.loadUrl(mAdSiteBean.getLinkUrl());
                }
            } else {
                onError("-1", "net error");
            }
        }
    }

    @Override
    public void setStatParams(String prePageId, String modelId, String source) {
        this.prePageId = prePageId;
        this.modelId = modelId;
        this.source = source;
    }

    @Override
    public void destroy() {
        super.destroy();
        Logger.e(TAG, "url横幅广告被销毁了");
    }
}
