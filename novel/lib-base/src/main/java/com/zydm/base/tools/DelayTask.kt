package com.zydm.base.tools

import com.zydm.base.common.BaseApplication

/**
 * Created by YinJiaYan on 2017/5/25.
 */

class DelayTask {

    private var mTask: Runnable? = null

    val isWaiting: Boolean
        get() = mTask != null

    @Synchronized
    fun doDelay(task: Runnable, delayMillis: Long) {
        cancel()
        if (delayMillis <= 0) {
            task.run()
            return
        }
        mTask = Runnable {
            task.run()
            mTask = null
        }
        BaseApplication.handler.postDelayed(mTask, delayMillis)
    }

    @Synchronized
    fun executeImmediately(): Boolean {
        val task = this.mTask ?: return false
        this.mTask = null
        BaseApplication.handler.removeCallbacks(task)
        task.run()
        return true
    }

    @Synchronized
    fun cancel(): Boolean {
        val task = this.mTask ?: return false
        this.mTask = null
        BaseApplication.handler.removeCallbacks(task)
        return true
    }
}
