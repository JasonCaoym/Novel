package com.duoyue.mianfei.xiaoshuo.book.common

import android.app.Activity
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.duoyue.app.common.mgr.BookExposureMgr
import com.duoyue.app.ui.activity.BookDetailActivity
import com.duoyue.app.ui.activity.BookListActivity
import com.duoyue.app.ui.activity.BookRankActivity
import com.duoyue.mianfei.xiaoshuo.book.ui.list.SelectedActivity
import com.zydm.base.R
import com.zydm.base.data.bean.CategoryBean
import com.zydm.base.data.tools.JsonUtils
import com.zydm.base.ui.BaseActivityHelper
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.activity.web.WebActivity
import com.zydm.base.ui.item.ListAdapter
import com.zzdm.ad.router.BaseData
import com.zzdm.ad.router.RouterPath

object ActivityHelper {
    fun gotoWeb(activity: Activity, url: String) {
        BaseActivityHelper.gotoWebActivity(activity, WebActivity.Data(url, ""))
    }

    /**
     * @param modelId : 1、书架每日推荐; 2、书架文字轮播; 3、书城Banner; 4、书城分栏; 5、书城榜单; 6、书城j精品; 7、书城新书
     * 8、书城完结; 9、书城搜索推荐; 10、书城搜索空状态; 11、书城搜索结果; 12、分类检索页; 13、阅读器末尾; 14、书籍详情;
     * 15、追书推送; 6、沉睡唤醒推送; 17、书城悬浮; 18 通知栏; 19 分类榜单； 20 阅读器
     */
    fun gotoBookDetails(activity: Activity, bookId: String, data: BaseData, prevPageId: String, modelId: Int, source: String) {
        gotoBookDetails(activity, bookId, data, prevPageId, modelId, source, 1)
    }

    fun gotoBookDetails(activity: Activity, bookId: String, data: BaseData, prevPageId: String, modelId: Int,
                        source: String, requestCode : Int) {
        ARouter.getInstance().build(RouterPath.Read.PATH_DETAIL)
            .withLong(RouterPath.KEY_BOOK_ID, bookId.toLong())
            .withString(RouterPath.KEY_PARENT_ID, prevPageId)
            .withString(RouterPath.KEY_SOURCE, source)
            .withInt(RouterPath.KEY_MODEL_ID, modelId)
            .withParcelable(BaseActivity.DATA_KEY, data)
            .navigation(activity, requestCode)
    }

    fun gotoBookList(
        activity: Activity,
        title: String,
        type: Int,
        adSite: Int,
        prevParentId: String,
        currPageId: String,
        chan: Int,
        mType: String
    ) {
        val intent = Intent(activity, BookListActivity::class.java)
        intent.putExtra(BaseActivity.DATA_KEY, title)
        intent.putExtra("type", type)
        intent.putExtra("adSite", adSite)
        intent.putExtra("chan", chan)
        intent.putExtra(ListAdapter.EXT_KEY_PARENT_ID, prevParentId)
        intent.putExtra(ListAdapter.EXT_KEY_CURRENT_PAGE_ID, currPageId)
        intent.putExtra(ListAdapter.EXT_KEY_CURRENT_PAGE_ID, currPageId)
        intent.putExtra(BookExposureMgr.PAGE_CHANNEL, mType)
        activity.startActivity(intent)
    }

    fun gotoRank(activity: Activity) {
        val intent = Intent(activity, BookRankActivity::class.java)
        activity.startActivity(intent)
    }

    fun gotoJingXuan(activity: Activity, mType: String) {
        val intent = Intent(activity, SelectedActivity::class.java)
        intent.putExtra(BookExposureMgr.PAGE_CHANNEL, mType)
        activity.startActivity(intent)
    }

    fun gotoCategoryBookList(activity: Activity, categoryBean: CategoryBean) {
        val intent = Intent(activity, com.duoyue.app.ui.activity.CategoryBookListActivity::class.java)
        intent.putExtra(BaseActivity.DATA_KEY, JsonUtils.toJson(categoryBean))
        activity.startActivityForResult(intent, BookDetailActivity.REQUEST_CODE_READ)
    }

    private fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}