package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.os.Looper;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.dao.*;
import com.zydm.base.data.dao.gen.BookRecordBeanDao;
import com.zydm.base.data.dao.gen.DaoSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BookRecordHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookRecordHelper";

    private static volatile BookRecordHelper sInstance;
    private static DaoSession daoSession;
    private static BookRecordBeanDao bookRecordBeanDao;
    private ArrayList<RecordDaoObserver> mObservers = new ArrayList<>();
    private Semaphore semaphore = new Semaphore(1, true);
    private AtomicLong counter = new AtomicLong(0);

    public synchronized static BookRecordHelper getsInstance() {
        if (sInstance == null) {
            synchronized (BookRecordHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookRecordHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    bookRecordBeanDao = daoSession.getBookRecordBeanDao();
                }
            }
        }
        return sInstance;
    }

    public void saveRecordBook(final BookRecordBean recordBean, final boolean notify) {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    bookRecordBeanDao.insertOrReplaceInTx(recordBean);
                    if (notify) {
                        notifyObserver(recordBean);
                    }
                    Logger.e("save_book", "保存书籍阅读位置信息,计数：  " + counter.incrementAndGet());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.e("save_book", "保存书籍阅读位置信息，报错： " + ex.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });

    }

    /**
     * 根据服务器下发阅读历史记录信息更新到本地数据库.
     * @param bookRecordList
     */
    public void updateRecordBookWithAsync(final List<BookRecordBean> bookRecordList)
    {
        daoSession.startAsyncSession().runInTx(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (bookRecordList == null || bookRecordList.isEmpty())
                    {
                        //刪除所有历史记录数据.
                        bookRecordBeanDao.deleteAll();
                        Logger.i(TAG, "updateRecordBookWithAsync: 删除本地所有阅读历史记录");
                    } else
                    {
                        //查询出所有的阅读历史记录信息.
                        List<BookRecordBean> localBookRecordList = bookRecordBeanDao.queryBuilder().list();
                        //将本地阅读历史记录转化为Map.
                        Map<String, BookRecordBean> bookRecordMap = null;
                        if (localBookRecordList != null && !localBookRecordList.isEmpty())
                        {
                            bookRecordMap = new HashMap<>();
                            for (BookRecordBean recordBean : localBookRecordList)
                            {
                                if (recordBean == null)
                                {
                                    continue;
                                }
                                bookRecordMap.put(recordBean.getBookId(), recordBean);
                            }
                        }
                        BookRecordBean bookRecordBean;
                        for (BookRecordBean bookRecord : bookRecordList)
                        {
                            //通过BookId获取本地DB中的阅读历史记录信息.
                            bookRecordBean = bookRecordMap != null && bookRecordMap.containsKey(bookRecord.getBookId()) ? bookRecordMap.get(bookRecord.getBookId()) : null;
                            //判断阅读历史记录是否需要修改.
                            if (bookRecordBean == null || bookRecordBean.getLastRead() < bookRecord.getLastRead())
                            {
                                //服务器阅读历史记录时间大于本地记录时间, 更新本地记录.
                                bookRecordBeanDao.insertOrReplace(bookRecord);
                                Logger.i(TAG, "updateRecordBookWithAsync: {} 更新本地所有阅读历史记录", bookRecord.getBookId());
                            }
                        }
                    }
                    //通知刷新阅读历史记录数据.
                    notifyObserver(null);
                } catch (Throwable throwable)
                {
                    Logger.e(TAG, "updateRecordBookWithAsync: 更新书架书籍列表异常:{}", throwable);
                }
            }
        });
    }

    public void updateRecordBook(BookRecordBean recordBean, boolean notify) {
        bookRecordBeanDao.update(recordBean);
        if (notify) {
            notifyObserver(recordBean);
        }
    }

    /**
     * 移除书籍阅读历史记录.
     * @param bookId
     */
    public void removeBook(String bookId) {
        bookRecordBeanDao
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 移除所有历史记录.
     */
    public void removeAllBook() {
        if (bookRecordBeanDao != null) {
            try {
                bookRecordBeanDao
                        .queryBuilder()
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public BookRecordBean findBookRecordById(String bookId) {
        BookRecordBean recordBean = bookRecordBeanDao.queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId)).unique();
        return recordBean;
    }

    public List<BookRecordBean> findAllBooks() {
        return bookRecordBeanDao
                .queryBuilder()
                .orderDesc(BookRecordBeanDao.Properties.LastRead).list();
    }

    public List<BookRecordBean> findAllBooks(int offset, int pageCount) {
        return bookRecordBeanDao
                .queryBuilder()
                .orderDesc(BookRecordBeanDao.Properties.LastRead)
                .offset(offset).limit(pageCount)
                .list();
    }

    public void addObserver(RecordDaoObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public void removeObserver(RecordDaoObserver observer) {
        mObservers.remove(observer);
    }

    private void notifyObserver(final BookRecordBean recordBean) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            excute(recordBean);
        } else {
            BaseApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    excute(recordBean);
                }
            });
        }
    }

    private void excute(BookRecordBean recordBean) {
        for (RecordDaoObserver observer : mObservers) {
            observer.onRecordChange(recordBean);
        }
    }

    public interface RecordDaoObserver {
        void onRecordChange(BookRecordBean recordBean);
    }
}
