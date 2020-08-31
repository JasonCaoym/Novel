package com.duoyue.mianfei.xiaoshuo.mine.ui;

import com.duoyue.mianfei.xiaoshuo.data.bean.FeedBackMsgBean;
import com.zydm.base.presenter.view.IPageView;

public interface IFeedbackPage extends IPageView {

    void showPage(FeedBackMsgBean listBean);
}
