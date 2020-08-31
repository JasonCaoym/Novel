package com.duoyue.app.presenter;

import com.duoyue.app.bean.CategoryBookBean;
import com.duoyue.app.bean.CategoryBookListBean;
import com.duoyue.app.common.data.request.bookcity.BookDetailTagReq;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookDetailCategoryPresenter {


    private PageView mPageView;

    public BookDetailCategoryPresenter(PageView pageView) {
        this.mPageView = pageView;
        mPageView.showLoading();
    }


    public void loadBookData(int tagType, int tagSecondType, int tagThreeType, int nextPage, String tag, final int type,final int wordType) {
        BookDetailTagReq request = new BookDetailTagReq();
        request.setTag(tag);
        request.setTagType(tagType);
        request.setTagSecondType(tagSecondType);
        request.setTagThreeType(tagThreeType);
        request.setNextPage(nextPage);
        new JsonPost.AsyncPost<CategoryBookListBean>()
                .setRequest(request)
                .setResponseType(CategoryBookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<CategoryBookListBean>>() {
                    @Override
                    public void onNext(JsonResponse<CategoryBookListBean> jsonResponse) {
                        CategoryBookListBean bookListBean = jsonResponse.data;

                        if (bookListBean != null) {

                            for (CategoryBookBean bookBean : bookListBean.getList()) {
                                bookBean.setType(type);
                                bookBean.setWordCountType(wordType);
                            }

                            mPageView.onLoadData(bookListBean);

                        } else {
                            mPageView.onLoadNullData();
                        }
                        mPageView.dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPageView.onLoadErrorData();
                        mPageView.dismissLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public interface PageView {
        void onLoadData(CategoryBookListBean categoryBookListBean);

        void onLoadErrorData();

        void onLoadNullData();

        void onLoadNoData();

        void showLoading();

        void dismissLoading();
    }
}
