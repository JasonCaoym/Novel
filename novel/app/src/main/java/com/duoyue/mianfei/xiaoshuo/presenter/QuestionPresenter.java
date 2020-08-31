package com.duoyue.mianfei.xiaoshuo.presenter;

import android.annotation.SuppressLint;
import com.duoyue.app.bean.FeedConfigBean;
import com.duoyue.app.common.data.request.bookcity.FeedCommitReq;
import com.duoyue.app.common.data.request.bookcity.FeedConfigListReq;
import com.duoyue.app.common.data.response.FeedCommitResp;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.mianfei.xiaoshuo.mine.ui.IQuestionPage;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

public class QuestionPresenter {

    private IQuestionPage mPage;

    public QuestionPresenter(IQuestionPage page) {
        mPage = page;
    }

    public void getProblemList() {
        new JsonPost.AsyncPost<FeedConfigBean>()
                .setRequest(new FeedConfigListReq())
                .setResponseType(FeedConfigBean.class)
                .subscribeOn(MtSchedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<FeedConfigBean>>() {
                    @Override
                    public void onNext(JsonResponse<FeedConfigBean> jsonResponse) {
                        mPage.dismissLoading();
                        if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getConfigList() != null
                                && !jsonResponse.data.getConfigList().isEmpty()) {
                            mPage.showProblemList(jsonResponse.data);
                        } else {
                            mPage.showEmpty();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPage.dismissLoading();
                        mPage.showNetworkError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @SuppressLint("CheckResult")
    public void commitProblem(int ideaId, String content, String desc, String concact) {

        FeedCommitReq feedCommitReq = new FeedCommitReq(ideaId, content, desc, concact);

        new JsonPost.AsyncPost<FeedCommitResp>()
                .setRequest(feedCommitReq)
                .setResponseType(FeedCommitResp.class)
                .subscribeOn(MtSchedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<FeedCommitResp>>() {
                    @Override
                    public void onNext(JsonResponse<FeedCommitResp> jsonResponse) {
                        mPage.dismissLoading();
                        if (jsonResponse.status == 1){
                            mPage.onCommitSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPage.dismissLoading();
                        ToastUtils.show("提交失败...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
