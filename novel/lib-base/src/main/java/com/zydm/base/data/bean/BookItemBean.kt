package com.zydm.base.data.bean

import android.os.Parcelable
import com.zydm.base.data.base.IIdGetter
import kotlinx.android.parcel.Parcelize

@Parcelize
class BookItemBean : IIdGetter, Parcelable {

    var bookId = ""
    var bookName = ""
    var bookCover = ""
    var resume = ""
    var chapterCount = 0
    var wordCount: Long = 0
    var isFinish = false
    var updateTime: Long = 0
    var currTime: Long = 0
    var author = ""
    var category: ArrayList<CategorySubBean>? = null
    var mRank: Int = 0
    var mRecommendStStr: String = ""
    /**
     * 人气值
     */
    var popularity: Long = 0

    override fun getId(): String {
        return bookId
    }
}
