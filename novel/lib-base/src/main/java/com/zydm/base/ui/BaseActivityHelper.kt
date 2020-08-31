package com.zydm.base.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.zydm.base.R
import com.zydm.base.common.BaseApplication
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.activity.web.WebActivity
import com.zydm.base.utils.ViewUtils

object BaseActivityHelper {

    fun gotoOutWebBrowser(activity: Activity, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val content_url = Uri.parse(url)
        intent.data = content_url
        if (intent.resolveActivity(BaseApplication.context.globalContext.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(intent, ViewUtils.getString(R.string.please_choose_browser)))
        }
    }

    fun gotoWebActivity(activity: Activity, data: WebActivity.Data) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(BaseActivity.DATA_KEY, data)
        activity.startActivity(intent)
    }

    open fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}