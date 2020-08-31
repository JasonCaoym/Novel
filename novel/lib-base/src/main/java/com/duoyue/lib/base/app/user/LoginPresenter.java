package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;

import com.duoyue.lib.base.log.Logger;
import com.zydm.base.statistics.umeng.StatisHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter implements ILoginContact.ILoginPresenter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#LoginPresenter";

    /**
     * 用户类型-游客.
     */
    public static final int USER_TYPE_TOURIST = 1;

    /**
     * 用户类型-微信.
     */
    public static final int USER_TYPE_WECHAT = 2;

    /**
     * 用户类型-QQ.
     */
    public static final int USER_TYPE_QQ = 3;

    /**
     * 用户类型-微博.
     */
    public static final int USER_TYPE_WEIBO = 4;

    /**
     * 用户类型-手机.
     */
    public static final int USER_TYPE_PHONE = 5;

    private ILoginContact.ILoginView mView;
    private LoginObserver observer;

    public LoginPresenter(ILoginContact.ILoginView view)
    {
        mView = view;
    }

    @Override
    public void login(int type, String code, String verifyCode)
    {
        LoginRequest request = new LoginRequest();
        request.type = type;
        request.code = code;
        request.vaildCode = verifyCode;
        login(request);
    }

    @Override
    public void login()
    {
        LoginRequest request = new LoginRequest();
        request.type = 1;
        request.code = UserManager.getInstance().getMid();
        login(request);
    }

    private void login(LoginRequest request)
    {
        mView.onLoginStart();
        observer = new LoginObserver();
        new JsonPost.AsyncPost<UserInfo>()
                .setRequest(request)
                .setResponseType(UserInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(observer);
    }

    @Override
    public void cancelLogin()
    {
        if (observer != null)
        {
            observer.dispose();
            observer = null;
            mView.onLoginCancel();
        }
    }

    private class LoginObserver extends DisposableObserver<JsonResponse<UserInfo>>
    {
        @Override
        public void onNext(JsonResponse<UserInfo> response)
        {
            if (response.status == 1)
            {
                UserInfo userInfo = response.data;
                UserManager.getInstance().setUserInfo(userInfo);
                mView.onLoginSuccess(response.data);
                try
                {
                    //添加登录成功统计.
                    Map<String, String> paramMap = new HashMap<>();
                    //MID
                    paramMap.put("MID", UserManager.getInstance().getMid());
                    //UID
                    paramMap.put("UID", userInfo != null ? userInfo.uid : "NULL");
                    //用户类型(1.游客,2.微信,3.QQ,4.微博,5手机).
                    paramMap.put("TYPE", String.valueOf(userInfo != null ? userInfo.type : -1));
                    StatisHelper.onEvent(BaseContext.getContext(), "LG_SU", paramMap);
                } catch (Throwable throwable)
                {
                    Logger.e(TAG, "LoginObserver: onNext: {}", throwable);
                }
            } else
            {
                mView.onLoginFailure(response.msg);
            }
        }

        @Override
        public void onError(Throwable e)
        {
            mView.onLoginError(e);
        }

        @Override
        public void onComplete()
        {

        }
    }
}
