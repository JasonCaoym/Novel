package com.duoyue.app.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duoyue.app.common.mgr.*;
import com.duoyue.app.event.DayEvent;
import com.duoyue.app.notification.NotificationHolderV2Service;
import com.duoyue.app.upgrade.ReadModeUtil;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.MobileInfoPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDetailLoadUtils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdConfigBean;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.net.AdConfigTastk;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.IAdView;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.ErrorStatsApi;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.ParamKey;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.*;
import com.zzdm.ad.router.BaseData;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class SplashActivity extends BaseActivity implements AdCallbackListener {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#SplashActivity";
    private int[] splashDrawableId = {R.mipmap.splash_1, R.mipmap.splash_2, R.mipmap.splash_3, R.mipmap.splash_4};
    private static final String SKIP_TEXT = "点击跳过 %d";

    private boolean mForceGoMain = false;
    private boolean mIsShowAd = false;
    private boolean mIsContinueTime = false;

    private TextView skipView;
    private ViewGroup container;
    private View shadeView;
    private int skipTimeDown = 5;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (skipTimeDown > 0) {
                skipView.setText(skipTimeDown + " | 跳转");
                sendEmptyMessageDelayed(1, 1000);
                --skipTimeDown;
            } else {
                gotoHome();
            }
        }
    };
    private long mStartTime;
    private ViewGroup adContainer2;
    private AdSiteBean adSiteBean;
    private AdSiteBean adSiteSecondBean;
    private AtomicBoolean firstAdShowed = new AtomicBoolean(false);
    private AtomicBoolean secondAdShowed = new AtomicBoolean(false);
    private ReentrantLock lock = new ReentrantLock(true);
    /**
     * 广播接收器.
     */
    private SplashReceiver mSplashReceiver;

    IAdView splashFirstView;
    IAdView splashSecondView;
    private boolean isFirstLoad = true;
    private int mLive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果Activity在task存在, 拿到最顶端, 不会启动新的Activity(修复APP下载安装后, 点击"直接打开", 启动应用后, 按下HOME键, 再次点击桌面上的应用, 会重启的问题).
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //上报开屏失败.
            ErrorStatsApi.addError(ErrorStatsApi.SPLASH_FAIL);
            //结束你的activity
            finish();
            Logger.e(TAG, "onCreate: finish");
            return;
        }
        //金立手机拉活
        if (getIntent() != null && getIntent().getIntExtra(NotificationHolderV2Service.ALIVE, 0) == NotificationHolderV2Service.SPPASH_CODE) {
            EventBus.getDefault().post(new DayEvent());
            mLive = NotificationHolderV2Service.SPPASH_CODE;
//            UserManager userManager = UserManager.getInstance();
//            if (userManager != null) {
//                if (userManager.getUserInfo() == null) {
//                    FuncPageStatsApi.pullAliveActivity();
//                } else {
//                    if (TextUtils.isEmpty(userManager.getUserInfo().uid)) {
//                        FuncPageStatsApi.pullAliveActivity();
//                    }
//                }
//            }else {
//                FuncPageStatsApi.pullAliveActivity();
//            }
        }
        checkPermission();
        //启动时间.
        mStartTime = TimeTool.currentTimeMillis();
        setContentView(R.layout.splash_activity);
        initView();
        //初始化数据.
        initData();
        try {
            //注册广播.
            mSplashReceiver = new SplashReceiver();
            registerReceiver(mSplashReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        } catch (Throwable throwable) {
            Logger.e(TAG, "onCreate: {}", throwable);
        }
    }

    private void initView() {
        container = findView(R.id.splash_container);
        adContainer2 = findView(R.id.splash_container2);
        skipView = findView(R.id.skip_view);
        skipView.setVisibility(View.GONE);
        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHome();
            }
        });
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.SPLASH;
    }

    private void initData() {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 友盟新用户上报
                if (SharePreferenceUtils.getBoolean(getApplication(), SPUtils.INSTANCE.getSHARED_IS_FIRST_YM_UPLOAD(), true)) {
                    SharePreferenceUtils.putBoolean(getApplication(), SPUtils.INSTANCE.getSHARED_IS_FIRST_YM_UPLOAD(), false);
                    StatisHelper.onEvent(getApplicationContext(), "REJ_ACTION", new HashMap());
                }
                try {
                    AdConfigTastk task = new AdConfigTastk();
                    task.timeUp();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                //上报开屏成功.
                ErrorStatsApi.addError(ErrorStatsApi.SPLASH_SUCC);
                //添加启动统计.
                FunctionStatsApi.startApp();
                //启动定位.
                BDLocationMgr.startLocation();
                //调用补充IMEI、IMSI等信息接口.
                MobileInfoPresenter.uploadSupplyMobileInfo();
                //初始化图片加载内存.
                GlideUtils.INSTANCE.initGlide(getApplicationContext());
                //清理网络缓存数据
                BookDetailLoadUtils.cleanCacheData(BookDetailLoadUtils.getCachePath());
            }
        });
    }

    private Runnable mDelayGoHome = new Runnable() {
        @Override
        public void run() {
            gotoHome();
        }
    };

    private int getDrawableBg() {
        Random random = new Random();
        return random.nextInt(splashDrawableId.length);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            checkLoadAdView();
        } else if (mForceGoMain) {
            gotoHome();
        } else if (mIsContinueTime) {
            if (handler != null && mDelayGoHome != null) {
                handler.postDelayed(mDelayGoHome, 1000);
            } else {
                gotoHome();
            }
        }
    }

    private void checkLoadAdView() {
        AdConfigBean adConfigBean = AdConfigManger.getInstance().getAvailableAdConfig(Constants.channalCodes[0]);
        adSiteBean = AdConfigManger.getInstance().showAd(this, Constants.channalCodes[0]);
        if (!checkAppIdChanged() && adSiteBean != null) {
            adSiteSecondBean = AdConfigManger.getInstance().getAvailiableAdSite(this, adConfigBean, adSiteBean.getId());
            mIsShowAd = true;
            shadeView = findViewById(R.id.splash_shade);
            shadeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.e(TAG, "遮罩层被点击了");
                }
            });
            // 判断广告来源和广告类型，广告类型只支持开屏和信息流
            splashFirstView = AdConfigManger.getInstance().getAdView(this, adSiteBean.getChannelCode(), adSiteBean);
            if (splashFirstView != null) {
                splashFirstView.init(container, skipView, 30, this);
                splashFirstView.showAd();
            }

            // 第二个广告View
            if (adSiteSecondBean != null) {
                splashSecondView = AdConfigManger.getInstance().getAdView(this, adSiteBean.getChannelCode(), adSiteSecondBean);
            }
            if (splashSecondView != null) {
                splashSecondView.init(adContainer2, skipView, 30, new AdCallbackListener() {
                    @Override
                    public void pull(AdSiteBean adSiteBean) {

                    }

                    @Override
                    public void pullFailed(AdSiteBean adSiteBean, String code, String errorMsg) {

                    }

                    @Override
                    public void onShow(final AdSiteBean adSiteBean) {
                        if (!firstAdShowed.get()) {
                            secondAdShowed.set(true);
                            Logger.e(TAG, "显示第二个广告");
                            container.setVisibility(View.GONE);
                            adContainer2.setVisibility(View.VISIBLE);
                            showAd(adSiteBean);
                        } else {
                            showGiveUp(adSiteBean);
                            Logger.e(TAG, "第一个广告已经显示了，第二隐藏");
                        }
                    }

                    @Override
                    public void onClick(AdSiteBean adSiteBean) {

                    }

                    @Override
                    public void onError(AdSiteBean adSiteBean, String code, String errorMsg) {

                    }

                    @Override
                    public void onDismiss(AdSiteBean adSiteBean) {
                        if (secondAdShowed.get()) {
                            dismiss();
                        }
                    }

                    @Override
                    public void onAdTick(long time) {
                        if (skipView != null) {
                            skipView.setText(String.format(SKIP_TEXT, Math.round(time / 1000f)));
                        }
                    }
                });
                splashSecondView.showAd();
            } else {
                Logger.w("ad#http", "没有可用的第二个开屏广告源可用");
            }

            // 避免广告无响应的情况
            handler.postDelayed(mDelayGoHome, 5000);
            //加载开屏广告.
            ErrorStatsApi.addError(ErrorStatsApi.SPLASH_AD);
        } else {
            Logger.e(TAG, "没有广告");
            mIsShowAd = false;
            handler.postDelayed(mDelayGoHome, 1000);
            //无开屏广告
            ErrorStatsApi.addError(ErrorStatsApi.SPLASH_NO_AD);
        }
    }

    private void checkPermission() {
        SPUtils.INSTANCE.putBoolean(PermissionMgr.PERMISSION_REQUEST_TODAY, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        //在没有开启广告的情况下, 如果用户点击Home键回到后台, 等延迟时间达到时, 直接启动到主页.
        //if (!mIsShowAd) {
        //handler.removeCallbacks(mDelayGoHome);
        //mIsContinueTime = true;
        //}
    }

    private void gotoHome() {
        try {
            //启动Home页.
            ErrorStatsApi.addError(ErrorStatsApi.GO_HOME);
            if (handler != null && mDelayGoHome != null) {
                handler.removeCallbacks(mDelayGoHome);
            }
            //创建启动HomeActivity Intent.
            if (!TextUtils.isEmpty(SPUtils.INSTANCE.getString(ReadModeUtil.TAG))) {
                long readTimeTotle = SPUtils.INSTANCE.getLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), 0);
                long showTiredDialogTime = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_TIRED_TIME, 10000)
                        * TimeUtils.MINUTE_1;
                if (readTimeTotle >= showTiredDialogTime) {
                    SPUtils.INSTANCE.putLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), 0);
                }
                //获取免广告剩余时长等信息.
                TaskMgr.getReadHistory(getApplicationContext());
                ActivityHelper.INSTANCE.gotoRead(SplashActivity.this, SPUtils.INSTANCE.getString(ReadModeUtil.TAG),
                        new BaseData("进程重启后恢复阅读状态"), PageNameConstants.SPLASH, "");
                FuncPageStatsApi.readrRestart();

                PushMgr.checkVivoAlias();
                PushMgr.checkXiaomiAlias(SplashActivity.this);

                int brand = PushMgr.getBrand();
