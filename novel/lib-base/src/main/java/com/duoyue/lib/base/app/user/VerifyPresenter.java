package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class VerifyPresenter implements IVerifyContact.IVerifyPresenter
{
    private IVerifyContact.IVerifyView mView;
    private VerifyObserver observer;

    public VerifyPresenter(IVerifyContact.IVerifyView view)
    {
        mView = view;
    }

    @Override
    public void sendVerifyCode(String phone)
    {
        mView.onVerifyStart();
        observer = new VerifyObserver();
        VerifyRequest request = new VerifyRequest();
        request.code = phone;
        new JsonPost.AsyncPost<Void>()
                .setRequest(request)
                .setResponseType(Void.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(observer);
    }

    @Override
    public void cancelVerify()
    {
        if (observer != null)
        {
            observer.dispose();
            observer = null;
            mView.onVerifyCancel();
        }
    }

    private class VerifyObserver extends DisposableObserver<JsonResponse<Void>>
    {
        @Override
        public void onNext(JsonResponse<Void> response)
        {
            if (response.status == 1)
            {
                mView.onVerifySuccess();
            } else
            {
                mView.onVerifyFailure(response.msg);
            }
        }

        @Override
        public void onError(Throwable e)
        {
            mView.onVerifyError(e);
        }

        @Override
        public void onComplete()
        {

        }
    }
}
