package com.zydm.base.tools

import android.os.SystemClock
import com.zydm.base.common.Constants

/**
 * Created by yan on 2016/11/3.
 */

class TooFastChecker @JvmOverloads constructor(private val mDefaultMinTimeSpan: Int = Constants.MILLIS_500) {

    private var mLastTime: Long = 0

    private val curTimestamp: Long
        get() = SystemClock.elapsedRealtime()

    @JvmOverloads
    fun isTooFast(minTimeSpan: Int = mDefaultMinTimeSpan): Boolean {
        val curTimestamp = curTimestamp
        if (curTimestamp - mLastTime < minTimeSpan) {
            return true
        } else {
            mLastTime = curTimestamp
            return false
        }
    }

    fun startTime() {
        mLastTime = curTimestamp
    }

    fun cancel() {
        mLastTime = 0
    }

    fun cancelDelay(delay: Int) {
        mLastTime = curTimestamp - mDefaultMinTimeSpan + delay
    }
}
