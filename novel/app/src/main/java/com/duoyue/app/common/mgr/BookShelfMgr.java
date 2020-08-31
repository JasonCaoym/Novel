package com.duoyue.app.common.mgr;

import com.duoyue.app.common.data.request.bookshelf.*;
import com.duoyue.app.common.data.response.bookshelf.*;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读历史记录管理类
 * @author caoym
 * @data 2019/4/17  21:06
 */
public class BookShelfMgr
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookShelfMgr";

    /**
     * 请求成功标识.
     */
    public static final String HTTP_OK = "OK";

    /**
     * 当前类对象
     */
    private static volatile BookShelfMgr sInstance;

    private BookShelfMgr()
    {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (BookShelfMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new BookShelfMgr();
                }
            }
        }
    }

    /**
     * 添加书籍到书架.
     * @param bookShelfBean
     * @return
     */
    public static String addBookShelf(BookShelfBean bookShelfBean)
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            //离线操作，先加入数据库，再有网同步服务器
            bookShelfBean.setIsAddLocalDb(true);
            BookShelfHelper.getsInstance().saveBook(bookShelfBean);
            Logger.i(TAG, "bookShelfBean : " + bookShelfBean.getBookName()
                    + "  IsAddLocalDb" + bookShelfBean.getIsAddLocalDb());
            return "添加成功！";
        }
        //创建当前类对象.
        createInstance();
        try
        {
            //查询阅读历史记录.
            BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(bookShelfBean.bookId);
            AddBookShelfInfoReq addBookShelfInfoReq = new AddBookShelfInfoReq(
                    Long.parseLong(bookShelfBean.bookId),
                    bookRecordBean != null ? bookRecordBean.seqNum : 0,
                    bookRecordBean != null ? bookRecordBean.pagePos : 0,
                    bookShelfBean.chapterCount,
                    0,
                    bookRecordBean != null ? bookRecordBean.getLastRead() : 0,
                    bookRecordBean != null ? bookRecordBean.getChapterTitle() : "");
            JsonResponse<AddBookShelfResp> jsonResponse = new JsonPost.SyncPost<AddBookShelfResp>()
                    .setRequest(new AddBookShelfReq(addBookShelfInfoReq))
                    .setResponseType(AddBookShelfResp.class).post();
            return jsonResponse != null && jsonResponse.status == 1 ? HTTP_OK : "添加到书架失败, 请稍后重试";
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "addBookShelf: {}, 异常:{}", (bookShelfBean != null ? bookShelfBean.getBookId() : "NULL"), throwable);
            return  "添加到书架失败, 请稍后重试";
        }
    }

    /**
     * 移除书架上的书籍.
     * @param bookShelfBookInfoList
     * @return
     */
    public static String removeBooksShelf(List<BookShelfBookInfoResp> bookShelfBookInfoList)
    {
        if (bookShelfBookInfoList == null || bookShelfBookInfoList.isEmpty())
        {
            return null;
        }
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            return "网络不可用";
        }
        try {
            //创建当前类对象.
            createInstance();
            List<RemoveBookInfoReq> bookInfoList = new ArrayList<>();
            //遍历书籍列表.
            for (BookShelfBookInfoResp bookShelfBookInfo : bookShelfBookInfoList)
            {
                if (bookShelfBookInfo == null)
                {
                    continue;
                }
                bookInfoList.add(new RemoveBookInfoReq(bookShelfBookInfo.getBookId(), bookShelfBookInfo.getType()));
            }
            JsonResponse<RemoveBookShelfResp> jsonResponse = new JsonPost.SyncPost<RemoveBookShelfResp>().setRequest(new RemoveBookShelfReq(bookInfoList)).setResponseType(RemoveBookShelfResp.class).post();
            return jsonResponse != null && jsonResponse.status == 1 ? HTTP_OK : "移除到书架失败, 请稍后重试";
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeBooksShelf: {}, 异常:{}", (bookShelfBookInfoList != null ? bookShelfBookInfoList.size() : "NULL"), throwable);
            return  "移除到书架失败, 请稍后重试";
        }
    }

    /**
     * 更新书架列表.
     * @return
     */
    public static BookShelfListResp getBookShelfDataList()
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "getBookShelfDataList: 网络不可用.");
            return new BookShelfListResp(0);
        }
        //创建当前类对象.
        createInstance();
        try {
            JsonResponse<BookShelfListResp> jsonResponse = new JsonPost.SyncPost<BookShelfListResp>().setRequest(new BookShelfListReq()).setResponseType(BookShelfListResp.class).post();
            Logger.i(TAG, "getBookShelfDataList: {}", jsonResponse);
            //判断是否响应数据成功.
            if (jsonResponse == null || jsonResponse.status != 1)
            {
                return new BookShelfListResp(jsonResponse != null ? jsonResponse.status : 0);
            }
            //获取响应的数据.
            BookShelfListResp bookShelfListResp = jsonResponse.data != null ? jsonResponse.data : new BookShelfListResp();
            //设置为成功.
            bookShelfListResp.status = jsonResponse.status;
            //获取书籍书籍.
            List<BookShelfBookInfoResp> storedBookList = bookShelfListResp != null ? bookShelfListResp.getStoredBookList() : null;
            List<BookShelfBean> bookShelfList = new ArrayList<>();
            if (!StringFormat.isEmpty(storedBookList))
            {
                for (BookShelfBookInfoResp bookShelfInfoResp : storedBookList)
                {
                    bookShelfList.add(bookShelfInfoResp.toBookShelfBean());
                }
            }
            //调用更新数据接口.
            BookShelfHelper.getsInstance().updateBookWithAsync(bookShelfList);
            return bookShelfListResp == null ? new BookShelfListResp(1) : bookShelfListResp;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getBookShelfDataList: {}", throwable);
            return new BookShelfListResp(0);
        }
    }

    /**
     * 书籍置顶
     *
     * @param bookInfoResp
     * @param topping 1:置顶;2:取消置顶
     * @param content
     */
    public static String toppingBookShelf(BookShelfBookInfoResp bookInfoResp, int topping, String content) {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            return "网络不可用";
        }
        //创建当前类对象.
        createInstance();
        try {
            //查询阅读历史记录.
            BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(String.valueOf(bookInfoResp.getBookId()));
            AddBookShelfInfoReq addBookShelfInfoReq = new AddBookShelfInfoReq(
                    bookInfoResp.getBookId(),
                    bookInfoResp.getLastReadChapter(),
                    bookRecordBean != null ? bookRecordBean.getPagePos() : 0,
                    bookInfoResp.getLastPushChapter(),
                    topping,
                    bookRecordBean != null ? bookRecordBean.getLastRead() : 0,
                    bookRecordBean != null ? bookRecordBean.getChapterTitle() : "");

            JsonResponse<AddBookShelfResp> jsonResponse = new JsonPost.SyncPost<AddBookShelfResp>().setRequest(
                    new AddBookShelfReq(addBookShelfInfoReq)).setResponseType(AddBookShelfResp.class).post();


            return jsonResponse != null && jsonResponse.status == 1 ? HTTP_OK : content + "失败, 请稍后重试";
        } catch (Throwable throwable) {
            Logger.e(TAG, "toppingBookShelf: {}, 异常:{}", (bookInfoResp != null ? bookInfoResp.getBookId() : "NULL"), throwable);
            return content + "失败, 请稍后重试";
        }
    }


    /**
     * 获取书架顶部每日推荐书籍
     * @return
     */
    public static BookShelfRecoInfoResp getBookShelfRecoInfoResp() {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Logger.e(TAG, "getBookShelfDataList: 网络不可用.");
            return null;
        }
        //创建当前类对象.
        createInstance();

        try{
            JsonResponse<BookShelfRecoInfoResp> jsonResponse = new JsonPost.SyncPost<BookShelfRecoInfoResp>().setRequest(new DayRecommendBookReq()).setResponseType(BookShelfRecoInfoResp.class).post();
            Logger.i(TAG, "getBookShelfDataList: {}", jsonResponse);
            //判断是否响应数据成功.
            if (jsonResponse == null || jsonResponse.status != 1) {
                return null;
            }
            BookShelfRecoInfoResp bookShelfRecoInfoResp = jsonResponse.data;
            return bookShelfRecoInfoResp;
        }catch (Throwable throwable){
            Logger.e(TAG, "getBookShelfDataList: {}", throwable);
            return null;
        }
    }
}
