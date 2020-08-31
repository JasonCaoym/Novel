package com.duoyue.mianfei.xiaoshuo.read.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.duoyue.app.bean.AllChapterDownloadBean;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.common.data.request.bookcity.BookDetailsReq;
import com.duoyue.app.common.data.request.bookdownload.ChapterDownloadOptionReq;
import com.duoyue.app.common.data.request.read.CatalogueReq;
import com.duoyue.app.common.data.request.read.ChapterContentReq;
import com.duoyue.app.common.data.request.read.ChapterErrorReq;
import com.duoyue.app.common.data.request.read.ReadTaskReq;
import com.duoyue.app.common.data.response.ReadTaskResp;
import com.duoyue.app.common.data.response.bookdownload.AllChapterDownloadResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.common.data.response.bookshelf.AddBookShelfResp;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.page.TxtChapter;
import com.duoyue.mianfei.xiaoshuo.read.presenter.view.IReadPage;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
import com.duoyue.mianfei.xiaoshuo.read.utils.*;
import com.duoyue.mod.stats.ErrorStatsApi;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.data.bean.ChapterUrlBean;
import com.zydm.base.data.dao.*;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;


public class ReadPresenter {

    private static final String TAG = "App#ReadPresenter";

    //滚动目录到顶部触发加载目录
    public static final int LOAD_CATALOGUE_TYPE_TOP_MORE = 0;
    //滚动目录到底部触发加载目录
    public static final int LOAD_CATALOGUE_TYPE_BOTTOM_MORE = 1;
    //点击倒序触发加载目录
    public static final int LOAD_CATALOGUE_TYPE_REVERSE = 2;
    //点击正序触发加载目录
    public static final int LOAD_CATALOGUE_TYPE_POSITIVE = 3;
    //首次进入触发加载用户上一次阅读的目录
    public static final int LOAD_CATALOGUE_TYPE_NORMAL = 4;
    //用户阅读时触发预加载目录
    public static final int LOAD_CATALOGUE_TYPE_PRE = 5;


    private IReadPage mIReadPage;

    private Context mContext;

    //仅用来标记获取章节内容是否成功
    private int chapter;
    private HashSet<Integer> mPreLoadingGroup = new HashSet<>();
    private DisposableObserver recommendDisposable;
    private DisposableObserver catalogueDisposable;
    private DisposableObserver catalogueListDisposable;
    private DisposableObserver mReadTaskDosposable;
    private DisposableObserver mChapterErrorDosposable;
    private DisposableObserver preLoadChaptersDosposable;

    private long startTime;
    private long endTime;

    private long loadTxtStartTime;
    private long loadTxtEndTime;

    public ReadPresenter(Context context, IReadPage mIReadPage) {
        this.mIReadPage = mIReadPage;
        mContext = context;
    }

    /**
     * 获取BookRecordBean
     *
     * @param bookId
     * @return
     */
    private BookRecordBean getBookRecordBean(String bookId, int targetSeqNum) {
        //查询阅读历史记录.
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(bookId);
        if (bookRecordBean == null) {
            //新阅读书籍, 添加阅读历史记录.
            BookDetailBean bookDetailBean = getBookDetailBean(bookId);
            bookRecordBean = bookDetailBean != null ? bookRecordBeanToBookRecord(bookDetailBean, targetSeqNum) : null;
        } else {
            //有历史阅读记录
            if (targetSeqNum != 0) {
                bookRecordBean.setSeqNum(targetSeqNum);
                bookRecordBean.setPagePos(0);
            }
        }

        if(bookRecordBean != null){
            BookShelfBean shelfBean = BookShelfHelper.getsInstance().findBookById(bookRecordBean.getBookId());
            bookRecordBean.mIsInShelf = shelfBean != null;
        }

        return bookRecordBean;
    }

