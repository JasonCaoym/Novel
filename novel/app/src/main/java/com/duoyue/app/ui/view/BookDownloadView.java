package com.duoyue.app.ui.view;

import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadCheckResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadResp;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.presenter.view.IPageView;

public interface BookDownloadView extends IPageView {

    void showSuccess(ChapterDownloadResp chapterDownloadResp);

    void bookBeansSuccess(BookRecordGatherResp bookRecordGather);

    void downloadCheckSuccess(ChapterDownloadCheckResp chapterDownloadCheckResp);

    void downloadCheckFailed();
}
