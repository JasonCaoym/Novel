package com.duoyue.mod.stats;

import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.duoyue.mod.stats.common.PageStatisticsMgr;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;

/**
 * 功能统计
 *
 * @author caoym
 * @data 2019/4/28  14:08
 */
public class FuncPageStatsApi {

    public static void addStatsForFunc(long bookId, String prevPageId, String currPageId, String oparetor) {
        addStatsForFunc(bookId, prevPageId, currPageId, 0, oparetor);
    }

    public static void addStatsForFunc(long bookId, String prevPageId, String currPageId, final int modelId, String oparetor) {
        addStatsForFunc(bookId, prevPageId, currPageId, modelId, oparetor, "");
    }

    public static void addStatsForFunc(final String prevPageId, String currPageId, String oparetor) {
        addStatsForFunc(0, prevPageId, currPageId, 0, oparetor, "");
    }

    public static void addStatsForFunc(final String prevPageId, String currPageId, final int modelId, String oparetor) {
        addStatsForFunc(0, prevPageId, currPageId, modelId, oparetor, "");
    }

    public static void addStatsForFunc(final String prevPageId, String currPageId, final int modelId, String oparetor, String source) {
        addStatsForFunc(0, prevPageId, currPageId, modelId, oparetor, source);
    }

    public static void addStatsForFunc(final long bookId, final String prevPageId, String currPageId, final int modelId, String oparetor, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, prevPageId, currPageId, modelId, oparetor, source);
    }

    //1.2.0 modelId字段类型修改
    public static void addStatsForFunc(final long bookId, final String prevPageId, String currPageId, final String modelId, String oparetor, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, prevPageId, currPageId, modelId, oparetor, source);
    }

    //1.2.1 modelId字段类型修改
    public static void addStatsForFunc(final long bookId, final String prevPageId, String currPageId, final String modelId, String oparetor, String source, String field1) {
        PageStatisticsMgr.addStatsForFunc(bookId, prevPageId, currPageId, modelId, oparetor, source, field1);
    }

    /**
     * 分享
     */
    public static void shareClick(final long bookId, final String prevPageId, final int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.SHARE_POP, String.valueOf(modelId), FunPageStatsConstants.SHARE, source);
    }

    // ---------------------------书架 节点------------------------------------------------------------------------------

    /**
     * @param modelId 1、点击tab进入; 2、后台唤起进入; 3、启动进入
     */
    public static void bookShelfShow(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0L, "", PageNameConstants.BOOKSHELF, String.valueOf(modelId), FunPageStatsConstants.BOOKSHELF_SHOW, "");
    }

    /**
     * 每日推荐书籍曝光
     *
     * @param bookId
     */
    public static void bookShelfRecomDaylyShow(long bookId, String source, int mid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(mid), FunPageStatsConstants.BOOKSHELF_RECOM_DAYLY_SHOW, source);
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, 0, FunPageStatsConstants.BOOKSHELF_RECOM_DAYLY_SHOW, source);
    }

    public static void bookShelfEdit() {
//        addStatsForFunc("", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_LONG_EDIT);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_LONG_EDIT, "");
    }

    /**
     * 每日推荐书籍点击
     *
     * @param bookId
     */
    public static void bookShelfRecomDaylyClick(long bookId, String source, int mid) {

        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(mid), FunPageStatsConstants.BOOKSHELF_RECOM_DAYLY_CLICK, source);
    }

    /**
     * 书城书单icon点击
     *
     * @param bookId
     */
    public static void bookCityBookListClick(long bookId, String source, int mid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(mid), FunPageStatsConstants.BOOKLIST_CLICK, source);
    }

    /**
     * 书架分享
     *
     * @param bookId
     */
    public static void bookShelfShare(long bookId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_SHARE);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_SHARE, "");
    }

    /**
     * 书架置顶
     *
     * @param bookId
     * @param modelId 1、置顶；2、取消置顶
     */
    public static void bookShelfSetTop(long bookId, int modelId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, modelId, FunPageStatsConstants.BOOKSHELF_SET_TOP);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(modelId), FunPageStatsConstants.BOOKSHELF_SET_TOP, "");
    }

    /**
     * 推荐书籍曝光
     *
     * @param bookId
     */
    public static void bookShelfRecomBookShow(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_RECOM_BOOK_SHOW, PageNameConstants.BOOK_SHELF_RECOMMEND_BOOK);
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_RECOM_BOOK_SHOW, PageNameConstants.BOOK_SHELF_RECOMMEND_BOOK);
    }

    /**
     * 推荐书籍点击
     *
     * @param bookId
     */
    public static void bookShelfRecmBookClick(long bookId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_RECOM_BOOK_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_RECOM_BOOK_CLICK, "");
    }

    public static void bookShelfHistory() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_HISTORY_CLICK, "");
//        addStatsForFunc("", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_HISTORY_CLICK);
    }

    public static void bookShelfSearch() {
        addStatsForFunc(0, PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_SEARCH_CLICK, 1, PageNameConstants.SEARCH);
    }

    public static void bookShelfMore() {
        addStatsForFunc("", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_MORE_CLICK);
    }

    /**
     * 书架文字轮播曝光
     *
     * @param bookId
     */
    public static void bookShelfTxtAdShow(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(0), FunPageStatsConstants.BOOKSHELF_TXTAD_SHOW, PageNameConstants.BOOK_SHELF_TEXT_SHOW);
    }

    public static void bookShelfTxtAdClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(0), FunPageStatsConstants.BOOKSHELF_TXTAD_CLICK, PageNameConstants.BOOK_SHELF_TEXT_SHOW);
    }

    public static void bookShelfTxtAdExpdClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(0), FunPageStatsConstants.BOOKSHELF_TXTAD_EXPEND_CLICK, PageNameConstants.BOOK_SHELF_TEXT_SHOW);
    }

    public static void bookShelfTxtAdExpdShow(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, String.valueOf(0), FunPageStatsConstants.BOOKSHELF_TXTAD_EXPAND, PageNameConstants.BOOK_SHELF_TEXT_SHOW);
    }

    public static void bookShelfAddBtn() {
//        addStatsForFunc("", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_ADD_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_ADD_CLICK, "");
    }

    /**
     * 收藏的书籍点击
     *
     * @param bookId
     */
    public static void bookShelfBookClick(long bookId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_BOOK_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_BOOK_CLICK, "");
    }


    public static void bookShelfRemoveBook() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOKSHELF, "0", FunPageStatsConstants.BOOKSHELF_REMOVE_BOOK, "");
