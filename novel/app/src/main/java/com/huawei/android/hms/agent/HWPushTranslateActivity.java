package com.huawei.android.hms.agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.duoyue.app.bean.MessageBean;
import com.duoyue.app.splash.SplashActivity;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.google.gson.Gson;
import com.zydm.base.common.ParamKey;
import com.zydm.base.ui.activity.BaseActivity;
import com.zzdm.ad.router.BaseData;


/**
 * 华为推送点击跳转的activity
 */
public class HWPushTranslateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String action = getIntent().getData().getQueryParameter("action");
            MessageBean messageBean = !StringFormat.isEmpty(action) ? new Gson().fromJson(action, MessageBean.class) : null;
            if (messageBean == null) {
                return;
            }
            Logger.d("HWPushTranslateActivity", "onCreate: " + messageBean.getType() + " <--> " + messageBean.getPath() + " <--> " + messageBean.getUserType());
            //点击通知统计
            FunctionStatsApi.bdPushBookClick(Integer.parseInt(messageBean.getUserType()), messageBean.getPath());
            FuncPageStatsApi.pushClick(Integer.parseInt(messageBean.getType()) == 1 ? Long.parseLong(messageBean.getPath()) : 0, Integer.parseInt(messageBean.getUserType()));
            Intent intent;
            //判断HomeActivity是否在堆栈中.
            if (Utils.isExsitMianActivity(this, HomeActivity.class)) {
                //已经在堆栈中, 直接启动HomeActivity.
                intent = new Intent(this, HomeActivity.class);
            } else {
                //启动开屏页.
                intent = new Intent(this, SplashActivity.class);
            }
            intent.putExtra(BaseActivity.DATA_KEY, new BaseData("PUSH"));
            intent.putExtra(ParamKey.PUSH_DATA_KEY, action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
