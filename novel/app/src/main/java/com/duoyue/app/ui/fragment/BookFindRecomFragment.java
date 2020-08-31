package com.duoyue.app.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.RelativeLayout;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.location.LocationModel;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.ParamKey;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.LogUtils;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;

/**
 * 发现-今日推荐
 * @author caoym
 * @data 2019/10/30  17:53
 */
public class BookFindRecomFragment extends BaseFragment
{
    /**
     * 日志Tag
     */
    private static final String LOG_TAG = "App#BookFindRecomFragment";

    /**
     * WebView
     */
    private WebView mWebView;

    /**
     * 提示Layout.
     */
    private View mPromptLayout;

    /**
     * 无网络提示.
     */
    private View mNoNetworkPromptLayout;

    /**
     * 加载进度提示.
     */
    private View mProgressPromptLayout;

    /**
     * 要加载的Url.
     */
    private String mWebUrl;

    /**
     * 选择的频道.
     */
    private int mFrequencyType;

    @Override
    public void onCreateView(@org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        setContentView(R.layout.fragment_find_recomm_list);
        //获取WebView对象.
        mWebView = findView(R.id.find_recomm_webview);
        //加载提示页面.
        mPromptLayout = findView(R.id.load_prompt_layout);
        //默认隐藏.
        mPromptLayout.setVisibility(View.GONE);
        //无网络提示.
        mNoNetworkPromptLayout = findView(R.id.prompt_info_layout);
        mNoNetworkPromptLayout.setVisibility(View.VISIBLE);
        //设置重试点击.
        mNoNetworkPromptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //隐藏无网络提示页.
                if (mPromptLayout != null)
                {
                    mPromptLayout.setVisibility(View.GONE);
                }
                //调用切换频道接口(默认且全部).
                switchFrequency(mFrequencyType);
            }
        });
        //加载中提示.
        mProgressPromptLayout = findView(R.id.prompt_progress_layout);
        mProgressPromptLayout.setVisibility(View.GONE);
        //初始化WebView.
        initWebView();
    }

    /**
     * 初始化操作
     */
    public void initWebView()
    {
        //设置默认布局大小
        mWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.requestFocus();
        //设置本地调用对象及其接口
        mWebView.addJavascriptInterface(new JavaScriptObject(), "novelObj");
        //WebView设置.
        setWebSetting();
        //调用切换频道接口(默认且全部).
        switchFrequency(0);
    }

    /**
     * 设置WebView
     */
    private void setWebSetting()
    {
        //是否开启Debug调试.
        //mWebView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings = mWebView.getSettings();
        //提高渲染的优先级.
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //设置支持javascript脚本.
        webSettings.setJavaScriptEnabled(true);
        //默认的是false，WebView是否支持新窗口, true:支持;false:不支持(默认), 如果设置为true, 则必须要重写WebChromeClient的onCreateWindow方法.
        webSettings.setSupportMultipleWindows(true);
        //自动打开窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //把所有内容放大webview等宽的一列中.
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //一定要设置,这就是出不来的主要原因,使用LocalStorage则必须打开.
        webSettings.setDomStorageEnabled(true);
        //WebView的缩放功能.
        webSettings.setSupportZoom(false);
        //设置显示缩放按钮.
        webSettings.setBuiltInZoomControls(true);
        //WebView保留缩放功能但隐藏缩放控件.
        //webSettings.setDisplayZoomControls(false);
        //取消滚动条.
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //启用地理定位.
        webSettings.setGeolocationEnabled(true);
        //允许访问文件  .
        webSettings.setAllowFileAccess(true);
        //是否使用缓存(默认不使用缓存),无论是否有网络,只要本地有缓存,都使用缓存.本地没有缓存时才从网络上获取.
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启应用程序缓存.
        webSettings.setAppCacheEnabled(true);
        //设置应用缓存的最大尺寸.
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        //设置应用缓存的路径.
        //webSettings.setAppCachePath(cachePath);
        //启用数据库.
        webSettings.setDatabaseEnabled(true);
        //设置数据库路径.
        //webSettings.setDatabasePath(cachePath + "webviewCache.db");
        //设置定位的数据库路径.
        webSettings.setGeolocationDatabasePath(getContext().getFilesDir().getPath());
        //将图片下载阻塞---先让图片不展示, 等内容加载完才加载图片.
        webSettings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 19)
        {
            //加载图片前false,加载完后设置true
            webSettings.setLoadsImagesAutomatically(true);
        } else
        {
            webSettings.setLoadsImagesAutomatically(false);
        }
        //设置是否启用插件.
        if (Build.VERSION.SDK_INT < 18/*Build.VERSION_CODES.JELLY_BEAN_MR2*/)
        {
            webSettings.setPluginState(WebSettings.PluginState.ON);
        }
        //Android 5.0以上默认不支持Mixed Content,所以需要手动开启支持.
        if (Build.VERSION.SDK_INT >= 21/*Build.VERSION_CODES.LOLLIPOP*/)
        {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        //设置监听器
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                // android 6.0 以下通过title获取判断
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                {
                    try
                    {
                        //获取当前处理的Url.
                        String currentUrl = view.getUrl();
                        if (TextUtils.isEmpty(currentUrl) || (!TextUtils.isEmpty(mWebUrl) && !currentUrl.startsWith(mWebUrl)))
                        {
                            //非首页链接.
                            return;
                        }
                        if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("找不到网页") || title.contains("网页无法打开"))
                        {
                            //避免出现默认的错误界面
                            view.loadUrl("about:blank");
                            //view.loadUrl(mErrorUrl);// 加载自定义错误页面
                            //显示重试界面.
                            if (mPromptLayout != null)
                            {
                                mPromptLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Throwable throwable)
                    {
                        Logger.e(LOG_TAG, "onReceivedTitle Throwable:{}, {}", mWebUrl, throwable);
                    }
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                super.onReceivedError(view, request, error);
                try
                {
                    //是否是为main frame创建
                    if (request.isForMainFrame())
                    {
                        //获取当前处理的Url.
                        String currentUrl = view.getUrl();
                        if (TextUtils.isEmpty(currentUrl) || (!TextUtils.isEmpty(mWebUrl) && !currentUrl.startsWith(mWebUrl)))
                        {
                            //非首页链接.
                            return;
                        }
                        //避免出现默认的错误界面
                        view.loadUrl("about:blank");
                        // 加载自定义错误页面
                        //view.loadUrl(mErrorUrl);
                        //显示重试界面.
                        if (mPromptLayout != null)
                        {
                            mPromptLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Throwable throwable)
                {
                    Logger.e(LOG_TAG, "onReceivedError Throwable:{}, {}", mWebUrl, throwable);
                }
            }
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                try
                {
                    //获取当前处理的Url.
                    String currentUrl = view.getUrl();
                    if (TextUtils.isEmpty(currentUrl) || (!TextUtils.isEmpty(mWebUrl) && !currentUrl.startsWith(mWebUrl)))
                    {
                        //非首页链接.
                        return;
                    }
                    //这个方法在android 6.0才出现
                    int statusCode = errorResponse.getStatusCode();
                    if (404 == statusCode || 500 == statusCode)
                    {
                        view.loadUrl("about:blank");// 避免出现默认的错误界面
                        //view.loadUrl(mErrorUrl);// 加载自定义错误页面
                        //显示重试界面.
                        if (mPromptLayout != null)
                        {
                            mPromptLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Throwable throwable)
                {
                    Logger.e(LOG_TAG, "onReceivedHttpError Throwable:{}, {}", mWebUrl, throwable);
                }
            }
        });
    }

    /**
     * 切换频道数据.
     * @param frequencyType 频道类型(0:全部;1:男频;2:女频)
     */
    public void switchFrequency(int frequencyType)
    {
        mFrequencyType = frequencyType;
        //获取加载链接.
        //String url = "http://192.168.0.79:8000/hotList?" + createParam(frequencyType);
        String url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.FIND_RECOM);
        if (TextUtils.isEmpty(url))
        {
            //此处需要填写默认地址.
            url = "http://taskcenter.duoyueapp.com/hotList";
        }
        mWebUrl = url + "?";
        url += "?" + createParam(mFrequencyType);
        LogUtils.i(LOG_TAG, "切换频道:" + url);
        //加载Url.
        mWebView.loadUrl(url);
    }

    /**
     * 创建请求参数.
     * @param frequencyType 频道(0:全部;1:男频;2:女频)
     * @return
     */
    private String createParam(int frequencyType)
    {
        StringBuffer paramBuffer = new StringBuffer();
        //用户编号.
        UserInfo info = UserManager.getInstance().getUserInfo();
        paramBuffer.append("uid=").append(info != null ? info.uid : "");
        //版本号.
        paramBuffer.append("&version=").append(PhoneStatusManager.getInstance().getAppVersionName());
        //AppId.
        paramBuffer.append("&appId=").append(Constants.APP_ID);
        //渠道号.
        paramBuffer.append("&channelCode=").append(PhoneStatusManager.getInstance().getAppChannel());
        //客户端时间戳.
        paramBuffer.append("&timestamp==").append(TimeTool.currentTimeMillis());
        //获取位置信息.
        LocationModel locationModel = BDLocationMgr.getLocation();
        //省份.
        paramBuffer.append("&province=").append(locationModel != null ? locationModel.getProvince() : "");
        //城市.
        paramBuffer.append("&city=").append(locationModel != null ? locationModel.getCity() : "");
        //WiFi列表.
        paramBuffer.append("&wifis=").append(BDLocationMgr.getWiFis());
        //频道(0:全部;1:男频;2:女频).
        paramBuffer.append("&channel=").append(frequencyType);
        return paramBuffer.toString();
    }

    /**
     * Js回调类.
     *
     * @author caoyaming
     */
    public class JavaScriptObject
    {
        //sdk17版本以上加上注解   @JavascriptInterface
        public JavaScriptObject()
        {
        }

        /**
         * 点击书籍.
         */
        @JavascriptInterface
        public void clickBook(String bookId)
        {
            //Toast.makeText(getContext(), "点击书籍:" + bookId, Toast.LENGTH_LONG).show();
            Logger.i(LOG_TAG, "点击今天推荐WebView中的书籍:{}", bookId);
            if (!TextUtils.isEmpty(bookId))
            {
                Intent read = new Intent(getContext(), ReadActivity.class);
                read.putExtra(ParamKey.BOOK_ID, bookId);
                read.putExtra(BaseActivity.DATA_KEY, new BaseData("发现-推荐小说"));
                read.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.FIND_RECOMMAND_NOVEL);
                //设置来源为:发现-推荐小说
                read.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NEAR_READ_BOOK);
                getContext().startActivity(read);
            }
        }
    }

    /**
     * 资源回收.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        try
        {
            if (mWebView != null)
            {
                //删除所有子View
                mWebView.removeAllViews();
                //清除缓存
                clearCache();
                clearCookie();
                mWebView.loadUrl("about:blank");
                mWebView.freeMemory();
                //从父View中移除自己
                if (mWebView.getParent() != null)
                {
                    ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                }
                //回收资源.
                mWebView.destroy();
                //
                mWebView.clearAnimation();
                mWebView.clearDisappearingChildren();
                mWebView.removeAllViews();
                mWebView = null;
            }
        } catch (Throwable throwable)
        {
        }
    }

    /**
     * 清除缓存
     */
    public void clearCache()
    {
        try
        {
            //if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != new Date(new File(cachePath).lastModified()).getDate()) {
            //清除缓存
            mWebView.clearCache(true);
            mWebView.clearHistory();
            //}
        } catch (Exception e)
        {
        }

    }

    /**
     * 清除Cookie
     */
    public void clearCookie()
    {
        try
        {
            CookieSyncManager.createInstance(getContext());
            CookieSyncManager.getInstance().startSync();
            CookieManager.getInstance().removeSessionCookie();
        } catch (Exception e)
        {
        }
    }
}
