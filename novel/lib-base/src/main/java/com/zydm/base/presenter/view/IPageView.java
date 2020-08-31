package com.zydm.base.presenter.view;


import android.app.Activity;

/**
 * Created by yan on 2017/5/4.
 */
public interface IPageView {

    void showLoading();

    void dismissLoading();

    void showEmpty();

    void showNetworkError();

    void showForceUpdateFinish(int result);

    void showLoadMoreFinish(int result);

    boolean isVisibleToUser();

    String getPageName();

    Activity getActivity();
}
