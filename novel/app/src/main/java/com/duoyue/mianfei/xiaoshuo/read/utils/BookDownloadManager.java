package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.content.DialogInterface;
import android.os.Build;
import com.duoyue.app.bean.BookDownloadChapterBean;
import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.bean.BookDownloadTask;
import com.duoyue.app.event.BookDownloadEvent;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.FileUtil;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 书籍下载管理类
 *
 * @author wangtian
 * @date 2019/07/01
 */
public class BookDownloadManager {

    private static final String TAG = "App#BookDownloadManager";

    private static volatile BookDownloadManager sInstance;

    //待下载队列
    private List<BookDownloadTask> pendingTaskList;

    //正在下载队列
    private List<BookDownloadTask> downloadingTaskList;

    private boolean isDownloadingTask;


    private BookDownloadManager() {
        pendingTaskList = new ArrayList<>();
        downloadingTaskList = new ArrayList<>();
    }

    public static BookDownloadManager getsInstance() {
        if (sInstance == null) {
            synchronized (BookDownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new BookDownloadManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 是否正在下载
     *
     * @return
     */
    public synchronized boolean isDownloading() {

        if (downloadingTaskList != null && !downloadingTaskList.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 添加下载任务到队列中
     *
     * @param bookId
     * @param selectedChapterBean
     */
    public synchronized void addDownloadTask(final long bookId, final String bookName, final List<BookDownloadChapterBean> selectedChapterBean) {

        Observable.just(selectedChapterBean).map(new Function<List<BookDownloadChapterBean>, List<BookDownloadDBBean>>() {
            @Override
            public List<BookDownloadDBBean> apply(List<BookDownloadChapterBean> bookDownloadChapterBeans) throws Exception {

                //保存所有的下载任务, 网络中断或者程序退出，下次进来时可以继续下载
                Logger.i(TAG, "保存下载任务");
                return saveDownloadTask(bookId, bookName, selectedChapterBean);

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<BookDownloadDBBean>>() {
                    @Override
                    public void onNext(List<BookDownloadDBBean> bookDownloadDBBeans) {

                        BookDownloadTask task = new BookDownloadTask(bookId, bookName, bookDownloadDBBeans, bookDownloadDBBeans.size(), 0);
                        pendingTaskList.add(task);
                        dispacherDownloadTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dispacherDownloadTask();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 调度下载任务
     */
    public synchronized void dispacherDownloadTask() {

        if (!downloadingTaskList.isEmpty()) {
            return;
        }

        if (!pendingTaskList.isEmpty()) {

            final BookDownloadTask task = pendingTaskList.get(0);
            downloadingTaskList.add(task);
            pendingTaskList.remove(task);

            downloadChapter(task, listener);
        }
    }

    /**
     * 重试下载任务
     * @param task
     */
    public void retryDownload(BookDownloadTask task) {

        Logger.i(TAG, "retryDownload: " + task.getBookName());

        downloadChapter(task, listener);
    }

    /**
     * 删除下载任务
     * @param task
     */
    public void removeTask(BookDownloadTask task) {
        downloadingTaskList.remove(task);
    }

    /**
     * 发送下载错误消息
     * @param task
     */
    public void sendErrorMessge(BookDownloadTask task) {
        EventBus.getDefault().post(new BookDownloadEvent(task.getBookId(), task, BookDownloadEvent.DOWNLOAD_ERROR));
    }

    IBookDownloadListener listener = new IBookDownloadListener() {
        @Override
        public void oneBookDownloadCallback(BookDownloadTask task, BookDownloadDBBean bookDownloadDBBean) {

            int progress = task.getProgress() + 1;
            task.setProgress(progress);

            task.setRetryCount(0);

            EventBus.getDefault().post(new BookDownloadEvent(
                    task.getBookId(),
                    bookDownloadDBBean,
                    BookDownloadEvent.DOWNLOADING,
                    task.getDownloadDBBeans().size(),
                    progress));
        }

        @Override
        public void allBookDownloadCallback(BookDownloadTask task) {

            task.setRetryCount(0);

            EventBus.getDefault().post(new BookDownloadEvent(task.getBookId(), task, BookDownloadEvent.DOWNLOAD_COMPLETE));

            ToastUtils.show("成功下载《" + task.getBookName() + "》" + task.getTotal() + "章节");

            downloadingTaskList.remove(task);
            dispacherDownloadTask();
        }

        @Override
        public void errorBookDownloadCallback(final BookDownloadTask task) {

            int retryCount = task.getRetryCount();

            if (retryCount > 2) {
                sendErrorMessge(task);
            } else {
                task.setRetryCount(++retryCount);
                Observable.timer(10, TimeUnit.SECONDS)
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Long aLong) {
                                if(!isDownloadingTask){
                                    retryDownload(task);
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
        }
    };

    /**
     * 下载章节
     *
     * @param task
     * @param listener
     */
    public synchronized void downloadChapter(final BookDownloadTask task, final IBookDownloadListener listener) {

        final List<BookDownloadDBBean> selectedChapterBean = task.getDownloadDBBeans();
        final int size = selectedChapterBean.size();
        Logger.i(TAG, "size:   " + size);
        if (size == 0) {
            return;
        }

        isDownloadingTask = true;

        Observable.just(selectedChapterBean).flatMap(new Function<List<BookDownloadDBBean>, ObservableSource<BookDownloadDBBean>>() {
            @Override
            public ObservableSource<BookDownloadDBBean> apply(List<BookDownloadDBBean> bookDownloadDBBeans) throws Exception {
                //遍历每一个BookDownloadDBBean
                Logger.i(TAG, "遍历下载任务");
                if (task.getProgress() != 0) {
                    Logger.i(TAG, "task.getProgress():  " + task.getProgress() + "  task.getTotal():  " + task.getTotal());
                    List<BookDownloadDBBean> dbBeanList = new ArrayList<>();
                    dbBeanList.addAll(bookDownloadDBBeans.subList(task.getProgress(), bookDownloadDBBeans.size() - 1));
                    return Observable.fromIterable(dbBeanList);
                }
                return Observable.fromIterable(bookDownloadDBBeans);
            }
        }).map(new Function<BookDownloadDBBean, BookDownloadDBBean>() {
            @Override
            public BookDownloadDBBean apply(BookDownloadDBBean bookDownloadDBBean) throws Exception {

                //开始下载任务
                Logger.i(TAG, "开始下载任务: " + bookDownloadDBBean.get_id());
                return downloadTask(bookDownloadDBBean);
            }
        }).subscribeOn(MtSchedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookDownloadDBBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BookDownloadDBBean bookDownloadDBBean) {
                        Logger.i(TAG, "onNext");

                        if (listener != null) {
                            listener.oneBookDownloadCallback(task, bookDownloadDBBean);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i(TAG, "onError");
                        isDownloadingTask = false;
                        if (listener != null) {
                            listener.errorBookDownloadCallback(task);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Logger.i(TAG, "onComplete");
                        isDownloadingTask = false;
                        if (listener != null) {
                            listener.allBookDownloadCallback(task);
                        }
                    }
                });
    }

    /**
     * 执行下载章节
     * @param bookDownloadDBBean
     * @return
     * @throws Exception
     */
    private BookDownloadDBBean downloadTask(BookDownloadDBBean bookDownloadDBBean) throws Exception {
        if (!isChapterCached(String.valueOf(bookDownloadDBBean.getBookId()), String.valueOf(bookDownloadDBBean.getChapterId()))) {
            Logger.i(TAG, "本地文件不存在");
            //解密url
            String url = DecryptUtils.decryptUrl(bookDownloadDBBean.getSecret(), bookDownloadDBBean.getUrl());
            //获取章节文本内容
            String contentResult = ChapterContentLoadUtils.loadContent(url, ChapterContentLoadUtils.LOAD_CONTENT_TYPE_DOWNLOAD);
            //保存到本地
            BookSaveUtils.saveChapterInfo(String.valueOf(bookDownloadDBBean.getBookId()), String.valueOf(bookDownloadDBBean.chapterId), contentResult);
            //更新下载数据库
            bookDownloadDBBean.states = 1;
            BookDownloadHelper.getsInstance().updateDownloadTask(bookDownloadDBBean);
        } else {
            bookDownloadDBBean.states = 1;
            BookDownloadHelper.getsInstance().updateDownloadTask(bookDownloadDBBean);
            Logger.i(TAG, "本地文件存在,不需要下载");
        }
        return bookDownloadDBBean;
    }

    /**
     * 保存下载任务
     */
    private synchronized List<BookDownloadDBBean> saveDownloadTask(long bookId, String bookName, List<BookDownloadChapterBean> selectedChapterBean) {

        List<BookDownloadDBBean> downloadDBBeans = new ArrayList<>();
        int total = selectedChapterBean.size();
        for (int i = 0; i < selectedChapterBean.size(); i++) {

            BookDownloadChapterBean chapterBean = selectedChapterBean.get(i);

            BookDownloadDBBean bookDownloadDBBean = new BookDownloadDBBean(
                    bookId + "_" + chapterBean.getSeqNum(),
                    bookId,
                    bookName,
                    chapterBean.getId(),
                    chapterBean.getSeqNum(),
                    chapterBean.getTitle(),
                    chapterBean.getSecret(),
                    chapterBean.getUrl(),
                    0
            );
            downloadDBBeans.add(bookDownloadDBBean);
        }
        BookDownloadHelper.getsInstance().saveDownloadTask(downloadDBBeans);

        return downloadDBBeans;
    }

    /**
     * 是否有缓存
     * @param folderName
     * @param fileName
     * @return
     */
    public boolean isChapterCached(String folderName, String fileName) {
        File file = new File(ReadConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_FILE);
        return file.exists();
    }

    /**
     * 检查是否有上一次APP退出时未下载完成的任务，如果有则继续下载
     */
    public void checkDownloadTask() {

        if (isDownloading()) {
            return;
        }

        Single.fromCallable(new Callable<List<BookDownloadDBBean>>() {
            @Override
            public List<BookDownloadDBBean> call() throws Exception {

                List<BookDownloadDBBean> downloadDBBeanList = BookDownloadHelper.getsInstance().queryAllDonwloadNotDoneTask();
                return downloadDBBeanList;
            }
        }).map(new Function<List<BookDownloadDBBean>, HashMap<String, List<BookDownloadDBBean>>>() {
            @Override
            public HashMap<String, List<BookDownloadDBBean>> apply(List<BookDownloadDBBean> bookDownloadDBBeans) throws Exception {

                HashMap<String, List<BookDownloadDBBean>> beanHashMap = new HashMap<>();
                for (BookDownloadDBBean dbBean : bookDownloadDBBeans) {
                    String sBookId = String.valueOf(dbBean.getBookId());
                    if (beanHashMap.containsKey(sBookId) && beanHashMap.get(sBookId) != null) {
                        beanHashMap.get(sBookId).add(dbBean);
                    } else {
                        List<BookDownloadDBBean> dbBeanList = new ArrayList<>();
                        dbBeanList.add(dbBean);
                        beanHashMap.put(sBookId, dbBeanList);
                    }
                }
                return beanHashMap;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi())
                .subscribe(new Consumer<HashMap<String, List<BookDownloadDBBean>>>() {
                    @Override
                    public void accept(HashMap<String, List<BookDownloadDBBean>> dbBeanListHashMap) throws Exception {
                        if (!dbBeanListHashMap.isEmpty()) {
                            for (Map.Entry<String, List<BookDownloadDBBean>> entry : dbBeanListHashMap.entrySet()) {
                                Logger.i(TAG, "继续下载：  " + entry.getKey());
                                BookDownloadTask task = new BookDownloadTask(Long.valueOf(entry.getKey()),
                                        !entry.getValue().isEmpty() && entry.getValue().get(0) != null ? entry.getValue().get(0).getBookName() : "",
                                        entry.getValue(), entry.getValue().size(), 0);
                                pendingTaskList.add(task);
                                dispacherDownloadTask();
                            }
                        } else {
                            Logger.i(TAG, "没有未完成的下载任务!");
                        }
                    }
                });
    }

    /**
     * 删除书籍的缓存
     */
    public void removeBookCache(String bookId) {

        //删除书籍对应的缓存文件夹
        FileUtil.deleteDir(ReadConstant.BOOK_CACHE_PATH + bookId);
        //删除书籍对应的所有下载记录
        BookDownloadHelper.getsInstance().deleteBookDownloadTask(bookId);

    }

    /**
     * 批量删除书籍的缓存
     */
    public void removeBookCacheList(List<BookShelfBean> bookBeanList) {
        for (BookShelfBean bookShelfBean : bookBeanList) {
            removeBookCache(bookShelfBean.getBookId());
        }
    }

    /**
     * 该书籍是否正在下载或者等待下载
     */
    public boolean isDownloadingOrPending(long bookId) {
        for (BookDownloadTask task : downloadingTaskList) {
            if (task.getBookId() == bookId) {
                return true;
            }
        }

        for (BookDownloadTask task : pendingTaskList) {
            if (task.getBookId() == bookId) {
                return true;
            }
        }

        return false;
    }

    /**
     * 书籍下载回调
     */
    public interface IBookDownloadListener {

        /**
         * 某一章节下载完成回调
         */
        void oneBookDownloadCallback(BookDownloadTask task, BookDownloadDBBean bookDownloadDBBean);

        /**
         * 所有章节下载完成回调
         */
        void allBookDownloadCallback(BookDownloadTask task);

        /**
         * 下载失败
         *
         * @param task
         */
        void errorBookDownloadCallback(BookDownloadTask task);

    }
}
