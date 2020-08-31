package com.duoyue.mianfei.xiaoshuo.read.common

import android.app.Activity
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.duoyue.app.bean.BookDetailBean
import com.duoyue.app.ui.activity.LoginActivity
import com.duoyue.app.ui.activity.RandomPushBookActivity
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.read.ui.catalogue.CatalogueActivity
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity
import com.zydm.base.common.ParamKey
import com.zydm.base.ui.BaseActivityHelper
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.activity.web.WebActivity
import com.zzdm.ad.router.BaseData
import com.zzdm.ad.router.RouterPath

object ActivityHelper {

    fun gotoRead(activity: Activity, bookId: String, seqNum: Int, data: BaseData, prevPageId: String, source: String) {
        val intent = Intent(activity, ReadActivity::class.java)
        intent.putExtra(ParamKey.BOOK_ID, bookId)
        intent.putExtra(ParamKey.SEQ_NUM, seqNum)
        intent.putExtra(BaseActivity.DATA_KEY, data)
        intent.putExtra(RouterPath.KEY_PARENT_ID, prevPageId)
        intent.putExtra(RouterPath.KEY_SOURCE, source)

        startActivity(activity, intent)
    }

    fun gotoRead(activity: Activity, bookId: String, data: BaseData, prevPageId: String, source: String) {
        val intent = Intent(activity, ReadActivity::class.java)
        intent.putExtra(ParamKey.BOOK_ID, bookId)
        intent.putExtra(BaseActivity.DATA_KEY, data)
        intent.putExtra(RouterPath.KEY_PARENT_ID, prevPageId)
        intent.putExtra(RouterPath.KEY_SOURCE, source)
        startActivity(activity, intent)
    }

    fun gotoReadForResult(activity: Activity, bookId: String, data: BaseData, prevPageId: String, source: String, requestCode : Int) {
        val intent = Intent(activity, ReadActivity::class.java)
        intent.putExtra(ParamKey.BOOK_ID, bookId)

        intent.putExtra(BaseActivity.DATA_KEY, data)
        intent.putExtra(RouterPath.KEY_PARENT_ID, prevPageId)
        intent.putExtra(RouterPath.KEY_SOURCE, source)
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun gotoCatalogue(activity: Activity, bookDetailBean: BookDetailBean) {
        val intent = Intent(activity, CatalogueActivity::class.java)
        intent.putExtra(BaseActivity.DATA_KEY, bookDetailBean)
        activity.startActivity(intent)
    }

    /**
     * @param parentId: 1、书架页; 2、书城页; 3、分类页
     */
    fun gotoSearch(parentId: String) {
        ARouter.getInstance().build(RouterPath.Book.PATH_SEARCH)
                .withString(BaseActivity.ID_KEY, parentId)
                .navigation()
    }

    fun gotoHome(activity: Activity, data: BaseData) {
        ARouter.getInstance().build(RouterPath.App.PATH_HOME)
                .withParcelable(BaseActivity.DATA_KEY, data)
                .navigation()
    }

    fun gotoWeb(activity: Activity, url: String) {
        BaseActivityHelper.gotoWebActivity(activity, WebActivity.Data(url, ""))
    }

    fun gotoLogin(activity: Activity, data: BaseData) {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra(BaseActivity.DATA_KEY, data)
        startActivity(activity, intent)
    }

    fun gotoRandomPushBook(activity: Activity,modelId:Int) {
        val intent = Intent(activity, RandomPushBookActivity::class.java)
        intent.putExtra("modelId",modelId)
        startActivity(activity, intent)
    }
}
