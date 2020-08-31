package com.duoyue.lib.base.app.user;

public interface IVerifyContact
{
    interface IVerifyPresenter
    {
        void sendVerifyCode(String phone);

        void cancelVerify();
    }

    interface IVerifyView
    {
        void onVerifyStart();

        void onVerifyCancel();

        void onVerifySuccess();

        void onVerifyFailure(String msg);

        void onVerifyError(Throwable throwable);
    }
}
