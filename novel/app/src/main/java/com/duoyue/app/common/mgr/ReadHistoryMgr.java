package com.duoyue.app.common.mgr;

import com.duoyue.app.common.data.request.bookrecord.*;
import com.duoyue.app.common.data.response.bookrecord.AddBookRecordResp;
import com.duoyue.app.common.data.response.bookrecord.BookRecordInfoResp;
import com.duoyue.app.common.data.response.bookrecord.BookRecordListResp;
import com.duoyue.app.common.data.response.bookrecord.RemoveBookRecordResp;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.utils.ViewUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoym
 * @data 2019/3/28  21:06
 */
public class ReadHistoryMgr
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#ReadHistoryMgr";

    /**
     * 请求成功标识.
     */
    public static final String HTTP_OK = "OK";

    /**
     * 当前类对象
     */
    private static volatile ReadHistoryMgr sInstance;

    private ReadHistoryMgr()
    {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (ReadHistoryMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new ReadHistoryMgr();
                }
            }
        }
    }

    /**
     * 获取历史阅读记录汇总信息
     * @return
     */
    public static BookRecordGatherResp getBookRecordGather()
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "getBookRecordGather: 网络不可用");
            return null;
        }
        try {
            //创建当前类对象.
            createInstance();
            JsonResponse<BookRecordGatherResp> jsonResponse = new JsonPost.SyncPost<BookRecordGatherResp>().setRequest(new BookRecordGatherReq()).setResponseType(BookRecordGatherResp.class).post();
            return jsonResponse != null && jsonResponse.status == 1 ? jsonResponse.data : null;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getBookRecordGather: {}", throwable);
            return null;
        }
    }

    /**
     * 添加阅读记录
     * @param bookRecordBean 阅读记录对象
     */
    public static void addRecordBook(BookRecordBean bookRecordBean)
    {
        if (bookRecordBean == null || StringFormat.isEmpty(bookRecordBean.getBookId()))
        {
            Logger.e(TAG, "addRecordBook: bookId:{} 为空.", bookRecordBean != null ? bookRecordBean.getBookId() : "NULL");
            return;
        }
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "addRecordBook: 网络不可用.");
            return;
        }
        //创建当前类对象.
        createInstance();
        try {
            Logger.i(TAG, "addRecordBook: begin");
            new JsonPost.AsyncPost<AddBookRecordResp>().setRequest(new AddBookRecordReq(bookRecordBean)).setResponseType(AddBookRecordResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<AddBookRecordResp>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                    Logger.i(TAG, "addRecordBook: onStart: ");
                }
                @Override
                public void onNext(JsonResponse<AddBookRecordResp> jsonResponse)
                {
                    Logger.i(TAG, "addRecordBook: onNext: {}", jsonResponse);
                }
                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "addRecordBook: onComplete: ");
                }
                @Override
                public void onError(Throwable e)
                {
                    Logger.i(TAG, "addRecordBook: onError: {}", e);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "addRecordBook: {}", throwable);
            return;
        }
    }

    /**
     * 更新阅读记录列表(启动应用或切换用户时调用)
     */
    public static void updateRecordBookList()
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "getRecordBookList: 网络不可用.");
            return;
        }
        //创建当前类对象.
        createInstance();
        try {
            Logger.i(TAG, "getRecordBookList: begin");
            new JsonPost.AsyncPost<BookRecordListResp>().setRequest(new BookRecordListReq()).setResponseType(BookRecordListResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<BookRecordListResp>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                    Logger.i(TAG, "getRecordBookList: onStart: ");
                }
                @Override
                public void onNext(JsonResponse<BookRecordListResp> jsonResponse)
                {
                    Logger.i(TAG, "getRecordBookList: onNext: {}", jsonResponse != null ? jsonResponse.data : jsonResponse);
                    if (jsonResponse != null && jsonResponse.status == 1)
                    {
                        //请求成功.
                        BookRecordListResp bookRecordListResp = jsonResponse.data != null ? jsonResponse.data : null;
                        List<BookRecordInfoResp> bookRecordInfoRespList = bookRecordListResp != null ? bookRecordListResp.getStoredBookList() : null;
                        List<BookRecordBean> bookRecordList = null;
                        if (bookRecordInfoRespList != null && bookRecordInfoRespList.size() > 0)
                        {
                            bookRecordList = new ArrayList<>();
                            for (BookRecordInfoResp recordInfoResp : bookRecordInfoRespList)
                            {
                                if (recordInfoResp == null)
                                {
                                    continue;
                                }
                                bookRecordList.add(recordInfoResp.toBookRecordBean());
                            }
                        }
                        //更新阅读历史记录信息.
                        BookRecordHelper.getsInstance().updateRecordBookWithAsync(bookRecordList);
                    }
                }
                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "getRecordBookList: onComplete: ");
                }
                @Override
                public void onError(Throwable e)
                {
                    Logger.i(TAG, "getRecordBookList: onError: {}", e);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getRecordBookList: {}", throwable);
            return;
        }
    }

    /**
     * 删除所有的阅读历史记录.
     */
    public static String removeAllReadHistory()
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "removeAllReadHistory: 网络不可用.");
            return getNetworkErrorPrompt();
        }
        try {
            //创建当前类对象.
            createInstance();
            JsonResponse<RemoveBookRecordResp> jsonResponse = new JsonPost.SyncPost<RemoveBookRecordResp>().setRequest(new RemoveAllBookRecordReq()).setResponseType(RemoveBookRecordResp.class).post();
            return jsonResponse != null && jsonResponse.status == 1 ? HTTP_OK : "移除历史记录失败, 请稍后重试";
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeAllReadHistory: {}", throwable);
            return "移除历史记录失败, 请稍后重试";
        }
    }

    /**
     * 删除阅读历史记录.
     */
    public static String removeReadHistory(long bookId)
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
        {
            Logger.e(TAG, "removeReadHistory: 网络不可用.");
            return getNetworkErrorPrompt();
        }
        try {
            //创建当前类对象.
            createInstance();
            JsonResponse<RemoveBookRecordResp> jsonResponse = new JsonPost.SyncPost<RemoveBookRecordResp>().setRequest(new RemoveBookRecordReq(new RemoveBookRecordInfoReq(bookId))).setResponseType(RemoveBookRecordResp.class).post();
            return jsonResponse != null && jsonResponse.status == 1 ? HTTP_OK : "移除历史记录失败, 请稍后重试";
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeReadHistory: {}", throwable);
            return "移除历史记录失败, 请稍后重试";
        }
    }

    /**
     * 网络错误提示.
     * @return
     */
    private static String getNetworkErrorPrompt()
    {
        return ViewUtils.getString(R.string.toast_no_net);
    }

}
