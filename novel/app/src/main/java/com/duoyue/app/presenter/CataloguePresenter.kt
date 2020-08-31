package com.duoyue.app.presenter

import com.duoyue.app.bean.BookDetailBean
import com.duoyue.app.common.data.request.read.CatalogueReq
import com.duoyue.lib.base.app.http.JsonPost
import com.duoyue.lib.base.app.http.JsonResponse
import com.duoyue.mianfei.xiaoshuo.read.utils.BookChapterHelper
import com.duoyue.mianfei.xiaoshuo.read.utils.StringUtils
import com.zydm.base.common.LoadResult
import com.zydm.base.data.dao.ChapterListBean
import com.zydm.base.presenter.view.ISimplePageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class CataloguePresenter(private val mReadChapterSeqNum : Int, private val mBookBean: BookDetailBean,
                         private val pageView: ISimplePageView<ChapterListBean>) {
    private var mGroup: Int = 0
    private var mDisposableObserver: DisposableObserver<JsonResponse<ChapterListBean>>? = null

    init {
        mGroup = 0
        requestChapter()
    }


    fun requestChapter() {
        val startSeqNum = mGroup * 50 + 1
        val request = CatalogueReq()
        request.bookId = mBookBean.bookId
        request.count = 50
        request.sort = 0
        request.startChapter = startSeqNum

        mDisposableObserver?.dispose()
        pageView.showLoading()

        mDisposableObserver = object : DisposableObserver<JsonResponse<ChapterListBean>>() {
            override fun onNext(jsonResponse: JsonResponse<ChapterListBean>) {
                pageView.dismissLoading()
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.list != null
                    && !jsonResponse.data.list.isEmpty()) {
                    var chapterListBean = jsonResponse.data
                    val chapterBeans = BookChapterHelper.getsInstance().findBookChapters(mBookBean.bookId)
                    for (chapterBean in chapterListBean.list) {
                        for (readBean in chapterBeans) {
                            if (chapterBean.chapterId == readBean.chapterId) {
                                chapterBean.setIsRead(true)
                            }
                        }
                        chapterBean.chapterTitle = StringUtils.convertChapterTitle(chapterBean.chapterTitle, chapterBean.seqNum)
                    }
                    val firstSeqNum = chapterListBean.list[0].seqNum
                    chapterListBean.mGroupIndex = if (firstSeqNum % 50 == 0) firstSeqNum / 50 - 1 else firstSeqNum / 50

                    onPageDataUpdated(chapterListBean)
                    pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED)
                } else {
                    pageView.showEmpty()
                    pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL)
                }
            }

            override fun onError(e: Throwable) {
                pageView.dismissLoading()
                pageView.showNetworkError()
                pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL)
            }

            override fun onComplete() {

            }
        }
        JsonPost.AsyncPost<ChapterListBean>()
            .setRequest(request)
            .setResponseType(ChapterListBean::class.java)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .post(mDisposableObserver)
    }

    fun onPageDataUpdated(pageData: ChapterListBean) {
        pageView.showPage(pageData)
    }

    fun setGroup(group: Int) {
        mGroup = group
        requestChapter()
    }

    fun destroy() {
        mDisposableObserver?.dispose()
    }
}