//        addStatsForFunc("", PageNameConstants.BOOKSHELF, FunPageStatsConstants.BOOKSHELF_REMOVE_BOOK);
    }


    // ------------------------------书城 节点---------------------------------------------------------------------------

    /**
     * @param modelId 1、点击tab进入; 2、后台唤起进入; 3、启动进入; 4、阅读器末尾页进入; 5、书架空状态进入; 6、阅读历史空状态进入; 7、书架列表添加按钮进入
     *                NOW_X
     */
    public static void bookCityShow(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_SHOW, "");
    }

    public static void bookCitySearchClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, String.valueOf(2), FunPageStatsConstants.BOOKCITY_SEARCH_CLICK, "");
    }

    /**
     * 口味是男的是时候，是精选男，否则为精选女
     *
     * @param modelId 1、精选-男; 2、精选-女;3、男生; 4、女生
     */
    public static void bookCityTabClick(int modelId) {
//        addStatsForFunc("", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_TAB_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_TAB_CLICK, "");
    }

    public static void bookCityBannerShow(long bookId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_BANNER_SHOW, source);
    }

    public static void bookCityBannerClick(long bookId, int modelId, String source) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_BANNER_CLICK, source);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_BANNER_CLICK, source);
    }

    /**
     * 推荐功能icon
     *
     * @param modelId： 1、榜单icon;2、精品icon;3、新书icon;4、完结icon;5、分类icon
     */
    public static void bookCityIconClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_RECOM_ICON, "");
    }

    public static void bookCityBookShow(long bookId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_BOOK_SHOW, source);
    }

    public static void bookCityBookClick(long bookId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_BOOK_CLICK, source);
    }

    public static void bookCitySwitch(int modelId) {
//        addStatsForFunc("", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_SWITCH);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_SWITCH, "");
    }

    public static void bookCityFloatBtnClick(long bookId) {
        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, FunPageStatsConstants.BOOKCITY_FLOATBTN_CLICK);
    }

    public static void bookCityTaste() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, "0", FunPageStatsConstants.BOOKCITY_TASTE, "");
//        addStatsForFunc("", PageNameConstants.BOOK_CITY, FunPageStatsConstants.BOOKCITY_TASTE);
    }

    /**
     * 书城排行榜曝光
     *
     * @param bookId
     * @param modelId
     */
    public static void bookCityRankShow(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_SHOW, "");
//        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_RANK_SHOW);
    }

    /**
     * 书城排行榜点击
     *
     * @param bookId
     * @param modelId
     */
    public static void bookCityRankClick(long bookId, int modelId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.BOOKCITY_RANK_BOOK_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_BOOK_CLICK, "");
    }

    /**
     * 书城排行榜tab点击
     */
    public static void bookCityRankTabClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.BOOK_CITY, PageNameConstants.RANK, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_CLICK, "");
    }

    /**
     * 书城更多榜单
     */
    public static void bookCityRankMore() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, "0", FunPageStatsConstants.BOOKCITY_RANK_MORE, "");
//        addStatsForFunc("", PageNameConstants.BOOK_CITY, FunPageStatsConstants.BOOKCITY_RANK_MORE);
    }

    public static void rankBoyClick(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.BOOK_CITY, PageNameConstants.RANK, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_BOY_CLICK, "");
    }

    public static void rankGirlClick(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.BOOK_CITY, PageNameConstants.RANK, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_GIRL_CLICK, "");
    }

    /**
     * 书城页面男生排行榜书籍曝光
     */
    public static void bookCityRankBoyShow(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.BOOK_CITY, PageNameConstants.RANK, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_MALE_SHOW, "");
    }

    /**
     * 书城页面女生排行榜书籍曝光
     */
    public static void bookCityRankGirlShow(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.BOOK_CITY, PageNameConstants.RANK, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_FEMALE_SHOW, "");
    }

    // ----------------------搜索 节点-----------------------------------------------------------------------------------

    public static void searchShow(String parentId, int modelId) {
//        addStatsForFunc(0, parentId, PageNameConstants.SEARCH, modelId, FunPageStatsConstants.SEARCH_SHOW);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, String.valueOf(modelId), FunPageStatsConstants.SEARCH_SHOW, "");
    }

    public static void searchRecomBookClick(long bookId, String parentId) {
//        addStatsForFunc(bookId, parentId, PageNameConstants.SEARCH, 0, FunPageStatsConstants.SEARCH_RECOM_BOOK_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.SEARCH, "0", FunPageStatsConstants.SEARCH_RECOM_BOOK_CLICK, "");
    }

    public static void searchHotClick(String parentId) {
//        addStatsForFunc(parentId, PageNameConstants.SEARCH, FunPageStatsConstants.SEARCH_HOT_TXT_CLICK);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, "0", FunPageStatsConstants.SEARCH_HOT_TXT_CLICK, "");
    }

    public static void searchHistoryClick(String parentId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, "0", FunPageStatsConstants.SEARCH_HISTORY_CLICK, "");
//        addStatsForFunc(parentId, PageNameConstants.SEARCH, FunPageStatsConstants.SEARCH_HISTORY_CLICK);
    }

    public static void searchHistoryClear(String parentId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, "0", FunPageStatsConstants.SEARCH_HISTORY_CLEAR, "");
//        addStatsForFunc(parentId, PageNameConstants.SEARCH, FunPageStatsConstants.SEARCH_HISTORY_CLEAR);
    }

    /**
     * 关键词联想列表点击
     */
    public static void searchKeyListClick(String parentId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, "0", FunPageStatsConstants.SEARCH_KEYWORD_LIST_CLICK, "");
//        addStatsForFunc(parentId, PageNameConstants.SEARCH, FunPageStatsConstants.SEARCH_KEYWORD_LIST_CLICK);
    }

    /**
     * 搜索界面搜索按钮点击
     *
     * @param modelId： 1、键盘搜索按钮; 2、搜索栏按钮
     */
    public static void searchBtnClick(String parentId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.SEARCH, String.valueOf(modelId), FunPageStatsConstants.SEARCH_BTN_CLICK, "");
