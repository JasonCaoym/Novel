package com.duoyue.app.presenter;

import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.BookNewBookInfoBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.data.request.bookcity.BookSiteListReq;
import com.duoyue.app.common.data.request.bookcity.DayRecommendBookBean;
import com.duoyue.app.common.data.request.bookcity.MineReq;
import com.duoyue.app.common.data.request.bookshelf.DayRecommendBookReq;
import com.duoyue.app.common.data.response.bookshelf.BookShelfBookInfoResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfListResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfRecoInfoResp;
import com.duoyue.app.common.mgr.BookShelfMgr;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.ui.view.BookShelfView;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.cache.NumberParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.cache.StringParser;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;
import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean;
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadManager;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.rx.MtSchedulers;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 书架
 *
 * @author caoym
 * @data 2019/4/11  11:13
 */
public class BookShelfPresenter {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookShelfPresenter";

    /**
     * 当前类对象
     */
    private static BookShelfPresenter sInstance;

    /**
     * 周阅读时长.
     */
    private RamCache<Long> mWeekReadTimeCache;

    /**
     * 总阅读时长.
     */
    private RamCache<Long> mTotalReadTimeCache;

    /**
     * 最新拉取章节数.
     */
    private RamCache<String> mPullChapterCache;

    /**
     * 构造方法
     */
    private BookShelfPresenter() {
        //最新拉取章节数.
        mPullChapterCache = new RamCache(new File(BaseContext.getContext().getFilesDir(), "novel/app/pull_chapter.dat"), new StringParser());
        //周阅读时长.
        mWeekReadTimeCache = new RamCache(new File(BaseContext.getContext().getFilesDir(), "novel/app/w_read_time.dat"), new NumberParser());
        //总阅读时长.
        mTotalReadTimeCache = new RamCache(new File(BaseContext.getContext().getFilesDir(), "novel/app/t_read_time.dat"), new NumberParser());
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance() {
        if (sInstance == null) {
            synchronized (BookShelfPresenter.class) {
                if (sInstance == null) {
                    sInstance = new BookShelfPresenter();
                }
            }
        }
    }

    /**
     * 添加阅读历史记录书籍到书架.
     *
     * @param recordBean
     * @return
     */
    public static String addBookShelf(BookRecordBean recordBean) {
        //调用添加书架接口.
        return addBookShelf(new BookShelfBean(recordBean.bookId, recordBean.bookName,
                recordBean.bookCover, recordBean.resume, recordBean.chapterCount,
                recordBean.wordCount, recordBean.isFinish, recordBean.updateTime,
                0, TimeTool.currentTimeMillis(), recordBean.author, recordBean.seqNum, recordBean.getLastRead()));
    }

    public static String addBookShelf(BookDetailBean bookDetailBean) {
        //查询阅读历史记录.
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(bookDetailBean.getBookId());
        BookShelfBean shelfBean = new BookShelfBean(bookDetailBean.getBookId(), bookDetailBean.getBookName(),
                bookDetailBean.getCover(), bookDetailBean.getResume(), bookDetailBean.getLastChapter(),
                bookDetailBean.getWordCount(), bookDetailBean.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), bookDetailBean.getAuthorName(), bookRecordBean != null ? bookRecordBean.seqNum : 0,
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    public static String addBookShelf(BookShelfBookInfoResp bookInfoResp) {
        //查询阅读历史记录.
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookInfoResp.getBookId()));
        BookShelfBean shelfBean = new BookShelfBean(String.valueOf(bookInfoResp.getBookId()), bookInfoResp.getBookName(),
                bookInfoResp.getBookCover(), "", bookInfoResp.getLastChapter(),
                0, bookInfoResp.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), null, bookRecordBean != null ? bookRecordBean.seqNum : 0,
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    /**
     * 口令弹框添加书架
     *
     * @param recommandBean
     * @return
     */
    public static String addBookShelf(RecommandBean recommandBean) {
        //查询阅读历史记录.
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(recommandBean.getBookId()));
        BookShelfBean shelfBean = new BookShelfBean(String.valueOf(recommandBean.getBookId()), recommandBean.getBookName(),
                recommandBean.getCover(), "", recommandBean.getLastChapter(),
                0, recommandBean.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), recommandBean.getAuthorName(), recommandBean.getLastReadChapter(),
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    /**
     * 随机推书添加书架
     *
     * @param bookBean
     * @return
     */
    public static String addBookShelf(RandomPushBean.BookBean bookBean) {
        //查询阅读历史记录.
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookBean.getBookId()));
        BookShelfBean shelfBean = new BookShelfBean(String.valueOf(bookBean.getBookId()), bookBean.getBookName(),
                bookBean.getCover(), "", bookBean.getLastChapter(),
                0, bookBean.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), bookBean.getAuthorName(), bookRecordBean != null ? bookRecordBean.seqNum : 0,
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    /**
     * 发现页面添加书架
     *
     * @param bookBean
     * @return
     */
    public static String addFindBookShelf(BookNewBookInfoBean bookBean) {
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookBean.getBookId()));
        BookShelfBean shelfBean = new BookShelfBean(String.valueOf(bookBean.getBookId()), bookBean.getName(),
                bookBean.getCover(), "", bookBean.getLastChapter(),
                bookBean.getWordCount(), bookBean.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), bookBean.getAuthorName(), bookRecordBean != null ? bookRecordBean.seqNum : 0,
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    /**
     * H5书单列表添加书架
     *
     * @param bookBean
     * @return
     */
    public static String addBookListShelf(BookNewBookInfoBean bookBean) {
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookBean.getBookId()));
        BookShelfBean shelfBean = new BookShelfBean(String.valueOf(bookBean.getBookId()), bookBean.getName(),
                bookBean.getCover(), "", bookBean.getLastChapter(),
                bookBean.getWordCount(), bookBean.getState() == 2, System.currentTimeMillis(),
                0, System.currentTimeMillis(), bookBean.getAuthorName(), bookRecordBean != null ? bookRecordBean.seqNum : 0,
                bookRecordBean == null ? 0 : bookRecordBean.getLastRead());
        return addBookShelf(shelfBean);
    }

    /**
     * 添加书籍到书架.
     *
     * @param bookShelfBean
     * @return
     */
    public static String addBookShelf(BookShelfBean bookShelfBean) {
        //调用添加书架接口.
        String result = BookShelfMgr.addBookShelf(bookShelfBean);
        if (ReadHistoryMgr.HTTP_OK.equals(result)) {
            //添加到书架成功.
            BookShelfHelper.getsInstance().saveBookWithAsync(bookShelfBean);
        }
        return result;
    }


    /**
     * 书籍是否已经存在书架
     *
     * @param bookId
     * @return
     */
    public static boolean isAdded(String bookId) {
        return BookShelfHelper.getsInstance().findBookById(bookId) != null;
    }

    /**
     * 删除书架书籍信息
     *
     * @param bookShelfBookInfoList
     * @return
     */
    public static String removeBookList(final List<BookShelfBookInfoResp> bookShelfBookInfoList) {
        //获取书架书籍信息列表.
        String msg = BookShelfMgr.removeBooksShelf(bookShelfBookInfoList);
        if (msg.equalsIgnoreCase(BookShelfMgr.HTTP_OK)) {
            //移除书架成功.
            List<BookShelfBean> bookBeanList = new ArrayList<>();
            for (BookShelfBookInfoResp bookInfo : bookShelfBookInfoList) {
                bookBeanList.add(bookInfo.toBookShelfBean());
            }
            //调用删除本地数据书籍信息.
            BookShelfHelper.getsInstance().removeBook(bookBeanList);

            //批量删除本地缓存
            BookDownloadManager.getsInstance().removeBookCacheList(bookBeanList);
        }
        return msg;
    }

    /**
     * 查询添加到书架的书籍Id列表.
     *
     * @return
     */
    public static List<String> getBookShelfBookIdList() {
        List<String> bookIdList = null;
        //查询出所有的书架书籍信息.
        List<BookShelfBean> bookShelfBeanList = BookShelfHelper.getsInstance().findAllBooks();
        if (bookShelfBeanList != null && !bookShelfBeanList.isEmpty()) {
            bookIdList = new ArrayList<>();
            for (BookShelfBean bookShelfBean : bookShelfBeanList) {
                if (bookShelfBean == null) {
                    continue;
                }
                bookIdList.add(bookShelfBean.getBookId());
            }
        }
        return bookIdList;
    }

    /**
     * 查询添加到书架的书籍列表.
     *
     * @return
     */
    public static List<BookShelfBean> getBookShelfBookList() {
        //查询出所有的书架书籍信息.
        List<BookShelfBean> bookShelfBeanList = BookShelfHelper.getsInstance().findAllBooks();
        return bookShelfBeanList;
    }

    /**
     * 获取页面书籍数据列表
     */
    public static void getPageBookDataList(final BookCallback callback) {
        Single.fromCallable(new Callable<BookShelfListResp>() {
            @Override
            public BookShelfListResp call() throws Exception {
                //获取书架书籍信息列表.
                BookShelfListResp bookShelfListResp = BookShelfMgr.getBookShelfDataList();
                if (bookShelfListResp == null) {
                    bookShelfListResp = new BookShelfListResp(0);
                }
                return bookShelfListResp;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<BookShelfListResp>() {
            @Override
            public void accept(BookShelfListResp bookShelfListResp) throws Exception {
                if (bookShelfListResp != null && callback != null) {
                    callback.onPullBookData(bookShelfListResp);
                }
            }
        });
    }

    /**
     * 修改周阅读时长.
     *
     * @param isIncrement  是否递增.
     * @param weekReadTime 周阅读时长.
     */
    public synchronized static void updateWeekReadTime(boolean isIncrement, long weekReadTime) {
        try {
            //创建当前类对象.
            createInstance();
            //获取本地总阅读时长.
            long locaWeekReadTime = sInstance.mWeekReadTimeCache.get(0L);
            locaWeekReadTime = locaWeekReadTime >= 0 ? locaWeekReadTime : 0;
            if (isIncrement) {
                //递增1
                sInstance.mWeekReadTimeCache.set(locaWeekReadTime + 1);
            } else {
                //查询出当日未上报的阅读时长.
                int readingTime = FunctionStatsApi.getCurrDayReadingTime();
                sInstance.mWeekReadTimeCache.set(weekReadTime + readingTime);
            }
            Logger.i(TAG, "updateWeekReadTime: {}, {}, {}, {}", isIncrement, weekReadTime, locaWeekReadTime, sInstance.mWeekReadTimeCache.get(0L));
        } catch (Throwable throwable) {
            Logger.e(TAG, "updateWeekReadTime: {}", throwable);
        }
    }

    /**
     * 读取周阅读时长.
     */
    public static long getWeekReadTime() {
        //创建当前类对象.
        createInstance();
        return sInstance.mWeekReadTimeCache != null ? sInstance.mWeekReadTimeCache.get(0L) : 0;
    }

    /**
     * 修改总阅读时长.
     *
     * @param isIncrement   是否递增.
     * @param totalReadTime 总阅读时长.
     */
    public synchronized static void updateTotalReadTime(boolean isIncrement, long totalReadTime) {
        try {
            //创建当前类对象.
            createInstance();
            //获取本地总阅读时长.
            long locaTotalReadTime = sInstance.mTotalReadTimeCache.get(0L);
            locaTotalReadTime = locaTotalReadTime >= 0 ? locaTotalReadTime : 0;
            if (isIncrement) {
                //递增1
                sInstance.mTotalReadTimeCache.set(locaTotalReadTime + 1);
            } else {
                //查询出所有未上报的阅读时长.
                int readingTime = FunctionStatsApi.getTotalReadingTime();
                sInstance.mTotalReadTimeCache.set(totalReadTime + readingTime);
            }
            Logger.i(TAG, "updateTotalReadTime: {}, {}, {}, {}", isIncrement, totalReadTime, locaTotalReadTime, sInstance.mTotalReadTimeCache.get(0L));
        } catch (Throwable throwable) {
            Logger.e(TAG, "updateTotalReadTime: {}", throwable);
        }
    }

    /**
     * 读取总阅读时长.
     */
    public static long getTotalReadTime() {
        //创建当前类对象.
        createInstance();
        return sInstance.mTotalReadTimeCache != null ? sInstance.mTotalReadTimeCache.get(0L) : 0;
    }

    /**
     * 更新拉取最新章节数(点击更新状态的书籍时调用)
     *
     * @param bookId      书籍Id
     * @param bookChapter 章节数
     */
    public static void updatePullChapter(String bookId, int bookChapter) {
        //创建当前类对象.
        createInstance();
        try {
            //获取拉取章节数信息.
            String pullChapter = sInstance.mPullChapterCache.get("");
            JSONObject pullChapterJSONObj = StringFormat.isEmpty(pullChapter) ? new JSONObject() : new JSONObject(pullChapter);
            //添加章节数.
            pullChapterJSONObj.put(bookId, bookChapter);
            sInstance.mPullChapterCache.set(pullChapterJSONObj.toString());
            Logger.i(TAG, "updatePullChapter: {}, {}, {}", bookId, bookChapter, pullChapterJSONObj);
        } catch (Throwable throwable) {
            Logger.e(TAG, "updatePullChapter: {}, {}, {}", bookId, bookChapter, throwable);
        }
    }

    /**
     * 获取拉取到的章节数.
     *
     * @return
     */
    public static JSONObject getPullChapter() {
        //创建当前类对象.
        createInstance();
        try {
            //获取拉取章节数信息.
            String pullChapter = sInstance.mPullChapterCache.get("");
            if (StringFormat.isEmpty(pullChapter)) {
                return null;
            }
            return new JSONObject(pullChapter);
        } catch (Throwable throwable) {
            Logger.e(TAG, "getPullChapter: {}", throwable);
            return null;
        }
    }

    /**
     * 移除本地记录的拉取章节数信息
     *
     * @param bookId
     */
    public static void removePullChapter(String bookId) {
        //创建当前类对象.
        createInstance();
        try {
            //获取拉取章节数信息.
            String pullChapter = sInstance.mPullChapterCache.get("");
            if (StringFormat.isEmpty(pullChapter)) {
                return;
            }
            JSONObject pullChapterJSONObj = new JSONObject(pullChapter);
            //添加章节数.
            pullChapterJSONObj.remove(bookId);
            sInstance.mPullChapterCache.set(pullChapterJSONObj.toString());
            Logger.i(TAG, "removePullChapter: {}, {}", bookId, pullChapterJSONObj);
        } catch (Throwable throwable) {
            Logger.e(TAG, "removePullChapter: {}, {}, {}", bookId, throwable);
        }
    }

    /**
     * 书籍置顶
     */
    public static String toppingBook(BookShelfBookInfoResp bookInfoResp) {

        //调用添加书架接口.
        String result = BookShelfMgr.toppingBookShelf(bookInfoResp, 1, "置顶");
        if (BookShelfMgr.HTTP_OK.equals(result)) {
            bookInfoResp.setToppingTime(System.currentTimeMillis());
            //如果没有在书架中，则添加到书架
            if (!isAdded(String.valueOf(bookInfoResp.getBookId()))) {
                //添加到书架
                BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookInfoResp.getBookId()));
                BookShelfBean shelfBean = new BookShelfBean(String.valueOf(bookInfoResp.getBookId()), bookInfoResp.getBookName(),
                        bookInfoResp.getBookCover(), "", bookInfoResp.getLastChapter(),
                        0, bookInfoResp.getState() == 2, System.currentTimeMillis(),
                        0, System.currentTimeMillis(), null, bookRecordBean != null ? bookRecordBean.seqNum : 0,
                        bookRecordBean != null ? bookRecordBean.getLastRead() : 0);
                BookShelfHelper.getsInstance().toppingBookWithAsync(shelfBean);
            }
        }
        return result;

    }

    /**
     * 取消置顶
     */
    public static String cancelToppingBook(final BookShelfBookInfoResp bookInfoResp) {

        //调用添加书架接口.
        String result = BookShelfMgr.toppingBookShelf(bookInfoResp, 2, "取消置顶");
        if (BookShelfMgr.HTTP_OK.equals(result)) {
            bookInfoResp.setToppingTime(0);
        }
        return result;

    }

    /**
     * 获取书架顶部每日推荐书籍
     *
     * @return
     */
    public static void getBookShelfRecoInfoResp(final DayRecommendBookCallback callback) {
//        Single.fromCallable(new Callable<BookShelfRecoInfoResp>() {
//            @Override
//            public BookShelfRecoInfoResp call() throws Exception {
//                BookShelfRecoInfoResp bookShelfRecoInfoResp = BookShelfMgr.getBookShelfRecoInfoResp();
//                return bookShelfRecoInfoResp;
//            }
//        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<BookShelfRecoInfoResp>() {
//            @Override
//            public void accept(BookShelfRecoInfoResp bookShelfRecoInfoResp) throws Exception {
//                if (callback != null && bookShelfRecoInfoResp != null) {
//                    callback.onDayRecommendBook(bookShelfRecoInfoResp);
//                }
//            }
//        });


        DayRecommendBookReq bookShelfRecoInfoResp = new DayRecommendBookReq();
        DisposableObserver getGetDetailDisposable = new DisposableObserver<JsonResponse<DayRecommendBookBean>>() {
            @Override
            public void onNext(JsonResponse<DayRecommendBookBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    if (callback != null) {
                        callback.onDayRecommendBook(jsonResponse.data.getRecommendBookList());
                    }
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<DayRecommendBookBean>()
                .setRequest(bookShelfRecoInfoResp)
                .setResponseType(DayRecommendBookBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(getGetDetailDisposable);
    }

    /**
     * 获取签到状态信息
     */
    public static void requestSignState(final BookShelfView bookShelfView) {

        DisposableObserver<JsonResponse<SignBean>> disposableObserver = new DisposableObserver<JsonResponse<SignBean>>() {
            @Override
            public void onNext(JsonResponse<SignBean> signBeanJsonResponse) {
                if (signBeanJsonResponse.status == 1 && signBeanJsonResponse.data != null) {
                    DataCacheManager.getInstance().setSignBean(signBeanJsonResponse.data);
                    bookShelfView.signSuccess(signBeanJsonResponse.data);
                } else {
                    bookShelfView.signEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                bookShelfView.signError();
            }

            @Override
            public void onComplete() {

            }
        };
        MineReq mineReq = new MineReq();

        new JsonPost.AsyncPost<SignBean>()
                .setRequest(mineReq)
                .setResponseType(SignBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(disposableObserver);
    }

    public static void loadBookSiteList(int site, int chan, final loadSiteDataListener loadSiteDataListener) {
        BookSiteListReq bookSiteListReq = new BookSiteListReq();
        bookSiteListReq.site = site;
        bookSiteListReq.chan = chan;

        new JsonPost.AsyncPost<BookSiteBean>()
                .setRequest(bookSiteListReq)
                .setResponseType(BookSiteBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<BookSiteBean>>() {
                    @Override
                    public void onNext(JsonResponse<BookSiteBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            loadSiteDataListener.loadSiteData(jsonResponse.data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface BookCallback {
        /**
         * 拉取到书架信息列表.
         *
         * @param bookShelfListResp
         */
        void onPullBookData(BookShelfListResp bookShelfListResp);
    }

    public interface loadSiteDataListener {
        void loadSiteData(BookSiteBean bookBannerAdBean);
    }

    /**
     * 每日推荐图书回调接口
     */
    public interface DayRecommendBookCallback {
        void onDayRecommendBook(List<BookShelfRecoInfoResp> bookShelfRecoInfoResp);
    }
}
