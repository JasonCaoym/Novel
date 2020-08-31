package com.duoyue.mianfei.xiaoshuo.read.ui.bookshelf

import com.duoyue.mianfei.xiaoshuo.R
import com.zydm.base.ui.item.AbsItemView
import kotlinx.android.synthetic.main.shelf_empty_layout.view.*

open class ShelfEmptyView : AbsItemView<String>() {

    override fun onCreate() {
        setContentView(R.layout.shelf_empty_layout)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        mItemView.empty_msg.text = mItemData
    }
}
