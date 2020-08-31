package com.zydm.base.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.zydm.base.R
import com.zydm.base.presenter.AbsPagePresenter
import com.zydm.base.presenter.view.IPageView
import com.zydm.base.widgets.PromptLayoutHelper
import com.zydm.base.widgets.refreshview.PullToRefreshLayout

abstract class AbsPageActivity : BaseActivity(), IPageView {

    private var mPageBusiness: AbsPagePresenter<*>? = null
    protected var mPullLayout: PullToRefreshLayout? = null
    protected var mPromptLayoutHelper: PromptLayoutHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPageBusiness = onCreatePage(savedInstanceState)
        initPullLayout()
        mPageBusiness!!.onPageCreated()
    }

    protected abstract fun onCreatePage(savedInstanceState: Bundle?): AbsPagePresenter<*>

    private fun initPullLayout() {
        onInitPullLayout(findViewById(R.id.pull_layout))
    }

    open protected fun onInitPullLayout(pullLayout: PullToRefreshLayout?) {
        if (null == pullLayout) {
            return
        }
        mPullLayout = pullLayout

        pullLayout.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener{
            override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
                this@AbsPageActivity.onPullRefresh()
            }

            override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {
                mPageBusiness?.loadMoreData()
            }

        })
    }

    protected fun getPromptLayoutHelper(): PromptLayoutHelper? {
        var helper: PromptLayoutHelper? = null
        if (mPullLayout != null) {
            helper = mPullLayout?.getPromptLayoutHelper()
        }
        if (helper != null) {
            return helper
        }
        val promptView = findView<View>(R.id.load_prompt_layout) ?: return null

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = PromptLayoutHelper(promptView)
        }
        return mPromptLayoutHelper
    }

    protected fun onPullRefresh() {
        mPageBusiness?.loadPageData(true)
    }

    override fun onResume() {
        super.onResume()
        mPageBusiness!!.onPageVisibleToUser()
    }

    override fun onPause() {
        super.onPause()
        mPageBusiness!!.onPageInvisibleToUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPageBusiness!!.onPageDestroy()
    }

    override fun showLoading() {
        getPromptLayoutHelper()?.showLoading()
    }

    override fun dismissLoading() {
        getPromptLayoutHelper()?.hide()
    }

    override fun showEmpty() {
        getPromptLayoutHelper()?.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null)
    }

    override fun showNetworkError() {
        getPromptLayoutHelper()?.showPrompt(PromptLayoutHelper.TYPE_NO_NET, View.OnClickListener {
            mPageBusiness?.loadPageData(false)
        })
    }

    override fun showForceUpdateFinish(result: Int) {
        mPullLayout?.refreshFinish(result)
    }

    override fun showLoadMoreFinish(result: Int) {
        mPullLayout?.loadMoreFinish(result)
    }

    override fun isVisibleToUser(): Boolean {
        return isOnForeground
    }

    override fun getActivity(): Activity {
        return this
    }
}
