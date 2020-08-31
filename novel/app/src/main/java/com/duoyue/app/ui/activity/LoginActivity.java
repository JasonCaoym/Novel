package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.duoyue.app.common.callback.UserLoginCallback;
import com.duoyue.app.common.mgr.UserLoginMgr;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.text.TextTool;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.share.platform.LoginUtil;
import com.share.platform.ShareUtil;
import com.share.platform.login.LoginListener;
import com.share.platform.login.LoginPlatform;
import com.share.platform.login.LoginResult;
import com.sina.weibo.sdk.WbSdk;
import com.zydm.base.data.net.DomainConfig;
import com.zydm.base.ui.BaseActivityHelper;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.activity.web.WebActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 登录Activity
 *
 * @author caoym
 * @data 2019/4/24  22:57
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, UserLoginCallback {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#LoginActivity";

    /**
     * 手机号码输入框.
     */
    private EditText mPhoneNumberEditText;

    /**
     * 验证码输入框.
     */
    private EditText mVerificationCodeEditText;

    /**
     * 发送验证码按钮.
     */
    private TextView mSendVerifiCodeBtn;

    /**
     * 倒计时按钮.
     */
    private TextView mSendCountdownBtn;

    /**
     * 发送登录手机按钮.
     */
    private Button mSendLoginPhoneBtn;

    /**
     * 登录微信入口.
     */
    private ImageButton mLoginWechatImgBtn;

    /**
     * 登录QQ入口.
     */
    private ImageButton mLoginQQImgBtn;

    /**
     * 登录微博入口.
     */
    private ImageButton mLoginWeiboImgBtn;

    /**
     * Hander
     */
    private Handler mHandler;

    /**
     * 新浪微博
     */
    //private SsoHandler mSsoHandler;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_phone_layout);
        //初始化View.
        initView();
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.LOGIN;
    }

    /**
     * 初始化View.
     */
    private void initView() {
        setToolBarLayout("登录");
        //获取电话号码输入框.
        mPhoneNumberEditText = findViewById(R.id.phone_number_edittext);
        //获取验证码输入框.
        mVerificationCodeEditText = findViewById(R.id.verification_code_edittext);
        //发送验证码按钮.
        mSendVerifiCodeBtn = findViewById(R.id.send_verifi_code_btn);
        //设置点击事件.
        mSendVerifiCodeBtn.setOnClickListener(this);
        //倒计时按钮.
        mSendCountdownBtn = findViewById(R.id.send_countdown_btn);
        //设置点击事件.
        mSendCountdownBtn.setOnClickListener(this);
        //发送登录手机按钮.
        mSendLoginPhoneBtn = findViewById(R.id.send_login_phone_btn);
        //设置点击事件.
        mSendLoginPhoneBtn.setOnClickListener(this);
        //用户协议.
        findViewById(R.id.user_agreement_btn).setOnClickListener(this);
        //微信登录按钮.
        mLoginWechatImgBtn = findViewById(R.id.login_wechat_imgbtn);
        //设置点击
        mLoginWechatImgBtn.setOnClickListener(this);
        //微QQ录按钮.
        mLoginQQImgBtn = findViewById(R.id.login_qq_imgbtn);
        //设置点击
        mLoginQQImgBtn.setOnClickListener(this);
        //微博登录按钮.
        mLoginWeiboImgBtn = findViewById(R.id.login_weibo_imgbtn);
        //设置点击
        mLoginWeiboImgBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_back:
                //关闭.
                finish();
                break;
            case R.id.login_wechat_btn:
            case R.id.login_wechat_imgbtn:
                //判断微信是否已安装.
                if (!ShareUtil.isWeiXinInstalled(getApplicationContext())) {
                    //微信未安装.
                    ToastUtils.show(R.string.no_install_wechat);
                    return;
                }
                //登录微信, 显示登录进度页面.
                showProcessingPage(null);
                //调用登录接口.
                loginWX();
                break;
            case R.id.user_agreement_btn:
                //用户协议.
                String url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.USER_AGREEMENT);
                if (TextUtils.isEmpty(url))
                {
                    //此处需要填写默认地址.
                    url = "http://app.duoyueapp.com/user_rules.html";
                }
                //BaseActivityHelper.INSTANCE.gotoWebActivity(this, new WebActivity.Data(DomainConfig.INSTANCE.getAboutH5(), ViewUtils.getString(R.string.tab_mine)));
                BaseActivityHelper.INSTANCE.gotoWebActivity(this, new WebActivity.Data(url, ViewUtils.getString(R.string.tab_mine)));
                break;
            case R.id.login_qq_btn:
            case R.id.login_qq_imgbtn:
                //判断QQ是否已安装.
                if (!ShareUtil.isQQInstalled(getApplicationContext())) {   //QQ未安装.
                    ToastUtils.show(R.string.no_install_qq);
                    return;
                }
                //登录QQ, 显示登录进度页面.
                showProcessingPage(null);
                //调用登录接口.
                loginQQ();
                break;
            case R.id.login_weibo_btn:
            case R.id.login_weibo_imgbtn:
                //判断微博是否已安装.
                if (!WbSdk.isWbInstall(getApplicationContext())) {
                    //微博未安装.
                    ToastUtils.show(R.string.no_install_weibo);
                    return;
                }
                //登录新浪微博, 显示登录进度页面.
                showProcessingPage(null);
                //调用登录接口.
                loginWeibo();
                break;
            case R.id.send_verifi_code_btn:
                //发送验证码.
                if (mPhoneNumberEditText != null && verifyPhoneNumber()) {
                    sendPhoneVerifyCode(TextTool.toString(mPhoneNumberEditText.getText()));
                }
                break;
            case R.id.send_login_phone_btn:
                //点击登录手机.
                if (mPhoneNumberEditText != null && verifyPhoneNumber()) {
                    //验证是否验证码有效.
                    String verificationCode = mVerificationCodeEditText != null ? StringFormat.toString(mVerificationCodeEditText.getText()) : null;
                    if (TextUtils.isEmpty(verificationCode) || !verificationCode.matches("\\d{6}")) {
                        ToastUtils.show(R.string.input_valid_verification_code);
                        if (mVerificationCodeEditText != null) {
                            //重新获取焦点.
                            mVerificationCodeEditText.setFocusable(true);
                            mVerificationCodeEditText.setFocusableInTouchMode(true);
                            mVerificationCodeEditText.requestFocus();
                        }
                        return;
                    }
                    //获取手机号.
                    String phoneNumber = TextTool.toString(mPhoneNumberEditText.getText());
                    if (TextUtils.isEmpty(phoneNumber)) {
                        Logger.e(TAG, "loginPhone: 请输入手机号码.");
                        return;
                    }
                    //获取验证码.
                    String verifyCode = TextTool.toString(mVerificationCodeEditText.getText());
                    if (TextUtils.isEmpty(verifyCode)) {
                        Logger.e(TAG, "loginPhone: 请输入验证码.");
                        return;
                    }
                    //显示登录进度页面.
                    showProcessingPage(null);
                    //调用登录接口.
                    UserLoginMgr.loginPhone(phoneNumber, verifyCode, this);
                }
                break;
        }
    }

    /**
     * 发送手机验证码
     *
     * @param number
     */
    private void sendPhoneVerifyCode(String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        if (mSendVerifiCodeBtn != null) {
            //显示倒计时按钮.
            mSendCountdownBtn.setVisibility(View.VISIBLE);
            mSendVerifiCodeBtn.setVisibility(View.GONE);
            //设置为60s
            mSendCountdownBtn.setText("60s");
            mSendCountdownBtn.setTag(60);
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int number = mSendCountdownBtn != null ? (int) mSendCountdownBtn.getTag() : 0;
                    if (number > 0) {
                        mSendCountdownBtn.setTag(--number);
                        mSendCountdownBtn.setText(mSendCountdownBtn.getTag() + "s");
                        mHandler.postDelayed(this, 1000);
                        return;
                    }
                    Logger.i(TAG, "run: 终止倒计时.");
                    mSendCountdownBtn.setVisibility(View.GONE);
                    mSendVerifiCodeBtn.setVisibility(View.VISIBLE);
                }
            });
        }
        //发送验证码.
        UserLoginMgr.sendPhoneVerifyCode(number);
    }

    /**
     * 登录开始
     */
    @Override
    public void onLoginStart(int type) {
    }

    /**
     * 取消登录.
     */
    @Override
    public void onLoginCancel(int type) {
        ToastUtils.show(ViewUtils.getString(R.string.cancel_login));
        //隐藏登录进度页面.
        hideProcessingPage();
    }

    /**
     * 登录成功.
     */
    @Override
    public void onLoginSucc(int type, UserInfo userInfo) {
        try {
            //发送登录成功广播.
            Intent intent = new Intent(Constants.LOGIN_SUCC_ACTION);
            BaseContext.getContext().sendBroadcast(intent);
        } catch (Throwable throwable) {
            Logger.e(TAG, "loginSucc: {}, {}", userInfo, throwable);
        }
        //隐藏进度.
        hideProcessingPage();
        //关闭当前登录页面.
        //详情页评论需要登录 然后判断是否加载第一章节
        setResult(1008);
        finish();
    }

    /**
     * 登录失败.
     *
     * @param errMsg
     */
    @Override
    public void onLoginFail(int type, String errMsg) {
        ToastUtils.show(ViewUtils.getString(R.string.login_fail));
        //隐藏登录进度页面.
        hideProcessingPage();
    }

    /**
     * 显示处理进度页面.
     *
     * @param msg 加载页面提示信息
     */
    private void showProcessingPage(String msg) {
        try {
            //获取处理中进度页面.
            XLinearLayout loadingLayout = findViewById(R.id.login_loading_layout);
            //显示进度页面.
            loadingLayout.setVisibility(View.VISIBLE);
            TextView msgTextView = loadingLayout.findViewById(R.id.login_msg_textview);
            if (!StringFormat.isEmpty(msg)) {
                msgTextView.setText(msg);
            } else {
                msgTextView.setText(R.string.signing_in);
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "showProcessingPage: {}, {}", msg, throwable);
        }
    }

    /**
     * 隐藏处理进度页面.
     */
    private void hideProcessingPage() {
        try {
            //隐藏处理进度页面.
            findViewById(R.id.login_loading_layout).setVisibility(View.GONE);
        } catch (Throwable throwable) {
            Logger.e(TAG, "hideProcessingPage: {}", throwable);
        }
    }

    /**
     * 验证手机号码是否有效.
     *
     * @return
     */
    private boolean verifyPhoneNumber() {
        if (mPhoneNumberEditText == null) {
            return false;
        }
        //获取手机号码.
        String phoneNumber = StringFormat.toString(mPhoneNumberEditText.getText());
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("[1][345678]\\d{9}")) {
            //提示输入有效手机号码.
            ToastUtils.show(R.string.input_valid_mobile_number);
            //重新获取焦点.
            mPhoneNumberEditText.setFocusable(true);
            mPhoneNumberEditText.setFocusableInTouchMode(true);
            mPhoneNumberEditText.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 登录微信.
     */
    private void loginWX() {
        LoginUtil.login(getApplicationContext(), LoginPlatform.WX, new LoginListener() {
            @Override
            public void loginSuccess(LoginResult result) {
                Logger.i(TAG, "loginWX: loginSuccess: {}", result != null ? result.getCode() : "NULL");
                if (result == null || StringFormat.isEmpty(result.getCode())) {
                    onLoginFail(LoginPresenter.USER_TYPE_WECHAT, "");
                    return;
                }
                //调用登录接口.
                UserLoginMgr.loginThirdParty(LoginPresenter.USER_TYPE_WECHAT, result.getCode(), LoginActivity.this);
            }

            @Override
            public void loginFailure(Exception e) {
                Logger.e(TAG, "loginWX: loginFailure: {}", e);
                onLoginFail(LoginPresenter.USER_TYPE_WECHAT, "");
            }

            @Override
            public void loginCancel() {
                Logger.e(TAG, "loginWX: loginCancel: ");
                onLoginCancel(LoginPresenter.USER_TYPE_WECHAT);
            }
        });
    }

    /**
     * 登录QQ.
     */
    private void loginQQ() {
        //申请QQ授权登录.
        LoginUtil.login(getApplicationContext(), LoginPlatform.QQ, new LoginListener() {
            @Override
            public void loginSuccess(LoginResult result) {
                Logger.i(TAG, "loginQQ: loginSuccess: {}", result != null && result.getToken() != null ? result.getToken().getAccessToken() : "NULL");
                if (result == null || result.getToken() == null || StringFormat.isEmpty(result.getToken().getAccessToken())) {
                    onLoginFail(LoginPresenter.USER_TYPE_QQ, "");
                    return;
                }
                //调用登录接口.
                UserLoginMgr.loginThirdParty(LoginPresenter.USER_TYPE_QQ, result.getToken().getAccessToken(), LoginActivity.this);
            }

            @Override
            public void loginFailure(Exception e) {
                Logger.e(TAG, "loginQQ: loginFailure: {}", e);
                onLoginFail(LoginPresenter.USER_TYPE_QQ, "");
            }

            @Override
            public void loginCancel() {
                Logger.e(TAG, "loginQQ: loginCancel: ");
                onLoginCancel(LoginPresenter.USER_TYPE_QQ);
            }
        });
    }

    /**
     * 新浪微博授权.
     */
    private void loginWeibo() {
        LoginUtil.login(BaseContext.getContext(), LoginPlatform.WEIBO, new LoginListener() {
            @Override
            public void loginSuccess(LoginResult result) {
                Logger.i(TAG, "loginWeibo: loginSuccess: {}", result != null && result.getToken() != null ? result.getToken().getAccessToken() : "NULL");
                if (result == null || result.getToken() == null || StringFormat.isEmpty(result.getToken().getAccessToken())) {
                    onLoginFail(LoginPresenter.USER_TYPE_WEIBO, "");
                    return;
                }
                //调用登录接口.
                UserLoginMgr.loginThirdParty(LoginPresenter.USER_TYPE_WEIBO, result.getToken().getOpenid() + "#" + result.getToken().getAccessToken(), LoginActivity.this);
            }

            @Override
            public void loginFailure(Exception e) {
                Logger.e(TAG, "loginWeibo: loginFailure: {}", e);
                onLoginFail(LoginPresenter.USER_TYPE_WEIBO, "");
            }

            @Override
            public void loginCancel() {
                Logger.e(TAG, "loginWeibo: loginCancel: ");
                onLoginCancel(LoginPresenter.USER_TYPE_WEIBO);
            }
        });
    }
}
