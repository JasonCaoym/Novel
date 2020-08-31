package com.duoyue.app.ui.view;

import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;

public interface BookShelfView {

    void signSuccess(SignBean mineBean);

    void signEmpty();

    void signError();

}
