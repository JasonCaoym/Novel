package com.duoyue.app.ui.view;

import com.duoyue.app.bean.SearchResuleBean;
import com.duoyue.app.bean.SearchV2ListBean;
import com.duoyue.app.bean.SearchV2MoreListBean;

public interface SearchV2View {

    void showLoading();

    void dismissLoading();

    void showEmpty();

    void showNetworkError();


    void showPage(SearchV2ListBean bookDetailBean);

    void showAdPage(Object adObject);


    void showComment(SearchV2ListBean commentList);
    void showMoreComment(SearchV2MoreListBean commentList);

    void showKeyWord(SearchResuleBean searchResuleBean);
}


