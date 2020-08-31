package com.duoyue.mianfei.xiaoshuo.read.presenter.view

import com.zydm.base.presenter.view.IPageView

interface IBookShelfPage : IPageView {
    fun showPage(pageData: ArrayList<Any>)
}