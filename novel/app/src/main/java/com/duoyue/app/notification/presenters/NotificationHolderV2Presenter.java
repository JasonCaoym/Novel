package com.duoyue.app.notification.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.duoyue.app.notification.data.NotifiyBookResultBookListBean;
import com.duoyue.app.notification.data.NotifyBookPushRequest;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationHolderV2Presenter {
    private static final String TAG = "NotificationHolderV2Presenter";

    private OnDataListener mOnDataListener;

    public NotificationHolderV2Presenter(OnDataListener onDataListener) {
        this.mOnDataListener = onDataListener;
    }

    public void loadDayData(String ids) {

        NotifyBookPushRequest request = new NotifyBookPushRequest();
        if (!TextUtils.isEmpty(ids)) {
            request.setRepeatBookId(ids);
        }
        new JsonPost.AsyncPost<NotifiyBookResultBookListBean>()
                .setRequest(request)
                .setResponseType(NotifiyBookResultBookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<NotifiyBookResultBookListBean>>() {
                    @Override
                    public void onNext(JsonResponse<NotifiyBookResultBookListBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            NotifiyBookResultBookListBean bean = jsonResponse.data;
                            mOnDataListener.onDayData(bean);
                        } else {
                            mOnDataListener.onDataNULL();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mOnDataListener.onDataError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public interface OnDataListener {

        void onDayData(NotifiyBookResultBookListBean resultV2Bean);

        void onDataNULL();

        void onDataError();

    }

}
