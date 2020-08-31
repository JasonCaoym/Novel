package com.duoyue.mianfei.xiaoshuo.read.presenter.view;


import com.duoyue.app.common.data.response.ReadTaskResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.zydm.base.data.dao.ChapterListBean;

import java.util.List;

public interface IReadPage {

    void loadBookChaptersSuccess(List<ChapterListBean> chapterListBean);

    void preLoadBookChaptersSuccess(ChapterListBean chapterListBean, int groupPos);

    void loadChapterContentsSuccess();

    void loadChapterContentsFailed();

    void loadCatalogueSuccess(ChapterListBean chapterBean, int groupPos, int type);

    void loadCatalogueFailed(int groupPos, int type);

    void loadTodayReadTimeSuccess(ReadTaskResp readTaskResp);

    void getDownloadOptionSuccess(ChapterDownloadOptionResp resp);

    void getDownloadOptionError();

}
