package com.duoyue.app.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.duoyue.app.bean.MessageBean;
import com.duoyue.app.event.TabSwitchEvent;
import com.duoyue.app.splash.SplashActivity;
import com.duoyue.app.ui.activity.BookRankActivity;
import com.duoyue.app.ui.activity.CategoryBookListActivity;
import com.duoyue.app.ui.fragment.NewCategoryActivity;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.google.gson.Gson;
import com.zydm.base.common.ParamKey;
import com.zydm.base.ui.activity.BaseActivity;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static com.duoyue.app.receiver.TagAliasOperatorHelper.sequence;
import static com.zzdm.tinker.util.SampleApplicationContext.application;

public class PushMessageReceiver extends JPushMessageReceiver {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#PushMessageReceiver";

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Logger.i(TAG, "onMessage: {}", customMessage);
        processCustomMessage(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Logger.i(TAG, "onNotifyMessageOpened: {}", message.notificationId);
        try {
            MessageBean messageBean = !StringFormat.isEmpty(message.notificationExtras) ? new Gson().fromJson(message.notificationExtras, MessageBean.class) : null;
            if (messageBean == null) {
                return;
            }
            //点击通知统计  userType  有时可能为空
            if (!TextUtils.isEmpty(messageBean.getUserType())){
                FunctionStatsApi.bdPushBookClick(Integer.parseInt(messageBean.getUserType()), messageBean.getPath());
                FuncPageStatsApi.pushClick(Integer.parseInt(messageBean.getType()) == 1 ? Long.parseLong(messageBean.getPath()) : 0, Integer.parseInt(messageBean.getUserType()));
            }
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
            intent.putExtra(ParamKey.PUSH_DATA_KEY, message.notificationExtras);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,message.notificationTitle);
//            bundle.putString(JPushInterface.EXTRA_ALERT,message.notificationContent);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);
        } catch (Throwable throwable) {
            Logger.e(TAG, "onNotifyMessageOpened: {}", throwable);
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Logger.i(TAG, "onNotifyMessageArrived: {}", message);
        MessageBean messageBean = !StringFormat.isEmpty(message.notificationExtras) ? new Gson().fromJson(message.notificationExtras, MessageBean.class) : null;
        if (messageBean == null) {
            return;
        }
        //push曝光统计
        FuncPageStatsApi.pushExpose(Integer.parseInt(messageBean.getType()) == 1 ? Long.parseLong(messageBean.getPath()) : 0, Integer.parseInt(messageBean.getUserType()));
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Logger.i(TAG, "onNotifyMessageDismiss: {}", message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Logger.i(TAG, "onRegister: {}", registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Logger.i(TAG, "onConnected: {}", isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Logger.i(TAG, "onCommandResult: {}", cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, CustomMessage customMessage) {
//        if (MainActivity.isForeground) {
//            String message = customMessage.message;
//            String extras = customMessage.extra;
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!TextUtils.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getsInstance(context).sendBroadcast(msgIntent);
//        }
    }

    public static void regiesterJiGuang() {
        try {
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            if (userInfo == null) {
                return;
            }
            TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
            tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
            tagAliasBean.alias = userInfo.uid.replace("-", "");
            Logger.i(TAG, "regiesterJiGuang: {}", userInfo.uid);
            tagAliasBean.isAliasAction = true;
            sequence++;
            TagAliasOperatorHelper.getInstance().handleAction(application, sequence, tagAliasBean);
        } catch (Throwable throwable) {
            Logger.e(TAG, "regiesterJiGuang: {}", throwable);
        }
    }

    public static void deletAliasJiGuang() {
        try {
            TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
            tagAliasBean.action = TagAliasOperatorHelper.ACTION_DELETE;
            tagAliasBean.alias = "";
            tagAliasBean.isAliasAction = true;
            sequence--;
            TagAliasOperatorHelper.getInstance().handleAction(application, sequence, tagAliasBean);
        } catch (Throwable throwable) {
            Logger.e(TAG, "regiesterJiGuang: {}", throwable);
        }
    }

    /**
     * 打开Push消息.
     *
     * @param activity
     * @param pushMessage Push数据
     */
    public static void openPushMessage(Activity activity, String pushMessage) {
        try {
            Logger.i(TAG, "openPushMessage: {}, {}", activity, pushMessage);
            MessageBean messageBean = !StringFormat.isEmpty(pushMessage) ? new Gson().fromJson(pushMessage, MessageBean.class) : null;
            if (messageBean == null) {
                return;
            }
            if ("1".equals(messageBean.getType()) && "2".equals(messageBean.getType())) {
                Logger.e(TAG, "openPushMessage: {}, type error", messageBean.getType());
                return;
            }
            switch (Integer.parseInt(messageBean.getType())) {
                case 1:
                    //书籍
                    if ("1".equals(messageBean.getUserType())) {
                        com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(activity, messageBean.getPath(), new BaseData("PUSH"),
                                PageNameConstants.PUSH, 15, PageNameConstants.PUSH_RECOMMEND);
                    } else if ("2".equals(messageBean.getUserType())) {
                        com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(activity, messageBean.getPath(), new BaseData("PUSH"),
                                PageNameConstants.PUSH, 16, PageNameConstants.PUSH_RECOMMEND);
                    } else {
                        com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(activity, messageBean.getPath(), new BaseData("PUSH"),
                                PageNameConstants.PUSH, messageBean.getModelId(), PageNameConstants.PUSH_RECOMMEND);
                    }
                    break;
                case 2:
                    //H5
                    ActivityHelper.INSTANCE.gotoWeb(activity, messageBean.getPath());
                    break;
                case 3:
                    EventBus.getDefault().post(new TabSwitchEvent(HomeActivity.BOOKSHELF));
                    break;
                case 4:
                    EventBus.getDefault().post(new TabSwitchEvent(HomeActivity.BOOK_CITY));
                    break;
                case 5:
                    Intent intent2 = new Intent(activity, BookRankActivity.class);
                    intent2.putExtra(BookRankActivity.CLASSID, Integer.valueOf(messageBean.getPath()));
                    intent2.putExtra(BookRankActivity.SELECTEDID, Integer.valueOf(messageBean.getSex()));
                    activity.startActivity(intent2);
                    break;
                case 6:
                    Intent intent = new Intent(activity, NewCategoryActivity.class);
                    intent.putExtra(NewCategoryActivity.SELECTED, Integer.valueOf(messageBean.getSex()));
                    activity.startActivity(intent);
                    break;
                case 7:
                    Intent intent1 = new Intent(activity, CategoryBookListActivity.class);
                    intent1.putExtra(CategoryBookListActivity.CLASSID, Integer.valueOf(messageBean.getPath()));
                    if (!TextUtils.isEmpty(messageBean.getTag())) {

                        intent1.putExtra(CategoryBookListActivity.TAG_N, messageBean.getTag());
                    }
                    if (!TextUtils.isEmpty(messageBean.getCategory())) {
                        intent1.putExtra(CategoryBookListActivity.CATEGORY_N, messageBean.getCategory());
                    }
                    intent1.putExtra(CategoryBookListActivity.SELECTEDID, Integer.valueOf(messageBean.getSex()));
                    activity.startActivity(intent1);
                    break;
                case 8:
                    //阅读器
                    Intent read = new Intent(activity, ReadActivity.class);
                    read.putExtra(ParamKey.BOOK_ID, messageBean.getPath());
                    read.putExtra(BaseActivity.DATA_KEY, new BaseData("通知栏继续阅读"));
                    read.putExtra(RouterPath.KEY_PARENT_ID, "");
                    read.putExtra(RouterPath.KEY_SOURCE, "");
                    activity.startActivity(read);
                    FuncPageStatsApi.notifyBookClick(Long.valueOf(messageBean.getPath()), messageBean.getModelId());
                    break;
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "openPushMessage: {}, {}, {}", activity, pushMessage, throwable);
        }
    }
}
