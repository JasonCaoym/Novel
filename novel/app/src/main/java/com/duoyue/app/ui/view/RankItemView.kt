package com.duoyue.app.ui.view

import android.text.TextUtils
import android.view.View
import com.duoyue.app.bean.BookRankItemBean
import com.duoyue.app.common.mgr.BookExposureMgr
import com.duoyue.app.ui.activity.BookRankActivity
import com.duoyue.lib.base.format.StringFormat
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper
import com.duoyue.mianfei.xiaoshuo.read.utils.StringUtil
import com.duoyue.mod.stats.FuncPageStatsApi
import com.duoyue.mod.stats.FunctionStatsApi
import com.duoyue.mod.stats.common.PageNameConstants
import com.zydm.base.ext.setHtmlText
import com.zydm.base.ext.setVisible
import com.zydm.base.ui.item.AbsItemView
import com.zydm.base.ui.item.ListAdapter
import com.zydm.base.utils.GlideUtils
import com.zzdm.ad.router.BaseData
import kotlinx.android.synthetic.main.book_rank_item_view.view.*

class RankItemView : AbsItemView<BookRankItemBean>() {

    var mPageName: String = ""
    private var mIsShowRank = false
    /**
     * 页面Id
     */
    private var mPageId: String? = null

    /**
     * 频道(1:男生;2:女生)
     */
    private var frequency: String? = null

    /**
     * 榜单Id
     */
    private var rankId: String? = null

    override fun onCreate() {
        setContentView(R.layout.book_rank_item_view)
        mItemView.setOnClickListener(this)
        mItemView.rank_rank_pos.setVisible(true)
        mIsShowRank = false
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {

        //获取页面Id.
        mPageId = StringFormat.toString(mAdapter?.getExtParam(BookExposureMgr.PAGE_ID_KEY))
        //频道(1:男生;2:女生)
        frequency = StringFormat.toString(mAdapter?.getExtParam(ListAdapter.EXT_KEY_RANK_FREQUENCY))
        //榜单Id
        rankId = StringFormat.toString(mAdapter?.getExtParam(ListAdapter.EXT_KEY_RANK_ID))

        GlideUtils.loadImage(mActivity, mItemData.cover, mItemView.rank_cover, GlideUtils.getBookRadius())
        if (!TextUtils.isEmpty(mItemData.resume)) {
            mItemView.rank_resume.setHtmlText(mItemData.resume)
        }
        if (!TextUtils.isEmpty(mItemData.authorName)) {
            mItemView.rank_author.setHtmlText(mItemData.authorName)
        }
        if (!TextUtils.isEmpty(mItemData.name)) {
            mItemView.rank_name.setHtmlText(mItemData.name)
        }

        setRankPost()

        mItemView.tv_score.setHtmlText(StringFormat.toString(mItemData.star))

        when (StringFormat.parseInt(rankId, 0)) {
            //人气榜
            9001, 9002 -> {
                when {
                    mItemData.realWeekCollect >= 100000000 -> mItemView.tv_rank_value.text = (mItemData.realWeekCollect / 100000000).toString() + "万"
                    mItemData.realWeekCollect >= 10000 -> mItemView.tv_rank_value.text = ((mItemData.realWeekCollect * 1f / 10000f).toInt()).toString() + "万"
                    else -> mItemView.tv_rank_value.text = mItemData.realWeekCollect.toString()
                }
                mItemView.tv_rank_type.setHtmlText("热度")
            }
            //飙升榜
            9007, 9008 -> {
                when {
                    mItemData.realWeekRead >= 100000000 -> mItemView.tv_rank_value.text = (mItemData.realWeekRead / 100000000).toString() + "万"
                    mItemData.realWeekRead >= 10000 -> mItemView.tv_rank_value.text = ((mItemData.realWeekRead * 1f / 10000f).toInt()).toString() + "万"
                    else -> mItemView.tv_rank_value.text = mItemData.realWeekRead.toString()
                }
                mItemView.tv_rank_type.setHtmlText("人在读")
            }
            //新书榜
            9017, 9018 -> {
                mItemView.tv_rank_value.setHtmlText(StringUtil.transformValue(mItemData.popularityNum))
                mItemView.tv_rank_type.setHtmlText("人气")
            }
            //完结榜
            9003, 9004 -> {
                mItemView.tv_rank_value.setHtmlText(StringUtil.transformValue(mItemData.wordCount))
                mItemView.tv_rank_type.setHtmlText("字")
            }
            //连载榜
            9005, 9006 -> {
                mItemView.tv_rank_value.setHtmlText(StringUtil.transformValue(mItemData.wordCount))
                mItemView.tv_rank_type.setHtmlText("字")
            }
            //热搜榜
            9011, 9012 -> {
                mItemView.tv_rank_value.setHtmlText(StringUtil.transformValue(mItemData.wordCount))
                mItemView.tv_rank_type.setHtmlText("字")
            }
            else -> {
                mItemView.tv_rank_value.setHtmlText("")
                mItemView.tv_rank_type.setHtmlText("")
            }
        }

        if (mActivity is BookRankActivity) {
            BookExposureMgr.addOnGlobalLayoutListener(
                mPageId, rankId, mItemView, mItemData.id, mItemData.name,
                StringFormat.parseInt(frequency, 0), null
            )
        } else {
            BookExposureMgr.addOnGlobalLayoutListener(
                mPageId, rankId, mItemView, mItemData.id, mItemData.name,
                StringFormat.parseInt(frequency, 0), null
            )
        }
    }

    private fun setRankPost() {
        val rankPos = mItemView.rank_rank_pos
        rankPos.setBackgroundResource(
            when (mItemData.rank) {
                1 -> R.mipmap.icon_rank_first
                2 -> R.mipmap.icon_rank_second
                3 -> R.mipmap.icon_rank_third
                else -> R.mipmap.icon_rank_four
            }
        )
        if (mItemData.rank > 99) {
            rankPos.textSize = 10f
        } else {
            rankPos.textSize = 12f
        }
        rankPos.text = "${mItemData.rank}"
    }

    override fun onClick(view: View) {
//        super.onClick(view)
        if (!noDoubleListener()) {
            return
        }
        //判断是否为女生频道
        if (StringFormat.toString(BookRankActivity.FEMALE).equals(frequency)) {
            //女生频道.
            FunctionStatsApi.bdGirlLeaderboardBookClick(rankId, mItemData.id)
            if (mActivity is BookRankActivity) {
                FuncPageStatsApi.rankGirlClick(mItemData.id, StringFormat.parseInt(rankId, 0))
            } else {
                FuncPageStatsApi.categoryRankGirlClick(mItemData.id, StringFormat.parseInt(rankId, 0))
            }

        } else {


            //男生频道.
            FunctionStatsApi.bdBoyLeaderboardBookClick(rankId, mItemData.id)
            if (mActivity is BookRankActivity) {
                FuncPageStatsApi.rankBoyClick(mItemData.id, StringFormat.parseInt(rankId, 0))
            } else {
                FuncPageStatsApi.categoryRankBoyClick(mItemData.id, StringFormat.parseInt(rankId, 0))
            }
        }
        if (mActivity is BookRankActivity) {
            ActivityHelper.gotoBookDetails(
                mActivity,
                mItemData.id.toString(),
                BaseData(mPageName),
                PageNameConstants.RANK,
                5,
                ""
            )
        } else {
            ActivityHelper.gotoBookDetails(
                mActivity,
                mItemData.id.toString(),
                BaseData(mPageName),
                PageNameConstants.CATEGORY,
                19,
                ""
            )
        }
    }
}