//        addStatsForFunc(0, parentId, PageNameConstants.SEARCH, modelId, FunPageStatsConstants.SEARCH_BTN_CLICK);
    }

    /**
     * 搜索关键字上报
     */
    public static void searchKeyWordClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, "26", FunPageStatsConstants.SEARCH_KEYWORD, "");
    }

    public static void searchEmptyClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, "0", FunPageStatsConstants.SEARCH_EMPTY_BOOK_CLICK, "");
//        addStatsForFunc(bookId, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, 0, FunPageStatsConstants.SEARCH_EMPTY_BOOK_CLICK);
    }

    /**
     * 搜索结果界面搜索按钮点击
     *
     * @param modelId： 1、键盘搜索按钮; 2、搜索栏按钮
     */
    public static void searchResultBtnClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, String.valueOf(modelId), FunPageStatsConstants.SEARCH_RESULT_BTN_CLICK, "");
//        addStatsForFunc(0, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, modelId, FunPageStatsConstants.SEARCH_RESULT_BTN_CLICK);
    }

    /**
     * 搜索结果界面作者点击
     */
    public static void searchResultAuthClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, "0", FunPageStatsConstants.SEARCH_AUTH_CLICK, "");
//        addStatsForFunc(0, PageNameConstants.SEARCH, PageNameConstants.SEARCH_RESULT, 0, FunPageStatsConstants.SEARCH_AUTH_CLICK);
    }


    // ---------------------------书籍详情-----------------------------------------------------------------------------------

    /**
     * @param bookId
     * @param parentId
     * @param modelId
     * @param source   NOW_X
     */
    public static void bookDetailShow(long bookId, String parentId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, modelId, FunPageStatsConstants.BOOK_DETAIL_SHOW, source);
    }

    public static void bookDetailCatalogue(long bookId, String parentId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_CATALOGUE, source);
//        addStatsForFunc(bookId, parentId, PageNameConstants.BOOK_DETAIL, 0, FunPageStatsConstants.BOOK_DETAIL_CATALOGUE, source);
    }

    public static void bookDetailMoreComment(long bookId, String parentId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_COMMENT_MORE, source);
//        addStatsForFunc(bookId, parentId, PageNameConstants.BOOK_DETAIL, 0, FunPageStatsConstants.BOOK_DETAIL_COMMENT_MORE, source);
    }

    /**
     * 写书评按钮点击
     *
     * @param parentId
     * @param modelId: 1:书籍详情; 2:书评列表
     * @param source
     */
    public static void bookDetailEditComment(String parentId, String currPageId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, currPageId, String.valueOf(modelId), FunPageStatsConstants.BOOK_DETAIL_COMMENT, source);
//        addStatsForFunc(parentId, currPageId, modelId, FunPageStatsConstants.BOOK_DETAIL_COMMENT, source);
    }

    /**
     * @param bookId
     * @param source
     */
    public static void bookDetailSendComment(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.SEND_COMMENT, "0", FunPageStatsConstants.BOOK_DETAIL_COMMENT_SEND, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.SEND_COMMENT, 0, FunPageStatsConstants.BOOK_DETAIL_COMMENT_SEND, source);
    }

    /**
     * @param bookId
     * @param modelId :1-方案A; 2-方案B
     * @param source
     */
    public static void bookDetailAddBook(long bookId, String parentId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, String.valueOf(modelId), FunPageStatsConstants.BOOK_DETAIL_ADD_BOOK, source);
//        addStatsForFunc(bookId, parentId, PageNameConstants.BOOK_DETAIL, modelId, FunPageStatsConstants.BOOK_DETAIL_ADD_BOOK, source);
    }

    /**
     * @param bookId
     * @param modelId :1-方案A; 2-方案B
     * @param source
     */
    public static void bookDetailReadClick(long bookId, String parentId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, String.valueOf(modelId), FunPageStatsConstants.BOOK_DETAIL_READ_CLICK, source);
    }

    /**
     * 抢先阅读第一章按钮点击
     */
    public static void bookDetailFirstChapter(long bookId, String parentId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_FIRST_CHAPTER, source);
//        addStatsForFunc(bookId, parentId, PageNameConstants.BOOK_DETAIL, 0, FunPageStatsConstants.BOOK_DETAIL_FIRST_CHAPTER, source);
    }

    /**
     * 继续阅读第二章按钮点击
     */
    public static void bookDetailSecondChapter(long bookId, String parentId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_SECOND_CHAPTER, source);
//        addStatsForFunc(bookId, parentId, PageNameConstants.BOOK_DETAIL, 0, FunPageStatsConstants.BOOK_DETAIL_SECOND_CHAPTER, source);
    }

    /**
     * 读者都在看-曝光
     */
    public static void bookDetailSimilarShow(long bookId, String parentId, String source, String currentBookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_SIMILAR_SHOW, source, currentBookId);
    }

    /**
     * 读者都在看-点击
     */
    public static void bookDetailSimilarClick(String bookId, String parentId, String source, long currBookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(Long.parseLong(bookId), parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_SIMILAR_CLICK, source, String.valueOf(currBookId));
    }

    /**
     * 同类热门书-曝光
     */
    public static void bookDetailHotShow(long bookId, String parentId, String source, String currentBookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_HOT_SHOW, source, currentBookId);
    }

    /**
     * 同类热门书-点击
     */
    public static void bookDetailHotClick(String bookId, String parentId, String source, long currBookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(Long.parseLong(bookId), parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_HOT_CLICK, source, String.valueOf(currBookId));
    }

    /**
     * 读者都在看-换一换
     */
    public static void bookDetailSimilarSwitch(String parentId, String source, long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_SIMILAR_SWITCH, source, String.valueOf(bookId));
    }

    /**
     * 同类热门书-换一换
     */
    public static void bookDetailHotSwitch(String parentId, String source, long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, parentId, PageNameConstants.BOOK_DETAIL, "0", FunPageStatsConstants.BOOK_DETAIL_HOT_SWITCH, source, String.valueOf(bookId));
    }

    /**
     * 点击小说口令加入书架,继续读
     */
    public static void commandAddShelfRead(long bookId) {
//        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, 0, FunPageStatsConstants.NOVEL_COMMAND_CLICK, "1");
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, "0", FunPageStatsConstants.NOVEL_COMMAND_CLICK, "1");
    }

    /**
     * 点击小说口令曝光
     *
     * @return
     */
    public static void commandShow(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_CITY, "0", FunPageStatsConstants.NOVEL_COMMAND_SHOW, "1");
