package com.duoyue.mod.ad.platform.url;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.zydm.base.utils.ViewUtils;

public class UrlInfoFlowAdView extends BaseUrlAdView {

    private static final String TAG = "ad#UrlInfoFlowAdView";
    private int viewWidth;
    private int maxHeight;


    public UrlInfoFlowAdView(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        viewWidth = PhoneUtil.getScreenSize(activity)[0] - ViewUtils.dp2px(30);
        maxHeight = (int) (PhoneUtil.getScreenSize(activity)[1] * 0.6);
    }

    @Override
    public void initData() {
        if (mAdSiteBean != null) {
            if (!TextUtils.isEmpty(mAdSiteBean.getPicUrl()) && mActivity != null && !mActivity.isDestroyed()) {
                ivUrl.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
                Glide.with(mActivity)
                        .asBitmap()//强制Glide返回一个Bitmap对象
                        .load(mAdSiteBean.getPicUrl())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                Logger.e("width_height", "图片原始尺寸，width = " + width  + "--------height = " + height);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
                                params.width = viewWidth;

                                float scaleX = width * 1.0f / viewWidth;
                                float scaleY = height * 1.0f / maxHeight;

                                // 高宽都小
                                if (width <= viewWidth && height <= maxHeight) {
                                    params.height = height;
                                    params.width = viewWidth;
                                } else if (width > viewWidth && height > maxHeight) { // 图高宽都过大
                                    float scale = Math.max(scaleX, scaleY);
                                    width = (int) (width / scale);
                                    height = (int) (height / scale);
                                    params.height = height;
                                } else if (width > viewWidth && height <= maxHeight) {
                                    width = viewWidth;
                                    height = (int) (height / scaleX);
                                    params.height = height;
                                } else if (width <= viewWidth && height > maxHeight) {
                                    width = (int) (width / scaleY);
                                    height = maxHeight;
                                    params.height = maxHeight ;
                                }
                                Logger.e("width_height", "图片处理过后，width = " + width  + "--------height = " + height);
                                Logger.e("width_height", "父容器，width = " + params.width  + "--------height = " + params.height);
                                if (mainView != null) {
                                    mainView.setLayoutParams(params);
                                    RequestOptions myOptions = new RequestOptions()
                                            .centerCrop()
                                            .placeholder(ivUrl.getDrawable()) // 设置了占位图
                                            .skipMemoryCache(false) //设置内存缓存
//                                        .transforms(new CenterCrop(), new RoundedCorners(ViewUtils.dp2px(8)))
                                            .override(width, height)
                                            .dontAnimate();//取消加载变换动画
                                    if (TextUtils.isEmpty(mAdSiteBean.getPicUrl())) {
                                        Glide.with(mActivity)
                                                .load("http://null")
                                                .apply(myOptions).into(ivUrl);
                                    } else {
                                        Glide.with(mActivity)
                                                .load(mAdSiteBean.getPicUrl())
                                                .apply(myOptions).into(ivUrl);
                                    }
                                    onShow();
                                } else {
                                    onError("", "ad view not exist");
                                }
                            }
                        });


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
        Logger.e(TAG, "url信息流广告被销毁了");
    }
}
