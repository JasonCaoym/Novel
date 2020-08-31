package com.duoyue.mianfei.xiaoshuo.presenter;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import com.duoyue.app.common.data.request.bookcity.RandomPushReq;
import com.duoyue.app.common.data.request.read.ChapterContentReq;
import com.duoyue.app.ui.view.RandomPushBookDialog;
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

public class RandomPushBookDialogPresenter {

    private final Activity mActivity;
    private final FragmentManager mFragmentManager;
    private final int mModelId;
    private final String mTitle;
    private DisposableObserver<JsonResponse<ChapterUrlBean>> recommendDisposable;

    private StringBuilder stringBuilder;
    private DisposableObserver<JsonResponse<RandomPushBean>> mDisposableObserver;

    public RandomPushBookDialogPresenter(Activity activity, FragmentManager fragmentManager, int modelId, String title) {
        mActivity = activity;
        mFragmentManager = fragmentManager;
        mModelId = modelId;
        mTitle = title;
    }

    public void loadData(long bookId) {
        mDisposableObserver = new DisposableObserver<JsonResponse<RandomPushBean>>() {
            @Override
            public void onNext(JsonResponse<RandomPushBean> randomPushBeanJsonResponse) {
                if (randomPushBeanJsonResponse.status == 1 && randomPushBeanJsonResponse.data != null && randomPushBeanJsonResponse.data.getBook() != null
                        && randomPushBeanJsonResponse.data.getBook().getBookName() != null) {
                    preLoadNextChapter(randomPushBeanJsonResponse.data.getBook());
                }
            }

            @Override
            public void onError(Throwable e) {
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

    public void preLoadNextChapter(final RandomPushBean.BookBean bookBean) {
        ChapterContentReq contentRequest = new ChapterContentReq();
        contentRequest.bookId = String.valueOf(bookBean.getBookId());
        contentRequest.seqNum = 1;

        recommendDisposable = new DisposableObserver<JsonResponse<ChapterUrlBean>>() {
            @Override
            public void onNext(final JsonResponse<ChapterUrlBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    ZExecutorService.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            final ChapterUrlBean chapterUrlBean = jsonResponse.data;
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
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RandomPushBookDialog randomPushBookDialog = new RandomPushBookDialog();
                                        randomPushBookDialog.setModelId(mModelId);
                                        randomPushBookDialog.setBookBean(bookBean);
                                        randomPushBookDialog.setTitle(mTitle);
                                        randomPushBookDialog.setFirstChapterData(stringBuilder.toString(), chapterUrlBean.chapterTitle);
                                        randomPushBookDialog.show(mFragmentManager, "randomPushBook");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
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

    public void destroy() {
        if (mDisposableObserver != null && !mDisposableObserver.isDisposed()){
            mDisposableObserver.dispose();
        }

        if (recommendDisposable != null && !recommendDisposable.isDisposed()){
            recommendDisposable.dispose();
        }
    }
}