//        addStatsForFunc(bookId, "", PageNameConstants.BOOK_CITY, 0, FunPageStatsConstants.NOVEL_COMMAND_SHOW, "1");
    }


    // ----------------------分类 节点-------------------------------------------------------------------------------------

    /**
     * 进入分类页
     *
     * @param modelId: 1、点击tab进入; 2、后台唤起进入; 3、点击书城精选页icon进入
     */
    public static void categoryShow(int modelId) {
//        addStatsForFunc("", PageNameConstants.CATEGORY, modelId, FunPageStatsConstants.CATEGORY_SHOW);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_SHOW, "");
    }

    /**
     * 分类男女生tab点击
     *
     * @param modelId: 1、男生TAB; 2、女生TAB; 3 图书TAB
     */
    public static void categoryTabClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_SEX_CLICK, "");
    }

    /**
     * 分类页搜索点击
     */
    public static void categorySearch() {
//        addStatsForFunc(0, PageNameConstants.CATEGORY, FunPageStatsConstants.CATEGORY_SEARCH, 3, "");
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.CATEGORY, FunPageStatsConstants.CATEGORY_SEARCH, String.valueOf(3), "F4", "");
    }

    /**
     * 分类列表点击
     */
    public static void categoryListClick(long categoryId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(categoryId, "", PageNameConstants.CATEGORY, String.valueOf(0), FunPageStatsConstants.CATEGORY_LIST_CLICK, "");
    }

    /**
     * 排序条件
     *
     * @param modelId： 1、按人气； 2、按更新； 3、按评分
     */
    public static void categorySelecteFirst(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_SORT_FIRST, "");
//        addStatsForFunc(PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, modelId, FunPageStatsConstants.CATEGORY_SORT_FIRST);
    }

    /**
     * 筛选连载状态
     *
     * @param modelId： 1、全部状态； 2、连载中； 3、已完结
     */
    public static void categorySelecteStatus(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_SORT_STATUS, "");
//        addStatsForFunc(PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, modelId, FunPageStatsConstants.CATEGORY_SORT_STATUS);
    }

    /**
     * 筛选字数
     *
     * @param fontSize: 女生： 1:不限； 2、30万以下;  3、30-80万; 4、80万以上
     *                  男生： 1:不限； 2、100万以下; 3、100-300万 4、300万以上
     * @param modelId：  1、男； 2、女
     */
    public static void categorySelecteWords(long fontSize, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(fontSize, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_SORT_SIZE, "");
//        addStatsForFunc(fontSize, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, modelId, FunPageStatsConstants.CATEGORY_SORT_SIZE);
    }

    /**
     * 筛选标签
     */
    public static void categorySelecteLabel(long labelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(labelId, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, "0", FunPageStatsConstants.CATEGORY_SORT_LABEL, "");
//        addStatsForFunc(labelId, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, FunPageStatsConstants.CATEGORY_SORT_LABEL);
    }

    /**
     * 筛选二级分类，1.1.8
     */
    public static void categorySelecteLabel2(long labelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(labelId, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, "0", FunPageStatsConstants.CATEGORY_SORT_LABEL2, "");
//        addStatsForFunc(labelId, PageNameConstants.CATEGORY, PageNameConstants.CATEGORY_DETAIL, FunPageStatsConstants.CATEGORY_SORT_LABEL2);
    }

    /**
     * 分类页面排行榜tab点击
     */
    public static void categoryRankTabClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_CLICK, "");
    }

    /**
     * 分类页面男生排行榜书籍点击
     */
    public static void categoryRankBoyClick(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_BOY_CLICK, "");
    }

    /**
     * 分类页面女生排行榜书籍点击
     */
    public static void categoryRankGirlClick(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.BOOKCITY_RANK_GIRL_CLICK, "");
    }

    /**
     * 分类页面男生排行榜书籍曝光
     */
    public static void categoryRankBoyShow(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_MALE_SHOW, "");
    }

    /**
     * 分类页面女生排行榜书籍曝光
     */
    public static void categoryRankGirlShow(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.CATEGORY, String.valueOf(modelId), FunPageStatsConstants.CATEGORY_RANK_FEMALE_SHOW, "");
    }

    // -----------------------------我的 节点---------------------------------------------------------------------------------

    /**
     * 我的展示
     *
     * @param modelId: 1、点击tab进入; 2、后台唤起进入
     */
    public static void mineShow(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, String.valueOf(modelId), FunPageStatsConstants.MINE_SHOW, "");
//        addStatsForFunc("", PageNameConstants.MINE, modelId, FunPageStatsConstants.MINE_SHOW);
    }

    /**
     * 登录入口点击
     */
    public static void mineLoginClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, "0", FunPageStatsConstants.MINE_LOGIN, "");
//        addStatsForFunc("", PageNameConstants.MINE, FunPageStatsConstants.MINE_LOGIN);
    }

    /**
     * 阅读口味入口点击
     */
    public static void mineTasteClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, "0", FunPageStatsConstants.MINE_TASTE, "");
//        addStatsForFunc("", PageNameConstants.MINE, FunPageStatsConstants.MINE_TASTE);
    }

    /**
     * 阅读历史入口点击
     */
    public static void mineHistoryClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, "0", FunPageStatsConstants.MINE_HISTORY, "");
