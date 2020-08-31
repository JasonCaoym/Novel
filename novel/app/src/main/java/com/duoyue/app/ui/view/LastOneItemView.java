package com.duoyue.app.ui.view;


import android.view.View;

import com.duoyue.app.bean.LastOneItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.ui.item.AbsItemView;

public class LastOneItemView extends AbsItemView<LastOneItemBean> {


    @Override
    public void onCreate() {
        setContentView(R.layout.item_more_book_city_go);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {

    }
}

