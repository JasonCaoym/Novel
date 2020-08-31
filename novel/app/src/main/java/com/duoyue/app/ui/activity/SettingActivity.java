package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.common.callback.UserLoginCallback;
import com.duoyue.app.common.mgr.UserLoginMgr;
import com.duoyue.app.common.mgr.WhiteListMgr;
import com.duoyue.app.ui.widget.CustomImageView;
import com.duoyue.app.ui.widget.SwitchButton;
import com.duoyue.app.upgrade.UpgradeManager;
import com.duoyue.app.upgrade.UpgradeMsgUtils;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.devices.UtilSharedPreferences;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "App#SettingActivity";

    public static final String SETTING_BOOKSHELF_RECOMMEND_KEY = "setting_bookshelf_recommend_key";
    public static final String ACTION_SETTING_BOOKSHELF_RECOMMEND_CHANGED = "action_setting_bookshelf_recommend_changed";

    /**
     * 退出登录Layout
     */
    private View mSignOutView;
    private View mIgnorBatteryView;
    private View mWhiteListView;
    /**
     * 书架书籍推荐开关
     */
    private SwitchButton switchRecommend;
    private List<String> brandsList = WhiteListMgr.getSupportWhiteList();
    private String guidUrl;
    private CustomImageView mIvredPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.setting);
        mSignOutView = findViewById(R.id.setting_exit);
        //获取用户信息.
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.type != LoginPresenter.USER_TYPE_TOURIST) {
            mSignOutView.setVisibility(View.VISIBLE);
            mSignOutView.setOnClickListener(this);
        } else {
            mSignOutView.setVisibility(View.GONE);
        }

        mIgnorBatteryView = findViewById(R.id.setting_ignor_battery);
        Logger.e(TAG, "Build.MANUFACTURER : " + Build.MANUFACTURER);
        mWhiteListView = findViewById(R.id.setting_whitelist);

        // 判断系统是否6.0及以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mIgnorBatteryView.setVisibility(View.VISIBLE);
            // 判断是否是支持的四大厂商的手机
            if (brandsList.contains(Build.MANUFACTURER.toLowerCase())) {
                mWhiteListView.setVisibility(View.VISIBLE);
                findViewById(R.id.setting_whitelist_click).setOnClickListener(this);

                if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
                    guidUrl = WhiteListMgr.getOppoWhiteListUrl();
                } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
                    guidUrl = WhiteListMgr.getVivoWhiteListUrl();
                } else if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
                    guidUrl = WhiteListMgr.getHuaweiWhiteListUrl();
                } else if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
                    guidUrl = WhiteListMgr.getXiaomiWhiteListUrl();
                } else if (Build.MANUFACTURER.equalsIgnoreCase("meizu")) {
                    guidUrl = WhiteListMgr.getMeizuWhiteListUrl();
                } else if (Build.MANUFACTURER.equalsIgnoreCase("gionee")) {
                    guidUrl = WhiteListMgr.getJinliWhiteListUrl();
                }
            } else {
                mWhiteListView.setVisibility(View.GONE);
            }
        } else {
            mIgnorBatteryView.setVisibility(View.GONE);
            mWhiteListView.setVisibility(View.GONE);
        }

        switchRecommend = findViewById(R.id.switch_bookshelf_recommend);
        boolean isCanRecommend = SPUtils.INSTANCE.getBoolean(SETTING_BOOKSHELF_RECOMMEND_KEY, true);
        switchRecommend.setChecked(isCanRecommend);
        switchRecommend.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SPUtils.INSTANCE.putBoolean(SETTING_BOOKSHELF_RECOMMEND_KEY, isChecked);
                Intent croadcast = new Intent(ACTION_SETTING_BOOKSHELF_RECOMMEND_CHANGED);
                sendBroadcast(croadcast);
                FuncPageStatsApi.switchBookshelfReco(isChecked ? 1 : 2);
            }
        });

        findViewById(R.id.setting_ignor_battery_click).setOnClickListener(this);
        if (PhoneUtil.isIgnorBatteryOptimization(this)) {
            findViewById(R.id.setting_ignor_battery_tip).setVisibility(View.VISIBLE);
            findViewById(R.id.setting_ignor_battery_icon).setVisibility(View.GONE);
        } else {
            findViewById(R.id.setting_ignor_battery_tip).setVisibility(View.GONE);
            findViewById(R.id.setting_ignor_battery_icon).setVisibility(View.VISIBLE);
        }

        //检查更新
        View updateView = findViewById(R.id.setting_update_click);
        ((TextView) updateView.findViewById(R.id.setting_update_version)).setText(ViewUtils.getString(R.string.current_version_name,
                PhoneStatusManager.getInstance().getAppVersionName()));
        updateView.setOnClickListener(this);
        mIvredPoint = findViewById(R.id.iv_red_point);
        if (UpgradeMsgUtils.isHasUpdateInfo(this)) {
            mIvredPoint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.SETTING;
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.setting_exit:
                exitLogin();
                break;
            case R.id.setting_whitelist_click:
                ActivityHelper.INSTANCE.gotoWeb(this, guidUrl);
                FuncPageStatsApi.settingWhileList();
                break;
            case R.id.setting_ignor_battery_click:
                PhoneUtil.setBatteryOptimization(SettingActivity.this);
                break;
            case R.id.setting_update_click:
                checkUpdate();
                break;
        }
    }

    private void checkUpdate() {
        boolean isDownloading = UtilSharedPreferences.getBooleanData(this, UtilSharedPreferences.KEY_IS_DOWNLOADING);
        if (isDownloading) {
            ToastUtils.show(R.string.downloading);
            return;
        }
        ToastUtils.showLimited(R.string.start_check);
        StatisHelper.onEvent().upgradeClick();
        UpgradeManager.getInstance(this).startManualCheck(this);
    }

    private void exitLogin() {
        //退出登录.
        UserLoginMgr.userSignOut(this, new UserLoginCallback() {
            /**
             * 登录开始
             */
            @Override
            public void onLoginStart(int type) {
                //退出登录成功, 提示退出登录中.
                if (mSignOutView != null) {
                    mSignOutView.setClickable(false);
                }
                //点击退出登录.
                FunctionStatsApi.mSignOutClick();
                FuncPageStatsApi.mineUnloginClick();
            }

            /**
             * 取消登录.
             */
            @Override
            public void onLoginCancel(int type) {
                //取消退出登录.
            }

            /**
             * 登录成功.
             */
            @Override
            public void onLoginSucc(int type, UserInfo userInfo) {
                //退出登录成功, 隐藏登出按钮.
                if (mSignOutView != null) {
                    mSignOutView.setVisibility(View.GONE);
                }
                try {
                    //发送登录成功广播.
                    Intent intent = new Intent(Constants.LOGIN_SUCC_ACTION);
                    BaseContext.getContext().sendBroadcast(intent);
                } catch (Throwable throwable) {
                    Logger.e(TAG, "loginSucc: {}, {}", userInfo, throwable);
                }
            }

            /**
             * 登录失败.
             * @param errMsg
             */
            @Override
            public void onLoginFail(int type, String errMsg) {
                //退出登录失败.
                if (mSignOutView != null) {
                    mSignOutView.setClickable(true);
                }
                ToastUtils.show(R.string.sign_out_fail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (PhoneUtil.isIgnorBatteryOptimization(this)) {
                findViewById(R.id.setting_ignor_battery_tip).setVisibility(View.VISIBLE);
                findViewById(R.id.setting_ignor_battery_icon).setVisibility(View.GONE);
                FuncPageStatsApi.settingIgnorBattery();
            }
        }
    }
}
