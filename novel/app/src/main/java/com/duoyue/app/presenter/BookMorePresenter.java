package com.duoyue.app.presenter;

import android.app.Activity;
import com.duoyue.app.bean.BookListBean;
import com.duoyue.app.common.data.request.bookcity.BookMoreReq;
import com.duoyue.app.ui.activity.BookMoreActivity;
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

public class BookMorePresenter implements BookMoreActivity.Presenter {

    private static final String TAG = "app#BookMorePresenter";
    private Activity mActivity;
    private BookPageView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private String columnId;
    private String repeatBookId;
    private String tag;
    private String typeId;
    private DisposableObserver<JsonResponse<BookListBean>> disposableObserver;


    public BookMorePresenter(Activity activity, BookPageView pageView, String columnId, String repeatBookId, String tag, String typeId) {
        mActivity = activity;
        this.pageView = pageView;
        this.columnId = columnId;
        this.repeatBookId = repeatBookId;
        this.tag = tag;
        this.typeId = typeId;
    }

    @Override
    public void loadMoreData(int pageIndex) {
        loadPageData(pageIndex);
    }

    @Override
    public void loadPageData(final int pageIndex) {
        BookMoreReq request = new BookMoreReq();
        request.setQuePages(pageIndex);
        request.setColumnId(columnId);
        request.setRepeatBookId(repeatBookId);
        request.setTag(tag);
        request.setTypeId(typeId);

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
                    List<Object> list = new ArrayList<>();
                    list.addAll(jsonResponse.data.getList());
                    pageView.showPage(list);
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
}
