package com.duoyue.app.common.mgr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.duoyue.app.common.callback.UserLoginCallback;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.ILoginContact;
import com.duoyue.lib.base.app.user.IVerifyContact;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.app.user.VerifyPresenter;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mod.stats.ErrorStatsApi;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.duoyue.mod.stats.data.StatsDaoDBHelper;
import com.zydm.base.data.dao.DaoDbHelper;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * 登录管理类
 *
 * @author caoym
 * @data 2019/4/1  20:55
 */
public class UserLoginMgr implements UserLoginCallback {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#UserLoginMgr";

    /**
     * 当前类对象.
     */
    private static UserLoginMgr sInstance;

    /**
     * 数据加载Layout.
     */
    private View mLoadingPageView;

    /**
     * Hander
     */
    private Handler mHandler;


    private static int mLive;

    /**
     * 构造方法
     */
    private UserLoginMgr() {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance() {
        if (sInstance == null) {
            synchronized (UserLoginMgr.class) {
                if (sInstance == null) {
                    sInstance = new UserLoginMgr();
                }
            }
        }
    }

    public static void isLive(int live) {
        mLive = live;
    }

    /**
     * 检查用户登录(本地无登录记录, 则使用游客身份登录)
     *
     * @param homeActivity
     */
    public static boolean checkLogin(Activity homeActivity) {
        //读取本地用户信息.
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) {
            try {
                Logger.e(TAG, "开始加载用户数据");
                //创建当前类对象.
                createInstance();
                //展示加载页面.
                sInstance.mLoadingPageView = homeActivity.findViewById(R.id.loading_page_id);
                sInstance.mLoadingPageView.setVisibility(View.VISIBLE);
                //设置重试按钮点击事件.
                sInstance.mLoadingPageView.findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击加载失败页面重试按钮.
                        sInstance.showLoadingPage();
                    }
                });
                //调用显示加载中页面接口.
                sInstance.showLoadingPage();
            } catch (Throwable throwable) {
                Logger.e(TAG, "checkLogin: {}", throwable);
                //处理登录流程异常.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_FAIL, "checkLogin_throwable:" + (sInstance.mLoadingPageView != null ? "PageView" : "NULL"));
            }
            return false;
        }
        Logger.e(TAG, "checkLogin 有用户数据");
        return true;
    }

    /**
     * 显示加载中页面.
     */
    private void showLoadingPage() {
        if (mLoadingPageView == null) {
            return;
        }
        try {
            //显示加载中Layout.
            mLoadingPageView.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
            //隐藏加载重试Layout.
            mLoadingPageView.findViewById(R.id.load_failed_layout).setVisibility(View.GONE);
            //判断当前网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
                //定时执行三秒后回到加载重试页面.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadingPageView != null) {
                            try {
                                //隐藏加载中Layout.
                                mLoadingPageView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
                                //显示加载重试Layout.
                                mLoadingPageView.findViewById(R.id.load_failed_layout).setVisibility(View.VISIBLE);
                            } catch (Throwable throwable) {
                                Logger.e(TAG, "showLoadingPage.run: {}", throwable);
                            }
                        }
                    }
                }, 3000);
            }
            //调用登录游客接口.
            loginTourist(this);
        } catch (Throwable throwable) {
            Logger.e(TAG, "showLoadingPage: {}", throwable);
            //处理登录流程异常.
            ErrorStatsApi.addError(ErrorStatsApi.LOGIN_FAIL, "showLoadingPage_throwable:" + (mHandler != null ? "Handler" : "NULL"));
        }
    }

    /**
     * 用户退出登录
     */
    public static void userSignOut(Activity activity, final UserLoginCallback callback) {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Logger.e(TAG, "userSignOut: 网络不可用");
            return;
        }
        try {
            SimpleDialog simpleDialog = new SimpleDialog.Builder(activity).setCanceledOnTouchOutside(false).setMessage(R.string.confirm_sign_out).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //创建当前类对象.
                    createInstance();
                    //调用登录游客接口.
                    sInstance.loginTourist(callback);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (callback != null) {
                        callback.onLoginCancel(LoginPresenter.USER_TYPE_PHONE);
                    }
                }
            }).create();
            //显示Dialog.
            simpleDialog.show();
        } catch (Throwable throwable) {
            Logger.e(TAG, "userSignOut: {}", throwable);
        }
    }

    /**
     * 展示手机登录界面
     */
    public static void showLoginPhonePage(Activity activity) {
        //启动登录页面.
        ActivityHelper.INSTANCE.gotoLogin(activity, new BaseData(ViewUtils.getString(R.string.tab_mine)));
    }

    /**
     * 发送手机验证码
     *
     * @param number
     */
    public static void sendPhoneVerifyCode(final String number) {
        VerifyPresenter verifyPresenter = new VerifyPresenter(new IVerifyContact.IVerifyView() {
            @Override
            public void onVerifyStart() {
                Logger.i(TAG, "onVerifyStart: {}", number);
            }

            @Override
            public void onVerifyCancel() {
                Logger.i(TAG, "onVerifyCancel: {}", number);
            }

            @Override
            public void onVerifySuccess() {
                Logger.i(TAG, "onVerifySuccess: {}", number);
            }

            @Override
            public void onVerifyFailure(String msg) {
                Logger.i(TAG, "onVerifyFailure: {}, {}", number, msg);
            }

            @Override
            public void onVerifyError(Throwable throwable) {
                Logger.i(TAG, "onVerifyError: {}, {}", number, throwable);
            }
        });
        //发送手机验证码.
        verifyPresenter.sendVerifyCode(number);
    }

    /**
     * 登录手机.
     *
     * @param phoneNumber 手机号码
     * @param verifyCode  验证码
     * @param callback
     */
    public static void loginPhone(final String phoneNumber, final String verifyCode, final UserLoginCallback callback) {
        LoginPresenter loginPresenter = new LoginPresenter(new ILoginContact.ILoginView() {
            @Override
            public void onLoginStart() {
                Logger.i(TAG, "loginPhone: onLoginStart: {}, {}", phoneNumber, verifyCode);
                //回调登录开始.
                if (callback != null) {
                    callback.onLoginStart(LoginPresenter.USER_TYPE_PHONE);
                }
                //上报统计数据.
                uploadStatsData();
            }

            @Override
            public void onLoginCancel() {
                Logger.i(TAG, "loginPhone: onLoginCancel: {}, {}", phoneNumber, verifyCode);
                if (callback != null) {
                    callback.onLoginCancel(LoginPresenter.USER_TYPE_PHONE);
                }
            }

            @Override
            public void onLoginSuccess(UserInfo info) {
                Logger.i(TAG, "loginPhone: onLoginSuccess: {}, {}, {}", phoneNumber, verifyCode, info.toString());
                //调用登录成功接口.
                loginSucc();
                if (callback != null) {
                    callback.onLoginSucc(LoginPresenter.USER_TYPE_PHONE, info);
                }
            }

            @Override
            public void onLoginFailure(String msg) {
                //登录失败.
                Logger.e(TAG, "loginPhone: onLoginFailure: {}, {}, {}", phoneNumber, verifyCode, msg);
                if (callback != null) {
                    callback.onLoginFail(LoginPresenter.USER_TYPE_PHONE, msg);
                }
            }

            @Override
            public void onLoginError(Throwable throwable) {
                //登录异常
                Logger.e(TAG, "loginPhone: onLoginError: {}, {}, {}", phoneNumber, verifyCode, throwable);
                if (callback != null) {
                    callback.onLoginFail(LoginPresenter.USER_TYPE_PHONE, ViewUtils.getString(R.string.login_fail));
                }
            }
        });
        //发送登录请求.
        loginPresenter.login(LoginPresenter.USER_TYPE_PHONE, phoneNumber, verifyCode);
    }

    /**
     * 第三方登录
     *
     * @param type
     * @param code
     * @param callback 回调对象.
     */
    public static void loginThirdParty(final int type, final String code, final UserLoginCallback callback) {
        if (type <= 0 || TextUtils.isEmpty(code)) {
            Logger.e(TAG, "loginThirdParty: 参数错误: {}, {}", type, code);
            return;
        }
        LoginPresenter loginPresenter = new LoginPresenter(new ILoginContact.ILoginView() {
            @Override
            public void onLoginStart() {
                Logger.i(TAG, "loginThirdParty: onLoginStart: {}, {}", type, code);
                //回调登录开始.
                if (callback != null) {
                    callback.onLoginStart(type);
                }
                //上报统计数据.
                uploadStatsData();
            }

            @Override
            public void onLoginCancel() {
                Logger.i(TAG, "loginThirdParty: onLoginCancel: {}, {}", type, code);
                if (callback != null) {
                    callback.onLoginFail(type, ViewUtils.getString(R.string.cancel_login));
                }
            }

            @Override
            public void onLoginSuccess(UserInfo info) {
                Logger.i(TAG, "loginThirdParty: onLoginSuccess: {}: {}, {}", type, code, info.toString());
                //调用登录成功接口.
                loginSucc();
                if (callback != null) {
                    callback.onLoginSucc(type, info);
                }
            }

            @Override
            public void onLoginFailure(String msg) {
                //登录失败.
                Logger.e(TAG, "loginThirdParty: onLoginFailure: {}: {}, {}", type, code, msg);
                if (callback != null) {
                    callback.onLoginFail(type, msg);
                }
            }

            @Override
            public void onLoginError(Throwable throwable) {
                //登录异常
                Logger.e(TAG, "loginThirdParty: onLoginError: {}: {}, {}", type, code, throwable);
                if (callback != null) {
                    callback.onLoginFail(type, ViewUtils.getString(R.string.login_fail));
                }
            }
        });
        //发送登录请求.
        loginPresenter.login(type, code, null);
    }

    /**
     * 登录游客
     *
     * @param callback 回调对象.
     */
    private void loginTourist(final UserLoginCallback callback) {
        LoginPresenter loginPresenter = new LoginPresenter(new ILoginContact.ILoginView() {
            @Override
            public void onLoginStart() {
                Logger.i(TAG, "loginTourist: onLoginStart: ");
                //开始登录.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_START);
                //回调登录开始.
                if (callback != null) {
                    callback.onLoginStart(LoginPresenter.USER_TYPE_TOURIST);
                }
                //上报统计数据.
                uploadStatsData();
            }

            @Override
            public void onLoginCancel() {
                Logger.i(TAG, "loginTourist: onLoginCancel: ");
                //取消登录.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_FAIL, "login_cancel");
                if (callback != null) {
                    callback.onLoginFail(LoginPresenter.USER_TYPE_TOURIST, ViewUtils.getString(R.string.cancel_login));
                }
            }

            @Override
            public void onLoginSuccess(UserInfo info) {
                Logger.i(TAG, "loginTourist: onLoginSuccess: {}", (info != null ? info.toString() : "NULL"));
                //登录成功.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_SUCC, info != null ? info.uid : "NULL");
                //调用登录成功接口.
                loginSucc();
                if (callback != null) {
                    callback.onLoginSucc(LoginPresenter.USER_TYPE_TOURIST, info);
                }

                //拉活登录
                if (mLive != 0) {
                    FuncPageStatsApi.pullAliveActivity();
                    mLive = 0;
                }
            }

            @Override
            public void onLoginFailure(String msg) {
                //登录失败.
                Logger.e(TAG, "loginTourist: onLoginFailure: {}", msg);
                //登录失败.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_FAIL, "login_failure:" + msg);
                if (callback != null) {
                    callback.onLoginFail(LoginPresenter.USER_TYPE_TOURIST, msg);
                }
            }

            @Override
            public void onLoginError(Throwable throwable) {
                //登录异常
                Logger.e(TAG, "loginTourist: onLoginError: {}", throwable);
                //登录失败.
                ErrorStatsApi.addError(ErrorStatsApi.LOGIN_FAIL, "login_error:" + (throwable != null ? throwable.getMessage() : "NULL"));
                if (callback != null) {
                    callback.onLoginFail(LoginPresenter.USER_TYPE_TOURIST, ViewUtils.getString(R.string.login_fail));
                }
            }
        });
        //发送登录请求.
        loginPresenter.login();
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
        onChangeLoginStatus(false);
    }

    /**
     * 登录成功.
     */
    @Override
    public void onLoginSucc(int type, UserInfo info) {
        onChangeLoginStatus(true);
        try {
            //发送登录成功广播.
            Intent intent = new Intent(Constants.LOGIN_SUCC_ACTION);
            BaseContext.getContext().sendBroadcast(intent);
        } catch (Throwable throwable) {
            Logger.e(TAG, "loginSucc: {}, {}", info, throwable);
        }
    }

    /**
     * 登录失败.
     *
     * @param errMsg
     */
    @Override
    public void onLoginFail(int type, String errMsg) {
        onChangeLoginStatus(false);
    }

    /**
     * 游客登录状态变化
     *
     * @param isSucc 是否登录成功.
     */
    private void onChangeLoginStatus(boolean isSucc) {
        if (mLoadingPageView != null) {
            try {
                if (isSucc) {
                    //登录成功.
                    //sInstance.mLoadingPageView.setVisibility(View.GONE);
                    sInstance.mLoadingPageView = null;
                } else {
                    //登录失败, 隐藏加载进度View.
                    mLoadingPageView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
                    //显示加载重试View.
                    mLoadingPageView.findViewById(R.id.load_failed_layout).setVisibility(View.VISIBLE);
                }
            } catch (Throwable throwable) {
                Logger.e(TAG, "onChangeLoginStatus: {}, {}", isSucc, throwable);
            }
        }
    }

    /**
     * 登录成功.
     */
    private static void loginSucc() {
        //清理数据库文件.
        DaoDbHelper.getInstance().onLoginSucc();
        StatsDaoDBHelper.getInstance().onLoginSucc();
    }

    /**
     * 上报统计数据(登录开始时调用, 防止登录后导致统计数据丢失).
     */
    private static void uploadStatsData() {
        /*//上报广告统计数据.
        Single.fromCallable(new Callable<String>()
        {
            @Override
            public String call()
            {
                UploadStatsMgr.getInstance().uploadAdStats();
                return null;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>()
        {
            @Override
            public void accept(String result)
            {
            }
        });
        //上报功能统计数据.
        Single.fromCallable(new Callable<String>()
        {
            @Override
            public String call()
            {
                UploadStatsMgr.getInstance().uploadFuncStats();
                return null;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>()
        {
            @Override
            public void accept(String result)
            {
            }
        });*/
        //上报页面统计数据.
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
                PageStatsUploadMgr.getInstance().uploadFuncStats();
                return null;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String result) {
            }
        });
    }
}
