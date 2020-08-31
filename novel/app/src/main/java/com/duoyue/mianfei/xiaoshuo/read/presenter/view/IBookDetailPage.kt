package com.duoyue.mianfei.xiaoshuo.read.presenter.view

import com.zydm.base.data.bean.BookDetailBean
import com.zydm.base.presenter.view.IPageView

interface IBookDetailPage : IPageView {

    fun showPage(pageData: BookDetailBean)
    fun setReadBtnNext()
    fun addShelfSuccess()
    fun refreshChapter(pageData: BookDetailBean)
}
