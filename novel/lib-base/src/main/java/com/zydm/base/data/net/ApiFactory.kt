package com.zydm.base.data.net

import android.support.v4.util.LruCache
import com.zydm.base.utils.LogUtils
import java.lang.reflect.Proxy

/**
 * Created by yan on 2017/3/17.
 */

object ApiFactory {

    private val TAG = "ApiFactory"

    private val sApiInstances = object : LruCache<Class<*>, Any>(15) {

        override fun create(apiClazz: Class<*>): Any {
            return createApi(apiClazz)
        }
    }

    private fun createApi(apiInterface: Class<*>): Any {
        val handler = ApiMethodsHandler()
        return Proxy.newProxyInstance(apiInterface.classLoader, arrayOf(apiInterface), handler)
    }

    fun <A> getApiInstance(apiClazz: Class<A>): A {
        val apiInstance = sApiInstances.get(apiClazz)
        LogUtils.d(TAG, "getApiInstance from cache:" + apiClazz.simpleName + " size:" + sApiInstances.size())
        return apiInstance as A
    }
}
