package com.zydm.base.data.bean

class TextAdItemBean {

    companion object {
        const val LINK_TYPE_WEB = 1
        const val LINK_TYPE_BOOK = 2
    }

    var id = ""
    var subject = ""
    var linkType = 2
    var link = ""

    fun getBookId() = link

}

class TextAdListBean : ListBean<TextAdItemBean>()
