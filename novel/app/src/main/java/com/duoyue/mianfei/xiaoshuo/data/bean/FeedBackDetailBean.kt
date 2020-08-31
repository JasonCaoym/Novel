package com.duoyue.mianfei.xiaoshuo.data.bean

class FeedBackDetailBean {

    var type: Int = 0
    var time: String = ""
    var content: String = ""
    var img: String = ""
    var feedback: FeedbackBean? = null

    var isShrink = false

    val isFeedBack: Boolean
        get() = type == 1

    class FeedbackBean {

        var userName: String = ""
        var typeName: String = ""
        var time: String = ""
        var content: String = ""
    }
}
