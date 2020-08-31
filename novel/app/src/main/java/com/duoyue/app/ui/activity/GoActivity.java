package com.duoyue.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.duoyue.app.notification.NotificationHolderV2Service;
import com.duoyue.lib.base.log.Logger;

//金立手机拉活
public class GoActivity extends Activity {


    private static final String TAG = "GoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "金立手机拉活");
        Intent intent = new Intent(GoActivity.this, NotificationHolderV2Service.class);
        intent.putExtra(NotificationHolderV2Service.INTENT_KEY, NotificationHolderV2Service.LIVE_CODE);
        startService(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
