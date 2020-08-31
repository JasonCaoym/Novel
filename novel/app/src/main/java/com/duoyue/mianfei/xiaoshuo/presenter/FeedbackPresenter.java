package com.duoyue.mianfei.xiaoshuo.presenter;

import com.duoyue.mianfei.xiaoshuo.data.api.Api;
import com.duoyue.mianfei.xiaoshuo.data.bean.FeedBackMsgBean;
import com.duoyue.mianfei.xiaoshuo.mine.ui.IFeedbackPage;
import com.zydm.base.common.ParamKey;
import com.zydm.base.presenter.AbsPagePresenter;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

public class FeedbackPresenter extends AbsPagePresenter<FeedBackMsgBean> {
    private final IFeedbackPage mPageView;

    public FeedbackPresenter(@NonNull IFeedbackPage pageView) {
        super(pageView);
        mPageView = pageView;
    }

    @Override
    protected Single<FeedBackMsgBean> getPageDataSrc(boolean isForceUpdate, boolean isLoadMore) {
        return Api.INSTANCE.message().feedBackHistory().setForceUpdate(isForceUpdate).addReqParam(ParamKey.CURSOR, getCursor(isLoadMore)).build();
    }

    @Override
    protected void onPageDataUpdated(FeedBackMsgBean pageData, boolean isByForceUpdate, boolean isLoadMore) {
        mPageView.showPage(pageData);
    }
}