//        addStatsForFunc("", PageNameConstants.MINE, FunPageStatsConstants.MINE_HISTORY);
    }

    /**
     * 确认退出登录点击
     */
    public static void mineUnloginClick() {
//        addStatsForFunc(0, prevPageId, currPageId, 0, oparetor, "");
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, "0", FunPageStatsConstants.MINE_UNLOGIN, "");
//        addStatsForFunc("", PageNameConstants.MINE, FunPageStatsConstants.MINE_UNLOGIN);
    }


    // ----------------------阅读器 节点---------------------------------------------------------------------------------

    /**
     * 进入阅读器，用户跨天的也需要上传
     * <p>
     * NOW_X
     */
    public static void readShow(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_SHOW, source);
    }

    /**
     * 深度阅读，用户跨天的也需要上传
     * <p>
     * NOW_X
     */
    public static void readDeep(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_DEEP, source);
    }

    /**
     * 向前翻页次数
     */
    public static void readNextPage(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_NEXT_NUM, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_NEXT_NUM, source);
    }

    /**
     * 进入阅读器第一次翻页
     */
    public static void readNextPageOnce(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_NEXT_ONECE, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_NEXT_ONECE, source);
    }

    /**
     * 向后翻页次数
     */
    public static void readPrevPage(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_PREV_NUM, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_PREV_NUM, source);
    }

    /**
     * 真实翻页次数
     */
    public static void readFlipPageReal(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_REAL_MOVE_NUM, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_REAL_MOVE_NUM, source);
    }

    /**
     * 章节阅读量
     */
    public static void readChapterNum(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_CHAPTER_NUM, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_CHAPTER_NUM, source);
    }

    /**
     * 阅读时长
     * NOW_X
     */
    public static void readDuration(long bookId, String prevPageId, String source) {
        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_DURATION, source);
    }

    /**
     * 阅读器目录点击
     */
    public static void readCatalogueMenu(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_CATALOGUE_MENU, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_CATALOGUE_MENU, source);
    }

    /**
     * 阅读器目录章节切换
     */
    public static void readCatalogueClick(long bookId, String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_CATALOGUE_CLICK, source);
//        addStatsForFunc(bookId, prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_CATALOGUE_CLICK, source);
    }

    /**
     * 切换到夜间模式
     */
    public static void readNightMode(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_NIGHT_MODE, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_NIGHT_MODE, source);
    }

    /**
     * 切换到日间模式
     */
    public static void readDayMode(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_DAY_MODE, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_DAY_MODE, source);
    }

    /**
     * 设置菜单点击次数
     */
    public static void readSettingClick(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_SETTING_MENU, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_SETTING_MENU, source);
    }

    /**
     * 字号加大点击次数
     */
    public static void readFontInc(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_FONT_INC, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_FONT_INC, source);
    }

    /**
     * 字号减小点击次数
     */
    public static void readFontDec(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_FONT_DEC, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_FONT_DEC, source);
    }

    /**
     * 亮度调节次数
     */
    public static void readLightChanged(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_LIGHT_CHANGE, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_LIGHT_CHANGE, source);
    }

    /**
     * 切换到系统亮度次数
     */
    public static void readLightSys(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_LIGHT_SYS, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_LIGHT_SYS, source);
    }

    /**
     * 背景1切换次数
     */
    public static void readBg1(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_BG_1_NUM, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_BG_1_NUM, source);
    }

    /**
     * 背景2切换次数
     */
    public static void readBg2(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_BG_2_NUM, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_BG_2_NUM, source);
    }

    /**
     * 背景3切换次数
     */
    public static void readBg3(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_BG_3_NUM, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_BG_3_NUM, source);
    }

    /**
     * 背景4切换次数
     */
    public static void readBg4(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_BG_4_NUM, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_BG_4_NUM, source);
    }

    /**
     * 音量加-上一页
     */
    public static void readVolPrevPage(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_VOL_PREV, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_VOL_PREV, source);
    }

    /**
     * 音量减-下一页
     */
    public static void readVolNextPage(String prevPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "0", FunPageStatsConstants.READ_VOL_NEXT, source);
//        addStatsForFunc(prevPageId, PageNameConstants.READER, 0, FunPageStatsConstants.READ_VOL_NEXT, source);
    }

    /**
     * 确认退出阅读器
     *
     * @param duration: 上报时长(分钟数)
     * @param modelId:  1、加入书架; 2、取消
     */
    public static void readExit(long duration, String prevPageId, int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(duration, prevPageId, PageNameConstants.READER, String.valueOf(modelId), FunPageStatsConstants.READ_EXIT, source, "");
//        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(duration, prevPageId, PageNameConstants.READER, modelId, FunPageStatsConstants.READ_EXIT, source);
    }

    /**
     * 去书城按钮点击
     *
     * @param modelId: 1、已完结; 2、连载中
     */
    public static void readGoBookCity(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.READER, PageNameConstants.READER_END, String.valueOf(modelId), FunPageStatsConstants.READ_GO_BOOK_CITY, source);
//        addStatsForFunc(PageNameConstants.READER, PageNameConstants.READER_END, modelId, FunPageStatsConstants.READ_GO_BOOK_CITY, source);
    }

    /**
     * 阅读器末尾页推荐书籍点击
     */
    public static void readEndBookClick(long bookId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, PageNameConstants.READER, PageNameConstants.READER_END, "0", FunPageStatsConstants.READ_END_CLICK, source);
