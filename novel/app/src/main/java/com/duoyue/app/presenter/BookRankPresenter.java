package com.duoyue.app.presenter;

import android.app.Activity;
import com.duoyue.app.bean.BookRankItemBean;
import com.duoyue.app.bean.BookRankListBean;
import com.duoyue.app.common.data.request.bookcity.BookRankReq;
import com.duoyue.app.ui.fragment.BookRankFragment;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.common.LoadResult;
import com.zydm.base.rx.RxUtils;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class BookRankPresenter implements BookRankFragment.Presenter {

    private static final String TAG = "app#BookRankPresenter";

    private Activity mActivity;
    private BookPageView pageView;
    private Disposable mLoadDisposable;
    private Disposable mLoadMoreDisposable;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private CompositeDisposable mPageComposite = new CompositeDisposable();
    private Object adData;

    private long mCategoryId;
    private int mPageIndex = 1;
    private boolean showAd;

    public BookRankPresenter(Activity activity, BookPageView pageView) {
        mActivity = activity;
        this.pageView = pageView;
        pageView.showLoading();
    }

    @Override
    public void loadMoreData(int pageIndex) {
        loadPageData(mCategoryId, pageIndex, false);
    }

    @Override
    public void loadPageData(final long categoryId, final int pageIndex, boolean loadAd) {
        mCategoryId = categoryId;
        mPageIndex = pageIndex;
        this.showAd = loadAd;
        /*if (showAd) {
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
                            getBookList(categoryId, pageIndex);
                        }

                        @Override
                        public void onError(Throwable e) {
                            getBookList(categoryId, pageIndex);
                        }
                    });
        } else*/ {
            getBookList(categoryId, pageIndex);
        }
    }

    private void getBookList(long categoryId, int pageIndex) {
        BookRankReq request = new BookRankReq();
        request.categoryId = categoryId;
        request.pageIndex = pageIndex;

        new JsonPost.AsyncPost<BookRankListBean>()
                .setRequest(request)
                .setResponseType(BookRankListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<BookRankListBean>>() {
                    @Override
                    public void onNext(JsonResponse<BookRankListBean> jsonResponse) {
                        mTooFastChecker.cancel();
                        pageView.dismissLoading();
                        if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                                && !jsonResponse.data.getList().isEmpty()) {
                            List<Object> bookList = new ArrayList<>();
                            List<BookRankItemBean> newBookList = jsonResponse.data.getList();
                            if (mPageIndex == 1) {
                                pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED);
                                if (showAd && adData != null && newBookList.size() > 3) {
                                    bookList.addAll(newBookList.subList(0, 3));
                                    bookList.add(adData);
                                    bookList.addAll(newBookList.subList(3, newBookList.size()));
                                } else {
                                    bookList.addAll(newBookList);
                                }
                            } else {
                                pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
                                bookList.addAll(newBookList);
                            }

                            pageView.showPage(bookList);
                        } else {
                            if (mPageIndex == 1) {
                                pageView.showEmpty();
                                pageView.showLoadMoreFinish(LoadResult.FORCE_UPDATE_FAIL);
                            } else {
                                pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTooFastChecker.cancel();
                        pageView.dismissLoading();
                        if (mPageIndex == 1) {
                            pageView.showNetworkError();
                        } else {
                            pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL);
                        }
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onPageDestroy() {

    }

    @Override
    public Object getAdData() {
        return adData;
    }

    private boolean isPageDataLoading() {
        return RxUtils.isDisposed(mLoadDisposable);
    }

    private boolean isMoreDataLoading() {
        return RxUtils.isDisposed(mLoadMoreDisposable);
    }

    private void stopLoading() {
        if (!isPageDataLoading()) {
            return;
        }
        remove(mLoadDisposable);
        mLoadDisposable = null;
    }

    private void stopLoadMore() {
        if (!isMoreDataLoading()) {
            return;
        }
        remove(mLoadMoreDisposable);
        mLoadMoreDisposable = null;
    }

    private boolean hasMoreData() {

        return false;
    }

    protected boolean remove(Disposable disposable) {
        return null != disposable && mPageComposite.remove(disposable);
    }

}
