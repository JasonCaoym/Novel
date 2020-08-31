package com.duoyue.mianfei.xiaoshuo.common

import android.app.Activity
import android.content.Intent
import com.duoyue.app.ui.activity.ReadHistoryActivity
import com.duoyue.mianfei.xiaoshuo.mine.ui.QuestionActivity
import com.zydm.base.R

object ActivityHelper {

    fun gotoQuestion(activity: Activity) {
        startActivity(activity, Intent(activity, QuestionActivity::class.java))
    }

    fun gotoHistory(activity: Activity) {
        startActivity(activity, Intent(activity, ReadHistoryActivity::class.java))
    }

    fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}