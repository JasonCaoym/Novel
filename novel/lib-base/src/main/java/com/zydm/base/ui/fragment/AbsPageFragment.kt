package com.zydm.base.ui.fragment

import android.os.Bundle
import android.view.View
import com.zydm.base.R
import com.zydm.base.presenter.AbsPagePresenter
import com.zydm.base.presenter.view.IPageView
import com.zydm.base.widgets.PromptLayoutHelper
import com.zydm.base.widgets.refreshview.PullToRefreshLayout

abstract class AbsPageFragment : BaseFragment(), IPageView {

    private var mPageBusiness: AbsPagePresenter<*>? = null
    protected var mPullLayout: PullToRefreshLayout? = null
    protected var mPromptLayoutHelper: PromptLayoutHelper? = null

    override fun onCreateView(savedInstanceState: Bundle?) {
        mPageBusiness = onCreatePage(savedInstanceState)
        initPullLayout()
        if (mPageBusiness != null) {
            mPageBusiness!!.onPageCreated()
        }
    }

    protected abstract fun onCreatePage(savedInstanceState: Bundle?): AbsPagePresenter<*>

    private fun initPullLayout() {
        onInitPullLayout(findView(R.id.pull_layout))
    }

    protected fun onInitPullLayout(pullLayout: PullToRefreshLayout?) {
        if (null == pullLayout) {
            return
        }
        mPullLayout = pullLayout

        pullLayout.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener{
            override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
                this@AbsPageFragment.onPullRefresh()
            }

            override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {
                mPageBusiness?.loadMoreData()
            }
        })
    }

    protected fun onPullRefresh() {
        mPageBusiness?.loadPageData(true)
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        super.onVisibleToUserChanged(isVisibleToUser)
        if (mPageBusiness == null) {
            return
        }
        if (isVisibleToUser) {
            mPageBusiness!!.onPageVisibleToUser()
        } else {
            mPageBusiness!!.onPageInvisibleToUser()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPageBusiness != null) {
            mPageBusiness!!.onPageDestroy()
        }
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

    override fun showForceUpdateFinish(result: Int) {
        mPullLayout?.refreshFinish(result)
    }

    override fun showLoadMoreFinish(result: Int) {
        mPullLayout?.loadMoreFinish(result)
    }
}
