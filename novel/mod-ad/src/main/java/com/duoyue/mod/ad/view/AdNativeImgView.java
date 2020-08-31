package com.duoyue.mod.ad.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.mod.ad.NativeAd;
import com.duoyue.mod.ad.listener.ADListener;
import com.duoyue.mod.ad.utils.AdConstants;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.R;

/**
 * 原生信息流广告：包括普通信息流和列表信息流（）
 */
public class AdNativeImgView {
    private static final String TAG = "ad#AdNativeImgView";
    private ViewGroup parentView;
    private View rootView;
    private Context context;
    private ADListener adListener;
    private TextView tvAdLable;
    private boolean showBigImag;
    private ViewGroup bigImgRootView;

    public AdNativeImgView(Context context, ViewGroup parentView, NativeAd dataBean, boolean showBigImag) {
        this(context, parentView, dataBean, showBigImag, null);
    }

    public AdNativeImgView(Context context, ViewGroup parentView, NativeAd dataBean, boolean showBigImag, ADListener adListener) {
        this.parentView = parentView;
        this.context = context;
        this.showBigImag = showBigImag;
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
            rootView = LayoutInflater.from(context).inflate(R.layout.ad_native_common, null, false);
        }
        if (parentView != null) {
            parentView.removeAllViews();
            parentView.addView(rootView);
        }
        bigImgRootView = rootView.findViewById(R.id.ad_native_ad);

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
        if (dataBean.getAdSite() == AdConstants.Position.BOOK_RANK) {
            int width = ViewUtils.dp2px(64);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivIcon.getLayoutParams();
            params.width = width;
            params.height = width;
            ivIcon.setLayoutParams(params);
        }
        if (!TextUtils.isEmpty(dataBean.getIcon())) {
            GlideUtils.INSTANCE.loadImage(ivIcon.getContext(), dataBean.getIcon(), ivIcon, ViewUtils.dp2px(4));
        }

        ImageView ivOriginLogo = rootView.findViewById(R.id.ad_native_originlogo);
        if (!TextUtils.isEmpty(dataBean.getAdLogoUrl())) {
            GlideUtils.INSTANCE.loadImage(ivOriginLogo.getContext(), dataBean.getAdLogoUrl(), ivOriginLogo);
        } else {
            if (dataBean.getAdLogo() != null) {
                GlideUtils.INSTANCE.loadImage(ivOriginLogo.getContext(), dataBean.getAdLogo(), ivOriginLogo);
            }
        }

        TextView tvOriginName = rootView.findViewById(R.id.ad_native_originName);
        if (!TextUtils.isEmpty(dataBean.getSource())) {
            tvOriginName.setText(dataBean.getSource());
        }

        // 是否显示大图
        ImageView ivBigImg = rootView.findViewById(R.id.ad_native_img);
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) bigImgRootView.getLayoutParams();
        if (showBigImag && dataBean.getImageList() != null && !dataBean.getImageList().isEmpty()) {
            ivBigImg.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg.getContext(), dataBean.getImageList().get(0), ivBigImg);
            imgParams.topMargin = ViewUtils.dp2px(14);
        } else {
            ivBigImg.setVisibility(View.GONE);
            imgParams.topMargin = ViewUtils.dp2px(2);
        }
        bigImgRootView.setLayoutParams(imgParams);
    }

    public void hideAdLable() {
        if (tvAdLable != null) {
            tvAdLable.setVisibility(View.GONE);
        }
    }

}
