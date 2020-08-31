package com.duoyue.app.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.duoyue.app.bean.MessageBean;
import com.duoyue.app.common.mgr.PushMgr;
import com.duoyue.app.splash.SplashActivity;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xiaomi.mipush.sdk.*;
import com.zydm.base.common.ParamKey;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.SPUtils;
import com.zzdm.ad.router.BaseData;

import java.util.List;

public class XiaoMiMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "App#XiaoMiMessageReceiver";
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Logger.d(TAG, "onReceivePassThroughMessage: mTopic=" + mTopic + "<-->mAlias=" + mAlias + "<-->mUserAccount=" + mUserAccount);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        MessageBean messageBean = new Gson().fromJson(mMessage, MessageBean.class);
        Logger.d(TAG, "onNotificationMessageClicked: " + messageBean.getType() + "<-->" + messageBean.getPath() + "<-->" + messageBean.getUserType());
        //点击通知统计
        FunctionStatsApi.bdPushBookClick(Integer.parseInt(messageBean.getUserType()), messageBean.getPath());
        FuncPageStatsApi.pushClick(Integer.parseInt(messageBean.getType()) == 1 ? Long.parseLong(messageBean.getPath()) : 0, Integer.parseInt(messageBean.getUserType()));
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
        intent.putExtra(ParamKey.PUSH_DATA_KEY, mMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Logger.d(TAG, "onNotificationMessageArrived: mTopic=" + mTopic + "<-->mAlias=" + mAlias + "<-->mUserAccount=" + mUserAccount);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
        Logger.d(TAG, "onCommandResult: mRegId=" + mRegId + "<-->mTopic" + mTopic + "<-->mAlias=" + mAlias + "<-->mUserAccount=" + mUserAccount);
        if (mAlias != null) {
            SPUtils.INSTANCE.putString(PushMgr.SET_XIAOMI_ALIAS, mAlias);
            PushMgr.uploadPushDeviceInfo();
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
        Logger.d(TAG, "onNotificationMessageArrived: mRegId=" + mRegId);
    }
}