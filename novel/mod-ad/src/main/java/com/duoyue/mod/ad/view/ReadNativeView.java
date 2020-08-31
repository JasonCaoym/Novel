package com.duoyue.mod.ad.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.NativeAd;
import com.duoyue.mod.ad.listener.ADListener;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.R;

/**
 * 阅读器原生信息流广告
 */
public class ReadNativeView {
    private static final String TAG = "ad#AdNativeImgView";
    private ViewGroup parentView;
    private View rootView;
    private Context context;
    private ADListener adListener;
    private TextView tvAdLable;
    private FrameLayout videoView;

    public ReadNativeView(Context context, ViewGroup parentView, NativeAd dataBean) {
        this.parentView = parentView;
        this.context = context;
        initView(dataBean);
    }

    public View getRootView() {
        return rootView;
    }

    public View getClickView() {
        if (rootView != null) {
            return rootView.findViewById(R.id.ad_read_main);
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
                adListener.onError(null, "ReadNativeView data bean is null !");
            }
            return;
        }
        if (adListener != null) {
            adListener.onShow(null);
        }

        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.ad_native_read, null, false);
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
        tvAdLable = rootView.findViewById(R.id.ad_read_text_ad);
        tvAdLable.setVisibility(View.VISIBLE);

        videoView = rootView.findViewById(R.id.ad_read_video);

        if (dataBean != null) {
            setData(dataBean);
        }
    }

    private void initData(NativeAd dataBean) {
        TextView tvTitle = rootView.findViewById(R.id.ad_read_title);
        if (!TextUtils.isEmpty(dataBean.getDescription())) {
            tvTitle.setText(dataBean.getDescription());
        }

        TextView tvDescribtion = rootView.findViewById(R.id.ad_read_describtion);
        if (!TextUtils.isEmpty(dataBean.getTitle())) {
            tvDescribtion.setText(dataBean.getTitle());
        }

        Button btnDetail = rootView.findViewById(R.id.ad_read_download);
        switch (dataBean.getInteractionType()) {
            case 1:
                btnDetail.setVisibility(View.VISIBLE);
                btnDetail.setText(R.string.download_immediate);
                break;
            case 2:
            default:
                btnDetail.setVisibility(View.VISIBLE);
                btnDetail.setText(R.string.see_detail);
                break;
        }
        ImageView ivBigImg = rootView.findViewById(R.id.ad_read_img);
        if (dataBean.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
            videoView.setVisibility(View.VISIBLE);
            ivBigImg.setVisibility(View.GONE);
            Logger.e("ad#video", "有视频广告了");
            videoView.addView(dataBean.getAdView());
        } else {
            Logger.e("ad#video", "dataBean.getImageMode() = " + dataBean.getImageMode());
            videoView.setVisibility(View.GONE);
            if (dataBean.getImageList() != null && !dataBean.getImageList().isEmpty()) {
                ivBigImg.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg.getContext(), dataBean.getImageList().get(0), ivBigImg);
            } else {
                ivBigImg.setVisibility(View.GONE);
            }
        }
    }

    public void hideAdLable() {
        if (tvAdLable != null) {
            tvAdLable.setVisibility(View.GONE);
        }
    }

}
