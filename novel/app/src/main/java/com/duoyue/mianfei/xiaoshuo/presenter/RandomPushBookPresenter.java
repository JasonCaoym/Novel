package com.duoyue.mianfei.xiaoshuo.presenter;

import android.app.Activity;
import com.duoyue.app.common.data.request.bookcity.RandomPushReq;
import com.duoyue.app.common.data.request.read.ChapterContentReq;
import com.duoyue.app.ui.view.RandomPushBookDialog;
import com.duoyue.app.ui.view.RandomPushView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDetailLoadUtils;
import com.duoyue.mianfei.xiaoshuo.read.utils.DecryptUtils;
import com.zydm.base.data.bean.ChapterUrlBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RandomPushBookPresenter implements RandomPushBookDialog.Presenter {

    private final RandomPushView mRandomPushView;
    private final Activity mActivity;
    private DisposableObserver<JsonResponse<ChapterUrlBean>> recommendDisposable;

    private StringBuilder stringBuilder;
    private DisposableObserver<JsonResponse<RandomPushBean>> mDisposableObserver;

    public RandomPushBookPresenter(Activity activity, RandomPushView randomPushView) {
        mActivity = activity;
        mRandomPushView = randomPushView;
    }

    @Override
    public void loadData(long bookId) {
        mRandomPushView.showLoading();
        mDisposableObserver = new DisposableObserver<JsonResponse<RandomPushBean>>() {
            @Override
            public void onNext(JsonResponse<RandomPushBean> randomPushBeanJsonResponse) {
                if (randomPushBeanJsonResponse.status == 1 && randomPushBeanJsonResponse.data != null && randomPushBeanJsonResponse.data.getBook() != null
                        && randomPushBeanJsonResponse.data.getBook().getBookName() != null) {
                    mRandomPushView.showSuccess(randomPushBeanJsonResponse.data.getBook());
                    preLoadNextChapter(randomPushBeanJsonResponse.data.getBook().getBookId());
                } else {
                    mRandomPushView.dismissLoading();
                    mRandomPushView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mRandomPushView.dismissLoading();
                mRandomPushView.showError();
            }

            @Override
            public void onComplete() {

            }
        };

        RandomPushReq randomPushReq = new RandomPushReq();
        randomPushReq.setRepeatBookId(bookId);
        new JsonPost.AsyncPost<RandomPushBean>()
                .setRequest(randomPushReq)
                .setResponseType(RandomPushBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mDisposableObserver);
    }

    @Override
    public void preLoadNextChapter(final long bookId) {
        ChapterContentReq contentRequest = new ChapterContentReq();
        contentRequest.bookId = String.valueOf(bookId);
        contentRequest.seqNum = 1;

        recommendDisposable = new DisposableObserver<JsonResponse<ChapterUrlBean>>() {
            @Override
            public void onNext(final JsonResponse<ChapterUrlBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    ZExecutorService.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            ChapterUrlBean chapterUrlBean = jsonResponse.data;
                            try {
                                String contentResult = BookDetailLoadUtils.getInstance().loadDetailContent(DecryptUtils.decryptUrl(chapterUrlBean.secret, chapterUrlBean.content));
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("\u3000\u3000");
                                for (char cc : contentResult.toCharArray()) {
                                    stringBuilder.append(cc);
                                    if (cc == '\n') {
                                        stringBuilder.append("\u3000\u3000");
                                    }
                                }
                                mRandomPushView.loadFirstChapterData(stringBuilder.toString(), chapterUrlBean.chapterTitle);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRandomPushView.showError();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    mRandomPushView.dismissLoading();
                    mRandomPushView.showError();
                }
            }

            @Override
            public void onError(Throwable e) {
                mRandomPushView.dismissLoading();
                mRandomPushView.showError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<ChapterUrlBean>()
                .setRequest(contentRequest)
                .setResponseType(ChapterUrlBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(recommendDisposable);
    }

    @Override
    public void destroy() {
        if (mDisposableObserver != null && !mDisposableObserver.isDisposed()) {
            mDisposableObserver.dispose();
        }
        if (recommendDisposable != null && !recommendDisposable.isDisposed()) {
            recommendDisposable.dispose();
        }
    }
}
