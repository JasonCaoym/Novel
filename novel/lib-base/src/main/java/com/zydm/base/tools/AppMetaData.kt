package com.zydm.base.tools

import android.content.pm.PackageManager
import com.zydm.base.common.BaseApplication
import com.zydm.base.utils.StringUtils

object AppMetaData {

    private val mMetaData by lazy {
        val application = BaseApplication.context
        val packageManager = application.globalContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(application.globalContext.packageName, PackageManager.GET_META_DATA)
        applicationInfo.metaData
    }

    val qqAppId by lazy {
        getValue("QQ_APP_ID")
    }

    val wxAppId by lazy {
        getValue("WX_APP_ID")
    }

    val wxAppSecret by lazy {
        getValue("WX_APP_SECRET")
    }

    val tutuAppSecret by lazy {
        getValue("TUTU_APP_SECRET")
    }

    @Synchronized fun getValue(key: String): String {
        return StringUtils.asString(mMetaData[key])
    }
}