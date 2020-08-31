package com.duoyue.mianfei.xiaoshuo.read.utils;

import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.dao.AppDaoDbHelper;
import com.duoyue.app.dao.gen.BookDownloadDBBeanDao;
import com.duoyue.app.dao.gen.DaoSession;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class BookDownloadHelper {

    private static volatile BookDownloadHelper sInstance;

    private static DaoSession daoSession;

    private static BookDownloadDBBeanDao bookDownloadDBBeanDao;

    public synchronized static BookDownloadHelper getsInstance() {

        if (sInstance == null) {
            synchronized (BookDownloadHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookDownloadHelper();
                    daoSession = AppDaoDbHelper.getInstance().getSession();
                    bookDownloadDBBeanDao = daoSession.getBookDownloadDBBeanDao();
                }
            }
        }
        return sInstance;
    }

    /**
     * 新增下载任务
     */
    public void saveDownloadTask(List<BookDownloadDBBean> downloadDBBeans) {
        bookDownloadDBBeanDao.insertOrReplaceInTx(downloadDBBeans);
    }

    /**
     * 修改下载任务
     *
     * @param downloadDBBean
     */
    public void updateDownloadTask(BookDownloadDBBean downloadDBBean) {
        bookDownloadDBBeanDao.update(downloadDBBean);
    }

    /**
     * 删除下载任务
     *
     * @param downloadDBBean
     */
    public void deleteDownloadTask(BookDownloadDBBean downloadDBBean) {
        bookDownloadDBBeanDao.delete(downloadDBBean);
    }

    /**
     * 删除一本书的所有下载记录
     */
    public void deleteBookDownloadTask(String bookId) {
        QueryBuilder<BookDownloadDBBean> where = bookDownloadDBBeanDao.queryBuilder().where(BookDownloadDBBeanDao.Properties.BookId.eq(bookId));
        DeleteQuery<BookDownloadDBBean> deleteQuery = where.buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 查询某本书已经下载完成的任务
     */
    public List<BookDownloadDBBean> queryDownloadCompleteTask(String bookId) {

        List<BookDownloadDBBean> beanList = bookDownloadDBBeanDao.queryBuilder().where(
                BookDownloadDBBeanDao.Properties.BookId.eq(bookId), BookDownloadDBBeanDao.Properties.States.eq(1)).list();
        return beanList;
    }

    /**
     * 查询某本书已经下载完成的任务的总数
     */
    public long queryDownloadCompleteTaskCount(String bookId) {

        long count = bookDownloadDBBeanDao.queryBuilder().where(
                BookDownloadDBBeanDao.Properties.BookId.eq(bookId), BookDownloadDBBeanDao.Properties.States.eq(1)).count();
        return count;
    }

    /**
     * 查询某本书已经下载完成的任务--从某个起始位置开始50章
     */
    public List<BookDownloadDBBean> queryDownloadCompleteTaskByGroup(String bookId, int startSeqNum) {

        List<BookDownloadDBBean> beanList = bookDownloadDBBeanDao.queryBuilder().where(
                BookDownloadDBBeanDao.Properties.BookId.eq(bookId),
                BookDownloadDBBeanDao.Properties.States.eq(1),
                BookDownloadDBBeanDao.Properties.SeqNum.ge(startSeqNum),
                BookDownloadDBBeanDao.Properties.SeqNum.le(startSeqNum + 50)).list();
        return beanList;
    }

    /**
     * 查询所有未下载完成的任务
     *
     * @return
     */
    public List<BookDownloadDBBean> queryAllDonwloadNotDoneTask() {

        List<BookDownloadDBBean> beanList = bookDownloadDBBeanDao.queryBuilder().where(
                BookDownloadDBBeanDao.Properties.States.eq(0)).list();
        return beanList;
    }

    /**
     * 查询某本书的某个章节
     *
     * @return
     */
    public BookDownloadDBBean queryChapterByChapterId(String bookId, int chapterId) {

        List<BookDownloadDBBean> beanList = bookDownloadDBBeanDao.queryBuilder().where(
                BookDownloadDBBeanDao.Properties.BookId.eq(bookId),
                BookDownloadDBBeanDao.Properties.ChapterId.eq(chapterId)).list();
        if (!beanList.isEmpty()) {
            return beanList.get(0);
        }
        return null;
    }

}
