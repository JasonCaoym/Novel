package com.duoyue.app.presenter;

import android.text.TextUtils;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.RecommendBean;
import com.duoyue.app.common.data.request.bookcity.BookDetailsOtherReadReq;
import com.duoyue.app.common.data.request.bookcity.BookDetailsRecomReq;
import com.duoyue.app.common.data.request.bookcity.BookDetailsReq;
import com.duoyue.app.common.data.request.bookdownload.ChapterDownloadOptionReq;
import com.duoyue.app.common.data.request.read.ChapterContentReq;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.view.BookDetailsView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDetailLoadUtils;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookSaveUtils;
import com.duoyue.mianfei.xiaoshuo.read.utils.DecryptUtils;
import com.zydm.base.data.bean.ChapterUrlBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.tools.TooFastChecker;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookDetailsPresenter implements BookDetailActivity.Presenter {

    private BookDetailsView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private DisposableObserver detailDisposable;
    private DisposableObserver recommendDisposable;
    private DisposableObserver downloadDisposable;
    private StringBuilder stringBuilder;


    public BookDetailsPresenter(BookDetailsView pageView) {
        this.pageView = pageView;
    }

    @Override
    public void loadData(long bookId) {
        pageView.showLoading();
        BookDetailsReq request = new BookDetailsReq();
        request.setBookId(bookId);

        detailDisposable = new DisposableObserver<JsonResponse<BookDetailBean>>() {
            @Override
            public void onNext(JsonResponse<BookDetailBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.showPage(jsonResponse.data);
                } else {
                    pageView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<BookDetailBean>()
                .setRequest(request)
                .setResponseType(BookDetailBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(detailDisposable);
        updateRecoomendBooks(bookId, true);
    }

    @Override
    public void updateRecoomendBooks(long bookId, boolean bool) {
        BookDetailsRecomReq request = new BookDetailsRecomReq();
        request.setBookId(bookId);

        recommendDisposable = new DisposableObserver<JsonResponse<RecommendBean>>() {
            @Override
            public void onNext(JsonResponse<RecommendBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.showRecommend(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<RecommendBean>()
                .setRequest(request)
                .setResponseType(RecommendBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(recommendDisposable);
        if (bool) loadOtherReadBooks(bookId);
    }

    @Override
    public void distory() {
        if (detailDisposable != null && !detailDisposable.isDisposed()) {
            detailDisposable.dispose();
        }
        if (recommendDisposable != null && !recommendDisposable.isDisposed()) {
            recommendDisposable.dispose();
        }
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }

        if (stringBuilder != null) {
            stringBuilder = null;
        }
    }

    @Override
    public void loadOtherReadBooks(long bookId) {
        BookDetailsOtherReadReq bookDetailsOtherReadReq = new BookDetailsOtherReadReq();
        bookDetailsOtherReadReq.setBookId(bookId);

        recommendDisposable = new DisposableObserver<JsonResponse<RecommendBean>>() {
            @Override
            public void onNext(JsonResponse<RecommendBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.loadOtherReadData(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showNetworkError();
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<RecommendBean>()
                .setRequest(bookDetailsOtherReadReq)
                .setResponseType(RecommendBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(recommendDisposable);
    }

    //此方法有两个作用   进入详情页默认加载和下载第一章
    public void preLoadNextChapter(final long bookId) {

        ChapterContentReq contentRequest = new ChapterContentReq();
        contentRequest.bookId = String.valueOf(bookId);
        contentRequest.seqNum = 1;

        recommendDisposable = new DisposableObserver<JsonResponse<ChapterUrlBean>>() {
            @Override
            public void onNext(final JsonResponse<ChapterUrlBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    ZExecutorService.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            ChapterUrlBean chapterUrlBean = jsonResponse.data;
                            try {
                                //下载第一章节  用于  进入阅读器不用再去加载第一章
                                BookSaveUtils.saveChapterInfo(chapterUrlBean.bookId, chapterUrlBean.chapterId + "", chapterUrlBean.content);
                                //获取第一章节  用于  如果用户处于A模式  详情页会显示第一章节内容
                                String contentResult = BookDetailLoadUtils.getInstance().loadDetailContent(DecryptUtils.decryptUrl(chapterUrlBean.secret, chapterUrlBean.content));
                                //详情页显示第一章数据  文本需要经过一些处理
                                stringBuilder = new StringBuilder();
                                //开头默认空格
                                stringBuilder.append("\u3000\u3000");
                                for (char cc : contentResult.toCharArray()) {
                                    stringBuilder.append(cc);
                                    if (cc == '\n') {
                                        //文本内容换行  需要加入空格
                                        stringBuilder.append("\u3000\u3000");
                                    }
                                }
                                //获取接口数据 然后进过处理后在回调的详情页
                                pageView.loadFirstChapterData(stringBuilder.toString(), chapterUrlBean.chapterTitle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showNetworkError();
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

    /**
     * 请求下载配置项
     */
    @Override
    public void getDownloadOption(final long bookId) {

        ChapterDownloadOptionReq optionReq = new ChapterDownloadOptionReq();
        optionReq.bookId = bookId;

        downloadDisposable = new DisposableObserver<JsonResponse<ChapterDownloadOptionResp>>() {
            @Override
            public void onNext(JsonResponse<ChapterDownloadOptionResp> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.showDownloadDialog(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.show("下载配置信息获取失败!");
            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<ChapterDownloadOptionResp>()
                .setRequest(optionReq)
                .setResponseType(ChapterDownloadOptionResp.class)
                .subscribeOn(MtSchedulers.io())
                .observeOn(MtSchedulers.mainUi())
                .post(downloadDisposable);

    }
}
