package com.duoyue.app.presenter;

import com.duoyue.app.common.data.request.bookdownload.ChapterDownloadCheckReq;
import com.duoyue.app.common.data.request.bookdownload.DownloadChapterListReq;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadCheckResp;
import com.duoyue.app.common.data.response.bookdownload.DownloadChapterListResp;
import com.duoyue.app.ui.view.BookDownloadDialogView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BookDownloadDialogPresenter {


    private BookDownloadDialogView dialogView;

    public BookDownloadDialogPresenter(BookDownloadDialogView dialogView) {

        this.dialogView = dialogView;

    }

    /**
     * 检查是否可下载
     */
    public void downloadCheck(int totalChapter, int seqNum, long bookId) {

        ChapterDownloadCheckReq chapterDownloadCheckReq = new ChapterDownloadCheckReq();
        chapterDownloadCheckReq.chapterCount = totalChapter;
        chapterDownloadCheckReq.bookId = bookId;

        String chapters = "";
        for (int i = 0; i < totalChapter; i++) {
            chapters = chapters + (seqNum + i + 1) + ",";
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

                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            //开始下载
                            dialogView.downloadCheckSuccess(jsonResponse.data);
                        } else {
                            dialogView.dismissLoading();
                            ToastUtils.show(jsonResponse.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogView.dismissLoading();
                        ToastUtils.show("请求下载失败，请检查网络...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getDownloadChapterList(final long bookId, int seqNum, int countNum, final String bookName) {

        DownloadChapterListReq downloadChapterListReq = new DownloadChapterListReq();
        downloadChapterListReq.bookId = bookId;
        downloadChapterListReq.seqNum = seqNum;
        downloadChapterListReq.countNum = countNum;

        new JsonPost.AsyncPost<DownloadChapterListResp>()
                .setRequest(downloadChapterListReq)
                .setResponseType(DownloadChapterListResp.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<DownloadChapterListResp>>() {
                    @Override
                    public void onNext(JsonResponse<DownloadChapterListResp> jsonResponse) {
                        dialogView.dismissLoading();
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            dialogView.dismissDialog();
                            BookDownloadPresenter.downloadChapter(bookId, bookName, jsonResponse.data.getChapters());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogView.dismissLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
