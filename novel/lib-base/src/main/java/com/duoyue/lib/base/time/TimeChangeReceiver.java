package com.duoyue.lib.base.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "Base#TimeChangeReceiver";
    private static String currData;
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals(Intent.ACTION_TIME_TICK)
                || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)
                || intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            Logger.e("date_changed", "接收到时间变更广播，当前日期" + currData);
            if (TextUtils.isEmpty(currData)) {
                currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02);
            } else if (!currData.equals(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02))) {
                EventBus.getDefault().post(new AdConfigEvent());
                currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02);
                Logger.e("date_changed", "接收到日期变更广播，开始更新数据，当前日期: " + currData);
            }
        }*/

    }
}
