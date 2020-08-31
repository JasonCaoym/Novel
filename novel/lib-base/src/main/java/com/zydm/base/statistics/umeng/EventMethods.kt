package com.zydm.base.statistics.umeng

import com.zydm.base.data.bean.JsonSerialize

/**
 * Created by wangdy on 2017/4/17.
 */

interface EventMethods: JsonSerialize {

    fun bookstore(@Param(StatisConst.KEY_FROM) from: String)
    fun classify()
    fun bookshelf()
    fun home()
    fun openAd()
    fun openAdSkip()
    fun openAdClick()
    fun search(@Param(StatisConst.KEY_FROM) from: String)
    fun searchTypeClick(@Param(StatisConst.KEY_TYPE) type: String)
    fun bannerClick(@Param(StatisConst.KEY_POS) pos: Int,
                    @Param(StatisConst.KEY_TYPE) type: Int,
                    @Param(StatisConst.KEY_LINK) link: String,
                    @Param(StatisConst.KEY_TITLE) title: String)

    fun quickClick(@Param(StatisConst.KEY_TYPE) type: String)
    fun recommendClick(@Param(StatisConst.KEY_NAME) name: String)

    fun classifyClick(@Param(StatisConst.KEY_NAME) name: String)
    fun classifyListClick(@Param(StatisConst.KEY_NAME) name: String,
                          @Param(StatisConst.KEY_TYPE) type: String)

    fun listAdExposure(@Param(StatisConst.KEY_POS) pos: String)
    fun listAdClick(@Param(StatisConst.KEY_POS) pos: String)
    fun bookDetail(@Param(StatisConst.KEY_NAME) name: String,
                   @Param(StatisConst.KEY_FROM) from: String)

    fun detailAdExposure()
    fun detailAdClick()
    fun detailCatalog(@Param(StatisConst.KEY_NAME) name: String)
    fun subscription(@Param(StatisConst.KEY_NAME) name: String,
                     @Param(StatisConst.KEY_FROM) from: String)

    fun bookReading(@Param(StatisConst.KEY_NAME) name: String,
                    @Param(StatisConst.KEY_FROM) from: String)

    fun readAdExposure()
    fun readAdClick()
    fun readBottomAdExposure()
    fun readBottomAdClick()
    fun catalogClick(@Param(StatisConst.KEY_NAME) name: String)
    fun readSetClick(@Param(StatisConst.KEY_NAME) name: String)
    fun bookshelfAdExposure()
    fun bookshelfAdClick()
    fun bookshelfDelete(@Param(StatisConst.KEY_NAME) name: String)
    fun historyClick(@Param(StatisConst.KEY_NAME) name: String)
    fun helpClick()
    fun upgradeClick()
    fun upgradeTips()
    fun laterUpgrade()
    fun nowUpgrade()
    fun laterInstall()
    fun nowInstall()
    fun strongUpgrade()
    fun strongNowUpgrade()
    fun strongNowInstall()
    fun screenAd()
    fun screenAdClick()
    fun listTopAdExposure()
    fun listTopAdClick()

}
