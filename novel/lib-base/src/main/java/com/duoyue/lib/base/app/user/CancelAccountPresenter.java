package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 注销账号.
 */
public class CancelAccountPresenter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#CancelAccountPresenter";

    /**
     * 注销账号回调.
     */
    private ILoginContact.ILoginView mView;

    private CancelObserver observer;

    public CancelAccountPresenter(ILoginContact.ILoginView view)
    {
        mView = view;
    }

    /**
     * 注销账号.
     */
    public void cancelAccount()
    {
        mView.onLoginStart();

        CancelAccountRequest request = new CancelAccountRequest();
        request.type = 1;
        request.code = UserManager.getInstance().getMid();

        observer = new CancelObserver();
        new JsonPost.AsyncPost<UserInfo>()
                .setRequest(request)
                .setResponseType(UserInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(observer);
    }

    private class CancelObserver extends DisposableObserver<JsonResponse<UserInfo>>
    {
        @Override
        public void onNext(JsonResponse<UserInfo> response)
        {
            if (response.status == 1)
            {
                UserInfo userInfo = response.data;
                UserManager.getInstance().setUserInfo(userInfo);
                mView.onLoginSuccess(response.data);
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
