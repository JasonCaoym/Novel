package com.duoyue.app.presenter;

import android.annotation.SuppressLint;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.BookDownloadChapterBean;
import com.duoyue.app.common.data.request.bookcity.BookDetailsReq;
import com.duoyue.app.common.data.request.bookdownload.AllChapterDownloadReq;
import com.duoyue.app.common.data.request.bookdownload.ChapterDownloadCheckReq;
import com.duoyue.app.common.data.request.bookdownload.ChapterDownloadReq;
import com.duoyue.app.common.data.request.bookrecord.BookRecordGatherReq;
import com.duoyue.app.common.data.response.bookdownload.AllChapterDownloadResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadCheckResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadResp;
import com.duoyue.app.ui.view.BookDownloadView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadManager;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookSaveUtils;
import com.zydm.base.common.LoadResult;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.Callable;

public class BookDownloadPresenter {

    private static final String TAG = "App#BookDownloadPresenter";

    private BookDownloadView pageView;
    private long bookId;

    public BookDownloadPresenter(BookDownloadView bookDownloadView, long bookId) {
        this.pageView = bookDownloadView;
        this.bookId = bookId;
    }

    /**
     * 获得分组章节
     *
     * @param pageIndex
     * @param order
     */
    public void getChapterData(final int pageIndex, int order) {

        ChapterDownloadReq chapterDownloadReq = new ChapterDownloadReq();
        chapterDownloadReq.quePages = pageIndex;
        chapterDownloadReq.bookId = bookId;
        chapterDownloadReq.order = order;

        new JsonPost.AsyncPost<ChapterDownloadResp>()
                .setRequest(chapterDownloadReq)
                .setResponseType(ChapterDownloadResp.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<ChapterDownloadResp>>() {
                    @Override
                    public void onNext(JsonResponse<ChapterDownloadResp> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getCollect() != null
                                && !jsonResponse.data.getCollect().isEmpty()) {

                            if (pageIndex == 1) {
                                pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED);
                            } else {
                                pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
                            }

                            pageView.showSuccess(jsonResponse.data);

                        } else {
                            pageView.dismissLoading();
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
                        pageView.dismissLoading();
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                        if (pageIndex == 1) {
                            pageView.showNetworkError();
                        } else {
                            pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获得当前书豆数
     */
    public void getbookBeans() {

        new JsonPost.AsyncPost<BookRecordGatherResp>()
                .setRequest(new BookRecordGatherReq())
                .setResponseType(BookRecordGatherResp.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<BookRecordGatherResp>>() {
                    @Override
                    public void onNext(JsonResponse<BookRecordGatherResp> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            pageView.bookBeansSuccess(jsonResponse.data);
                        } else {
                            ToastUtils.show("书豆数量获取失败，请检查网络...");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.show("书豆数量获取失败，请检查网络...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 检查是否可下载
     */
    public void downloadCheck(List<BookDownloadChapterBean> selectedChapterList) {

        ChapterDownloadCheckReq chapterDownloadCheckReq = new ChapterDownloadCheckReq();
        chapterDownloadCheckReq.chapterCount = selectedChapterList.size();
        chapterDownloadCheckReq.bookId = bookId;

        String chapters = "";
        for(BookDownloadChapterBean bean : selectedChapterList){
            chapters = chapters + bean.getSeqNum() + ",";
        }
        chapters = chapters.substring(0, chapters.length() - 1);
        chapterDownloadCheckReq.chapterSeqNumStr = chapters;

        new JsonPost.AsyncPost<ChapterDownloadCheckResp>()
                .setRequest(chapterDownloadCheckReq)
                .setResponseType(ChapterDownloadCheckResp.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<ChapterDownloadCheckResp>>() {
                    @Override
                    public void onNext(JsonResponse<ChapterDownloadCheckResp> jsonResponse) {
                        pageView.dismissLoading();
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            pageView.downloadCheckSuccess(jsonResponse.data);
                        } else {
                            ToastUtils.show(jsonResponse.msg);
                            pageView.downloadCheckFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        pageView.dismissLoading();
                        ToastUtils.show("请求下载失败，请检查网络...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 下载章节
     */
    public static void downloadChapter(final long bookId, final String bookName, final List<BookDownloadChapterBean> selectedChapterBean) {

        if (selectedChapterBean == null || selectedChapterBean.isEmpty()) {
            return;
        }

        //因为需要实现离线阅读，下载章节之前，需要先下载书籍详情和书籍全部章节列表
        Observable.merge(Observable.fromCallable(new Callable<BookDetailBean>() {
            @SuppressLint("LongLogTag")
            @Override
            public BookDetailBean call() throws Exception {

                if (!BookSaveUtils.isCached(String.valueOf(bookId), BookSaveUtils.BOOK_DETAIL_BEAN)) {
                    try {

                        BookDetailsReq request = new BookDetailsReq();
                        request.setBookId(bookId);
                        JsonResponse<BookDetailBean> response = new JsonPost.SyncPost<BookDetailBean>()
                                .setRequest(request)
                                .setResponseType(BookDetailBean.class)
                                .post();

                        BookDetailBean bookDetailBean = response.data;
                        //保存书籍详情到本地

                        BookSaveUtils.saveBookDetailBean(String.valueOf(bookId), bookDetailBean);

                        //自动加入书架
                        if (!BookShelfPresenter.isAdded(String.valueOf(bookId))) {
                            BookShelfPresenter.addBookShelf(bookDetailBean);
                        }

                        return bookDetailBean;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else {

                    BookDetailBean bookDetailBean = BookSaveUtils.getCacheBookDetailBean(String.valueOf(bookId));
                    //自动加入书架
                    if (!BookShelfPresenter.isAdded(String.valueOf(bookId))) {
                        BookShelfPresenter.addBookShelf(bookDetailBean);
                    }

                }

                return new BookDetailBean();
            }
        }), Observable.fromCallable(new Callable<AllChapterDownloadResp>() {
            @SuppressLint("LongLogTag")
            @Override
            public AllChapterDownloadResp call() throws Exception {

                if (!BookSaveUtils.isCached(String.valueOf(bookId), BookSaveUtils.ALL_CHAPTER)) {
                    try {

                        AllChapterDownloadReq request = new AllChapterDownloadReq();
                        request.bookId = bookId;
                        JsonResponse<AllChapterDownloadResp> response = new JsonPost.SyncPost<AllChapterDownloadResp>()
                                .setRequest(request)
                                .setResponseType(AllChapterDownloadResp.class)
                                .post();

                        //保存全部目录到本地
                        BookSaveUtils.saveAllChapter(String.valueOf(bookId), response.data);

                        return response.data;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                return new AllChapterDownloadResp();
            }
        })).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        //真正开始下载章节
                        BookDownloadManager.getsInstance().addDownloadTask(bookId, bookName, selectedChapterBean);
                    }
                });

    }
}
