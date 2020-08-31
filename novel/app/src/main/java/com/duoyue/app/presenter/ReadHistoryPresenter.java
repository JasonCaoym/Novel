package com.duoyue.app.presenter;

import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.ui.adapter.ReadHistoryAdapter;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfHelper;

import java.util.List;

/**
 * 阅读历史记录
 * @author caoym
 * @data 2019/4/17  11:13
 */
public class ReadHistoryPresenter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#ReadHistoryPresenter";

    /**
     * 当前类对象
     */
    private static ReadHistoryPresenter sInstance;

    /**
     * 构造方法
     */
    private ReadHistoryPresenter()
    {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (ReadHistoryPresenter.class)
            {
                if (sInstance == null)
                {
                    sInstance = new ReadHistoryPresenter();
                }
            }
        }
    }

    /**
     * 删除所有阅读历史记录信息
     * @return
     */
    public static String removeAllReadHistory()
    {
        return ReadHistoryMgr.removeAllReadHistory();
    }

    /**
     * 删除指定书籍阅读历史记录信息
     * @return
     */
    public static String removeReadHistory(String bookId)
    {
        return ReadHistoryMgr.removeReadHistory(Long.valueOf(bookId));
    }

    /**
     * 获取阅读历史记录列表
     * @param offset 开始位置
     */
    public static List<BookRecordBean> getPageHistoryDataList(int offset)
    {
        return BookRecordHelper.getsInstance().findAllBooks(offset > 0 ? offset : 0, ReadHistoryAdapter.PAGE_COUNT);
    }

    /**
     * 查询书籍阅读历史记录信息.
     * @param bookId
     * @return
     */
    public static BookRecordBean findBookRecordBean(String bookId)
    {
        return BookRecordHelper.getsInstance().findBookRecordById(bookId);
    }
}
