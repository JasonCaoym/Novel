package com.duoyue.mianfei.xiaoshuo.read.presenter

import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper
import com.zydm.base.data.bean.ListBean
import com.zydm.base.data.dao.BookRecordBean
import com.zydm.base.presenter.AbsPagePresenter
import com.zydm.base.presenter.view.ISimplePageView
import io.reactivex.Single

class HistoryPresenter(private val pageView: ISimplePageView<ListBean<BookRecordBean>>) : AbsPagePresenter<ListBean<BookRecordBean>>(pageView) {

    private var mOffset: Int = 0
    private val mPageCount: Int = 15
    private var mHasMoreData: Boolean = false

    override fun getPageDataSrc(isForceUpdate: Boolean, isLoadMore: Boolean): Single<out ListBean<BookRecordBean>> {
        if (!isLoadMore) {
            mOffset = 0
        }
        return Single.fromCallable({
            val list: List<BookRecordBean> = BookRecordHelper.getsInstance().findAllBooks(mOffset, mPageCount)
            val listBean = ListBean<BookRecordBean>()
            listBean.list.addAll(list)
            listBean
        })
    }

    override fun onPageDataUpdated(pageData: ListBean<BookRecordBean>, isByForceUpdate: Boolean, isLoadMore: Boolean) {
        mOffset++
        mHasMoreData = pageData.list.size == mPageCount
        pageView.showPage(pageData)
    }

    override fun hasMoreData(): Boolean {
        return mHasMoreData
    }
}