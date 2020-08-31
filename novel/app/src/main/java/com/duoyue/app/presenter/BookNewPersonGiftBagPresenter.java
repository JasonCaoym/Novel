package com.duoyue.app.presenter;

import com.duoyue.app.bean.BookBagCompleteBean;
import com.duoyue.app.common.data.request.bookcity.BookBagCompleteReq;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookNewPersonGiftBagPresenter {

    private PageView mPageView;

    public BookNewPersonGiftBagPresenter(PageView pageView){
        this.mPageView = pageView;
    }


    public void loadData() {
        BookBagCompleteReq request = new BookBagCompleteReq();
        new JsonPost.AsyncPost<BookBagCompleteBean>()
                .setRequest(request)
                .setResponseType(BookBagCompleteBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<BookBagCompleteBean>>() {
                    @Override
                    public void onNext(JsonResponse<BookBagCompleteBean> jsonResponse) {
                        mPageView.onLoadData(jsonResponse.data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPageView.onLoadErrorData();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public interface  PageView{

        void onLoadData(BookBagCompleteBean bookBagCompleteBean);
        void onLoadErrorData();
    }
}
