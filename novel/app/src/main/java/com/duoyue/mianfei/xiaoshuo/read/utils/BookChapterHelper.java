package com.duoyue.mianfei.xiaoshuo.read.utils;


import com.zydm.base.data.dao.ChapterBean;
import com.zydm.base.data.dao.gen.ChapterBeanDao;
import com.zydm.base.data.dao.DaoDbHelper;
import com.zydm.base.data.dao.gen.DaoSession;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.util.List;

public class BookChapterHelper {
    private static volatile BookChapterHelper sInstance;
    private static DaoSession daoSession;
    private static ChapterBeanDao bookChapterBeanDao;

    public static BookChapterHelper getsInstance() {
        if (sInstance == null) {
            synchronized (BookChapterHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookChapterHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    bookChapterBeanDao = daoSession.getChapterBeanDao();
                }
            }
        }
        return sInstance;
    }

    public void saveBookChaptersAsync(final List<ChapterBean> chapterBeans) {
        daoSession.startAsyncSession()
                .runInTx(new Runnable() {
                    @Override
                    public void run() {
                        daoSession.getChapterBeanDao().insertOrReplaceInTx(chapterBeans);
                    }
                });
    }

    public void updateBookChaptersAsync(final ChapterBean chapterBean) {
        daoSession.startAsyncSession()
                .runInTx(new Runnable() {
                    @Override
                    public void run() {
                        daoSession.getChapterBeanDao().insertOrReplaceInTx(chapterBean);
                    }
                });
    }

    public void removeBookChapters(String bookId) {
        bookChapterBeanDao.queryBuilder().where(ChapterBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    public Observable<List<ChapterBean>> findBookChaptersInRx(final String bookId) {
        return Observable.create(new ObservableOnSubscribe<List<ChapterBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChapterBean>> emitter) throws Exception {
                List<ChapterBean> chapterBeans = daoSession.getChapterBeanDao()
                        .queryBuilder()
                        .where(ChapterBeanDao.Properties.BookId.eq(bookId))
                        .list();
                emitter.onNext(chapterBeans);
            }
        });
    }

    public List<ChapterBean> findBookChapters(final String bookId) {
        return daoSession.getChapterBeanDao()
                .queryBuilder()
                .where(ChapterBeanDao.Properties.BookId.eq(bookId))
                .list();
    }

    public void clearAll() {
        if (bookChapterBeanDao != null) {
            List<ChapterBean> list = bookChapterBeanDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    bookChapterBeanDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
