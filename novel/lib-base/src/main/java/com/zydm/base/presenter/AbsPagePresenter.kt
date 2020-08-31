package com.zydm.base.presenter

import com.duoyue.lib.base.log.Logger
import com.google.gson.Gson
import com.zydm.base.common.BaseErrorCode
import com.zydm.base.common.Constants
import com.zydm.base.common.LoadResult
import com.zydm.base.data.bean.ListBean
import com.zydm.base.data.label.LabelMgr
import com.zydm.base.data.tools.DataUtils
import com.zydm.base.presenter.view.IPageView
import com.zydm.base.rx.ApiSingleObserver
import com.zydm.base.rx.LoadException
import com.zydm.base.rx.MtSchedulers
import com.zydm.base.rx.RxUtils
import com.zydm.base.tools.DelayTask
import com.zydm.base.tools.TooFastChecker
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.StringUtils
import io.reactivex.Single
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

/**
 * Created by yan on 2017/5/4.
 */

abstract class AbsPagePresenter<D>(@param:NonNull private val mPageView: IPageView) {

    protected var TAG = this.javaClass.simpleName

    var isForceUpdate: Boolean = false
        private set
    private var mLoadDisposable: Disposable? = null
    private var mLoadMoreDisposable: Disposable? = null
    val mPageComposite = CompositeDisposable()
    private val mTooFastChecker = TooFastChecker()
    var dataLoadedTimestamp: Long = 0
        private set
    var pageData: D? = null
        protected set
    private val mDelayLoading = DelayTask()
    private var mIsLoadOnCreated: Boolean = false

    val isFirstLoadData: Boolean
        get() = dataLoadedTimestamp <= 0

    val isPageDataLoading: Boolean
        get() = !RxUtils.isDisposed(mLoadDisposable)

    val isMoreDataLoading: Boolean
        get() = !RxUtils.isDisposed(mLoadMoreDisposable)

    open protected fun isNeedLoadPageData(): Boolean {
        return isFirstLoadData || isLabelUpdate
    }

    protected val attentionLabels: IntArray?
        get() = null

    open fun isPageDataEmpty() = DataUtils.isEmptyData(pageData)

    protected val moreDataMerger: BiFunction<D, D, D>
        get() = DefaultMoreDataMerger()

    private val isLabelUpdate: Boolean
        get() {
            val attentionLabels = attentionLabels
            if (attentionLabels == null || attentionLabels.size == 0) {
                return false
            }
            var isLabelUpdated = false
            for (label in attentionLabels) {
                if (!LabelMgr.isLabelUpdate(label, dataLoadedTimestamp)) {
                    continue
                }
                isLabelUpdated = true
                if (label > LabelMgr.FORCE_CLEAR_CACHE_START) {
                    clearPageData()
                }
            }
            return isLabelUpdated
        }

    protected val loadingDelayTime: Int
        get() = Constants.MILLIS_500

    open fun onPageCreated() {
        mIsLoadOnCreated = loadPageData(false)
    }

    open fun onPageVisibleToUser() {
        if (mIsLoadOnCreated) {
            mIsLoadOnCreated = false
            return
        }
        loadPageData(false)
    }

    open fun onPageInvisibleToUser() {}

    open fun onPageDestroy() {
        mPageComposite.clear()
    }

