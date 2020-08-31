package com.duoyue.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

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
 * Oppo推送点击跳转的activity
 * 这个Activity没有实际的含义，只是起到一个承接的作用，即当点击通知时，获取传递的数据，然后根据不同的数据跳转到不同的页面。
 */
public class OppoPushTranslateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String action = getIntent().getData().getQueryParameter("action");
            MessageBean messageBean = !StringFormat.isEmpty(action) ? new Gson().fromJson(action, MessageBean.class) : null;
            if (messageBean == null) {
                return;
            }
            //点击通知统计
            if (TextUtils.isEmpty(messageBean.getUserType()) || TextUtils.isEmpty(messageBean.getPath()) || TextUtils.isEmpty(messageBean.getType())) {
                Logger.d("OppoPushTranslateActivity", "请检查userType  path  Type是否为空，如果为空联系后端开发人员");
                finish();
                return;
            } else {
                Logger.d("OppoPushTranslateActivity", "onCreate: " + messageBean.getType() + " <--> " + messageBean.getPath() + " <--> " + messageBean.getUserType());
            }
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
