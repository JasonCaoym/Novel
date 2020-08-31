package com.zydm.base.utils

import java.util.*

/**
 * Created by YinJiaYan on 2017/7/22.
 */

object SpRedis {

    private val PREFIX_WITH_EXPIRE = "@EXPIRE_FOR@"
    private val TAG = "CMApp.Redis"

    fun putStringSet(key: String, value: Set<String>, expireTime: Long) {
        if (isExpire(expireTime)) {
            LogUtils.d(TAG, "set PastDue return")
            return
        }
        saveTime(key, expireTime)
        SPUtils.putStringSet(key, value)
    }

    fun getStringSet(key: String): Set<String> {
        LogUtils.d(TAG, "get key:$key")
        if (clearPastDueData(key)) {
            LogUtils.d(TAG, "get clearPastDueData return")
            return HashSet()
        }

        return SPUtils.getStringSet(key)
    }

    fun putString(key: String, value: String, expireTime: Long) {
        if (isExpire(expireTime)) {
            LogUtils.d(TAG, "set PastDue return")
            return
        }
        saveTime(key, expireTime)
        SPUtils.putString(key, value)
    }

    fun getString(key: String): String {
        LogUtils.d(TAG, "get key:$key")
        if (clearPastDueData(key)) {
            LogUtils.d(TAG, "get clearPastDueData return")
            return ""
        }

        return SPUtils.getString(key)
    }

    fun getTodayEnd(): Long {
        return TimeUtils.getTodayEnd()
    }

    private fun isExpire(expireTime: Long): Boolean {
        val l = System.currentTimeMillis()
        return expireTime - l <= 0
    }

    private fun clearPastDueData(key: String): Boolean {
        val expireTime = SPUtils.getLong(getTimeKey(key), 0)
        if (expireTime == 0L || !isExpire(expireTime)) {
            return false
        }
        SPUtils.remove(key)
        SPUtils.remove(getTimeKey(key))
        LogUtils.d(TAG, "get clearPastDueData clear")
        return true
    }

    private fun saveTime(key: String, expireTime: Long) {
        SPUtils.putLong(getTimeKey(key), expireTime)
        LogUtils.d(TAG, "after saveTime  expireTime:" + SPUtils.getLong(getTimeKey(key), 0L))
    }

    private fun getTimeKey(key: String): String {
        return PREFIX_WITH_EXPIRE + key
    }
}
