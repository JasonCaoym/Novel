package com.zydm.base.data.bean

class ModuleBean {

    companion object {
        const val STYLE_HORIZONTAL_FOUR = 14;//水平4宫格
        const val STYLE_VERTICAL_THREE = 23; //纵向3宫格
        const val STYLE_VERTICAL_FOUR = 24; //纵向4宫格
        const val STYLE_VERTICAL_FIVE = 25; //纵向5宫格
    }

    var moduleId = ""
    var styleId = 0
    var name = ""
    var mIsFirst: Boolean = false

    fun isVerticalStyle(): Boolean {
        return (styleId / 10) == 2
    }

    fun itemCount(): Int {
        return styleId % 10
    }
}

