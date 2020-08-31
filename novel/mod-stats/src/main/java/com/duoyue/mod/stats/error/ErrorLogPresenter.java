package com.duoyue.mod.stats.error;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.FunctionStatsApi;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 错误日志上报.
 * @author caoym
 * @data 2019/5/15  15:52
 */
public class ErrorLogPresenter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#ErrorLogPresenter";

    /**
     *
     * @param errorType
     * @param errorMsg
     */
    public static void addError(final String errorType, final String errorMsg)
    {
        try
        {
            Logger.e(TAG, "addError: {}, {}", errorType, errorMsg);
            //判断网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
            {
                //网络不可用.
                uploadFail(errorType);
                return;
            }
            new JsonPost.AsyncPost<Object>().setRequest(new ErrorLogRequest(errorType, errorMsg)).setResponseType(Object.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<Object>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                }

                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "uploadData: onComplete:  {}, {}", errorType, errorMsg);
                }

                @Override
                public void onNext(JsonResponse<Object> response)
                {
                    Logger.i(TAG, "uploadData: onNext: {}, {}, {}", errorType, errorMsg, response);
                    if (response == null || response.status != 1)
                    {
                        //上报数据失败.
                        uploadFail(errorType);
                    }
                }

                @Override
                public void onError(Throwable throwable)
                {
                    Logger.e(TAG, "uploadData: onError: {}, {}, {}", errorType, errorMsg, throwable);
                    uploadFail(errorType);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "uploadData: {}, {}, {}", errorType, errorMsg, throwable);
            uploadFail(errorType);
        }
    }

    /**
     * 上报失败.
     * @param errorType
     */
    private static void uploadFail(String errorType)
    {
        //将错误类型添加到统计中, 进行离线上报.
        FunctionStatsApi.addError(errorType);
    }
}
