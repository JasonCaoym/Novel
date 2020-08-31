package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.common.callback.UserLoginCallback;
import com.duoyue.app.common.mgr.UserLoginMgr;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.devices.SystemUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.net.DomainConfig;
import com.zydm.base.ui.BaseActivityHelper;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.activity.web.WebActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 关于我们Activity
 *
 * @author caoym
 * @data 2019/4/17  20:23
 */
public class AboutUsActivity extends BaseActivity {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#AboutUsActivity";

    /**
     * 退出登录Layout
     */
    private View mSignOutView;

    /**
     * 退出登录TextView
     */
    private TextView mSignOutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);
        //应用版本名称.
        PackageInfo packageInfo = SystemUtil.getPackageInfo(getApplicationContext(), getPackageName());
        ((TextView) findViewById(R.id.au_version_textview)).setText(packageInfo != null ? packageInfo.versionName : "");
        //用户协议.
        findViewById(R.id.au_user_agreement_layout).setOnClickListener(this);
        //退出登录Layout.
        mSignOutView = findViewById(R.id.au_sign_out_layout);
        //退出登录按钮.
        mSignOutTextView = findViewById(R.id.au_sign_out_btn);
        //获取用户信息.
//        UserInfo userInfo = UserManager.getsInstance().getUserInfo();
//        if (userInfo != null && userInfo.type != LoginPresenter.USER_TYPE_TOURIST) {
//            //已登录状态, 显示登出操作.
//            mSignOutView.setVisibility(View.VISIBLE);
//            mSignOutView.setOnClickListener(this);
//        }
    }

    @Override
    public void initStateBar(@Nullable View layoutTitle) {
        super.initStateBar(layoutTitle);
        setToolBarLayout(R.string.about_us);
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.ABOUT_US;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.au_user_agreement_layout:
                //用户协议.
                String url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.USER_AGREEMENT);
                if (TextUtils.isEmpty(url))
                {
                    //此处需要填写默认地址.
                    url = "http://app.duoyueapp.com/user_rules.html";
                }
                //BaseActivityHelper.INSTANCE.gotoWebActivity(this, new WebActivity.Data(DomainConfig.INSTANCE.getAboutH5(), ViewUtils.getString(R.string.about_us)));
                BaseActivityHelper.INSTANCE.gotoWebActivity(this, new WebActivity.Data(url, ViewUtils.getString(R.string.about_us)));
                break;
            case R.id.au_sign_out_layout:
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
                        if (mSignOutTextView != null) {
                            mSignOutTextView.setText(R.string.sign_out_ing);
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
                        if (mSignOutTextView != null) {
                            mSignOutTextView.setText(R.string.sign_out);
                        }
                        ToastUtils.show(R.string.sign_out_fail);
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
