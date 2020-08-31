package com.duoyue.app.presenter;

import com.duoyue.app.bean.SearchRecommdBookListBean;
import com.duoyue.app.bean.SearchResultBean;
import com.duoyue.app.bean.SearchResultListBean;
import com.duoyue.app.common.data.request.bookcity.SearchRecommdBookListReq;
import com.duoyue.app.common.data.request.bookcity.SearchResultAuthListReq;
import com.duoyue.app.common.data.request.bookcity.SearchResultListReq;
import com.duoyue.app.ui.view.SearchResultListView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class SearchResultListPresenter {

    private DisposableObserver detailDisposable;
    private DisposableObserver getDetailDisposable;

    private DisposableObserver getGetDetailDisposable;

    private SearchResultListView mSearchResultListView;

    private StringBuilder stringBuilder;

    public SearchResultListPresenter(SearchResultListView searchResultListView) {

        this.mSearchResultListView = searchResultListView;
        this.mSearchResultListView.showLoading();
    }

    public void loadData(String value, final int index, int searchType) {
        SearchResultListReq searchResultListReq = new SearchResultListReq();
        searchResultListReq.setKeyword(value);
        searchResultListReq.setCurrentCursor(index);
        searchResultListReq.setSearchType(searchType);
        detailDisposable = new DisposableObserver<JsonResponse<SearchResultListBean>>() {
            @Override
            public void onNext(JsonResponse<SearchResultListBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    SearchResultListBean searchResultListBean = jsonResponse.data;
                    mSearchResultListView.showComment(searchResultListBean);
                    if (index == 1) {
                        if (searchResultListBean.getCommentList().size() >= 3) {
                            loadRecommdBookList(searchResultListBean.getCommentList().subList(0, 3));
                        } else {
                            loadRecommdBookList(searchResultListBean.getCommentList());
                        }
                    }
                } else {
                    mSearchResultListView.showEmpty();
                    mSearchResultListView.dismissLoading();
                }
            }

            @Override
            public void onError(Throwable e) {
                mSearchResultListView.dismissLoading();
                mSearchResultListView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<SearchResultListBean>()
                .setRequest(searchResultListReq)
                .setResponseType(SearchResultListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(detailDisposable);
    }

    public void loadData(String value, final int index) {
        SearchResultAuthListReq searchResultListReq = new SearchResultAuthListReq();
        searchResultListReq.setKeyword(value);
        searchResultListReq.setCurrentCursor(index);
        getDetailDisposable = new DisposableObserver<JsonResponse<SearchResultListBean>>() {
            @Override
            public void onNext(JsonResponse<SearchResultListBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    SearchResultListBean searchResultListBean = jsonResponse.data;
                    if (searchResultListBean.getCommentList() == null) {
                        mSearchResultListView.showEmpty();
                    } else {
                        mSearchResultListView.showComment(jsonResponse.data);
                    }
                } else {
                    mSearchResultListView.showEmpty();
                }
                mSearchResultListView.dismissLoading();
            }

            @Override
            public void onError(Throwable e) {
                mSearchResultListView.dismissLoading();
                mSearchResultListView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<SearchResultListBean>()
                .setRequest(searchResultListReq)
                .setResponseType(SearchResultListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(getDetailDisposable);
    }

    public void loadRecommdBookList(List<SearchResultBean> bookids) {
        stringBuilder = new StringBuilder();
        for (SearchResultBean searchResultBean : bookids) {
            stringBuilder.append(searchResultBean.getBookId());
            stringBuilder.append(",");
        }
        SearchRecommdBookListReq searchRecommdBookListReq = new SearchRecommdBookListReq();
        searchRecommdBookListReq.setBookIds(stringBuilder.toString());

        getGetDetailDisposable = new DisposableObserver<JsonResponse<SearchRecommdBookListBean>>() {
            @Override
            public void onNext(JsonResponse<SearchRecommdBookListBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    SearchRecommdBookListBean searchRecommdBookListBean = jsonResponse.data;
                    if (searchRecommdBookListBean.getBookList() != null && !searchRecommdBookListBean.getBookList().isEmpty()) {
                        mSearchResultListView.showRecommdBookList(jsonResponse.data);
                    }
                }
                mSearchResultListView.dismissLoading();
            }

            @Override
            public void onError(Throwable e) {
                mSearchResultListView.dismissLoading();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<SearchRecommdBookListBean>()
                .setRequest(searchRecommdBookListReq)
                .setResponseType(SearchRecommdBookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(getGetDetailDisposable);
    }

    public void onDeroty() {
        if (detailDisposable != null) {
            detailDisposable.dispose();
        }
        if (getDetailDisposable != null) {
            getDetailDisposable.dispose();
        }
        if (getGetDetailDisposable != null) {
            getGetDetailDisposable.dispose();
        }
    }
}
