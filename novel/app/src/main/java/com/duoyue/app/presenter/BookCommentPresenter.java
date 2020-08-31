package com.duoyue.app.presenter;

import com.duoyue.app.bean.CommentListBean;
import com.duoyue.app.common.data.request.bookcity.BookDetailCommentReq;
import com.duoyue.app.common.data.request.bookcity.BookDetailSaveCommentReq;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.view.BookDetailsView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookCommentPresenter implements BookDetailActivity.CommentPresenter {

    private BookDetailsView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private DisposableObserver commentDisposable;
    private DisposableObserver commentSaveDisposable;
    private long bookId;
    private int pageIndex = 1;
    private int pageSize = 20;
    private boolean isCommentListActivity;

    public BookCommentPresenter(BookDetailsView pageView) {
        this(pageView, false);
    }

    public BookCommentPresenter(BookDetailsView pageView, boolean isCommentListActivity) {
        this.pageView = pageView;
        this.isCommentListActivity = isCommentListActivity;
    }

    @Override
    public void loadData(long bookId, int pageIndex) {
        BookDetailCommentReq request = new BookDetailCommentReq();
        request.setBookId(bookId);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);

        commentDisposable = new DisposableObserver<JsonResponse<CommentListBean>>() {
            @Override
            public void onNext(JsonResponse<CommentListBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.showComment(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<CommentListBean>()
                .setRequest(request)
                .setResponseType(CommentListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(commentDisposable);
    }

    @Override
    public void distory() {
        if (commentDisposable != null && !commentDisposable.isDisposed()) {
            commentDisposable.dispose();
        }
        if (commentSaveDisposable != null && !commentSaveDisposable.isDisposed()) {
            commentSaveDisposable.dispose();
        }
    }

    public void loadComment(String content, long bookId, int start) {
        BookDetailSaveCommentReq bookDetailSaveCommentReq = new BookDetailSaveCommentReq();
        bookDetailSaveCommentReq.setBookId(bookId);
        bookDetailSaveCommentReq.setContent(content);
        bookDetailSaveCommentReq.setVote(start);

        commentSaveDisposable = new DisposableObserver<JsonResponse>() {
            @Override
            public void onNext(JsonResponse jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1) {
                    pageView.loadSaveComment();
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost()
                .setRequest(bookDetailSaveCommentReq)
                .setResponseType(JsonResponse.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(commentSaveDisposable);
    }



}
