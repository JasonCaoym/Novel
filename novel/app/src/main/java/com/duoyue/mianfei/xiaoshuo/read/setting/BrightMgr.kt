package com.duoyue.mianfei.xiaoshuo.read.setting

import android.app.Activity
import android.content.Context
import android.os.PowerManager

class BrightMgr(activity: Activity) {

    private companion object {
        val TAG_WAKE_LOCK = "wake_lock"
    }

    private var mWakeLock: PowerManager.WakeLock


    init {
        val pm = activity.getSystemService(Context.POWER_SERVICE)!! as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, activity.packageName.plus(TAG_WAKE_LOCK))
    }

    fun acquireWakeLock(time: Long) {
        try {
            if (mWakeLock.isHeld) {
                return
            }
            mWakeLock.acquire(time)
        } catch (e: Exception) {

        }
    }

    fun acquireWakeLock() {
        try {
            if (mWakeLock.isHeld) {
                return
            }
            mWakeLock.acquire()
        } catch (e: Exception) {

        }
    }

    fun releaseWakeLock() {
        try {
            if(mWakeLock.isHeld) {
                mWakeLock.release()
            }
        } catch (e: Exception) {

        }
    }
}