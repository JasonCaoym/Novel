package com.zydm.base.ui.item;

import java.util.List;

/**
 * Created by YinJiaYan on 2017/6/27.
 */

public interface IAdapter {

    int getItemCount();

    void notifyDataSetChanged();

    List<?> getDataList();

    String getExtParam(String key);
}