//                if (brand != 3 && brand != 4) {
                if (brand == 2) {
                    PushMgr.uploadPushDeviceInfo();
                }
            } else {

                if (UserManager.getInstance().getUserInfo() != null) {
                    //已登录, 直接进入主页.
                    startHomeActivity();
                    PushMgr.checkVivoAlias();
                    PushMgr.checkXiaomiAlias(SplashActivity.this);
                    int brand = PushMgr.getBrand();
//                    if (brand != 3 && brand != 4) {
                    if (brand == 2) {
                        PushMgr.uploadPushDeviceInfo();
                    }
                    Logger.e(TAG, "有用户数据");
                } else {
                    Logger.e(TAG, "没有用户数据");
                    //用户未登录, 注册登录成功广播.
                    if (mSplashReceiver == null) {
                        mSplashReceiver = new SplashReceiver();
                    }
                    registerReceiver(mSplashReceiver, new IntentFilter(Constants.LOGIN_SUCC_ACTION));
                    //用户登录.
                    //用户登录.
                    if (mLive != 0) {
                        UserLoginMgr.isLive(mLive);
                    }
                    UserLoginMgr.checkLogin(this);
                }
            }
        } catch (Throwable throwable) {
            //启动Home页.
            ErrorStatsApi.addError(ErrorStatsApi.GO_HOME_FAIL, throwable != null ? throwable.getMessage() : "NULL");
            Logger.e(TAG, "gotoHome: {}", throwable);
        }
    }

    // 检测appId是否更改
    private boolean checkAppIdChanged() {
        long prevAppid = SharePreferenceUtils.getLong(getApplication(), SharePreferenceUtils.PREV_APPID, Constants.APP_ID);
        if (prevAppid == Constants.APP_ID) {
            Logger.e(TAG, "appid 没有发生变化：" + Constants.APP_ID);
            return false;
        } else {
            // 清除用户所有数据
            Logger.e(TAG, "appid 发生变化，开始清除数据");
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            if (userInfo != null) {
                UserManager.getInstance().setUserInfo(null);
            }
            StartGuideMgr.clearData();
            // 系统配置
            SharePreferenceUtils.removeAllValue(getApplicationContext());
            SPUtils.INSTANCE.removeAll();
            SharePreferenceUtils.putLong(getApplication(), SharePreferenceUtils.PREV_APPID, Constants.APP_ID);
            /*ZExecutorService.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 广告数据库
                        AdOriginConfigHelp.getsInstance().clearAll();
                        AdPositionConfigHelp.getsInstance().clearAll();
                        AdReadConfigHelp.getsInstance().clearAll();
                        AdConfigHelp.getsInstance().clearAll();
                        // 书籍数据库
                        BookRecordHelper.getsInstance().removeAllBook();
                        BookShelfHelper.getsInstance().clearAll();
                        BookChapterHelper.getsInstance().clearAll();
                        // 书籍统计数据库
                        AdStatsHelper.getInstance().clearAll();
                        FuncPagetatsHelper.getInstance().clearAll();
                        FunctionStatsHelper.getInstance().clearAll();
                    } catch (Exception ex) {
                        Logger.e(TAG, "数据清除报错：" + ex.getMessage());
                    } finally {
                        Logger.e(TAG, "app数据清除完成");

                    }
                }
            });*/
            return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e(TAG, "启动界面被销毁了");
        //销毁开屏.
        ErrorStatsApi.addError(ErrorStatsApi.DESTORY_SPLASH, String.valueOf(TimeTool.currentTimeMillis() - mStartTime));
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        //注销广播接收器.
        if (mSplashReceiver != null) {
            try {
                unregisterReceiver(mSplashReceiver);
                mSplashReceiver = null;
            } catch (Throwable throwable) {
            }
        }
        if (splashFirstView != null) {
            splashFirstView.destroy();
        }
        if (splashSecondView != null) {
            splashSecondView.destroy();
        }
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (findViewById(R.id.loading_page_id).getVisibility() == View.VISIBLE) {
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void pull(AdSiteBean adSiteBean) {

    }

    @Override
    public void pullFailed(AdSiteBean adSiteBean, String code, String errorMsg) {

    }

    @Override
    public void onShow(AdSiteBean adSiteBean) {
        if (!secondAdShowed.get()) {
            firstAdShowed.set(true);
            adContainer2.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
            Logger.e(TAG, "显示第一个广告");
            showAd(adSiteBean);
        } else {
            Logger.e(TAG, "第二个广告已经显示了，第一个隐藏");
            showGiveUp(adSiteBean);
        }
    }

    private void showAd(AdSiteBean adSiteBean) {
        if (lock.isLocked()) {
            return;
        }
        if (lock.tryLock()) {
            if (handler != null && mDelayGoHome != null) {
                handler.removeCallbacks(mDelayGoHome);
            }
            if (adSiteBean.getAdType() != AdConstants.Type.LAUNCHING) {
                if (firstAdShowed.get()) {
                    container.setPadding(30, 150, 30, 0);
                    container.setBackgroundResource(splashDrawableId[getDrawableBg()]);
                } else if (secondAdShowed.get()) {
                    adContainer2.setPadding(30, 150, 30, 0);
                    adContainer2.setBackgroundResource(splashDrawableId[getDrawableBg()]);
                }
            }
            findView(R.id.default_start_img).setVisibility(View.GONE);
            if (adSiteBean.getAdType() != AdConstants.Type.LAUNCHING) {
                skipView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(1);
            } else {
                skipView.setVisibility(View.GONE);
            }
            AdConfigManger.getInstance().updateShowNum(adSiteBean);
            //展示开屏广告.
            ErrorStatsApi.addError(ErrorStatsApi.SHOW_SPLASH_AD, String.valueOf(true));
            AdHttpUtil.pullSuccess(adSiteBean);
            AdHttpUtil.showSuccess(adSiteBean);
        }
    }

    @Override
    public void onClick(AdSiteBean adSiteBean) {

    }

    @Override
    public void onError(AdSiteBean adSiteBean, String code, String errorMsg) {

    }

    @Override
    public void onDismiss(AdSiteBean adSiteBean) {
        if (firstAdShowed.get()) {
            dismiss();
        }
    }

    private void dismiss() {
        //关闭开屏广告.
        ErrorStatsApi.addError(ErrorStatsApi.CLOSE_SPLASH_AD);
        gotoHome();
    }

    @Override
    public void onAdTick(long time) {
        if (skipView != null) {
            skipView.setText(String.format(SKIP_TEXT, Math.round(time / 1000f)));
        }
    }

    /**
     * 上报放弃展示上报节点
     *
     * @param adSiteBean
     */
    public void showGiveUp(AdSiteBean adSiteBean) {
        AdHttpUtil.showGiveUp(adSiteBean);
    }

    /**
     * 启动主页Activity.
     */
    private void startHomeActivity() {
        try {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra(ParamKey.PUSH_DATA_KEY, getIntent() != null ? getIntent().getStringExtra(ParamKey.PUSH_DATA_KEY) : null);
            startActivity(homeIntent);
            finish();
        } catch (Throwable throwable) {
            Logger.e(TAG, "startHomeActivity: {}", throwable);
        }
    }

    /**
     * 开屏广播接收器.
     */
    private class SplashReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equalsIgnoreCase(intent.getAction())) {
                    String reason = intent.getStringExtra("reason");
                    if (!StringUtils.isEmpty(reason) && reason.equalsIgnoreCase("homekey")) {
                        //点击Home键.
                        ErrorStatsApi.addError(ErrorStatsApi.SPLASH_HOME_KEY, String.valueOf(TimeTool.currentTimeMillis() - mStartTime));
                    }
                } else if (Constants.LOGIN_SUCC_ACTION.equalsIgnoreCase(intent.getAction())) {
                    // oppo渠道部分机型极端情况下会报错：https://bbs.coloros.com/thread-174655-3-1.html
                    try {
                        context.startService(new Intent(SplashActivity.this, NotificationHolderV2Service.class));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //登录成功广播, 进入主页.
                    startHomeActivity();
                    //调用补充IMEI、IMSI等信息接口.
                    MobileInfoPresenter.uploadSupplyMobileInfo();
                }
            } catch (Throwable throwable) {
                Logger.e(TAG, "onReceive: {}", throwable);
            }
        }
    }
}
