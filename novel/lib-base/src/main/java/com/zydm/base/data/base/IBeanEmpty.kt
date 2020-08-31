package com.zydm.base.data.base

import com.zydm.base.data.bean.JsonSerialize

/**
 * Created by yan on 2017/5/3.
 */
interface IBeanEmpty: JsonSerialize {

    fun isEmpty(): Boolean
}
