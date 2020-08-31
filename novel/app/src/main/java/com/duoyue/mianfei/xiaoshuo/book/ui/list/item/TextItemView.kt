package com.duoyue.mianfei.xiaoshuo.book.ui.list.item

import com.duoyue.mianfei.xiaoshuo.R
import com.zydm.base.ui.item.AbsItemView
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.book_list_text_item_view.view.*

class TextItemView: AbsItemView<String>() {
    override fun onCreate() {
        setContentView(R.layout.book_list_text_item_view)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        mItemView.title.text = mItemData
        ViewUtils.setViewVisible(mItemView.div, mPosition > 0)
    }
}