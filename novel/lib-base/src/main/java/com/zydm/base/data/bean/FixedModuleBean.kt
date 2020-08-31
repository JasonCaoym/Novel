package com.zydm.base.data.bean

import android.os.Parcelable
import com.zydm.base.data.base.IIdGetter
import kotlinx.android.parcel.Parcelize

@Parcelize
class FixedModuleBean: IIdGetter, Parcelable {

    companion object {
        const val ID_MALE = "1"    //  男生精选
        const val ID_FEMALE = "2"  //  女生精选
        const val ID_FINISH = "3"  //  完结
        const val ID_NEW = "4"     //   新书
    }

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
    /**
     * 人气值
     */
    var popularity: Long = 0

    override fun getId(): String {
        return bookId
    }

}

class FixedModuleListBean : ListBean<BookItemBean>()

class FixedRowModuleListBean : ListBean<BookItemBean>()