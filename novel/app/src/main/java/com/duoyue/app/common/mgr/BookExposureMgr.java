package com.duoyue.app.common.mgr;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书籍曝光管理类.
 *
 * @author caoym
 * @data 2019/5/27  9:15
 */
public class BookExposureMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookExposureMgr";

    /**
     * 页面Id(比如:书城+精选、书城+男生), 自定义的.
     */
    public static final String PAGE_ID_KEY = "page_id";

    /**
     * 频道
     */
    public static final String PAGE_CHANNEL = "page_channel";

    /**
     * 分类Id
     */
    //public static final String CATEGORY_ID_KEY = "category_id";
    //====================页面Id(定义规则, 独立刷新数据)=======================
    /**
     * 主页-书城(xxx::->0:精选;1:男生;2:女生)
     */
    public static final String PAGE_ID_CITY = "h_city_";
    public static final String BOOK_CITY_RANK = "book_city_rank";
    public static final String BOOK_SHELF = "book_shelf";
    /**
     * 书城-分栏更多
     */
    public static final String PAGE_ID_CITY_MORE = "city_more";
    /**
     * 书籍详情标签检索页书籍曝光
     */
    public static final String PAGE_ID_TAG_BOOKLIST = "tag_booklist";

    /**
     * 二级分类书籍列表
     */
    public static final String PAGE_ID_CATEGORY = "category";

    /**
     * 分类排行榜
     */
    public static final String PAGE_ID_CATEGORY_RANK = "category_rank";

    /**
     * 书城排行榜
     */
    public static final String PAGE_ID_BOOK_CITY_RANK = "book_city_rank";

    /**
     * 详情页这本书的读者都在看书籍
     */
    public static final String PAGE_ID_DETAIL_READERS_LOOKING = "detail_readers_looking";

    /**
     * 详情页同类热门书籍
     */
    public static final String PAGE_ID_DETAIL_HOT = "detail_hot";

    /**
     * 书城精品页
     */
    public static final String BOOK_CITY_BOUTIQUE = "book_city_boutique";

    /**
     * 书城新书
     */
    public static final String BOOK_CITY_NEW_BOOK = "book_city_new_book";

    /**
     * 书城完结
     */
    public static final String BOOK_CITY_FINISH = "book_city_finish";

    /**
     * 搜索作者作品
     */
    public static final String SEARCH_AUTH_RESULT
            = "SEARCH_AUTH_RESULT";

    /**
     * 当前类对象.
     */
    private static BookExposureMgr sInstance;

    /**
     * 存放已上报过的BookId所属页面(例如:书城-精选-小编浪漫私藏;书城-男生-热门都市小说)
     * key:页面Id;value:来源列表
     */
    private Map<String, List<String>> mPageIdMap;

    /**
     * 存放分类已上报过的BookId列表
     * key:来源Id;value:BookId列表
     */
    private Map<String, List<Long>> mCategoryIdMap;

    /**
     * 屏幕高度.
     */
    private int mScreenHeight;
    private String prevPageId;
    private String source;
    private int rankId;
    // 保存书城排行榜书籍id
    private List<Long> books = new ArrayList<>();

    /**
     * 构造方法.
     */
    private BookExposureMgr() {
        mPageIdMap = new HashMap<>();
        mCategoryIdMap = new HashMap<>();
        //获取屏幕大小.
        int[] screentSize = PhoneUtil.getScreenSize(BaseContext.getContext());
        //屏幕高度.
        mScreenHeight = Math.max(screentSize[0], screentSize[1]);
    }

    /**
     * 创建当前类单例对象.
     */
    private synchronized static void createInstance() {
        if (sInstance == null) {
            synchronized (BookExposureMgr.class) {
                if (sInstance == null) {
                    sInstance = new BookExposureMgr();
                }
            }
        }
    }

    public static BookExposureMgr getInstance() {
        createInstance();
        return sInstance;
    }

    public void setStatiscParams(String prevPageId, String source) {
        this.prevPageId = prevPageId;
        this.source = source;
    }

    /**
     * 添加View可见监控.
     *
     * @param pageId     页面Id
     * @param categoryId 分类Id
     * @param view
     * @param bookId
     * @param bookName
     * @param type       : 兼容书城排行榜类型-1、人气榜; 2、飙升榜; 3、完结榜
     */
    public synchronized static void addOnGlobalLayoutListener(final String pageId, final String categoryId, final View view, final long bookId, final String bookName, final int type, final List<Long> books) {
        //Logger.i(TAG, "addOnGlobalLayoutListener: {}, {}, {}, {}, {}", pageId, categoryId, view, bookId, bookName);
        if (StringFormat.isEmpty(pageId) || StringFormat.isEmpty(categoryId)) {
            return;
        }
        //创建当前类对象.
        createInstance();
        if (books != null) {
            sInstance.rankId = type;
            sInstance.books.clear();
            sInstance.books.addAll(books);
        }
        //获取分类Key.
        String categoryKey = pageId + "@" + categoryId;
        //获取来源对应的BookId列表.
        List<Long> bookList = sInstance.mCategoryIdMap.get(categoryKey);
        if (bookList != null && bookList.contains(bookId)) {
            //已展示过.
            return;
        }
        if (bookList == null) {
            bookList = new ArrayList<>();
            sInstance.mCategoryIdMap.put(categoryKey, bookList);
        }
        bookList.add(bookId);
        //存储页面Id信息.
        if (!sInstance.mPageIdMap.containsKey(pageId)) {
            //未存储页面信息.
            List<String> categoryIdList = new ArrayList<>();
            categoryIdList.add(categoryKey);
            sInstance.mPageIdMap.put(pageId, categoryIdList);
        } else {
            sInstance.mPageIdMap.get(pageId).add(categoryKey);
        }
        //Logger.i(TAG, "addOnGlobalLayoutListener: {}, {}, {}, {}, {}", pageId, categoryId, view, bookId, bookName);
        //view加载完成时回调
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Logger.i(TAG, "onGlobalLayout: {}, {}, {}",  view, bookId, bookName);
                try {
                    if (sInstance == null || view == null) {
                        if (view != null) {
                            //移除View对应监听.
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        return;
                    }
                    //判断View是否在屏幕可见区域内.
                    if (sInstance.isVisibleArea(view, bookName, pageId.startsWith(PAGE_ID_CITY) || pageId.startsWith(PAGE_ID_DETAIL_READERS_LOOKING) || pageId.startsWith(PAGE_ID_DETAIL_HOT))) {
                        //移除View对应监听.
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //判断是否为书城分栏书籍曝光.
                        if (TextUtils.equals(categoryId, "rank")) {
                            for (long id : sInstance.books) {
                                FuncPageStatsApi.bookCityRankShow(id, sInstance.rankId);
                            }
                        } else if (pageId.startsWith(PAGE_ID_CITY)) {
                            //书城分栏书籍曝光.
                            FunctionStatsApi.cBookExposure(categoryId, bookId, bookName);
                            FuncPageStatsApi.bookCityBookShow(bookId, StringFormat.parseInt(categoryId, 0), PageNameConstants.SOURCE_CAROUSEL + " + " + StringFormat.parseInt(categoryId, 0) + " + " + type);
                        } else if (pageId.equalsIgnoreCase(PAGE_ID_CITY_MORE)) {
                            //书城分栏更多列表页书籍曝光.
                            FunctionStatsApi.cMoreBookExposure(categoryId, bookId, bookName);
                        } else if (pageId.equalsIgnoreCase(PAGE_ID_CATEGORY)) {
                            //二级分类列表页书籍曝光.
                            FunctionStatsApi.categoryBookExposure(categoryId, bookId, bookName);
                        } else if (pageId.equalsIgnoreCase(PAGE_ID_DETAIL_READERS_LOOKING)) {
                            //详情页这本书的读者都在看分类书籍曝光.
                            FunctionStatsApi.bdReadersLookingExposure(bookId, bookName);
                            FuncPageStatsApi.bookDetailSimilarShow(bookId, sInstance.prevPageId, sInstance.source, categoryId);
                        } else if (pageId.equalsIgnoreCase(PAGE_ID_DETAIL_HOT)) {
                            //详情页同类热门书籍曝光.
                            FunctionStatsApi.bdSimilarPopularExposure(bookId, bookName);
                            FuncPageStatsApi.bookDetailHotShow(bookId, sInstance.prevPageId, sInstance.source, categoryId);
                        } else if (pageId.equals(BookExposureMgr.BOOK_SHELF)) {//书架书籍曝光
                            FuncPageStatsApi.bookShelfRecomBookShow(bookId);
                        } else if (pageId.equals(BookExposureMgr.PAGE_ID_CATEGORY_RANK)) {
                            //分类页面排行榜书籍曝光
                            if (type == StartGuideMgr.SEX_MAN) {
                                FuncPageStatsApi.categoryRankBoyShow(bookId, StringFormat.parseInt(categoryId, 0));
                            } else if (type == StartGuideMgr.SEX_WOMAN) {
                                FuncPageStatsApi.categoryRankGirlShow(bookId, StringFormat.parseInt(categoryId, 0));
                            }
                        } else if (pageId.equals(BookExposureMgr.PAGE_ID_BOOK_CITY_RANK)) {
                            //书城页面排行榜书籍曝光
                            if (type == StartGuideMgr.SEX_MAN) {
                                FuncPageStatsApi.bookCityRankBoyShow(bookId, StringFormat.parseInt(categoryId, 0));
                            } else if (type == StartGuideMgr.SEX_WOMAN) {
                                FuncPageStatsApi.bookCityRankGirlShow(bookId, StringFormat.parseInt(categoryId, 0));
                            }
                        } else if (pageId.equals(BookExposureMgr.PAGE_ID_TAG_BOOKLIST)) {
                            //书城页面排行榜书籍曝光
                            FuncPageStatsApi.bookDetailEptaglClick(bookId, categoryId);

                        } else if (pageId.equals(BOOK_CITY_BOUTIQUE)) {//书城精品
                            FuncPageStatsApi.boutiqueExpose(bookId);

                        } else if (pageId.equals(BOOK_CITY_NEW_BOOK)) {//书城新书
                            FuncPageStatsApi.newBookExpose(bookId);

                        } else if (pageId.equals(BOOK_CITY_FINISH)) {//书城完结
                            FuncPageStatsApi.finishExpose(bookId);
                        } else if (pageId.equals(BookExposureMgr.SEARCH_AUTH_RESULT)) {//搜索作者作品书籍
                            FuncPageStatsApi.searchAuthResultList(bookId, categoryId);
                        } else if (pageId.equals(FunPageStatsConstants.EP_SEARCH_RESULT)) {
                            //搜索结果页曝光
                            FuncPageStatsApi.searchResultList(bookId);
                        } else if (pageId.equals(FunPageStatsConstants.NEAR_READER_EXPOSE)) {
                            //发现页面曝光
                            FuncPageStatsApi.nearReadBookExp(bookId);
                        } else {
                            Logger.e(TAG, "onGlobalLayout: {}, {}", pageId, categoryId);
                        }
                        //Logger.i(TAG, "onGlobalLayout: {}, {}, {}", bookName, "----OK----", view);
                        return;
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onGlobalLayout: {}", throwable);
                }
            }
        });
    }

    /**
     * 添加View可见监控.
     *
     * @param pageId     页面Id
     * @param categoryId 分类Id
     * @param view
     * @param bookId
     * @param bookName
     */
    public synchronized static void addOnGlobalLayoutListener(final String pageId, final String categoryId, final View view, final long bookId, final String bookName) {
        addOnGlobalLayoutListener(pageId, categoryId, view, bookId, bookName, 0, null);
    }

    /**
     * View是否在屏幕可见区域.
     *
     * @param view
     * @param bookName
     * @param isAll    是否显示全部才认为曝光(主页需要, 因为有Tab)
     * @return
     */
    private boolean isVisibleArea(View view, String bookName, boolean isAll) {
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }
        //此方式不适合书城(书城Item布局复杂程度高)
        //Rect rect = new Rect();
        //boolean cover = view.getGlobalVisibleRect(rect);
        //return cover && rect.height() >= (isAll ? view.getHeight() : view.getHeight() / 2);
        //获取在整个屏幕内的绝对坐标.
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        //Logger.i(TAG, "isVisibleArea: {}, {}, {}, {}, {}, {}", bookName, location[0], location[1], mScreenHeight, view.getHeight(), isAll);
        return location[1] > 0 && location[1] <= mScreenHeight - (isAll ? view.getHeight() : view.getHeight() / 2);
    }

    /**
     * 刷新数据列表
     *
     * @param pageId 页面Id
     */
    public synchronized static void refreshBookData(String pageId) {
        Logger.i(TAG, "refreshBookData: {}, {}", sInstance, pageId);
        if (StringFormat.isEmpty(pageId)) {
            return;
        }
        if (sInstance == null || sInstance.mPageIdMap == null || sInstance.mCategoryIdMap == null) {
            return;
        }
        try {
            //获取分类Key.
            List<String> categoryIdList = sInstance.mPageIdMap.get(pageId);
            if (StringFormat.isEmpty(categoryIdList)) {
                return;
            }
            sInstance.rankId = 0;
            sInstance.books.clear();
            //清除页面对应所有分类.
            sInstance.mPageIdMap.remove(pageId);
            //清除页面对应所有分类下已记录的书籍, 重新开始计算曝光.
            for (String categoryId : categoryIdList) {
                sInstance.mCategoryIdMap.remove(categoryId);
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "refreshBookData: {}, {}", pageId, throwable);
        }
    }
}
