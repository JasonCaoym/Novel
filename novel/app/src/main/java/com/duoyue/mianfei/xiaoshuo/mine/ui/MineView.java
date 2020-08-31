package com.duoyue.mianfei.xiaoshuo.mine.ui;

import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;

public interface MineView {
    void showSuccess(SignBean mineBean);

    void showEmpty();

    void showError();

    void showSite(BookSiteBean bookSiteBean);
}


