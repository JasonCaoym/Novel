package com.duoyue.app.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.duoyue.app.bean.MessageBean;
import com.duoyue.app.splash.SplashActivity;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.google.gson.Gson;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;
import com.zydm.base.common.ParamKey;
import com.zydm.base.ui.activity.BaseActivity;
import com.zzdm.ad.router.BaseData;

import java.util.Map;


/**
 * vivo推送自定义回调类
 */
public class PushMessageReceiverImpl extends OpenClientPushMessageReceiver {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#PushMessageReceiverImpl";


    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage upsNotificationMessage) {
        try {
            Map<String, String> params = upsNotificationMessage.getParams();
            if (params == null) {
                Logger.d(TAG, "onNotificationMessageClicked: 参数为空");
                return;
            }
            MessageBean messageBean = new MessageBean();
            String path = params.get("path");
            String type = params.get("type");
            String userType = params.get("userType");
            String sex = params.get("sex");
            String tag = params.get("tag");
            String category = params.get("category");
            if (!TextUtils.isEmpty(path)) {
                messageBean.setPath(path);
            }
            if (!TextUtils.isEmpty(type)) {
                messageBean.setType(type);
            }
            if (!TextUtils.isEmpty(userType)) {
                messageBean.setUserType(userType);
            }
            if (!TextUtils.isEmpty(sex)) {
                messageBean.setSex(sex);
            }
            if (!TextUtils.isEmpty(tag)) {
                messageBean.setTag(tag);
            }
            if (!TextUtils.isEmpty(category)) {
                messageBean.setCategory(category);
            }

            Logger.d(TAG, "onNotificationMessageClicked: " + messageBean.getType() + "<-->" + messageBean.getPath() + "<-->" + messageBean.getUserType());
            //点击通知统计
            FunctionStatsApi.bdPushBookClick(Integer.parseInt(messageBean.getUserType()), messageBean.getPath());
            FuncPageStatsApi.pushClick("1".equals(messageBean.getType()) ? Long.parseLong(messageBean.getPath()) : 0, Integer.parseInt(messageBean.getUserType()));
            Intent intent;
            //判断HomeActivity是否在堆栈中.
            if (Utils.isExsitMianActivity(context, HomeActivity.class)) {
                //已经在堆栈中, 直接启动HomeActivity.
                intent = new Intent(context, HomeActivity.class);
            } else {
                //启动开屏页.
                intent = new Intent(context, SplashActivity.class);
            }
            intent.putExtra(BaseActivity.DATA_KEY, new BaseData("PUSH"));
            intent.putExtra(ParamKey.PUSH_DATA_KEY, new Gson().toJson(messageBean));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        String responseString = "onReceiveRegId regId = " + regId;
        Logger.d(TAG, "onReceiveRegId: " + responseString);
    }
}
