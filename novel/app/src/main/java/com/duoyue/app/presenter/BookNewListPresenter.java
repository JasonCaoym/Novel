package com.duoyue.app.presenter;

import com.duoyue.app.bean.BookNewHeaderBean;
import com.duoyue.app.bean.BookNewListHeaderBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.request.bookcity.BookNewBookListHeaderReq;
import com.duoyue.app.common.data.request.bookcity.BookSiteListReq;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.common.LoadResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookNewListPresenter {

    private PageView mPageView;

    private final List<BookNewHeaderBean> objectList = new ArrayList<>();

    private DisposableObserver bookHeaderNewListDisposable;
    //    private DisposableObserver bookNewListDisposable;
    private DisposableObserver loadBookSiteListDisposable;

    public BookNewListPresenter(PageView pageView) {
        this.mPageView = pageView;
        this.mPageView.showLoading();
    }


    public void loadHeaderData(final int nextPage) {
        loadBookSiteList();

        bookHeaderNewListDisposable = new DisposableObserver<JsonResponse<BookNewListHeaderBean>>() {
            @Override
            public void onNext(JsonResponse<BookNewListHeaderBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    objectList.clear();
                    BookNewListHeaderBean bookNewListHeaderBean = jsonResponse.data;
                    if (bookNewListHeaderBean.getList() != null && !bookNewListHeaderBean.getList().isEmpty()) {
                        if (nextPage == 1) {
                            mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_SUCCEED);
                        } else {
                            if (bookNewListHeaderBean.getNextPage() == -1) {
                                mPageView.loadMoreData(LoadResult.LOAD_MORE_FAIL_NO_DATA);
                            } else {
                                mPageView.loadMoreData(LoadResult.LOAD_MORE_SUCCEED);
                            }
                        }
                        objectList.addAll(bookNewListHeaderBean.getList());
                        mPageView.loadViewData(objectList, bookNewListHeaderBean.getNextPage());
                    }
                } else {
                    if (objectList.isEmpty()) {
                        mPageView.loadNullData();
                    } else {
                        if (nextPage == 1) {
                            mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_FAIL);
                        } else {
                            mPageView.loadMoreData(LoadResult.LOAD_MORE_FAIL);
                        }
                    }
                }
                mPageView.dismissLoading();
//                loadData();
            }

            @Override
            public void onError(Throwable e) {
//                loadData();
                if (objectList.isEmpty()) {
                    mPageView.loadError();
                } else {
                    if (nextPage == 1) {
                        mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_FAIL);
                    } else {
                        mPageView.loadMoreData(LoadResult.LOAD_MORE_FAIL);
                    }
                }
                mPageView.dismissLoading();
            }

            @Override
            public void onComplete() {

            }
        };
        BookNewBookListHeaderReq request = new BookNewBookListHeaderReq();
        request.setNextPage(nextPage);
        new JsonPost.AsyncPost<BookNewListHeaderBean>()
                .setRequest(request)
                .setResponseType(BookNewListHeaderBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(bookHeaderNewListDisposable);


    }

//    private void loadData() {
//
//        bookNewListDisposable = new DisposableObserver<JsonResponse<BookNewBookListBean>>() {
//            @Override
//            public void onNext(JsonResponse<BookNewBookListBean> jsonResponse) {
//
//                if (jsonResponse.status == 1 && jsonResponse.data != null) {
//                    BookNewBookListBean bookNewBookListBean = jsonResponse.data;
//                    if (bookNewBookListBean.getList() != null && !bookNewBookListBean.getList().isEmpty()) {
//                        mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_SUCCEED);
//                        objectList.add(jsonResponse.data);
//                        mPageView.loadViewData(objectList);
//                    }
//                } else {
//                    if (objectList.isEmpty()) {
//                        mPageView.loadNullData();
//                    } else {
//                        mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_FAIL);
//                    }
//                }
//                mPageView.dismissLoading();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                if (objectList.isEmpty()) {
//                    mPageView.loadError();
//                } else {
//                    mPageView.loadRefreshData(LoadResult.FORCE_UPDATE_FAIL);
//                }
//                mPageView.dismissLoading();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//        BookNewBookListReq request = new BookNewBookListReq();
//        new JsonPost.AsyncPost<BookNewBookListBean>()
//                .setRequest(request)
//                .setResponseType(BookNewBookListBean.class)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .post(bookNewListDisposable);
//
//    }

    public void loadBookSiteList() {


        loadBookSiteListDisposable = new DisposableObserver<JsonResponse<BookSiteBean>>() {
            @Override
            public void onNext(JsonResponse<BookSiteBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    mPageView.loadSiteData(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                //此处不需要回调 如果发现页面数据展示 防止悬浮广告接口返回失败最终影响整个界面
            }

            @Override
            public void onComplete() {

            }
        };
        BookSiteListReq bookSiteListReq = new BookSiteListReq();
        bookSiteListReq.site = 5;

        new JsonPost.AsyncPost<BookSiteBean>()
                .setRequest(bookSiteListReq)
                .setResponseType(BookSiteBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(loadBookSiteListDisposable);
    }

    public void onDistory() {
        if (bookHeaderNewListDisposable != null && !bookHeaderNewListDisposable.isDisposed()) {
            bookHeaderNewListDisposable.dispose();
        }
//        if (bookNewListDisposable != null && !bookNewListDisposable.isDisposed()) {
//            bookNewListDisposable.dispose();
//        }
        if (loadBookSiteListDisposable != null && !loadBookSiteListDisposable.isDisposed()) {
            loadBookSiteListDisposable.dispose();
        }
    }

    public interface PageView {

        void showLoading();

        void dismissLoading();

        void loadViewData(List<BookNewHeaderBean> list, int nextPage);

        void loadNullData();

        void loadError();

        void loadRefreshData(int result);

        void loadMoreData(int result);

        void loadSiteData(BookSiteBean bookSiteBean);

    }

}
