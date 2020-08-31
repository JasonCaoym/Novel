package com.zzdm.ad.router

import com.zydm.base.common.ParamKey

/*
    模块路由 路径定义
 */
object RouterPath {

    const val KEY_BOOK_ID = ParamKey.BOOK_ID
    const val KEY_PARENT_ID = ParamKey.PARENT_ID
    const val TARGET_SEQ_NUM = ParamKey.SEQ_NUM
    const val KEY_SOURCE = ParamKey.SOURCE
    const val KEY_MODEL_ID = ParamKey.MODULE_ID

    class Read {
        companion object {
            const val PATH_READ = "/read/read"
            const val PATH_DETAIL = "/read/detail"
        }
    }

    class Book {
        companion object {
            const val PATH_SEARCH = "/book/search"
        }
    }


    class App {
        companion object {
            const val PATH_HOME = "/app/home"
        }
    }
}
