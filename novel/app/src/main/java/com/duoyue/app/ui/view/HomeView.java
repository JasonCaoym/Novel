package com.duoyue.app.ui.view;

import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean;

public interface HomeView  {
    void showDialog(RecommandBean data);

    void showEmpty();

    void showError();
}


