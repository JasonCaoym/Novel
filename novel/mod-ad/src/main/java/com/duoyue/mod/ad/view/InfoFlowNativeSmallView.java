package com.duoyue.mod.ad.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.mod.ad.ITransformAd;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.R;

/**
 * 阅读器原生信息流广告
 */
public class InfoFlowNativeSmallView {
    private static final String TAG = "ad#AdNativeImgView";
    private ViewGroup parentView;
    private View rootView;
    private Context context;
    private AdSiteBean mAdSiteBean;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvAdLabel;
    private TextView tvSourceLabel;
    private ImageView ivSourcIcon;
    private Resources resources;


    public InfoFlowNativeSmallView(Context context, ViewGroup parentView, AdSiteBean adSiteBean, ITransformAd dataBean) {
        this.parentView = parentView;
        this.context = context;
        this.resources = context.getResources();
        this.mAdSiteBean = adSiteBean;
        initView(dataBean);
    }

    public View getRootView() {
        return rootView;
    }

    public View getClickView() {
        if (rootView != null) {
            return rootView.findViewById(R.id.ad_read_small_main);
        } else {
            return rootView;
        }
    }

    public void setData(ITransformAd adBean) {
        initData(adBean);
    }


    private void initView(ITransformAd dataBean) {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.ad_native_read_small, null, false);
            tvTitle = rootView.findViewById(R.id.ad_read_small_title);
            tvDescription = rootView.findViewById(R.id.ad_read_small_describtion);
            tvAdLabel = rootView.findViewById(R.id.ad_read_small_text_ad);
            tvSourceLabel = rootView.findViewById(R.id.ad_read_small_source_label);
            ivSourcIcon = rootView.findViewById(R.id.ad_read_small_source_icon);
        }
        if (parentView != null) {
            parentView.removeAllViews();
            parentView.addView(rootView);
        }

        if (dataBean != null) {
            setData(dataBean);
        }
    }

    private void initData(ITransformAd dataBean) {
        ImageView ivBigImg1 = rootView.findViewById(R.id.ad_read_small_img1);
        ImageView ivBigImg2 = rootView.findViewById(R.id.ad_read_small_img2);
        ImageView ivBigImg3 = rootView.findViewById(R.id.ad_read_small_img3);
        ivBigImg1.setVisibility(View.GONE);
        ivBigImg2.setVisibility(View.GONE);
        ivBigImg3.setVisibility(View.GONE);
        if (dataBean.getImageList() != null && !dataBean.getImageList().isEmpty()) {
            switch (dataBean.getImageList().size()) {
                default:
                case 3:
                    ivBigImg3.setVisibility(View.VISIBLE);
                    GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg3.getContext(), dataBean.getImageList().get(2), ivBigImg3);
                case 2:
                    ivBigImg2.setVisibility(View.VISIBLE);
                    GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg2.getContext(), dataBean.getImageList().get(1), ivBigImg2);
                    tvDescription.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(dataBean.getDescription())) {
                        tvTitle.setText(dataBean.getDescription());
                        tvTitle.setVisibility(View.VISIBLE);
                    } else {
                        tvTitle.setVisibility(View.GONE);
                    }
                case 1:
                    ivBigImg1.setVisibility(View.VISIBLE);
                    GlideUtils.INSTANCE.loadImageWidthNoCorner(ivBigImg1.getContext(), dataBean.getImageList().get(0), ivBigImg1);
                    if (dataBean.getImageList().size() == 1) {
                        tvTitle.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(dataBean.getDescription())) {
                            tvDescription.setText(dataBean.getDescription());
                            tvDescription.setVisibility(View.VISIBLE);
                        } else {
                            tvDescription.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        } else {
            ivBigImg1.setVisibility(View.GONE);
            ivBigImg2.setVisibility(View.GONE);
            ivBigImg3.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(dataBean.getSource())) {
            tvSourceLabel.setVisibility(View.VISIBLE);
            tvSourceLabel.setText(dataBean.getSource());
        } else {
            tvSourceLabel.setVisibility(View.GONE);
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
        }
    }
    
}
