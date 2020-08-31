package com.duoyue.mod.ad.platform.url;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.platform.AbstractAdView;
import com.zydm.base.ui.BaseActivityHelper;
import com.zydm.base.ui.activity.web.WebActivity;
import com.zzdm.ad.R;

public abstract class BaseUrlAdView extends AbstractAdView implements View.OnClickListener {
    private static final String TAG = "ad#AbstractBannerView";

    protected ViewGroup adContainerView;
    protected View rootView;
    protected ImageView ivUrl;
    protected WebView mWebView;
    protected WebSettings mWebSettings;
    protected View mainView;
    private TextView tvAdLabel;
    private Resources resources;
    private int viewHeight;
    private boolean hasShowed;
    /**
     * 网页第一次加载时显示不全
     */
    private boolean hasRetryLoad;

    public BaseUrlAdView(Activity activity, AdSiteBean adSiteBean, AdCallbackListener adListener) {
        super(activity, adSiteBean, adListener);
        resources = mActivity.getResources();
    }

    @Override
    public void init(final ViewGroup adContainer, View otherContainer, int refreshTime, AdCallbackListener adListener) {
        this.adContainerView = adContainer;
        addListener(adListener);
        initViews();
        initSetting();
    }

    @Override
    public void showAd() {
        pull();
        initData();
    }

    private void initViews() {
        rootView = LayoutInflater.from(mActivity).inflate(R.layout.ad_url_layout, null);
        mainView = rootView.findViewById(R.id.ad_url_main_layout);
        ivUrl = rootView.findViewById(R.id.ad_url_img);
        mWebView = rootView.findViewById(R.id.ad_url_webview);
        tvAdLabel = rootView.findViewById(R.id.ad_url_text_ad);
        tvAdLabel.setVisibility(View.GONE);
        adContainerView.addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void updateDayModel(boolean isNightMode) {
        /*if (!isNightMode) {
            tvAdLabel.setBackgroundResource(R.drawable.ad_txt_bg);
            tvAdLabel.setTextColor(resources.getColor(R.color.white));
        } else {
            tvAdLabel.setBackgroundResource(R.drawable.ad_txt_night_bg);
            tvAdLabel.setTextColor(resources.getColor(R.color.color_51FFFFFF));
        }*/
    }

    /**
     * 通过设置mainView的属性调整UI样式
     */
    public abstract void initData();

    private void initSetting() {
        mWebView.requestFocusFromTouch();
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebSettings = mWebView.getSettings();

        //支持js
        mWebSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        //支持web本地存储
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setDomStorageEnabled(true);

        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //设置缓存

        mWebView.setClickable(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        if (mAdSiteBean.getChannelCode().equals(Constants.channalCodes[2])) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
            params.height = 0;
            mainView.setLayoutParams(params);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(final WebView view, final int newProgress) {
                if (newProgress == 100) {
                    int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    view.measure(width, height);
                    viewHeight = view.getMeasuredHeight();
                    Logger.e(TAG, mAdSiteBean.getChannelCode() + "， width = " + view.getMeasuredWidth()
                            + ", height = " + viewHeight); // 获取高度
                    if (mAdSiteBean.getChannelCode().equals(Constants.channalCodes[2])) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
                        if (viewHeight * 100f / PhoneUtil.getScreenSize(mActivity)[1] >= 60) {
                            params.height = (int) (PhoneUtil.getScreenSize(mActivity)[1] * 0.6f);
                        } else {
                            if (viewHeight < 200 && !hasRetryLoad && mWebView != null) {
                                hasRetryLoad = true;
                                mWebView.reload();
                                return;
                            }
                            // 重新加载后高度还是0，则渲染失败
                            if (viewHeight < 200) {
                                viewHeight = 600;
                            }
                            params.height = viewHeight;
                        }
                        if (mainView != null) {
                            mainView.setLayoutParams(params);
                        }
                    }
                    if (!hasShowed) {
                        hasShowed = true;
                        onShow();
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onClick(View v) {
        onClick();
        Logger.e("ad#Extr", "banner被点击了啊");
        if (mWebView != null && mWebView.getVisibility() != View.VISIBLE) {
            Logger.e("ad#Extr", "banner相应了点击事件");
            BaseActivityHelper.INSTANCE.gotoWebActivity(mActivity, new WebActivity.Data(mAdSiteBean.getLinkUrl(), ""));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            mWebView.stopLoading();

            mWebSettings.setJavaScriptEnabled(false);
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    // 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //这里进行url拦截
            if (url != null) {
                onClick(null);
                Logger.e(TAG, "跳转的uri路径： " + url);
                BaseActivityHelper.INSTANCE.gotoWebActivity(mActivity, new WebActivity.Data(url, ""));
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //这里进行url拦截
            if (request != null && request.getUrl() != null) {
                onClick(null);
                Logger.e(TAG, "跳转的uri路径： " + request.getUrl().toString());
                BaseActivityHelper.INSTANCE.gotoWebActivity(mActivity, new WebActivity.Data(request.getUrl().toString(), ""));
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

}
