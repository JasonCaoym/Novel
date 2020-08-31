package com.duoyue.mianfei.xiaoshuo.mine.ui;

import com.duoyue.app.bean.FeedConfigBean;
import com.duoyue.mianfei.xiaoshuo.data.bean.ProblemListBean;

public interface IQuestionPage {

    void showLoading();

    void dismissLoading();

    void showNetworkError();

    void showEmpty();

    void showProblemList(FeedConfigBean feedConfigBean);

    void onCommitSuccess();
}
