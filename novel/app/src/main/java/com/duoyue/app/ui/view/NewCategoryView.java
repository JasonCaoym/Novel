package com.duoyue.app.ui.view;

import com.duoyue.app.bean.BookCategoryListBean;
import com.duoyue.app.bean.BookSiteBean;

public interface NewCategoryView {

    void showLoading();

    void dismissLoading();

    void showNetworkError();

    void updateCategory(BookCategoryListBean maleBean, BookCategoryListBean femaleBean);


    void showSite(BookSiteBean bookSiteBean);

}
