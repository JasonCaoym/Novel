package com.zydm.base.data.dao;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.dao.gen.BookShelfBeanDao;
import com.zydm.base.data.dao.gen.DaoSession;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.util.ArrayList;
import java.util.List;

public class BookShelfHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#BookShelfHelper";

    private static volatile BookShelfHelper sInstance;
    private static DaoSession daoSession;
    private static BookShelfBeanDao shelfBookBeanDao;
    private ArrayList<ShelfDaoObserver> mObservers = new ArrayList<>();

    public synchronized static BookShelfHelper getsInstance() {
        if (sInstance == null) {
            synchronized (BookShelfHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookShelfHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    shelfBookBeanDao = daoSession.getBookShelfBeanDao();
                }
            }
        }
        return sInstance;
    }

    public void addObserver(ShelfDaoObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public void removeObserver(ShelfDaoObserver observer) {
        mObservers.remove(observer);
    }

    public void saveBook(BookShelfBean bookBean) {
        shelfBookBeanDao.insertOrReplace(bookBean);
        ShelfEvent event = new ShelfEvent();
        event.mType = ShelfEvent.TYPE_ADD;
        event.mChangeList.add(bookBean);
        notifyObserver(event);
    }

    public void saveBooks(List<BookShelfBean> bookBeans) {
        shelfBookBeanDao.insertOrReplaceInTx(bookBeans);
        ShelfEvent event = new ShelfEvent();
        event.mType = ShelfEvent.TYPE_ADD;
        event.mChangeList.addAll(bookBeans);
        notifyObserver(event);
    }

    private void notifyObserver(final ShelfEvent event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            excute(event);
        } else {
            BaseApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    excute(event);
                }
            });
        }
    }

    private void excute(ShelfEvent event) {
        if (mObservers == null) {
            return;
        }
        for (ShelfDaoObserver observer : mObservers) {
            observer.onShelfChange(event);
        }
    }

    public void saveBookWithAsync(final BookShelfBean bookBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                shelfBookBeanDao.insertOrReplace(bookBean);
                ShelfEvent event = new ShelfEvent();
                event.mType = ShelfEvent.TYPE_ADD;
                event.mChangeList.add(bookBean);
                notifyObserver(event);
            }
        });
    }

    public void saveBooksWithAsync(final List<BookShelfBean> bookBeans) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                shelfBookBeanDao.insertOrReplaceInTx(bookBeans);
                ShelfEvent event = new ShelfEvent();
                event.mType = ShelfEvent.TYPE_ADD;
                event.mChangeList.addAll(bookBeans);
                notifyObserver(event);
            }
        });
    }

    /**
     * 置顶也需要保存到数据库
     * @param bookBean
     */
    public void toppingBookWithAsync(final BookShelfBean bookBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                shelfBookBeanDao.insertOrReplace(bookBean);
                ShelfEvent event = new ShelfEvent();
                event.mType = ShelfEvent.TYPE_UPDATE;
                event.mChangeList.add(bookBean);
                notifyObserver(event);
            }
        });
    }

    /**
     * 移除书架书籍信息.
     * @param bookBeanList
     */
    public void removeBook(List<BookShelfBean> bookBeanList)
    {
        if (shelfBookBeanDao != null && bookBeanList != null && !bookBeanList.isEmpty())
        {
            shelfBookBeanDao.deleteInTx(bookBeanList);
            ShelfEvent event = new ShelfEvent();
            event.mType = ShelfEvent.TYPE_REMOVE;
            event.mChangeList.addAll(bookBeanList);
            notifyObserver(event);
        }
    }

    public Observable<String> removeBookInRx(final BookShelfBean bookBean) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                shelfBookBeanDao.delete(bookBean);
                e.onNext("删除成功");
                ShelfEvent event = new ShelfEvent();
                event.mType = ShelfEvent.TYPE_REMOVE;
                event.mChangeList.add(bookBean);
                notifyObserver(event);
            }
        });
    }

    public Observable<String> removeBooksInRx(final List<BookShelfBean> bookBeans, final boolean notify) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String[] keys = new String[bookBeans.size()];
                for (int i = 0; i < bookBeans.size(); i++) {
                    keys[i] = shelfBookBeanDao.getKey(bookBeans.get(i));
                }
                shelfBookBeanDao.deleteByKeyInTx(keys);
                if (notify) {
                    ShelfEvent event = new ShelfEvent();
                    event.mType = ShelfEvent.TYPE_REMOVE;
                    event.mChangeList.addAll(bookBeans);
                    notifyObserver(event);
                }
                e.onNext("删除成功");
            }
        });
    }

    public BookShelfBean findBookById(String bookId) {
        return shelfBookBeanDao.queryBuilder().where(BookShelfBeanDao.Properties.BookId.eq(bookId)).unique();
    }

    public void updateBook(BookShelfBean shelfBean) {
        shelfBookBeanDao.update(shelfBean);
    }

    public List<BookShelfBean> findAllBooks() {
        return shelfBookBeanDao
                .queryBuilder()
                .orderDesc(BookShelfBeanDao.Properties.AddTime)
                .list();
    }

    /**
     * 查询本地添加书架还未上传服务器的书籍数据
     * @return
     */
    public List<BookShelfBean> findLocalAddBooks(){
        return shelfBookBeanDao.queryBuilder().where(BookShelfBeanDao.Properties.IsAddLocalDb.eq(true)).list();
    }

    public void clearAll() {
        if (shelfBookBeanDao != null) {
            List<BookShelfBean> list = shelfBookBeanDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    shelfBookBeanDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public interface ShelfDaoObserver {
        void onShelfChange(@NonNull ShelfEvent event);
    }






    //=======================================================================================
    /**
     * 将服务器下发的第一页书籍数据更新到本地数据库.
     * @param bookShelfList 要添加的书籍列表
     */
    public void updateBookWithAsync(final List<BookShelfBean> bookShelfList)
    {
        daoSession.startAsyncSession().runInTx(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //刪除所有书籍.
                    shelfBookBeanDao.deleteAll();
                    //保存收藏书籍.
                    if (bookShelfList != null && !bookShelfList.isEmpty())
                    {
                        saveBooks(bookShelfList);
                    }
                } catch (Throwable throwable)
                {
                    Logger.e(TAG, "updateBookWithAsync: 更新书架书籍列表异常:{}", throwable);
                }
            }
        });
    }

}