    open fun loadPageData(isForceUpdate: Boolean): Boolean {
        LogUtils.d(TAG, "loadPageData  start  isForceUpdate:$isForceUpdate")
        if (!isForceUpdate) {
            if (isPageDataLoading || !isNeedLoadPageData()) {
                LogUtils.d(TAG, "loadPageData  return : $isPageDataLoading")
                return false
            }
        }
        if (mTooFastChecker.isTooFast()) {
            if (isForceUpdate) {
                mPageView.showForceUpdateFinish(LoadResult.LOAD_MORE_FAIL)
            }
            LogUtils.d(TAG, "loadPageData  return: isTooFast")
            return false
        }
        stopLoading()
        stopLoadMore()
        this.isForceUpdate = isForceUpdate
        mLoadDisposable = getPageDataSrc(this.isForceUpdate, false)
            .doOnSubscribe { toShowLoading() }
            .observeOn(MtSchedulers.mainUi())
            .subscribeWith(object : ApiSingleObserver<D>(mPageComposite) {
                override fun onLoadSuccess(@NonNull data: D) {
                    mTooFastChecker.cancel()
                    mDelayLoading.cancel()
                    mPageView.dismissLoading()
                    mPageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED)
                    processSuccess(data)
                }

                override fun onLoadFail(@NonNull loadError: LoadException) {
                    mTooFastChecker.cancel()
                    mDelayLoading.cancel()
                    mPageView.dismissLoading()
                    mPageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL)
                    processError(loadError)
                }
            })
        return true
    }

    fun loadMoreData(): Boolean {
        LogUtils.d(TAG, "loadMoreData  start")
        if (!hasMoreData()) {
            mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA)
            return false
        }
        if (isPageDataLoading || isMoreDataLoading) {
            mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL)
            return false
        }
        if (mTooFastChecker.isTooFast()) {
            mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL)
            return false
        }
        val curPageData = pageData
        if (DataUtils.isEmptyData(curPageData)) {
            mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL)
            return false
        }
        mLoadMoreDisposable = Single.just(curPageData!!)
            .zipWith(getPageDataSrc(isForceUpdate, true), moreDataMerger)
            .observeOn(MtSchedulers.mainUi())
            .subscribeWith(object : ApiSingleObserver<D>(mPageComposite) {

                override fun onLoadSuccess(@NonNull data: D) {
                    mTooFastChecker.cancel()
                    mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED)
                    pageData = data
                    onPageDataUpdated(data, isForceUpdate, true)
                }

                override fun onLoadFail(@NonNull error: LoadException) {
                    mTooFastChecker.cancel()
                    mPageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL)
                    onLoadMoreFail(error)
                    error.intercept()
                }
            })
        return true
    }

    protected fun onLoadMoreFail(error: LoadException) {

    }

    open fun hasMoreData(): Boolean {
        return !StringUtils.isBlank(getCursor(true))
    }

    fun add(disposable: Disposable?): Boolean {
        return null != disposable && mPageComposite.add(disposable)
    }

    protected fun remove(disposable: Disposable?): Boolean {
        return null != disposable && mPageComposite.remove(disposable)
    }

    protected abstract fun getPageDataSrc(isForceUpdate: Boolean, isLoadMore: Boolean): Single<out D>

    protected abstract fun onPageDataUpdated(@NonNull pageData: D, isByForceUpdate: Boolean, isLoadMore: Boolean)

    open protected fun interceptLoadPageDataError(@NonNull error: LoadException) {}

    protected fun getCursor(isLoadMore: Boolean): String {
        val curPageData = pageData
        return if (isLoadMore && curPageData is ListBean<*>) {
            (curPageData as ListBean<*>).nextCursor
        } else Constants.EMPTY
    }

    protected fun clearPageData() {
        pageData = null
        dataLoadedTimestamp = Constants.NEGATIVE_ONE_NUM.toLong()
    }

    private fun stopLoading() {
        if (!isPageDataLoading) {
            return
        }
        remove(mLoadDisposable)
        mLoadDisposable = null
    }

    private fun stopLoadMore() {
        if (!isMoreDataLoading) {
            return
        }
        remove(mLoadMoreDisposable)
        mLoadMoreDisposable = null
    }

    protected fun toShowLoading() {
        if (!isPageDataEmpty()) {
            return
        }
        mDelayLoading.doDelay(Runnable { mPageView.showLoading() }, loadingDelayTime.toLong())
    }

    private fun processSuccess(@NonNull data: D) {
        pageData = data
        Logger.e("ad#", "获取到的数据 ： " + Gson().toJson(data))
        if (isPageDataEmpty()) {
            mPageView.showEmpty()
        } else {
            onPageDataUpdated(data, isForceUpdate, false)
        }
        dataLoadedTimestamp = System.currentTimeMillis()
    }

    protected fun processError(@NonNull loadError: LoadException) {
        if (loadError.errorCode == BaseErrorCode.DATA_EMPTY) {
            pageData = null
            mPageView.showEmpty()
            loadError.intercept()
        } else if (BaseErrorCode.isNetWorkError(loadError.errorCode)) {
            if (isPageDataEmpty()) {
                mPageView.showNetworkError()
                loadError.intercept()
            }
        } else if (loadError.errorCode == BaseErrorCode.NETWORK_INVALID_RESPONSE) {
            pageData = null
            mPageView.showEmpty()
            loadError.intercept()
        } else {
            interceptLoadPageDataError(loadError)
            if (isPageDataEmpty() && !loadError.isIntercepted) {
                mPageView.showEmpty()
            }
        }
    }
}
