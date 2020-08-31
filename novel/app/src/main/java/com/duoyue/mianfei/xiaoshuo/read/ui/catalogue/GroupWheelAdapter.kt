package com.duoyue.mianfei.xiaoshuo.read.ui.catalogue

import android.content.Context
import com.zydm.base.widgets.wheel.adapters.AbstractWheelTextAdapter

class GroupWheelAdapter(var dataList: ArrayList<CatalogueActivity.GroupItem>, context: Context?) : AbstractWheelTextAdapter(context) {
    override fun getItemsCount(): Int {
       return  dataList.size
    }

    override fun getItemText(index: Int): CharSequence {
        return dataList.get(index).name
    }

}
