package com.duoyue.mianfei.xiaoshuo.presenter;

import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.request.bookcity.BookSiteListReq;
import com.duoyue.app.common.data.request.bookcity.MineReq;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;
import com.duoyue.mianfei.xiaoshuo.mine.ui.MineFragment;
import com.duoyue.mianfei.xiaoshuo.mine.ui.MineView;
import com.zydm.base.tools.PhoneStatusManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MinePresenter implements MineFragment.Presenter {

    private final MineView mMineView;
    private DisposableObserver<JsonResponse<SignBean>> mSignDisposableObserver;
    private DisposableObserver<JsonResponse<BookSiteBean>> mFloatDisposableObserver;

    public MinePresenter(MineView mineView) {
        mMineView = mineView;
    }

    /**
     * 获取签到信息
     */

    @Override
    public void loadData() {
        mSignDisposableObserver = new DisposableObserver<JsonResponse<SignBean>>() {
            @Override
            public void onNext(JsonResponse<SignBean> signBeanJsonResponse) {
                if (signBeanJsonResponse.status == 1 && signBeanJsonResponse.data != null) {
                    mMineView.showSuccess(signBeanJsonResponse.data);
                } else {
                    mMineView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mMineView.showError();
            }

            @Override
            public void onComplete() {

            }
        };
        MineReq mineReq = new MineReq();

        new JsonPost.AsyncPost<SignBean>()
                .setRequest(mineReq)
                .setResponseType(SignBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mSignDisposableObserver);
    }

    /**
     * 拼接h5地址
     */
    public static String getUrl(String s) {

        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        Boolean isLogin;
        if (userInfo == null) return "";
        isLogin = userInfo.type != 1;

        StringBuilder stringBuilder = new StringBuilder(s);

        stringBuilder.append("?uid=").append(userInfo.uid)
                .append("&version=").append(PhoneStatusManager.getInstance().getAppVersionName())
                .append("&appId=").append(Constants.APP_ID)
                .append("&channelCode=").append(PhoneStatusManager.getInstance().getAppChannel())
                .append("&isLogin=").append(isLogin);

        return stringBuilder.toString();
    }



    @Override
    public void loadSiteData(int chan) {
        BookSiteListReq bookSiteListReq = new BookSiteListReq();
        bookSiteListReq.site = 4;
        bookSiteListReq.chan = chan;

        mFloatDisposableObserver = new DisposableObserver<JsonResponse<BookSiteBean>>() {
            @Override
            public void onNext(JsonResponse<BookSiteBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    mMineView.showSite(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<BookSiteBean>()
                .setRequest(bookSiteListReq)
                .setResponseType(BookSiteBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mFloatDisposableObserver);
    }

    @Override
    public void destroy() {
        if (mSignDisposableObserver != null && !mSignDisposableObserver.isDisposed()) {
            mSignDisposableObserver.dispose();
        }
        if (mFloatDisposableObserver != null && !mFloatDisposableObserver.isDisposed()) {
            mFloatDisposableObserver.dispose();
        }
    }
}
