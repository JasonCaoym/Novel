package com.duoyue.app.ui.view;

import com.duoyue.app.bean.BookSiteBean;
import com.zydm.base.data.bean.CategoryBean;

import java.util.List;

public interface NewCategoryNotificationView {

    void showLoading();

    void dismissLoading();

    void showNetworkError();

    void updateCategory(List<CategoryBean> categoryBeanList);


    void showSite(BookSiteBean bookSiteBean);

}
