package com.duoyue.app.presenter;

import android.util.Log;
import com.duoyue.app.bean.SearchCountBean;
import com.duoyue.app.bean.SearchResuleBean;
import com.duoyue.app.bean.SearchV2ListBean;
import com.duoyue.app.bean.SearchV2MoreListBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.data.request.bookcity.SearchCountReq;
import com.duoyue.app.common.data.request.bookcity.SearchMoreV2Req;
import com.duoyue.app.common.data.request.bookcity.SearchResultReq;
import com.duoyue.app.common.data.request.bookcity.SearchV2Req;
import com.duoyue.app.ui.view.SearchV2View;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.common.upload.response.FuncStatsResp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class SearchV2Presenter {

    private DisposableObserver detailDisposable;
    private DisposableObserver bookNewListDisposable;
    private DisposableObserver loadBookSiteListDisposable;
    private DisposableObserver countDisposable;

    private SearchV2View mSearchV2View;

    public void onDistory() {
        if (detailDisposable != null && !detailDisposable.isDisposed()) {
            detailDisposable.dispose();
        }
        if (bookNewListDisposable != null && !bookNewListDisposable.isDisposed()) {
            bookNewListDisposable.dispose();
        }
        if (loadBookSiteListDisposable != null && !loadBookSiteListDisposable.isDisposed()) {
            loadBookSiteListDisposable.dispose();
        }
        if (countDisposable != null && !countDisposable.isDisposed()) {
            countDisposable.dispose();
        }
    }

    public SearchV2Presenter(SearchV2View searchV2View) {
        this.mSearchV2View = searchV2View;

    }


    public void loadData() {
        mSearchV2View.showLoading();

        SearchMoreV2Req searchMoreV2Req = new SearchMoreV2Req();

        detailDisposable = new DisposableObserver<JsonResponse<SearchV2MoreListBean>>() {
            @Override
            public void onNext(JsonResponse<SearchV2MoreListBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    DataCacheManager.getInstance().setSearchV2MoreListBean(jsonResponse.data);
                    mSearchV2View.showMoreComment(jsonResponse.data);
                }
                loadMoreData();
            }

            @Override
            public void onError(Throwable e) {
                loadMoreData();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<SearchV2MoreListBean>()
                .setRequest(searchMoreV2Req)
                .setResponseType(SearchV2MoreListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(detailDisposable);


    }

    public void loadMoreData() {
        SearchV2Req searchV2Req = new SearchV2Req();
        bookNewListDisposable = new DisposableObserver<JsonResponse<SearchV2ListBean>>() {
            @Override
            public void onNext(JsonResponse<SearchV2ListBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    DataCacheManager.getInstance().setSearchV2ListBean(jsonResponse.data);
                    mSearchV2View.showComment(jsonResponse.data);
                } else {
                    mSearchV2View.showEmpty();
                }
                mSearchV2View.dismissLoading();
            }

            @Override
            public void onError(Throwable e) {
                mSearchV2View.dismissLoading();
                mSearchV2View.showNetworkError();

            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<SearchV2ListBean>()
                .setRequest(searchV2Req)
                .setResponseType(SearchV2ListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(bookNewListDisposable);
    }

    public void keyWord(String value, int searchtype) {
        SearchResultReq searchResultReq = new SearchResultReq();
        searchResultReq.setKeyword(value);
        searchResultReq.setSearchType(searchtype);
        loadBookSiteListDisposable = new DisposableObserver<JsonResponse<SearchResuleBean>>() {
            @Override
            public void onNext(JsonResponse<SearchResuleBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    mSearchV2View.showKeyWord(jsonResponse.data);
                } else {
                    mSearchV2View.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mSearchV2View.dismissLoading();
                mSearchV2View.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<SearchResuleBean>()
                .setRequest(searchResultReq)
                .setResponseType(SearchResuleBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(loadBookSiteListDisposable);
    }


    public void searchCount(List<SearchCountBean> strings) {
        if (strings == null || strings.isEmpty()) return;
        SearchCountReq searchResultReq = new SearchCountReq();
        searchResultReq.setSearchList(strings);
        searchResultReq.setBatchNumber(String.valueOf(TimeTool.currentTimeMillis()) + strings.hashCode());
        countDisposable = new DisposableObserver<JsonResponse<FuncStatsResp>>() {
            @Override
            public void onNext(JsonResponse<FuncStatsResp> jsonResponse) {
                Log.i("关键字上报成功", "onNext: ");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<FuncStatsResp>()
                .setRequest(searchResultReq)
                .setResponseType(FuncStatsResp.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(countDisposable);
    }
}
