package com.zydm.base.data.bean

class BookUpdatesBean {
    var bookId = ""
    var chapterCount = 0
    var updateTime: Long = 0
}

class BookUpdatesListBean : ListBean<BookUpdatesBean>()
