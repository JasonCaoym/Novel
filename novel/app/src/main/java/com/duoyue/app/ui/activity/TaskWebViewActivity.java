package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.duoyue.app.bean.BookNewBookInfoBean;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.app.common.mgr.UserLoginMgr;
import com.duoyue.app.event.LoginSuccessEvent;
import com.duoyue.app.event.TaskFinishEvent;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.view.OnWebCallBack;
import com.duoyue.app.ui.widget.ProgressWebView;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ExtraPageMgr;
import com.duoyue.mianfei.xiaoshuo.read.utils.AppMarketUtils;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.NetWorkUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zzdm.ad.router.BaseData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.duoyue.app.common.mgr.TaskMgr.REWARD_VIDEO_SIGN_TASK;
import static com.duoyue.app.common.mgr.TaskMgr.REWARD_VIDEO_TASK;

/**
 * 任务中心webView Activity
 */
public class TaskWebViewActivity extends BaseActivity implements OnWebCallBack {

    private static final String TAG = "App#TaskWebViewActivity";

    private ProgressWebView mWebView;
    private ImageView mIvCloseWeb;
    private PromptLayoutHelper mPromptLayoutHelper;
    private String mUrl;
    private WebSettings mWebSettings;
    private Handler handler = new Handler();
    private boolean isMark;
    private ExtraPageMgr mExtraPageMgr;
    private boolean isBackground;
    private FrameLayout adContainer;
    public static int mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_web);

        if (null != savedInstanceState && mWebView != null) {
            mWebView.restoreState(savedInstanceState);
            Logger.i(TAG, "restore state");
        } else {
            initIntent();
            initView();
            getPromptLayoutHelper();
            initSetting();
            loadUrl();
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.TASK_CENTERY;
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
    }

    private PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private void initTitle(String title) {
        setToolBarLayout(title);
    }

    private void loadUrl() {
        if (NetWorkUtils.INSTANCE.isNetWorkAvailable(this)) {
            mPromptLayoutHelper.hide();
            mWebView.loadUrl(mUrl);
        } else {
            showPromptProblem();
        }
    }

    private void showPromptProblem() {
        mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        });
    }

    private void initView() {
        if (mWebView == null) {
            mWebView = new ProgressWebView(getApplicationContext());
        }
        FrameLayout mWebContainer = findView(R.id.web_view_contain);
        mWebContainer.addView(mWebView);
        mIvCloseWeb = findView(R.id.iv_close_web);
        mIvCloseWeb.setOnClickListener(this);

        adContainer = findViewById(R.id.task_ad_container);
        mExtraPageMgr = new ExtraPageMgr();
        mExtraPageMgr.init(this,  "");
    }

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

        mWebView.addJavascriptInterface(new JsInterface(this), "android");

        mWebView.setOnWebCallBack(this);
    }

    @Override
    public void getTitle(String title) {
        initTitle(title);
    }

    @Override
    public void getUrl(String url) {
        Logger.d(TAG, url);
    }

    @Override
    public void onShowPromptProblem() {
//        showPromptProblem();
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_close_web:
                finish();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
        Logger.e(TAG, "save state...");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSwitchEvent(final LoginSuccessEvent event) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo = event.getUserInfo();
                if (mWebView == null) return;
                mWebView.loadUrl("javascript:androidSignSuccess('" + userInfo.uid + "');");
                //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState,(登录成功回到页面马上切回后台,onPouse状态下弹出弹框会抛出该异常)
                if (isBackground) return;
                if (userInfo.bookBeans > 0) {
                    TaskMgr.showDialog(event.getUserInfo().bookBeans, getString(R.string.finish_Login_task), getSupportFragmentManager(), TaskMgr.LOGIN_TASK);
                }
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//                    //4.4以下
//                    Logger.d(TAG, "handleSwitchEvent: 4.4以下");
//                    mWebView.loadUrl("javascript:androidSignSuccess('" + userInfo.uid + "');");
//                } else {
//                    //4.4以上，包括4.4
//                    Logger.d(TAG, "handleSwitchEvent: 4.4以上");
//                    mWebView.evaluateJavascript("javascript:androidSignSuccess('" + userInfo.uid + "');", new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//
//                        }
//                    });
//                }

                Logger.d(TAG, "handleSwitchEvent: 登录任务完成");
            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSwitchEvent(final TaskFinishEvent event) {
        if (event.getTaskId() == TaskMgr.MAARK_TASK || event.getTaskId() == TaskMgr.BATTERY_TASK
                || event.getTaskId() == REWARD_VIDEO_TASK || event.getTaskId() == REWARD_VIDEO_SIGN_TASK) {
//            mWebView.loadUrl(MinePresenter.getUrl(Constants.DOMAIN_TASK_H5 + "/taskCenter"));
//            mWebView.loadUrl(MinePresenter.getUrl(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER)));
            String url = "";
            if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER))) {
                url = "http://taskcenter.duoyueapp.com/";
            } else {
                url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER);
            }
            mWebView.loadUrl(url);
            if (event.getTaskId() == REWARD_VIDEO_SIGN_TASK) {
                mWebView.loadUrl("javascript:getSignVideoResult('" + 1 + "');");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (PhoneUtil.isIgnorBatteryOptimization(this)) {
                TaskMgr.show(TaskWebViewActivity.this, getSupportFragmentManager(), getString(R.string.finish_battery_task), TaskMgr.BATTERY_TASK);
            }
        } else if (requestCode == 102 && resultCode == 10003) {
            mWebView.loadUrl("javascript:refresh('" + 1 + "');");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebSettings.setJavaScriptEnabled(true);
        }
        Logger.d(TAG, "handleSwitchEvent: 回了" + isMark);
        if (isMark) {
            isMark = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TaskMgr.show(TaskWebViewActivity.this, getSupportFragmentManager(), getString(R.string.finish_mark_task), TaskMgr.MAARK_TASK);
                }
            }, 1000);
        }

        // 处理广点通激励视频不回调关闭动作问题
        if (isBackground && mExtraPageMgr.getHasShowVideo()) {
            TaskMgr.show(TaskWebViewActivity.this, getSupportFragmentManager(), getString(R.string.finish_reward_video_task), mTaskId);
        }
        mExtraPageMgr.setHasShowVideo(false);
        isBackground = false;
        adContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBackground = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }


    static class JsInterface extends Object {

        private WeakReference<TaskWebViewActivity> taskWebViewActivity;

        public JsInterface(TaskWebViewActivity context) {
            taskWebViewActivity = new WeakReference<>(context);
        }

        @JavascriptInterface
        public void toLogin() {// 无返回值，有参
            taskWebViewActivity.get().toLogin();
        }

        @JavascriptInterface
        public void signSuccess() {// 无返回值，有参
            taskWebViewActivity.get().signSuccess();
        }

        @JavascriptInterface
        public void bookBeansChanged() {// 无返回值，有参
            taskWebViewActivity.get().bookBeansChanged();
        }

        /**
         * @param type 0书架 1书城
         */
        @JavascriptInterface
        public void toBookCity(int type) {// 无返回值，有参
            taskWebViewActivity.get().toBookCity(type);
        }

        @JavascriptInterface
        public void giveAMark() {
            taskWebViewActivity.get().giveAMark();
        }

        @JavascriptInterface
        public void toast(String content) {
            taskWebViewActivity.get().toast(content);
        }

        @JavascriptInterface
        public void battery() {
            taskWebViewActivity.get().battery();
        }

        @JavascriptInterface
        public boolean isOpenBattery() {
            return taskWebViewActivity.get().isOpenBattery();
        }

        /**
         * 判断任务激励视频
         * @return
         */
        @JavascriptInterface
        public boolean hasRewardVideo() {
            return taskWebViewActivity.get().hasRewardVideo(REWARD_VIDEO_TASK);
        }

        /**
         * 判断签到激励视频
         * @return
         */
        @JavascriptInterface
        public boolean hasSignInRewardVideo() {
            return taskWebViewActivity.get().hasRewardVideo(REWARD_VIDEO_SIGN_TASK);
        }

        @JavascriptInterface
        public void playVideoAd(int taskId) {
            taskWebViewActivity.get().playVideoAd(taskId);
        }

        @JavascriptInterface
        public String getUserLoginInfo() {
            return taskWebViewActivity.get().getLoginInfo();
        }

        @JavascriptInterface
        public void onStartIntentBookDetail(int bookid, String source, int modelId) {
            taskWebViewActivity.get().onIntentBookDetail(bookid, source, modelId);
        }

        @JavascriptInterface
        public void onAddBookShelf(String json) {
            Gson gson = new Gson();
            BookNewBookInfoBean bookNewBookInfoBean = gson.fromJson(json, new TypeToken<BookNewBookInfoBean>() {
            }.getType());
            taskWebViewActivity.get().onAddBookShelf(bookNewBookInfoBean);
        }

    }

    /**
     * 获取参数
     */
    private String getLoginInfo() {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        Boolean isLogin;
        if (userInfo == null) return "";
        isLogin = userInfo.type != 1;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("uid=").append(userInfo.uid)
                .append("&version=").append(PhoneStatusManager.getInstance().getAppVersionName())
                .append("&appId=").append(Constants.APP_ID)
                .append("&channelCode=").append(PhoneStatusManager.getInstance().getAppChannel())
                .append("&isLogin=").append(isLogin)
                .append("&mid=").append(UserManager.getInstance().getMid());

        return stringBuilder.toString();
    }

    /**
     * 是否开启了电池优化白名单
     *
     * @return true已开启 false未开启
     */
    private boolean isOpenBattery() {
        return PhoneUtil.isIgnorBatteryOptimization(this);
    }

    /**
     * 开启忽略电池优化弹框
     */
    private void battery() {
        PhoneUtil.setBatteryOptimization(this);
    }


    private void onIntentBookDetail(int bookid, String source, int modelId) {
        ActivityHelper.INSTANCE.gotoBookDetails(this, "" + bookid, new BaseData(""),
                PageNameConstants.BOOKLIST, modelId, source, 102);
    }

    private void onAddBookShelf(BookNewBookInfoBean bookNewHeaderBean) {
        BookShelfPresenter.addBookListShelf(bookNewHeaderBean);
        StatisHelper.onEvent().subscription(bookNewHeaderBean.getName(), "H5书单页加入书架");
    }

    private void toast(String content) {
        ToastUtils.showLimited(content);
    }

    /**
     * 是否可以播放激励视频
     *
     * @return
     */
    private boolean hasRewardVideo(int taskId) {
        if (taskId == REWARD_VIDEO_TASK) {
            return AdConfigManger.getInstance().showAd(this, Constants.channalCodes[6]) != null;
        } else if (taskId == REWARD_VIDEO_SIGN_TASK){
            return AdConfigManger.getInstance().showAd(this, Constants.channalCodes[5]) != null;
        } else {
            return false;
        }
    }

    private void playVideoAd(final int taskId) {
        mTaskId = taskId;
        handler.post(new Runnable() {
            @Override
            public void run() {
                adContainer.setVisibility(View.VISIBLE);
                int index = 6;
                String prevPageId = FunPageStatsConstants.TASK    ;
                String modelId = "4";
                if (taskId == REWARD_VIDEO_SIGN_TASK) {
                    index = 5;
                    prevPageId = FunPageStatsConstants.H5_SIGNV;
                    modelId = "5";
                }
                AdSiteBean adSiteBean = AdConfigManger.getInstance().showAd(TaskWebViewActivity.this, Constants.channalCodes[index]);

                if (adSiteBean != null) {
                    mExtraPageMgr.showRewardVideo(handler, adContainer, 0, adSiteBean.getChannelCode(), prevPageId, modelId, "");
                }
            }
        });
    }

    /**
     * 去评分
     */
    private void giveAMark() {
        boolean b = AppMarketUtils.gotoMarket(this);
        isMark = b;
        Logger.d(TAG, "giveAMark: 去评分" + isMark);
    }

    /**
     * 去登陆
     */
    private void toLogin() {
        UserLoginMgr.showLoginPhonePage(this);
    }

    /**
     * 书豆变化
     */
    private void bookBeansChanged() {
        mJsCallBackListener.bookbeansChanged();
    }

    /**
     * 去书城
     *
     * @param type
     */
    private void toBookCity(int type) {
        mJsCallBackListener.toBookCity(type);
        finish();
    }

    /**
     * 签到成功
     */
    private void signSuccess() {
        mJsCallBackListener.signSuccess();
        //通知书架更改状态
        EventBus.getDefault().post(new TaskFinishEvent(TaskMgr.SIGN_TASK));
    }


    public static JsCallBackListener mJsCallBackListener;

    public static void setJsCallBackListener(JsCallBackListener jsCallBackListener) {
        mJsCallBackListener = jsCallBackListener;
    }

    public interface JsCallBackListener {

        void signSuccess();

        void toBookCity(int type);

        void bookbeansChanged();
    }
}
