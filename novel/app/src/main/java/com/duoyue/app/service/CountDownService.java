package com.duoyue.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.utils.SharePreferenceUtils;

public class CountDownService extends MiniService {
    private static long mTotalTime;
    private Handler handler;
    private Context context;

    public CountDownService(Service service) {
        super(service);
        //此处需要获取免广告剩余时间, 防止用户再阅读器通过杀进程退出, 然后启动时进入阅读器时, 免广告卡失效的问题. V1.3.0  20191127
        BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE);
        mTotalTime = bookRecordGatherResp != null ? bookRecordGatherResp.getLastSec() : 0;
        //大于0且小于等于60的情况下, 加60了为了解决倒计时任务启动时会减去60, 导致剩余免广告时间小于或等于1分钟时, 不会进行免广告.
        mTotalTime = mTotalTime > 0 && mTotalTime <= 60 ? mTotalTime + 60 : mTotalTime;
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Task());
        context = service.getApplicationContext();


    }

    public static void setTotalTime(long totalTime) {
        mTotalTime = totalTime;
        BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE);
        if (bookRecordGatherResp != null) {
            bookRecordGatherResp.setLastSec(mTotalTime);
            SharePreferenceUtils.putObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE, bookRecordGatherResp);
        }
    }

    @Override
    public void onStartCommand(Intent intent) {

    }

    @Override
    public void onDestroy() {

    }

    private class Task implements Runnable {
        @Override
        public void run() {
            handler.postDelayed(this, 60000);
            if (mTotalTime > 0) {
                mTotalTime = mTotalTime - 60;
                BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(context, SharePreferenceUtils.READ_HISTORY_CACHE);
                if (bookRecordGatherResp != null) {
                    bookRecordGatherResp.setLastSec(mTotalTime);
                    SharePreferenceUtils.putObject(context, SharePreferenceUtils.READ_HISTORY_CACHE, bookRecordGatherResp);
                }
                Log.d("CountDownService", "run: " + mTotalTime);
            } else {
                BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(context, SharePreferenceUtils.READ_HISTORY_CACHE);
                if (bookRecordGatherResp != null) {
                    bookRecordGatherResp.setLastSec(0);
                    SharePreferenceUtils.putObject(context, SharePreferenceUtils.READ_HISTORY_CACHE, bookRecordGatherResp);
                }
            }
        }
    }
}
