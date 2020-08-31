package com.zydm.base.data.label

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.zydm.base.data.base.MtMap
import com.zydm.base.data.tools.JsonUtils
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.SPUtils

/**
 * Created by yin on 2016/7/16.
 */
object LabelMgr {

    private val TAG = "LabelMgr"

    private val KEY_LABEL_TIMESTAMP_MAP = "label_timestamp_map"
    val FORCE_CLEAR_CACHE_START = 10000

    private var LABEL_TIME: MtMap<Int, Long> = MtMap()
    private var mLoginLabel = 1

    init {
        val labelTimeJson = SPUtils.getString(KEY_LABEL_TIMESTAMP_MAP)
        LogUtils.d(TAG, "labelTimeJson:" + labelTimeJson!!)
        if (!TextUtils.isEmpty(labelTimeJson)) {
            LABEL_TIME = JsonUtils.parseJson<MtMap<Int, Long>>(labelTimeJson, object : TypeToken<MtMap<Int, Long>>() {

            }.type) ?: MtMap()
            LogUtils.d(TAG, "LABEL_TIME:$LABEL_TIME")
        }
    }

    @Synchronized
    fun updateLabel(label: Int) {
        val timestamp = System.currentTimeMillis()
        LABEL_TIME!![label] = timestamp
        val jsonStr = JsonUtils.toJson(MtMap<Any, Any>(LABEL_TIME))
        LogUtils.d(TAG, "updateLabel() jsonStr:$jsonStr")
        SPUtils.putString(KEY_LABEL_TIMESTAMP_MAP, jsonStr)
    }

    @Synchronized
    fun isLabelsUpdateWithLogin(labels: IntArray, timestamp: Long): Boolean {
        //        LogUtils.d(TAG, "isLabelUpdate  timestamp:" + timestamp);
        //        LogUtils.d(TAG, "isLabelUpdate  LABEL_TIME:" + LABEL_TIME.toString());
        if (isLabelUpdate(mLoginLabel, timestamp)) {
            LogUtils.d(TAG, "isLabelUpdate() label:$mLoginLabel  isLabelUpdate:true")
            return true
        }

        return isLabelsUpdate(labels, timestamp)
    }

    @Synchronized
    fun isLabelsUpdate(labels: IntArray?, timestamp: Long): Boolean {
        //        LogUtils.d(TAG, "isLabelUpdate  timestamp:" + timestamp);
        //        LogUtils.d(TAG, "isLabelUpdate  LABEL_TIME:" + LABEL_TIME.toString());
        if (labels == null || labels.size <= 0) {
            return false
        }
        for (label in labels) {
            if (isLabelUpdate(label, timestamp)) {
                LogUtils.d(TAG, "isLabelUpdate() label:$label  isLabelUpdate:true")
                return true
            }
        }
        return false
    }

    @Synchronized
    fun isLabelUpdate(label: Int, timestamp: Long): Boolean {
        return timestamp < getLabelTime(label)
    }

    fun getLabelTime(label: Int): Long {
        return LABEL_TIME[label] ?: 0
    }

    fun setLoginLabel(loginLabel: Int) {
        mLoginLabel = loginLabel
    }
}
