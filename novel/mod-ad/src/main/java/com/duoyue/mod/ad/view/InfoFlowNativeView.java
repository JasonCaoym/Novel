package com.duoyue.mod.ad.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.mod.ad.ITransformAd;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.R;

/**
 * 阅读器原生信息流广告
 */
public class InfoFlowNativeView {
    private static final String TAG = "ad#AdNativeImgView";
    private ViewGroup parentView;
    private View rootView;
    private Context context;
    private TextView tvAdLable;
    private AdSiteBean mAdSiteBean;
    private FrameLayout videoView;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvAdLabel;
    private TextView tvDownload;
    private ImageView ivSourcIcon;
    
    private Resources resources;
    

    public InfoFlowNativeView(Context context, ViewGroup parentView, AdSiteBean adSiteBean, ITransformAd dataBean) {
        this.parentView = parentView;
        this.context = context;
        this.resources = context.getResources();
        this.mAdSiteBean = adSiteBean;
        initView(dataBean);
    }

    public View getClickView() {
        if (rootView != null) {
            return rootView.findViewById(R.id.ad_read_main);
        } else {
            return rootView;
        }
    }

    public void setData(ITransformAd adBean) {
        initData(adBean);
    }

    private void initView(ITransformAd dataBean) {
        if (dataBean == null) {
            return;
        }

        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.ad_native_read, null, false);
            tvAdLabel = rootView.findViewById(R.id.ad_read_title);
            tvDescription = rootView.findViewById(R.id.ad_read_describtion);
            tvDownload = rootView.findViewById(R.id.ad_read_download);
            tvAdLabel = rootView.findViewById(R.id.ad_read_text_ad);
            ivSourcIcon = rootView.findViewById(R.id.ad_read_source_icon);
        }
        if (parentView != null) {
            parentView.removeAllViews();
            if (mAdSiteBean != null && mAdSiteBean.getChannelCode().equals(Constants.channalCodes[0])) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                parentView.addView(rootView, params);
            } else {
                parentView.addView(rootView);
            }
        }

        // 默认显示广告字样，广点通自带广告字样
        tvAdLable = rootView.findViewById(R.id.ad_read_text_ad);
        tvAdLable.setVisibility(View.VISIBLE);

        videoView = rootView.findViewById(R.id.ad_read_video);

        if (dataBean != null) {
            setData(dataBean);
        }
    }

    private void initData(ITransformAd dataBean) {
        TextView tvTitle = rootView.findViewById(R.id.ad_read_title);
        if (!TextUtils.isEmpty(dataBean.getDescription())) {
            tvTitle.setText(dataBean.getDescription());
        }

        TextView tvDescribtion = rootView.findViewById(R.id.ad_read_describtion);
        if (!TextUtils.isEmpty(dataBean.getTitle())) {
            tvDescribtion.setText(dataBean.getTitle());
        }

        Button btnDetail = rootView.findViewById(R.id.ad_read_download);
//        switch (dataBean.getInteractionType()) {
//            case 1:
//                btnDetail.setVisibility(View.VISIBLE);
//                btnDetail.setText(R.string.download_immediate);
//                break;
//            case 2:
//            default:
                btnDetail.setVisibility(View.VISIBLE);
                btnDetail.setText(R.string.see_detail);
//                break;
//        }
        ImageView ivBigImg = rootView.findViewById(R.id.ad_read_img);
        if (dataBean.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
            videoView.setVisibility(View.VISIBLE);
            ivBigImg.setVisibility(View.GONE);
            videoView.addView(dataBean.getAdView());
        } else {
            videoView.setVisibility(View.GONE);
            if (dataBean.getImageList() != null && !dataBean.getImageList().isEmpty()) {
                ivBigImg.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg.getContext(), dataBean.getImageList().get(0), ivBigImg);
            } else {
                ivBigImg.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(dataBean.getAdLogoUrl())) {
            ivSourcIcon.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImageWidthNoCorner(ivSourcIcon.getContext(), dataBean.getAdLogoUrl(), ivSourcIcon);
        } else if(dataBean.getAdLogo() != null) {
            ivSourcIcon.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImage(ivSourcIcon.getContext(), dataBean.getAdLogo(), ivSourcIcon);
        } else {
            ivSourcIcon.setVisibility(View.GONE);
        }
    }

    public void updateDayModel(boolean isNightMode) {
        if (isNightMode) {
            if (tvTitle != null) {
                tvTitle.setTextColor(resources.getColor(R.color.color_A4A3A8));
            }
            if (tvDescription != null) {
                tvDescription.setTextColor(resources.getColor(R.color.color_A4A3A8));
            }
            if (tvAdLabel != null) {
                tvAdLabel.setBackgroundResource(R.drawable.ad_txt_night_bg);
                tvAdLabel.setTextColor(resources.getColor(R.color.color_51FFFFFF));
            }
            // 按钮
            if (tvDownload != null) {
                tvDownload.setBackgroundResource(R.drawable.btn_download_night_bg);
                tvDownload .setTextColor(resources.getColor(R.color.standard_red_main_light));
            }
        } else {
            if (tvTitle != null) {
                tvTitle.setTextColor(resources.getColor(R.color.text_black_333));
            }
            if (tvDescription != null) {
                tvDescription.setTextColor(resources.getColor(R.color.text_black_333));
            }
            if (tvAdLabel != null) {
                tvAdLabel.setBackgroundResource(R.drawable.ad_txt_bg);
                tvAdLabel.setTextColor(resources.getColor(R.color.white));
            }
            // 按钮
            if (tvDownload != null) {
                tvDownload.setBackgroundResource(R.drawable.btn_download_day_bg);
                tvDownload .setTextColor(resources.getColor(R.color.standard_red_main_color_c1));
            }
        }
    }
    
}
