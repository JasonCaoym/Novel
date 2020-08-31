package com.duoyue.lib.base.crash;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 崩溃日志上报.
 */
public class CrashLogPresenter {
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#CrashLogPresenter";

    /**
     * 上报崩溃日志
     *
     * @param crashInfo
     */
    public static void uploadCrashLog(String crashInfo) {
        try {
            //判断网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
                //网络不可用.
                return;
            }
            List<CrashList> lists = new ArrayList<>();
            lists.add(new CrashList(crashInfo));
            new JsonPost.AsyncPost<Object>().setRequest(new CrashLogRequest(lists)).setResponseType(Object.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<Object>>() {
                @Override
                protected void onStart() {
                    super.onStart();
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onNext(JsonResponse<Object> response) {
                    if (response == null || response.status != 1) {
                        //上报数据失败.
                        Logger.e(TAG, "onNext: " + response == null ? "上报失败" : response.msg);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Logger.e(TAG, "onError: 上报失败");
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "uploadCrashLog: 上报失败");
        }
    }

    /**
     * 上报数据拉取成功和失败
     *
     * @param url      拉取接口地址
     * @param operator 操作    1拉取成功  2拉取失败
     */
    public static void uploadPullData(String url, String operator) {
        /*try {
            //判断网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
                //网络不可用.
                return;
            }
            List<PullDataList> pullDataLists = new ArrayList<>();
            pullDataLists.add(new PullDataList("", url, operator));
            new JsonPost.AsyncPost<Object>().setRequest(new PullDataRequest(pullDataLists)).setResponseType(Object.class)
                    .subscribeOn(ZSchedulers.getInstance().io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<Object>>() {
                @Override
                protected void onStart() {
                    super.onStart();
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onNext(JsonResponse<Object> response) {
                    if (response == null || response.status != 1) {
                        //上报数据失败.
                        Logger.e(TAG, "onNext: " + response == null ? "上报失败" : response.msg);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Logger.e(TAG, "onError: 上报失败");
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "uploadCrashLog: 上报失败");
        }*/
    }
}
