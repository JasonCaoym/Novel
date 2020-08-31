package com.duoyue.mod.ad.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.mod.ad.NativeAd;
import com.duoyue.mod.ad.listener.ADListener;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.R;

/**
 * 原生信息流广告：包括普通信息流和列表信息流（）
 */
public class AdNativeListSmallView {
    private static final String TAG = "ad#AdNativeImgView";
    private ViewGroup parentView;
    private View rootView;
    private Activity activity;
    private ADListener adListener;
    private TextView tvAdLable;

    public AdNativeListSmallView(Activity activity, ViewGroup parentView, NativeAd dataBean) {
        this(activity, parentView, dataBean, null);
    }

    public AdNativeListSmallView(Activity activity, ViewGroup parentView, NativeAd dataBean, ADListener adListener) {
        this.parentView = parentView;
        this.activity = activity;
        this.adListener = adListener;
        initView(dataBean);
    }


    public void setAdListener(ADListener adListener) {
        this.adListener = adListener;
        adListener.onShow(null);
    }

    public View getRootView() {
        return rootView;
    }

    public View getClickView() {
        if (rootView != null) {
            return rootView.findViewById(R.id.ad_native_main);
        } else {
            return rootView;
        }
    }

    public void setData(NativeAd adBean) {
        initData(adBean);
    }


    private void initView(NativeAd dataBean) {
        if (dataBean == null) {
            if (adListener != null) {
                adListener.onError(null, "AdNativeImgView data bean is null !");
            }
            return;
        }
        if (adListener != null) {
            adListener.onShow(null);
        }

        if (rootView == null) {
            rootView = LayoutInflater.from(activity).inflate(R.layout.ad_native_list_small, null, false);
        }
        if (parentView != null) {
            parentView.removeAllViews();
            parentView.addView(rootView);
        }

        getClickView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adListener != null) {
                    adListener.onClick(null);
                }
            }
        });
        // 默认显示广告字样，广点通自带广告字样
        tvAdLable = rootView.findViewById(R.id.ad_native_text_ad);
        tvAdLable.setVisibility(View.VISIBLE);

        ImageView ivClose = rootView.findViewById(R.id.ad_native_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adListener != null) {
                    adListener.onDismiss(null);
                }
                if (parentView != null) {
                    parentView.removeView(rootView);
                }
            }
        });

        if (dataBean != null) {
            setData(dataBean);
        }
    }

    private void initData(NativeAd dataBean) {
        TextView tvTitle = rootView.findViewById(R.id.ad_native_title);
        if (!TextUtils.isEmpty(dataBean.getTitle())) {
            tvTitle.setText(dataBean.getTitle());
        }

        TextView tvDescribtion = rootView.findViewById(R.id.ad_native_describtion);
        if (!TextUtils.isEmpty(dataBean.getDescription())) {
            tvDescribtion.setText(dataBean.getDescription());
        }

        ImageView ivIcon = rootView.findViewById(R.id.ad_native_icon);
        if (!TextUtils.isEmpty(dataBean.getIcon())) {
            GlideUtils.INSTANCE.loadImage(activity, dataBean.getIcon(), ivIcon);
        }

        ImageView ivOriginLogo = rootView.findViewById(R.id.ad_native_originlogo);
        if (!TextUtils.isEmpty(dataBean.getAdLogoUrl())) {
            GlideUtils.INSTANCE.loadImage(activity, dataBean.getAdLogoUrl(), ivOriginLogo, GlideUtils.INSTANCE.getBookRadius());
        } else {
            if (dataBean.getAdLogo() != null) {
                GlideUtils.INSTANCE.loadImage(activity, dataBean.getAdLogo(), ivOriginLogo);
            }
        }

        TextView tvOriginName = rootView.findViewById(R.id.ad_native_originName);
        if (!TextUtils.isEmpty(dataBean.getSource())) {
            tvOriginName.setText(dataBean.getSource());
        }

    }

    public void hideAdLable() {
        if (tvAdLable != null) {
            tvAdLable.setVisibility(View.GONE);
        }
    }

}
