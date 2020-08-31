package com.duoyue.lib.base.app.user;

public interface ILoginContact
{
    interface ILoginPresenter
    {
        void login(int type, String code, String verifyCode);

        void login();

        void cancelLogin();
    }

    interface ILoginView
    {
        void onLoginStart();

        void onLoginCancel();

        void onLoginSuccess(UserInfo info);

        void onLoginFailure(String msg);

        void onLoginError(Throwable throwable);
    }
}
