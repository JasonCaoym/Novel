package com.duoyue.app.ui.view;

public interface OnWebCallBack {
    /**
     * 获取标题
     *
     * @param title
     */
    void getTitle(String title);

    /**
     * 获得WebView的地址
     *
     * @param url
     */
    void getUrl(String url);

    /**
     * 网络错误
     */
    void onShowPromptProblem();
}
