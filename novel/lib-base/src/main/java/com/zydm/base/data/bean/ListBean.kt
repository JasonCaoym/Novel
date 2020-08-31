package com.zydm.base.data.bean

import com.google.gson.annotations.SerializedName
import com.zydm.base.data.base.IBeanEmpty
import com.zydm.base.data.tools.DataUtils

/**
 * Created by yan on 2017/5/10.
 */

open class ListBean<I> : IBeanEmpty {

    var list: ArrayList<I> = ArrayList()

    @SerializedName("nextPage")
    var nextCursor = ""

    override fun isEmpty(): Boolean {
        return DataUtils.isEmptyList(list)
    }
}
