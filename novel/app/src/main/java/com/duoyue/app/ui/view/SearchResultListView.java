package com.duoyue.app.ui.view;

import com.duoyue.app.bean.SearchRecommdBookListBean;
import com.duoyue.app.bean.SearchResultListBean;
import com.duoyue.app.bean.SearchV2ListBean;

public interface SearchResultListView {

    void showLoading();

    void dismissLoading();

    void showEmpty();

    void showNetworkError();


    void showPage(SearchV2ListBean bookDetailBean);

    void showAdPage(Object adObject);


    void showComment(SearchResultListBean searchResultListBean);

    void showRecommdBookList(SearchRecommdBookListBean searchRecommdBookListBean);
}


