package com.duoyue.app.ui.view;

import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookSiteBean;
import com.zydm.base.presenter.view.IPageView;

import java.util.List;

public interface BookPageView extends IPageView {
    void showPage(List<Object> list);

    void showAdPage(Object adObject,boolean isBanner);

    //    void showMenuPage(Object adObject);
    void showMorePage(List<BookCityItemBean> cityItemBeanList);

//    void loadBagList(BookBagListBean bookBagListBean);

    void loadSiteData(BookSiteBean bookSiteBean);
}


