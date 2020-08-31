package com.duoyue.mianfei.xiaoshuo.read.ui.read

import android.view.View
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.read.page.TxtChapter
import com.zydm.base.ext.setVisible
import com.zydm.base.ui.item.AbsItemView
import kotlinx.android.synthetic.main.catalogue_item.view.*

open class ReadChapterListHolder : AbsItemView<TxtChapter>() {

    override fun onCreate() {
        setContentView(R.layout.catalogue_item)
        mItemView.setOnClickListener(this)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        mItemView.chapter_title.text = mItemData.title
    }

    fun setChapterTitleColor(color: Int) {
        mItemView.chapter_title.setTextColor(color)
    }

    fun setDownloadTextColor(color: Int) {
        mItemView.tv_download.setTextColor(color)
    }

    fun setDownloadTextVisible(visible: Boolean) {
        mItemView.tv_download.setVisible(false)
//        mItemView.tv_download.setVisible(visible)
    }

    fun setDownloadBackgruond(color: Int) {
        mItemView.tv_download.setBackgroundColor(color)
    }

    fun setChapterTitleBackgruond(color: Int){
        mItemView.chapter_title.setBackgroundColor(color)
    }

    fun setDividerColor(color: Int) {
        mItemView.findViewById<View>(R.id.divider_1px).setBackgroundColor(color)
    }
}