    /**
     * 如果没有历史记录，获取BookDetailBean转换成BookRecordBean
     */
    public BookDetailBean getBookDetailBean(String bookId) {

        BookDetailBean detailBean = null;

        if (PhoneUtil.isNetworkAvailable(mContext)) {
            //有网络，使用网络接口获得BookDetailBean对象
            BookDetailsReq request = new BookDetailsReq();
            request.setBookId(StringFormat.parseLong(bookId, 0));
            try {
                JsonResponse<BookDetailBean> response = new JsonPost.SyncPost<BookDetailBean>()
                        .setRequest(request)
                        .setResponseType(BookDetailBean.class)
                        .post();
                if (response != null && response.data != null) {
                    detailBean = response.data;
                    //拿到BookDetailBean对象以后，缓存到本地
                    BookSaveUtils.saveBookDetailBean(String.valueOf(bookId), detailBean);
                } else
                {
                    ErrorStatsApi.addError(ErrorStatsApi.LOAD_DETAIL_FAIL, "ReadPresenter.getBookDetailBean(response:" + (response != null ? "response.data is null" : "response is null") + ")");
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                ErrorStatsApi.addError(ErrorStatsApi.LOAD_DETAIL_FAIL, "ReadPresenter.getBookDetailBean(Throwable, " + Logger.getStackTraceString(throwable) + ")");
            }
        } else {
            //无网络，从缓存中获取BookDetailBean对象
            if (BookSaveUtils.isCached(bookId, BookSaveUtils.BOOK_DETAIL_BEAN)) {
                detailBean = BookSaveUtils.getCacheBookDetailBean(bookId);
            }
        }
        return detailBean;
    }

    /**
     * 将BookDetailBean转换成BookRecordBean
     */
    private BookRecordBean bookRecordBeanToBookRecord(BookDetailBean detailBean, int targetSeqNum) {
        BookRecordBean recordBean = new BookRecordBean();
        recordBean.bookId = detailBean.getBookId();
        recordBean.bookName = detailBean.getBookName();
        recordBean.author = detailBean.getAuthorName();
        recordBean.resume = detailBean.getResume();
        recordBean.bookCover = detailBean.getCover();
        recordBean.chapterCount = detailBean.getLastChapter();
        recordBean.wordCount = detailBean.getWordCount();
        recordBean.isFinish = detailBean.getState() == 2;
        recordBean.setSeqNum(targetSeqNum == 0 ? 1 : targetSeqNum);
        recordBean.setPagePos(0);
        return recordBean;
    }

    public void loadRecordedChaptersGroup(final String bookId, final int targetSeqNum, final String sourceStats) {
        startTime = System.currentTimeMillis();
        Single.fromCallable(new Callable<BookRecordBean>() {
            @Override
            public BookRecordBean call() throws Exception {
                //查询阅读历史记录.
                BookRecordBean bookRecordBean = getBookRecordBean(bookId, targetSeqNum);
                return bookRecordBean;
            }
        }).flatMap(new Function<BookRecordBean, SingleSource<List<ChapterListBean>>>() {
            @Override
            public SingleSource<List<ChapterListBean>> apply(final BookRecordBean recordBean) throws Exception {
                int lastReadSeqNum = recordBean.getSeqNum();
                int remain = lastReadSeqNum % 50;
                int startSeqNum = remain != 0 ? lastReadSeqNum - remain + 1 : lastReadSeqNum - 50 + 1;

                if(PhoneUtil.isNetworkAvailable(mContext)){
                    //有网络
                    final ChapterListBean chapterListBean1 = createChapterList(recordBean, startSeqNum, bookId);
                    if (chapterListBean1 == null) {
                        List<ChapterListBean> cacheList = groupAllChapter(bookId, startSeqNum, recordBean);
                        return Single.just(cacheList);
                    }
                    if (lastReadSeqNum % 50 > 0 && lastReadSeqNum % 50 < 3 && startSeqNum > 50) {
                        int preSeqNum = startSeqNum - 50;
                        final ChapterListBean chapterListBean2 = createChapterList(recordBean, preSeqNum, bookId);

                        List<ChapterListBean> list = new ArrayList<>();
                        list.add(chapterListBean1);
                        if (chapterListBean2.getList().size() != 0) {
                            list.add(chapterListBean2);
                        }
                        return Single.just(list);
                    } else if ((lastReadSeqNum % 50 == 0 || lastReadSeqNum % 50 > 48) && startSeqNum < recordBean.chapterCount - 50) {
                        int nextSeqNum = startSeqNum + 50;
                        final ChapterListBean chapterListBean3 = createChapterList(recordBean, nextSeqNum, bookId);
                        List<ChapterListBean> list = new ArrayList<>();
                        list.add(chapterListBean1);
                        if (chapterListBean3.getList().size() != 0) {
                            list.add(chapterListBean3);
                        }
                        return Single.just(list);
                    } else {
                        List<ChapterListBean> list = new ArrayList<>();
                        list.add(chapterListBean1);
                        return Single.just(list);
                    }
                }else {
                    //无网络
                    List<ChapterListBean> cacheList = groupAllChapter(bookId, startSeqNum, recordBean);
                    return Single.just(cacheList);
                }
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi())
                .subscribe(new SingleObserver<List<ChapterListBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<ChapterListBean> chapterBean) {
                        Logger.e("App#ReadActivity", "书籍历史记录加载成功");
                        mIReadPage.loadBookChaptersSuccess(chapterBean);
                        endTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.e("App#ReadActivity", "书籍历史记录加载失败:{}", throwable);
                        ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadRecordedChaptersGroup(Throwable, " + bookId + ", " + Logger.getStackTraceString(throwable) + ")");
                        mIReadPage.loadChapterContentsFailed();
                        endTime = System.currentTimeMillis();
                        FuncPageStatsApi.loadFail(Long.parseLong(bookId), String.valueOf(targetSeqNum == 0 ? 1 : targetSeqNum), sourceStats, String.valueOf(endTime - startTime));
                    }
                });


    }

    private ChapterListBean createChapterList(final BookRecordBean recordBean, int startSeqNum, final String bookId) {
        CatalogueReq request = new CatalogueReq();
        request.bookId = bookId;
        request.count = 50;
        request.sort = 0;
        request.startChapter = startSeqNum;

        try {
            JsonPost.SyncPost<ChapterListBean> post = new JsonPost.SyncPost<ChapterListBean>()
                    .setRequest(request)
                    .setResponseType(ChapterListBean.class);
            JsonResponse<ChapterListBean> jsonResponse = post.post();
            ChapterListBean chapterListBean = jsonResponse != null ? jsonResponse.data : null;
            if (chapterListBean == null || chapterListBean.isEmpty())
            {
                ErrorStatsApi.addError(ErrorStatsApi.LOAD_CHAPTER_FAIL, "ReadPresenter.createChapterList(chapterListBean is Empty:" + (chapterListBean == null ? "NULL" : "No Data") + ", " + (jsonResponse != null ? "jsonResponse" : "NULL") + ")");
            }
            chapterListBean.mOwnBook = recordBean;

            List<BookDownloadDBBean> dbBeanList = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskByGroup(bookId, startSeqNum);
            for (ChapterBean chapterBean : chapterListBean.getList()) {
                for (BookDownloadDBBean dbBean : dbBeanList) {
                    if (chapterBean.chapterId == dbBean.chapterId) {
                        chapterBean.isDownload = true;
                    }
                }
                chapterBean.bookId = bookId;
                chapterBean.chapterTitle = StringUtils.convertChapterTitle(chapterBean.chapterTitle, chapterBean.seqNum);
            }
            int firstSeqNum = chapterListBean.getList().get(0).seqNum;
            chapterListBean.mGroupIndex = firstSeqNum % 50 == 0 ? firstSeqNum / 50 - 1 : firstSeqNum / 50;
            return chapterListBean;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_CHAPTER_FAIL, "ReadPresenter.createChapterList(Throwable, " + bookId + ", " + startSeqNum + ", " + Logger.getStackTraceString(throwable) + ")");
        }
        return null;
    }

