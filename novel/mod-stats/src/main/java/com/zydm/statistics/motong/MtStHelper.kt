package com.zydm.statistics.motong

import com.duoyue.lib.base.log.Logger
import com.zydm.base.data.base.MtMap

object MtStHelper {
    private const val TAG = "Stats#MtStHelper";

    fun visit(): Boolean
    {
        Logger.i(TAG, "visit:")
        MtStEventMgr.getInstance().onEvent(MtStConst.EVENT_VISIT, 0.0, null, true)
        return true
    }

    fun readBookChaper(bookId: String, chapterId: String, seqNum: Int)
    {
        Logger.i(TAG, "readBookChaper: {}, {}, {}", bookId, chapterId, seqNum)
        val params = MtMap<String, String>()
        params[MtStConst.KEY_BOOK_ID] = bookId
        params[MtStConst.KEY_CHAPTER_ID] = chapterId
        params[MtStConst.KEY_SEQ_NUM] = seqNum.toString()
        MtStEventMgr.getInstance().onEvent(MtStConst.EVENT_READ_BOOKS, 0.0, params, false)
    }

    fun bookDetails(bookId: String, source: String)
    {
        Logger.i(TAG, "bookDetails: {}, {}, {}", bookId, source)
        val params = MtMap<String, String>()
        params[MtStConst.KEY_BOOK_ID] = bookId
        params[MtStConst.KEY_SOURCE] = source
        MtStEventMgr.getInstance().onEvent(MtStConst.EVENT_BOOK_DETAIL, 0.0, params, false)
    }

}