//        addStatsForFunc(bookId, PageNameConstants.READER, PageNameConstants.READER_END, 0, FunPageStatsConstants.READ_END_CLICK, source);
    }

    /**
     * 退出APP
     *
     * @param hasDrawed: 1、当前页面已渲染; 2、当前页面未渲染
     * @param modelId:   1、主页activity，点击返回键退出; 2、任意位置点击Home键退出
     *                   NOW_X
     */
    public static void exitApp(long hasDrawed, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(hasDrawed, "", "", modelId, FunPageStatsConstants.EXIT_APP, "");
    }

    /**
     * 读取当日未上报阅读时长.
     *
     * @return
     */
    public static int getCurrDayReadingTime() {
        return PageStatsUploadMgr.getInstance().getCurrDayReadingTime();
    }

    /**
     * 读取所有未上报阅读时长.
     *
     * @return
     */
    public static int getTotalReadingTime() {
        return PageStatsUploadMgr.getInstance().getTotalReadingTime();
    }

    /**
     * 签到按钮点击事件
     *
     * @param currentPage: BOOKSHELF  MINE
     * @param modelId:     1、未签订状态点击 2.已签到状态点击
     */
    public static void signInClick(String currentPage, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", currentPage, String.valueOf(modelId), FunPageStatsConstants.SIGNIN, "");
    }

    /**
     * 书豆数量和免广告点击事件
     *
     * @param modelId: 1、点击我的书豆数量  2、点击免广告特权
     */
    public static void beanFreeAdClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, String.valueOf(modelId), FunPageStatsConstants.BEAN_FREE_AD, "");
    }

    /**
     * 每日福利任务
     */
    public static void taskEveryDayClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, String.valueOf(0), FunPageStatsConstants.TASK_EVERYDAY, "");
    }

    /**
     * 通知栏搜索
     */
    public static void notifySearch() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", "", String.valueOf(0), FunPageStatsConstants.NOTIFY_SEARCH, "");
    }

    /**
     * 通知栏书籍曝光
     */
    public static void notifyBookShow(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", "", String.valueOf(0), FunPageStatsConstants.NOTIFY_BOOK_SHOW, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
    }

    /**
     * 通知栏书籍点击
     */
    public static void notifyBookClick(long bookId,int mid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", "", String.valueOf(mid), FunPageStatsConstants.NOTIFY_BOOK_CLICK, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
    }

    /**
     * 启动弹窗书籍曝光
     */
    public static void launcherDialogShow(long bookId, String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", currPageId, String.valueOf(0), FunPageStatsConstants.LAUNCHER_DIALOG_SHOW, PageNameConstants.SOURCE_LAUNCHER);
    }

    /**
     * 启动弹窗书籍点击
     */
    public static void launcherDialogClick(long bookId, String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", currPageId, String.valueOf(0), FunPageStatsConstants.LAUNCHER_DIALOG_CLICK, PageNameConstants.SOURCE_LAUNCHER);
    }

    /**
     * deeplink唤起
     */
    public static void deepLink(long bookId, String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", currPageId, String.valueOf(1), FunPageStatsConstants.DEEP_LINK, "");
    }

    /**
     * 后台启动次数
     */
    public static void backToForeground() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", "", String.valueOf(0), FunPageStatsConstants.BACK_TO_FOREGROUND, "");
    }

    /**
     * 在线时长
     * <p>
     * NOW_X
     */
    public static void onlineTime(String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", currPageId, 0, FunPageStatsConstants.ONLINE_TIME, "");
    }

    /**
     * 存活时长
     * NOW_X
     */
    public static void aliveTime(String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", currPageId, String.valueOf(0), FunPageStatsConstants.ALIVE_TIME, "");
    }

    /**
     * 阅读器更多操作
     *
     * @param source
     */
    public static void readMoreMenu(String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.READER, String.valueOf(0), FunPageStatsConstants.READ_MORE, source);
    }

    /**
     * 存活时长
     *
     * @param modelId 1:书籍详情； 2：分享； 3：章节报错
     */
    public static void readMoreDetal(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.READER, String.valueOf(modelId), FunPageStatsConstants.READ_MORE_DETAIL, source);
    }

    /**
     * 章节报错
     *
     * @param modelId 1:确定； 2：取消
     *                <p>
     *                NOW_X
     */
    public static void readChapterError(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER, modelId, FunPageStatsConstants.READ_CHAPTER_ERROR, source);
    }

    /**
     * 启动阅读
     */
    public static void readrRestart() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.READER, String.valueOf(0), FunPageStatsConstants.READ_RESTART, "");
    }

    /**
     * PUSH点击
     */
    public static void pushClick(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", "", String.valueOf(modelId), FunPageStatsConstants.PUSH_CLICK, PageNameConstants.PUSH_RECOMMEND);
    }

    /**
     * PUSH曝光
     */
    public static void pushExpose(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", "", String.valueOf(modelId), FunPageStatsConstants.PUSH_EXSPOSE, PageNameConstants.PUSH_RECOMMEND);
    }

    /**
     * 书城页面大礼包点击
     */
    public static void bookCityRedPageClick(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.BOOK_CITY, modelId, FunPageStatsConstants.NEWGIFT, "");
    }

    /**
     * 书籍详情一级分类点击
     */
    public static void bookDetailClsClick(int modelId, long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_DETAIL, String.valueOf(modelId), FunPageStatsConstants.CLS, "");
    }

    /**
     * 书籍详情标签点击
     */
    public static void bookDetailCltagClick(String modelId, long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOK_DETAIL, modelId, FunPageStatsConstants.CLTAG, "");
    }

    /**
     * 犹豫用户推书页曝光
     *
     * @param modelId 1 搜索页弹出  2书城页弹出
     */
    public static void randomPushExpose(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.RANDOM_PUSH, String.valueOf(modelId), FunPageStatsConstants.RANDOM_PUSH_EXSPOSE, PageNameConstants.RECOMMEND_PUSH_BOOK);
    }

    /**
     * 犹豫用户推书页尾页上拉进入阅读器时上报
     */
    public static void randomPushToRead(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.RANDOM_PUSH, String.valueOf(modelId), FunPageStatsConstants.RANDOM_PUSH_TO_READ, PageNameConstants.RECOMMEND_PUSH_BOOK);
    }

    /**
     * 犹豫用户推书页点击加入书架
     */
    public static void randomPushClickAddShelf(long bookId, int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.RANDOM_PUSH, String.valueOf(modelId), FunPageStatsConstants.RANDOM_PUSH_CLICK_ADD_SHELF, PageNameConstants.RECOMMEND_PUSH_BOOK);
    }

    /**
     * 犹豫用户推书页点击换一换
     */
    public static void randomPushClickChange(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.RANDOM_PUSH, String.valueOf(modelId), FunPageStatsConstants.RANDOM_PUSH_CLICK_CHANGE, PageNameConstants.RECOMMEND_PUSH_BOOK);
    }

    /**
     * 意见反馈入口点击
     */
    public static void userFeedBackClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, String.valueOf(0), FunPageStatsConstants.FEEDB, "");
    }

    /**
     * 设置入口点击
     */
    public static void settingClick() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.MINE, String.valueOf(0), FunPageStatsConstants.SETT, "");
    }

    /**
     * 书架推荐书籍开关
     */
    public static void switchBookshelfReco(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.MINE, PageNameConstants.SETTING, String.valueOf(modelId), FunPageStatsConstants.SETTBSR, "");
    }

    /**
     * 书籍详情标签检索页书籍点击
     */
    public static void bookDetailTaglClick(long bookId, String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.TAG_BOOKLIST, modelId, FunPageStatsConstants.TAGL, "");
    }

    /**
     * 书籍详情标签检索页书籍曝光
     */
    public static void bookDetailEptaglClick(long bookId, String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.TAG_BOOKLIST, modelId, FunPageStatsConstants.EPTAGL, "");
    }

    /**
     * 忽略电池优化
     */
    public static void settingIgnorBattery() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.MINE, PageNameConstants.SETTING, "0", FunPageStatsConstants.IGNOR_BATTERY_SUCCESS, "");
    }

    /**
     * 点击设置白名单
     */
    public static void settingWhileList() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, PageNameConstants.MINE, PageNameConstants.SETTING, "0", FunPageStatsConstants.WHILE_LIST_CLICK, "");
    }

    /**
     * 新用户选择性别
     */
    public static void newUserSelectSex(String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, FunPageStatsConstants.MINE_SHOW, PageNameConstants.SET_SEX, modelId, FunPageStatsConstants.SEXREAD, "");
    }

    /**
     * 阅读器点击切换行距
     */
    public static void changeTextLength(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER, modelId, FunPageStatsConstants.CHANGE_TEXT_LENGTH, source);
    }

    /**
     * 阅读器点击切换息屏时间
     */
    public static void changeOffTime(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER_SETTING, modelId, FunPageStatsConstants.CHANGE_OFF_TIME, source);
    }

    /**
     * 阅读器加载失败点击重试
     */
    public static void loadFailRetry(long bookId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.READER, "0", FunPageStatsConstants.LOAD_FAIL_RETRY, source);
    }

    /**
     * 阅读器加载失败
     */
    public static void loadFail(long bookId, String modelId, String source, String field1) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.READER, modelId, FunPageStatsConstants.LOAD_READ_FAIL, source, field1);
    }

    /**
     * 阅读器加载失败点击返回按钮
     */
    public static void loadFailBack(long bookId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.READER, "0", FunPageStatsConstants.LOAD_FAIL_BACK, source);
    }

    /**
     * 书籍详情阅读器点击下载
     */
    public static void bookDetailReadDownload(String currPageId, long bookId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, "", currPageId, 0, FunPageStatsConstants.BOOK_DETAIL_READ, source);
    }

    /**
     * 下载页点击下载
     */
    public static void downloadChapter(String prePageId, long bookId, String modelId, String source, String chapterNum, String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, prePageId, currPageId, modelId, FunPageStatsConstants.DOWNLOAD_CLICK, source, chapterNum);
    }

    /**
     * 书城页点击试读
     */
    public static void bookCityTryRead(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.BOOK_CITY, String.valueOf(modelId), FunPageStatsConstants.BOOK_CITY_TRY_READ, source);
    }

    /**
     * 悬浮广告曝光
     */
    public static void floatAdExpose(long bookId, int modelId, String currPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", currPageId, String.valueOf(modelId), FunPageStatsConstants.FLOAT_AD_EXPOSE, source);
    }

    /**
     * 悬浮广告点击
     */
    public static void floatAdClick(long bookId, int modelId, String currPageId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", currPageId, String.valueOf(modelId), FunPageStatsConstants.FLOAT_AD_CLICK, source);
    }

    /**
     * 精品页书籍曝光
     */
    public static void boutiqueExpose(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_SELECT, String.valueOf(0), FunPageStatsConstants.BOUTIQUE_EXPOSE, PageNameConstants.BOOK_CITY_SELECT_RECOMMEND);
    }

    /**
     * 精品页书籍点击
     */
    public static void boutiqueClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_SELECT, "0", FunPageStatsConstants.BOUTIQUE_CLICK, PageNameConstants.BOOK_CITY_SELECT_RECOMMEND);
    }

    /**
     * 新书页书籍曝光
     */
    public static void newBookExpose(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_NEW, String.valueOf(0), FunPageStatsConstants.NEW_BOOK_EXPOSE, PageNameConstants.BOOK_CITY_NEW_RECOMMEND);
    }

    /**
     * 新书页书籍点击
     */
    public static void newBookClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_NEW, "0", FunPageStatsConstants.NEW_BOOK_CLICK, PageNameConstants.BOOK_CITY_NEW_RECOMMEND);
    }

    /**
     * 完结页书籍曝光
     */
    public static void finishExpose(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_FINISH, String.valueOf(0), FunPageStatsConstants.FINISH_EXPOSE, PageNameConstants.BOOK_CITY_FINISH_RECOMMEND);
    }

    /**
     * 完结页书籍点击
     */
    public static void finishClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.BOOKSTORE_FINISH, String.valueOf(0), FunPageStatsConstants.FINISH_CLICK, PageNameConstants.BOOK_CITY_FINISH_RECOMMEND);
    }

    /**
     * 进入发现页
     */
    public static void intoDiscover(int modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", "", String.valueOf(modelId), FunPageStatsConstants.INTO_DISCOVER_PAGE, "");
    }

    /**
     * 附近书友在读-书籍点击
     */
    public static void nearBookClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.NEARREAD, String.valueOf(0), FunPageStatsConstants.NEAR_CLICK, PageNameConstants.NEAR_READ_BOOK);
    }

    /**
     * 附近书友在读-进入阅读器
     */
    public static void nearGoReadClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.NEARREAD, String.valueOf(0), FunPageStatsConstants.NEARREAD_CLICK, PageNameConstants.NEAR_READ_BOOK);
    }

    /**
     * 附近书友在读-加书架
     */
    public static void nearAddshelfClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.NEARREAD, String.valueOf(0), FunPageStatsConstants.NEARADD_CLICK, PageNameConstants.NEAR_READ_BOOK);
    }

    /**
     * 书单点击
     */
    public static void nearBookListClick(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.NEARREAD, "1", FunPageStatsConstants.BOOKLIST_CLICK, PageNameConstants.BOOK_LIST_RECOMMAND);
    }

    /**
     * 书单曝光
     */
    public static void nearBookListExp(String bookListId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.NEARREAD, "1", FunPageStatsConstants.BOOK_LIST_EXPOSE, PageNameConstants.BOOK_LIST_RECOMMAND, bookListId);
    }

    /**
     * 附近人在读书籍曝光
     */
    public static void nearReadBookExp(long bookId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookId, "", PageNameConstants.NEARREAD, "0", FunPageStatsConstants.NEAR_READER_EXPOSE, PageNameConstants.NEAR_READ_BOOK);
    }

    /**
     * 通知权限弹框弹出
     */
    public static void notificationPermissionOut(String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", currPageId, "0", FunPageStatsConstants.NOTIFY_PERMISSION_DIALOG_OUT, "");
    }

    /**
     * 通知权限弹框点击立即开启
     */
    public static void notificationPermissionClick(String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", currPageId, "0", FunPageStatsConstants.NOTIFY_PERMISSION_DIALOG_CLICK, "");
    }

    /**
     * 切换翻页方式
     */
    public static void changeTurnPageType(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER_SETTING, modelId, FunPageStatsConstants.CHANGE_TURN_PAGE_TYPE, source);
    }

    /**
     * 音量键翻页开关
     */
    public static void voiceTurnKey(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER_SETTING, modelId, FunPageStatsConstants.VOICE_PAGE_TURN, source);
    }

    /**
     * 阅读器更多设置点击
     */
    public static void readMoreSetting(int modelId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.READER, modelId, FunPageStatsConstants.READ_MORE_SETTING, source);
    }

    /**
     * 阅读器渲染失败
     * NOW_X
     */
    public static void readLoadFail(long bookId, String source) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(bookId, "", PageNameConstants.READER, 0, FunPageStatsConstants.READ_LOAD_FAIL, source);
    }

    /**
     * 更新弹框曝光
     */
    public static void updateDialogExpose() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_DIALOG_POP, "");
    }

    /**
     * 更新弹框点击立即更新
     */
    public static void updateDialogClickOk() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_DIALOG_CLICK_OK, "");
    }

    /**
     * 更新弹框点击取消
     */
    public static void updateDialogClickCancel() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_DIALOG_CANCEL, "");
    }

    /**
     * 更新包开始下载
     */
    public static void updateDialogstartDownload() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_START_DOWNLOAD, "");
    }

    /**
     * 更新包下载完成
     */
    public static void updateCompleteDownload() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_COMPLETE_DOWNLOAD, "");
    }

    /**
     * 更新包开始安装
     */
    public static void updateStartInstall() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_START_INSTALL, "");
    }

    /**
     * 更新后首次启动
     */
    public static void updateFirstStart() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.UPDATE_FIRST_START, "");
    }

    /**
     * 热更新包开始下载
     */
    public static void hotUpdateStartDownload() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.HOT_UPDATE_START_DOWNLOAD, "");
    }

    /**
     * 热更新包下载完成
     */
    public static void hotUpdateCompleteDownload() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.HOT_UPDATE_COMPLETE_DOWNLOAD, "");
    }

    /**
     * 热更新包生效
     */
    public static void hotUpdateSuccess() {
        PageStatsUploadMgr.getInstance().uploadUpdateFuncStatsNow(0, "", "", 0, FunPageStatsConstants.HOT_UPDATE_SUCCESS, "");
    }

    /**
     * 搜索作者作品页面书籍曝光
     */
    public static void searchAuthResultList(long bookid, String prev) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookid, prev, FunPageStatsConstants.SEARCH_AUTH_RESULT_X, "25", FunPageStatsConstants.SEARCH_AUTH_RESULT, "");
    }

    /**
     * 搜索结果书籍曝光
     */
    public static void searchResultList(long bookid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookid, "", FunPageStatsConstants.SEARCH_RESULT, "0", FunPageStatsConstants.EP_SEARCH_RESULT, "");
    }

    /**
     * 搜索结果推荐书籍曝光
     */
    public static void searchRecommendResultList(long bookid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookid, FunPageStatsConstants.SEARCH_SHOW, FunPageStatsConstants.SEARCH_RESULT, "0", FunPageStatsConstants.EP_S8, "");
    }

    /**
     * 搜索结果推荐书籍曝光
     */
    public static void searchRecommendResultClickList(long bookid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(bookid, FunPageStatsConstants.SEARCH_SHOW, FunPageStatsConstants.SEARCH_RESULT, "0", FunPageStatsConstants.C_S8, "");
    }

    /**
     * 退出弹窗曝光: 1、退出应用；2、触发广告；3、回到当前页
     */
    public static void showExitDialog(String currPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", currPageId, "", FunPageStatsConstants.SHOW_EXIT_DIALOG, "");
    }

    /**
     * 退出弹窗的退出按钮被点击: 1、退出应用；2、触发广告；3、回到当前页
     */
    public static void showExitClick(String currPageId, String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", currPageId, modelId, FunPageStatsConstants.EXIT_DIALOG_CLICK, "");
    }

    /**
     * 疲劳弹框曝光
     */
    public static void showTiredDialog(String prevPageId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, "", FunPageStatsConstants.SHOW_TIRED_DIALOG, "");
    }

    /**
     * 疲劳弹框点击：1、触发广告； 2、继续阅读
     */
    public static void tiredDialogClick(String prevPageId, String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, prevPageId, PageNameConstants.READER, modelId, FunPageStatsConstants.CLICK_TIRED_DIALOG, "");
    }

    /**
     * 奖励到账弹框曝光
     */
    public static void showRewardDialog() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.READER, "", FunPageStatsConstants.EPREWARD, "");
    }

    /**
     * 奖励到账弹框点击
     *
     * @param modelId :1、触发广告； 2、继续阅读
     */
    public static void clickRewardDialog(String modelId) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(0, "", PageNameConstants.READER, modelId, FunPageStatsConstants.C_REWARD, "");
    }

    /**
     * 金立手机外拉活
     */
    public static void pullAlive() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.LIVE, 0, FunPageStatsConstants.PULL_ALIVE, "");
    }

    /**
     * 金立手机外拉活
     */
    public static void pullOutAlive() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.LIVE, 0, FunPageStatsConstants.PULL_OUT_ALIVE, "");
    }

    /**
     * 金立手机拉活进入APP
     */
    public static void pullAliveActivity() {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", PageNameConstants.LIVE, 0, FunPageStatsConstants.PULL_ACTIVITY, "");
    }
    public static void pullNotification(int mid,int bookid) {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(mid, "", "", String.valueOf(bookid), FunPageStatsConstants.EPNOTIFB, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
    }



    /**
     * 第一次启动, 显示口味选择页面.
     */
    public static void showTastePage()
    {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", "", "", FunPageStatsConstants.SHOW_TASTE_PAGE, "");
    }

    /**
     * 第一次启动, 设置口味.
     */
    public static void setTaste(int sex)
    {
        PageStatsUploadMgr.getInstance().uploadFuncStatsNow(0, "", "", "", FunPageStatsConstants.SET_TASTE, String.valueOf(sex));
    }
}
