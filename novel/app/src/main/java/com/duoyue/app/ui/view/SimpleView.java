package com.duoyue.app.ui.view;

public interface SimpleView<T> {
    void showLoading();
    void dismissLoading();
    void showNetworkError();
    void showEmpty();
    void loadData(T data);
}
