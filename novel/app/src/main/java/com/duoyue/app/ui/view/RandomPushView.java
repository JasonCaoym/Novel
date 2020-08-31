package com.duoyue.app.ui.view;

import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;

public interface RandomPushView {
    void showSuccess(RandomPushBean.BookBean data);

    void showEmpty();

    void showError();

    void loadFirstChapterData(String content, String chapterTitle);

    void showLoading();

    void dismissLoading();
}


