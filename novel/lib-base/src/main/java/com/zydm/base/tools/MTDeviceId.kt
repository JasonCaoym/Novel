package com.zydm.base.tools

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.SPUtils
import com.zydm.base.utils.StringUtils

/**
 * 2.5.0版本的新DeviceId规则
 * 已经持久化的->imei->androidId->随机
 * Created by YinJiaYan on 2018/1/15.
 */
class MTDeviceId(val context: Context) {

    private val KEY_MT_DEVICE_ID = "MT_D_ID_LJALKSJDL"

    val androidId by lazy {
        val aId = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        val aId = Settings.System.getString(context.contentResolver, Settings.System.ANDROID_ID)
        if (isImeiEmpty(aId)) "" else aId
    }

    val imei by lazy { initImei() }

    val mtDeviceId by lazy { initDeviceId() }

    fun storeMTDeviceId() {
        LogUtils.d("MTDeviceId", "storeMTDeviceId $mtDeviceId")
        SPUtils.putString(KEY_MT_DEVICE_ID, mtDeviceId)
    }

    fun isMTDeviceIdStored(): Boolean {
        return !StringUtils.isBlank(readDeviceId())
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun initImei(): String {
        LogUtils.d("MTDeviceId", "initImei")
        try {
            val phoneDeviceId = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).getDeviceId()
            if (!isImeiEmpty(phoneDeviceId)) {
                return phoneDeviceId
            }
        } catch (e: Throwable) {
        }
        return ""
    }

    private fun initDeviceId(): String {
        val existDeviceId = readDeviceId()
        if (!StringUtils.isBlank(existDeviceId)) {
            return existDeviceId!!
        }
        if (!StringUtils.isBlank(imei)) {
            return imei
        }
        if (!StringUtils.isBlank(androidId)) {
            return androidId
        }
        return "${StringUtils.getRandomStr(6)}_${java.lang.Long.toString(System.currentTimeMillis(), 16)}"
    }

    private fun readDeviceId(): String? {
        return SPUtils.getString(KEY_MT_DEVICE_ID)
    }

    private fun isImeiEmpty(imei: String?): Boolean {
        if (StringUtils.isBlank(imei)) {
            return true
        }
        try {
            return Integer.valueOf(imei?.trim()) == 0
        } catch (e: Exception) {
            return false
        }
    }
}