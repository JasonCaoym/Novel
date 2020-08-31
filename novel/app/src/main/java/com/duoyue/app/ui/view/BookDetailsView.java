package com.duoyue.app.ui.view;

import android.app.Activity;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.CommentListBean;
import com.duoyue.app.bean.RecommendBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;

public interface BookDetailsView {

    void showLoading();

    void dismissLoading();

    void showEmpty();

    void showNetworkError();

    Activity getActivity();

    void showPage(BookDetailBean bookDetailBean);

    void showAdPage(Object adObject);

    void showRecommend(RecommendBean recommendBean);

    void showComment(CommentListBean commentList);


    void loadFirstChapterData(String data, String title);

    void loadOtherReadData(RecommendBean recommendBean);

    void loadSaveComment();

    void showDownloadDialog(ChapterDownloadOptionResp resp);
}