    /**
     * 加载章节目录
     *
     * @param bookId
     * @param groupPos
     */
    public void preLoadChaptersGroup(final String bookId, final int groupPos) {
        if (mPreLoadingGroup.contains(groupPos)) {
            return;
        }
        mPreLoadingGroup.add(groupPos);
        final int startSeqNum = groupPos * 50 + 1;

        CatalogueReq request = new CatalogueReq();
        request.bookId = bookId;
        request.count = 50;
        request.sort = 0;
        request.startChapter = startSeqNum;

        preLoadChaptersDosposable = new DisposableObserver<JsonResponse<ChapterListBean>>() {
            @Override
            public void onNext(JsonResponse<ChapterListBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    List<BookDownloadDBBean> dbBeanList = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskByGroup(bookId, startSeqNum);
                    ChapterListBean chapterListBean = jsonResponse.data;
                    for (ChapterBean chapterBean : chapterListBean.getList()) {
                        for (BookDownloadDBBean dbBean : dbBeanList) {
                            if (chapterBean.chapterId == dbBean.chapterId) {
                                chapterBean.isDownload = true;
                            }
                        }
                        chapterBean.bookId = bookId;
                        chapterBean.chapterTitle = StringUtils.convertChapterTitle(chapterBean.chapterTitle, chapterBean.seqNum);
                    }
                    chapterListBean.mGroupIndex = groupPos;

                    mIReadPage.preLoadBookChaptersSuccess(chapterListBean, groupPos);
                    mPreLoadingGroup.remove(groupPos);
                } else {
                    mPreLoadingGroup.remove(groupPos);
                }
            }

            @Override
            public void onError(Throwable e) {
                mPreLoadingGroup.remove(groupPos);
                Log.i("ggg", e.toString(), new Throwable());
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<ChapterListBean>()
                .setRequest(request)
                .setResponseType(ChapterListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(preLoadChaptersDosposable);

    }

    @SuppressLint("CheckResult")
    public void loadContent(final String bookId, final List<TxtChapter> bookChapterList, final String sourceStats, String flag) {
        final int size = bookChapterList.size();
        if (size == 0) {
            Logger.e("ReadActivity", "请求的下载书籍章节大小为0");
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadContent(" + bookId + ", " + flag + ", Chapter size is 0)");
            return;
        }
        loadTxtStartTime = System.currentTimeMillis();
        final int firstBookId = bookChapterList.get(0).chapterId;
        final int firstBookSeqNum = bookChapterList.get(0).seqNum;
        Observable.create(new ObservableOnSubscribe<ChapterBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChapterBean> emitter) throws Exception {
                boolean hasNotified = false;

                String downloadList = "";
                String cacheList = "";
                List<TxtChapter> chapterList = new ArrayList<>(bookChapterList);
                for (int i = 0; i < size; i++) {
                    final TxtChapter bookChapter = chapterList.get(i);
                    if (!BookManager.isChapterCached(bookId, "" + bookChapter.chapterId)) {
                        chapter = bookChapter.chapterId;

                        ChapterContentReq request = new ChapterContentReq();
                        request.bookId = bookChapter.getBookId();
                        request.seqNum = bookChapter.seqNum;

                        try {
                            JsonPost.SyncPost<ChapterUrlBean> jsonPost = new JsonPost.SyncPost().setRequest(request)
                                    .setResponseType(ChapterUrlBean.class);
                            JsonResponse<ChapterUrlBean> jsonResponse = jsonPost.post();
                            if (jsonResponse.status == 1 && jsonResponse.data != null) {
                                ChapterUrlBean chapterUrlBean = jsonResponse.data;
                                if (!"txt".equalsIgnoreCase(chapterUrlBean.format)) {
                                    ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadContent(BookId:" + bookId + ", Format:" + chapterUrlBean.format + ", not text");
                                    throw new IllegalBookFormatException("not txt");
                                }
                                String secret = chapterUrlBean.secret;
                                String content = chapterUrlBean.content;
                                chapterUrlBean.seqNum = bookChapter.seqNum;
                                chapterUrlBean.chapterId = bookChapter.chapterId;
                                chapterUrlBean.url = DecryptUtils.decryptUrl(secret, content);

                                // 类型转换
                                String contentResult = ChapterContentLoadUtils.loadContent(chapterUrlBean.url, ChapterContentLoadUtils.LOAD_CONTENT_TYPE_READ);
                                ChapterBean chapterBean = new ChapterBean();
                                chapterBean.chapterId = chapterUrlBean.chapterId;
                                chapterBean.content = contentResult;
                                chapterBean.seqNum = chapterUrlBean.seqNum;
                                chapterBean.bookId = chapterUrlBean.bookId;
                                chapterBean.chapterTitle = chapterUrlBean.chapterTitle;

                                // 处理回调
                                BookSaveUtils.saveChapterInfo(bookId, bookChapter.chapterId + "", chapterBean.content);
                                if (i == 0 || !hasNotified) {
                                    hasNotified = true;
                                    Logger.e("ReadActivity", "章节内容下载成功，开始回调");
                                    mIReadPage.loadChapterContentsSuccess();
                                }

                                downloadList = downloadList + chapterBean.seqNum + "," ;

                            } else
                            {
                                //拉取章节信息失败.
                                ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadContent(BookId:" + bookId + ", Code:" + jsonResponse.status + ", Data:" + jsonResponse.data);
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            if (chapterList.get(0).chapterId == chapter) {
                                mIReadPage.loadChapterContentsFailed();
                                loadTxtEndTime = System.currentTimeMillis();
                                ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadContent(subscribe.Throwable, " + bookId + ", " + Logger.getStackTraceString(throwable) + ")");
                                FuncPageStatsApi.loadFail(Long.parseLong(bookId), String.valueOf(bookChapter.seqNum), sourceStats, (endTime - startTime) + " + " + (loadTxtEndTime - loadTxtStartTime));
                            }

                            if (throwable instanceof IllegalBookFormatException) {
                                ToastUtils.showLimited(R.string.not_support_format);
                            }
                        }

                    } else{
                        cacheList = cacheList + bookChapter.seqNum + "," ;
                        if (i == 0) {
                            Logger.e("ReadActivity", "章节内容已下载：" + i);
                            //如果已经存在，再判断是不是我们需要的下一个章节，如果是才返回加载成功
                            if (mIReadPage != null) {
                                hasNotified = true;
                                Logger.e("ReadActivity", "章节内容已下载，开始回调");
                                mIReadPage.loadChapterContentsSuccess();
                            }
                        }
                    }
                }
                chapterList.clear();
                Logger.e(TAG, "章节内容下载成功:  " + downloadList + "   章节内容已有缓存:  " + cacheList);

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ChapterBean>() {
                    @Override
                    public void accept(ChapterBean chapterBean) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e("ReadActivity", "章节内容下载失败，章节：" + chapter);
                        if (firstBookId == chapter) {
                            mIReadPage.loadChapterContentsFailed();
                            loadTxtEndTime = System.currentTimeMillis();
                            ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "ReadPresenter.loadContent(Throwable.accept, BookId:" + bookId + ", " + Logger.getStackTraceString(throwable) + ")");
                            FuncPageStatsApi.loadFail(Long.parseLong(bookId), String.valueOf(firstBookSeqNum), sourceStats,
                                    (endTime - startTime) + " + " + (loadTxtEndTime - loadTxtStartTime));
                        }
                        if (throwable instanceof IllegalBookFormatException) {
                            ToastUtils.showLimited(R.string.not_support_format);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable subscription) throws Exception {
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void addBookToShelf(final BookRecordBean recordBean, final String content) {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                //调用添加书架接口.
                return com.duoyue.app.presenter.BookShelfPresenter.addBookShelf(recordBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(o)) {
                    //添加书架成功.
                    if (!TextUtils.isEmpty(content)) {
                        ToastUtils.showLimited(content);
                    }
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(o);
                }
            }
        });
    }

    public void uploadChapterError(String bookName, TxtChapter txtChapter) {
        ChapterErrorReq request = new ChapterErrorReq();
        request.setBookId(txtChapter.getBookId());
        request.setSeqNum(txtChapter.seqNum);
        request.setBookName(bookName);
        request.setChapterTitle(txtChapter.getTitle());

        mChapterErrorDosposable = new DisposableObserver<JsonResponse<AddBookShelfResp>>() {
            @Override
            public void onNext(JsonResponse<AddBookShelfResp> readTaskRespJsonResponse) {
                if (readTaskRespJsonResponse.status == 1) {
                    ToastUtils.showLimited(readTaskRespJsonResponse.msg);
                } else {
                    ToastUtils.showLimited("上报失败，请稍后再试");
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showLimited("上报失败，请稍后再试");
            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<ChapterListBean>()
                .setRequest(request)
                .setResponseType(ChapterListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mChapterErrorDosposable);
    }

    /**
     * 已更新
     *
     * @param bookId
     * @param chapterCount
     * @param groupPos
     * @param type
     */
    public void loadCatalogue(final String bookId, final int chapterCount, final int groupPos, final int type) {
        if (mPreLoadingGroup.contains(groupPos)) {
            return;
        }
        mPreLoadingGroup.add(groupPos);
        final int startSeqNum = groupPos * 50 + 1;

        if (type == LOAD_CATALOGUE_TYPE_REVERSE && chapterCount - startSeqNum < 25) {
            int preGroupPos = groupPos;
            int preStartSeqNum = preGroupPos * 50 + 1;
            CatalogueReq nextQequest = new CatalogueReq();
            nextQequest.bookId = bookId;
            nextQequest.count = 50;
            nextQequest.sort = 1;
            nextQequest.startChapter = preStartSeqNum;

            catalogueDisposable = new DisposableObserver<JsonResponse<ChapterListBean>>() {

                @Override
                public void onNext(JsonResponse<ChapterListBean> jsonResponse) {
                    if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                            && !jsonResponse.data.getList().isEmpty()) {
                        List<BookDownloadDBBean> dbBeanList = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskByGroup(bookId, startSeqNum);
                        ChapterListBean chapterListBean = jsonResponse.data;
                        for (ChapterBean chapterBean : chapterListBean.getList()) {
                            for (BookDownloadDBBean dbBean : dbBeanList) {
                                if (chapterBean.chapterId == dbBean.chapterId) {
                                    chapterBean.isDownload = true;
                                }
                            }
                            chapterBean.bookId = bookId;
                            chapterBean.chapterTitle = StringUtils.convertChapterTitle(chapterBean.chapterTitle, chapterBean.seqNum);
                        }
                        chapterListBean.mGroupIndex = groupPos;

                        int firstSeqNum = chapterListBean.getList().get(0).seqNum;
                        int group = firstSeqNum / 50;
                        mIReadPage.loadCatalogueSuccess(chapterListBean, group, type);
                        mPreLoadingGroup.remove(groupPos);

                        getCatalogueList(bookId, groupPos, type, 1);
                    } else {
                        mIReadPage.loadCatalogueFailed(groupPos, type);
                        mPreLoadingGroup.remove(groupPos);
                        Logger.e("App#", "目录数据异常");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    getCatalogueList(bookId, groupPos, type, 1);
                }

                @Override
                public void onComplete() {

                }
            };

            new JsonPost.AsyncPost<ChapterListBean>()
                    .setRequest(nextQequest)
                    .setResponseType(ChapterListBean.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .post(catalogueDisposable);
        } else {
            getCatalogueList(bookId, groupPos, type, 0);
        }
    }

    private void getCatalogueList(final String bookId, final int groupPos, final int type, final int sort) {
        int startSeqNum = 0;
        if (sort == 1) {
            startSeqNum = (groupPos - 1) * 50 + 1;
        } else {
            startSeqNum = groupPos * 50 + 1;
        }

        CatalogueReq request = new CatalogueReq();
        request.bookId = bookId;
        request.count = 50;
        request.sort = sort;
        request.startChapter = startSeqNum;

        final int finalStartSeqNum = startSeqNum;

        catalogueListDisposable = new DisposableObserver<JsonResponse<ChapterListBean>>() {
            @Override
            public void onNext(JsonResponse<ChapterListBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    ChapterListBean chapterListBean = jsonResponse.data;
                    List<BookDownloadDBBean> dbBeanList = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskByGroup(bookId, finalStartSeqNum);
                    for (ChapterBean chapterBean : chapterListBean.getList()) {
                        for (BookDownloadDBBean dbBean : dbBeanList) {
                            if (chapterBean.chapterId == dbBean.chapterId) {
                                chapterBean.isDownload = true;
                            }
                        }
                        chapterBean.bookId = bookId;
                        chapterBean.chapterTitle = StringUtils.convertChapterTitle(chapterBean.chapterTitle, chapterBean.seqNum);
                    }
                    chapterListBean.mGroupIndex = groupPos;

                    int firstSeqNum = chapterListBean.getList().get(0).seqNum;
                    int group = firstSeqNum / 50;
                    mIReadPage.loadCatalogueSuccess(chapterListBean, group, type);
                    mPreLoadingGroup.remove(groupPos);
                } else {
                    mIReadPage.loadCatalogueFailed(groupPos, type);
                    mPreLoadingGroup.remove(groupPos);
                    Logger.e("App#", "目录数据异常");
                }
            }

            @Override
            public void onError(Throwable e) {
                mPreLoadingGroup.remove(groupPos);
                Logger.e("App#", e.toString(), new Throwable());
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<ChapterListBean>()
                .setRequest(request)
                .setResponseType(ChapterListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(catalogueListDisposable);
    }

    /**
     * 今天已阅读时间
     */
    public void loadTodayReadTime() {
        mReadTaskDosposable = new DisposableObserver<JsonResponse<ReadTaskResp>>() {
            @Override
            public void onNext(JsonResponse<ReadTaskResp> readTaskRespJsonResponse) {
                if (readTaskRespJsonResponse.status == 1 && readTaskRespJsonResponse.data != null) {
                    ReadTaskResp data = readTaskRespJsonResponse.data;
                    mIReadPage.loadTodayReadTimeSuccess(data);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        new JsonPost.AsyncPost<ReadTaskResp>()
                .setRequest(new ReadTaskReq())
                .setResponseType(ReadTaskResp.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(mReadTaskDosposable);
    }

    public void destroy() {
        if (recommendDisposable != null && !recommendDisposable.isDisposed()) {
            recommendDisposable.dispose();
        }
        if (catalogueDisposable != null && !catalogueDisposable.isDisposed()) {
            catalogueDisposable.dispose();
        }
        if (catalogueListDisposable != null && !catalogueListDisposable.isDisposed()) {
            catalogueListDisposable.dispose();
        }
        if (mReadTaskDosposable != null && !mReadTaskDosposable.isDisposed()) {
            mReadTaskDosposable.dispose();
        }
        if (mChapterErrorDosposable != null && !mChapterErrorDosposable.isDisposed()) {
            mChapterErrorDosposable.dispose();
        }
        if (preLoadChaptersDosposable != null && !preLoadChaptersDosposable.isDisposed()) {
            preLoadChaptersDosposable.dispose();
        }
    }

    /**
     * 获取本地缓存中所有的章节数据，并进行分组
     *
     * @param bookId
     * @param startSeqNum
     * @param recordBean
     * @return
     */
    public List<ChapterListBean> groupAllChapter(String bookId, int startSeqNum, BookRecordBean recordBean) {

        if (BookSaveUtils.isCached(bookId, BookSaveUtils.ALL_CHAPTER)) {
            //获取本地目录缓存
            AllChapterDownloadResp resp = BookSaveUtils.getCacheAllChapter(bookId);
            if (resp != null) {
                //所有的本地缓存章节数据
                List<AllChapterDownloadBean> allChapterDownloadBeanList = resp.getChapters();

                //已下载完成文本的章节数据
                List<BookDownloadDBBean> bookDownloadDBBeans = BookDownloadHelper.getsInstance().queryDownloadCompleteTask(bookId);
                //已下载完成文本章节Id
                List<String> downloadChapterId = new ArrayList<>();
                for (BookDownloadDBBean downloadDBBean : bookDownloadDBBeans) {
                    downloadChapterId.add(String.valueOf(downloadDBBean.getChapterId()));
                }

                List<ChapterListBean> list = new ArrayList<>();

                //50章节为一组
                int groupCount = allChapterDownloadBeanList.size() % 50 == 0
                        ? allChapterDownloadBeanList.size() / 50 : allChapterDownloadBeanList.size() / 50 + 1;
                for (int i = 0; i < groupCount; i++) {
                    ArrayList<ChapterBean> chapterBeanList = new ArrayList<>();
                    ChapterListBean chapterListBean = new ChapterListBean();
                    chapterListBean.mOwnBook = recordBean;
                    chapterListBean.setList(chapterBeanList);
                    chapterListBean.from = resp.getFrom();

                    for (int j = 0; j < 50 && i * 50 + j < allChapterDownloadBeanList.size(); j++) {
                        AllChapterDownloadBean bean = allChapterDownloadBeanList.get(i * 50 + j);
                        //转换成chapterBean格式
                        ChapterBean chapterBean = new ChapterBean();
                        chapterBean.setSeqNum(Integer.valueOf(bean.getSeqNum()));
                        chapterBean.setBookId(bookId);
                        chapterBean.setChapterId(Integer.valueOf(bean.getId()));
                        chapterBean.setChapterTitle(bean.getTitle());
                        if (downloadChapterId.contains(bean.getId())) {
                            chapterBean.isDownload = true;
                        }
                        chapterBeanList.add(chapterBean);
                    }

                    int firstSeqNum = chapterListBean.getList().get(0).seqNum;
                    chapterListBean.mGroupIndex = firstSeqNum % 50 == 0 ? firstSeqNum / 50 - 1 : firstSeqNum / 50;
                    list.add(chapterListBean);
                }
                return list;
            }
        }
        return null;
    }

    /**
     * 请求下载配置项
     */
    public void getDownloadOption(final long bookId) {

        ChapterDownloadOptionReq optionReq = new ChapterDownloadOptionReq();
        optionReq.bookId = bookId;

        new JsonPost.AsyncPost<ChapterDownloadOptionResp>()
                .setRequest(optionReq)
                .setResponseType(ChapterDownloadOptionResp.class)
                .subscribeOn(MtSchedulers.io())
                .observeOn(MtSchedulers.mainUi())
                .post(new DisposableObserver<JsonResponse<ChapterDownloadOptionResp>>() {
                    @Override
                    public void onNext(JsonResponse<ChapterDownloadOptionResp> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            if(mIReadPage != null){
                                mIReadPage.getDownloadOptionSuccess(jsonResponse.data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(mIReadPage != null){
                            mIReadPage.getDownloadOptionError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private class IllegalBookFormatException extends Exception {
        public IllegalBookFormatException(String message) {
            super(message);
        }
    }
}
