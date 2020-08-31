package com.duoyue.app.presenter;

import com.duoyue.app.bean.RecomHotBean;
import com.duoyue.app.common.data.request.bookcity.RecomHotReq;
import com.duoyue.app.ui.activity.ReadRecommendActivity;
import com.duoyue.app.ui.view.SimpleView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ReadRecommendPresenter implements ReadRecommendActivity.Presenter {

    private SimpleView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private DisposableObserver disposableObserver;
    private long bookId;

    public ReadRecommendPresenter(SimpleView pageView, long bookId) {
        this.pageView = pageView;
        this.bookId = bookId;
        pageView.showLoading();
        loadData();
    }

    @Override
    public void loadData() {
        RecomHotReq request = new RecomHotReq();
        request.setBookId(bookId);

        disposableObserver = new DisposableObserver<JsonResponse<RecomHotBean>>() {
            @Override
            public void onNext(JsonResponse<RecomHotBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    pageView.loadData(jsonResponse.data);
                } else {
                    pageView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<RecomHotBean>()
                .setRequest(request)
                .setResponseType(RecomHotBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(disposableObserver);
    }

    @Override
    public void distory() {
        if (disposableObserver != null && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
    }

}
