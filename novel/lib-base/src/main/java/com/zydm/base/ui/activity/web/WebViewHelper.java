package com.zydm.base.ui.activity.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.*;
import com.zydm.base.common.Constants;
import com.zydm.base.tools.DelayTask;
import com.zydm.base.ui.BaseActivityHelper;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.StringUtils;

/**
 * Created by YinJiaYan on 2018/3/21.
 */

public class WebViewHelper {

    private static final String TAG = "WebViewHelper";

    public static final String H5_SUBJECT_LIST = "topicList";
    private static final String H5_USER_ID = "userId";

    private WebView mWebView;
    private BaseActivity mActivity;
    private DelayTask mTitleDelayTask = new DelayTask();
    private String mFunctionName;
    private int mInvokeid;

    public WebViewHelper(WebView webView, BaseActivity activity) {
        mWebView = webView;
        mActivity = activity;
        initSetting();
    }

    private void initSetting() {
        mWebView.requestFocusFromTouch();
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        //支持web本地存储
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setUserAgentString(webSettings.getUserAgentString() + Constants.USER_AGENT_SUFFIX);
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setDefaultTextEncodingName("utf-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.addJavascriptInterface(new JsInterfaceWithAnnotation(), "android");
        } else {
            mWebView.addJavascriptInterface(new JsInterFace(), "android");
        }
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri parse = Uri.parse(StringUtils.isBlank(url) ? Constants.EMPTY : url);
                LogUtils.d(TAG, "shouldOverrideUrlLoading start : url:" + url);
                String scheme = parse.getScheme();
                if (StringUtils.equalsIgnoreCase(Constants.HTTP, scheme) || TextUtils.equals(Constants.HTTPS, scheme)) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                //可以跳转qq群
                startExternalActivity(url);
                if (TextUtils.equals(Constants.APP_SCHEME, scheme)) {
                    String lastPathSegment = parse.getLastPathSegment();
                    onMoTongScheme(lastPathSegment);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error.getErrorCode() == -6) {
                        return;
                    }
                    onShowPromptProblem();
                } else {
                    onShowPromptProblem();
                }
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.d(TAG, "onPageFinished : " + StringUtils.getString(url));
                //是否显示关闭按钮
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCanGoBack(view.canGoBack());
                    }
                });
                mTitleDelayTask.doDelay(new Runnable() {
                    @Override
                    public void run() {
                        String title = mWebView.getTitle();
                        LogUtils.d(TAG, "delay onPageFinished  title: " + title);
                        onTitle(title);
                    }
                }, Constants.MILLIS_400);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.d(TAG, "WebChromeClient onReceivedTitle  title: " + title);
                if (mTitleDelayTask.cancel()) {
                    onTitle(title);
                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(intent);
            }
        });
    }


    private void startExternalActivity(String url) {
        LogUtils.d(TAG, "startExternalActivity");
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            mActivity.startActivity(intent);
        } catch (Throwable e) {
        }
    }

    public String getFunctionName() {
        return mFunctionName;
    }

    public int getInvokeid() {
        return mInvokeid;
    }

    private class JsInterfaceWithAnnotation {

        @JavascriptInterface
        public void onShareResult(String result) {
            onJsShareResult(result);
        }

        @JavascriptInterface
        public String getToken(String functionName, int invokeid) {
            return handleJsGetToken(functionName, invokeid);
        }

        @JavascriptInterface
        public void login(String functionName, int invokeid) {
            handleJsLogin(functionName, invokeid);
        }

        @JavascriptInterface
        public void openBrowser(String url) {
            handleJsOpenBrowser(url);
        }

        @JavascriptInterface
        public void pay(String id, String cost) {
            handleJsPay(id, cost);
        }
    }

    private void handleJsPay(final String id, final String cost) {
//        FreightPayInfo info = identity_new FreightPayInfo(cost, id)
        LogUtils.d(TAG, "id:" + id + " cost:" + cost);
        if (StringUtils.isBlank(id) || StringUtils.isBlank(cost)) {
            return;
        }
        final double money = Double.parseDouble(cost);
    }

    private class JsInterFace {

        public void onShareResult(String result) {
            onJsShareResult(result);
        }

        public String getToken(String functionName, int invokeid) {
            return handleJsGetToken(functionName, invokeid);
        }

        public void login(String functionName, int invokeid) {
            handleJsLogin(functionName, invokeid);
        }

        public void openBrowser(String url) {
            handleJsOpenBrowser(url);
        }


        public void pay(String id, String cost) {
            handleJsPay(id, cost);
        }
    }

    private String handleJsGetToken(String functionName, int invokeid) {
        mFunctionName = functionName;
        mInvokeid = invokeid;
        LogUtils.d(TAG, "handleGetToken getToken : " + (getAppTokenData() == null ? "token is null" : getAppTokenData()));
        return getAppTokenData();
    }

    private void handleJsOpenBrowser(String url) {
        BaseActivityHelper.INSTANCE.gotoOutWebBrowser(mActivity, url);
    }

    private void handleJsLogin(String functionName, int invokeid) {
        mFunctionName = functionName;
        mInvokeid = invokeid;
//        ActivityHelper.gotoLogin(mActivity, Constants.ZERO_NUM);
    }

    private String getAppTokenData() {
//        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
//        MtMap<String, String> tokenMap = new MtMap<>();
//        tokenMap.put(ApiParamConst.TOKEN, AccountUtils.getToken());
//        tokenMap.put(H5_USER_ID, AccountUtils.getUserId());
//        ApiParamConst.addCommonParam(tokenMap);
//        return gson.toJson(tokenMap);
        return "";
    }

    protected void onTitle(@Nullable String title) {
    }

    protected void onMoTongScheme(String lastPathSegment) {
//        if (TextUtils.equals(RankActivity.URI_LAST_SEGMENT, lastPathSegment)) {
//            CustomEventMgr.getInstance().put(StatisConst.KEY_VIEWNAME, mActivity.getPageName());
//        }
    }

    protected void onShowPromptProblem() {
    }

    protected void onCanGoBack(boolean webCanGoBack) {
    }

    protected void onJsShareResult(String result) {
    }
}
