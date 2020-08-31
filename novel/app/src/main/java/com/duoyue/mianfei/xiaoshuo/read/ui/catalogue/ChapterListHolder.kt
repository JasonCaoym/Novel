package com.duoyue.mianfei.xiaoshuo.read.ui.catalogue

import android.util.Log
import com.duoyue.mianfei.xiaoshuo.R
import com.zydm.base.data.dao.IChapter
import com.zydm.base.ui.item.AbsItemView
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.catalogue_item.view.*

open class ChapterListHolder : AbsItemView<IChapter>() {

    override fun onCreate() {
        setContentView(R.layout.catalogue_item)
        mItemView.setOnClickListener(this)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        mItemView.chapter_title.text = mItemData.title
        Log.i("ggg", mItemData.title + mItemData.isRead + "....")
        val color = ViewUtils.getColor(when {
            mItemData.isSelect -> R.color.color_666666
            mItemData.isRead -> R.color.color_666666
            else -> R.color.color_666666
//            mItemData.isSelect -> R.color.standard_red_main_color_c1
//            mItemData.isRead -> R.color.standard_black_third_level_color_c5
//            else -> R.color.standard_black_second_level_color_c4
        })
        mItemView.chapter_title.setTextColor(color)
    }

}