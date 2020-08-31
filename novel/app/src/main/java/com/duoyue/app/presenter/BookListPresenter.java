package com.duoyue.app.presenter;

import android.app.Activity;
import com.duoyue.app.bean.BookListBean;
import com.duoyue.app.common.data.request.bookcity.BookListReq;
import com.duoyue.app.ui.fragment.BookListFragment;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.common.LoadResult;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class BookListPresenter implements BookListFragment.Presenter {

    private Activity mActivity;
    private BookPageView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private DisposableObserver disposableObserver;
    private Object adData;
    private int type;
    private boolean showAd;

    private int mChan;

    public BookListPresenter(Activity activity, BookPageView pageView, int type, int chan) {
        mActivity = activity;
        this.pageView = pageView;
        this.type = type;
        this.mChan = chan;
    }

    @Override
    public void loadMoreData(int pageIndex) {
        loadPageData(pageIndex, false);
    }

    @Override
    public void loadPageData(final int pageIndex, boolean loadAd) {
        // 判断是否需要添加广告数据
        this.showAd = loadAd;
        /*if (loadAd) {
            AdManager.getInstance().createAdSource(mActivity).loadListAd(null, 640, 320)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new SingleObserver<ArrayList<?>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(ArrayList<?> objects) {
                            if (objects != null && objects.size() > 0) {
                                adData = objects.get(0);
                            }
                            loadData(pageIndex);
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadData(pageIndex);
                        }
                    });
        } else */{
            loadData(pageIndex);
        }
    }

    private void loadData(final int pageIndex) {
        BookListReq request = new BookListReq();
        request.setType(type);
        request.setChan(mChan);
        request.setPageIndex(pageIndex);

        disposableObserver = new DisposableObserver<JsonResponse<BookListBean>>() {
            @Override
            public void onNext(JsonResponse<BookListBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    if (pageIndex == 1) {
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED);
                    } else {
                        pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
                    }
                    List<Object> bookList = new ArrayList<>();
                    // 广告出现在第一个位置
                    if (showAd && pageIndex == 1 && adData != null) {
                        bookList.add(adData);
                        bookList.addAll(jsonResponse.data.getList());
                    } else {
                        bookList.addAll(jsonResponse.data.getList());
                    }
                    pageView.showPage(bookList);
                } else {
                    if (pageIndex == 1) {
                        pageView.showEmpty();
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                    } else {
                        pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (pageIndex == 1) {
                    pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                    pageView.showNetworkError();
                } else {
                    pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL);
                }
            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<BookListBean>()
                .setRequest(request)
                .setResponseType(BookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(disposableObserver);
    }

    @Override
    public void onPageDestroy() {
        if (disposableObserver != null) {
            disposableObserver.dispose();
        }
    }

    @Override
    public Object getAdData() {
        return adData;
    }

}
