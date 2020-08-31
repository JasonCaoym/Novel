package com.zydm.base.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.zydm.base.common.BaseApplication;

public class ZydmReceiver extends BroadcastReceiver {

    private Listener listene;

    public ZydmReceiver(Listener listene) {
        this.listene = listene;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action )) {
            if (listene != null) {
                listene.homeKeyPressed();
            }
        }
    }

    public interface Listener {
        void homeKeyPressed();
    }
}
