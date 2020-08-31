package com.zydm.base.utils

import android.content.Context
import android.content.SharedPreferences
import com.zydm.base.common.BaseApplication
import com.zydm.base.common.Constants
import java.util.*

object SPUtils {

    private val TAG = "SPUtils"

    private val PREF_NAME = "acgn_pref"
    open val SHARED_IS_FIRST_USE = "shared_afasdf"
    // 是否新用户第一次进来，第一次进来不推书
    open val SHARED_FIRST_SHOW_RECOMMAND_BOOK = "first_show_recommand_book"
    open val SHARED_IS_UPLOAD_TAOTIAO = "shared_taotiao"
    open val SHARED_IS_FIRST_YM_UPLOAD = "ym_new_user"
    open val REWARD_COMPLETE = "reward_complete"
    open val REWARD_CLICKED = "reward_clicked"
    /**
     * 当天累计疲劳阅读时长
     */
    open val READ_TIME_TOTAL = "read_time_total"
    /**
     * 疲劳弹窗免广告时给的书豆数量（个）
     */
    open val TIRED_SHUDOU = "RD_TIRED_SHUDOU"

    private lateinit var sPre: SharedPreferences

    fun initPres(context: Context) {
        sPre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    val all: Map<*, *>
        get() = sPre.all

    fun remove(key: String) {
        sPre.edit().remove(key).apply()
    }

    fun removeAll() {
        sPre.edit().clear().apply()
    }

    fun removeWithBack(key: String) {
        remove(key)
    }

    fun putInt(key: String, value: Int) {
        sPre.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return sPre.getInt(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sPre.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sPre.getBoolean(key, defValue)
    }

    fun putString(key: String, value: String) {
        sPre?.edit()?.putString(key, value)?.apply()
    }

    fun putStringSet(key: String, values: Set<String>) {
        sPre?.edit()?.putStringSet(key, values)?.apply()
    }

    fun getStringSet(key: String): Set<String> {
        return sPre.getStringSet(key, HashSet())
    }

    @JvmOverloads
    fun getString(key: String, defValue: String = Constants.EMPTY): String {
        return sPre.getString(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        sPre.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return sPre.getLong(key, defValue)
    }

    fun putFloat(key: String, value: Float) {
        sPre.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return sPre.getFloat(key, defValue)
    }

}
