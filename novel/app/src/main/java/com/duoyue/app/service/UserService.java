package com.duoyue.app.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.duoyue.app.common.mgr.BookShelfMgr;
import com.duoyue.app.common.mgr.PushMgr;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.receiver.PushMessageReceiver;
import com.duoyue.app.receiver.TagAliasOperatorHelper;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.MobileInfoPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.devices.SystemUtil;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.utils.SPUtils;

/**
 * 用户功能服务类
 *
 * @author caoym
 * @data 2019/4/10  14:01
 */
public class UserService extends MiniService {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#UserService";

    /**
     * 构造方法.
     *
     * @param service
     */
    public UserService(Service service) {
        super(service);
        //注册登录广播接收器.
        registerReceiver();
        //更新数据.
        updateData();
        //启动百度定位功能.
        BDLocationMgr.startLocation();
        //解决在Android P上的提醒弹窗.
        SystemUtil.closeAndroidPDialog();
    }

    @Override
    public void onStartCommand(Intent intent) {

    }

    /**
     * 更新用户相关数据.
     */
    private void updateData() {
        //判断用户是否已登录.
        if (UserManager.getInstance().getUserInfo() == null) {
            //用户未登录, 不进行数据刷新.
            Logger.e(TAG, "updateData: 用户未登录, 不进行用户相关数据更新.");
            return;
        }
        Logger.i(TAG, "updateData: ");
        //上报阅读口味.
        StartGuideMgr.updateReadingTasteInfo();
        //更新阅读历史记录.
        ReadHistoryMgr.updateRecordBookList();
        //上报用户手机信息.
        MobileInfoPresenter.uploadMobileInfo();
        //更新书架信息
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                BookShelfMgr.getBookShelfDataList();
            }
        });
    }

    /**
     * 登录成功.
     */
    private void onLoginSucc(Context context) {
        //更新数据.
        updateData();
        try {
            //登录成功, 需要重新设置极光推送别名.
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            if (userInfo == null) {
                return;
            }
            String oldUid = SPUtils.INSTANCE.getString(TagAliasOperatorHelper.REGISTER_MSG);
            if (TextUtils.isEmpty(oldUid)) {//为空,注册极光别名
                PushMessageReceiver.regiesterJiGuang(context);
            } else if (!TextUtils.equals(oldUid, userInfo.uid.replace("-", ""))) {//现有uid和已注册的uid不同,删除原有别名,重新注册
                PushMessageReceiver.deletAliasJiGuang(context);
            }

            PushMgr.checkVivoAlias();
            PushMgr.checkXiaomiAlias(context);
            int brand = PushMgr.getBrand();
            if (brand != 3 && brand != 4) {
                PushMgr.uploadPushDeviceInfo();
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "onLoginSucc: {}", throwable);
        }
        try {
            //判断是否已授权成功(存储空间、手机信息、定位).
            if (PhoneUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) && PhoneUtil.checkPermission(context, Manifest.permission.READ_PHONE_STATE)
                    && PhoneUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已授权成功, 上报激活节点.
                FunctionStatsApi.authSucc();
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "onLoginSucc: {}", throwable);
        }
    }

    @Override
    public void onDestroy() {
        //注销广播.
        unRegisterReceiver();
    }

    /**
     * 注册广播接收器.
     */
    private void registerReceiver() {
        try {
            //注册登陆广播接收器.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.LOGIN_SUCC_ACTION);
            //注册广播.
            BaseContext.getContext().registerReceiver(mReceiver, intentFilter);
        } catch (Throwable throwable) {
            Logger.e(TAG, "registerReceiver: {}", throwable);
        }
    }

    /**
     * 注销广播接收器
     */
    private void unRegisterReceiver() {
        //============注销Dsp广告事件广播================
        try {
            BaseContext.getContext().unregisterReceiver(mReceiver);
        } catch (Throwable throwable) {
            Logger.e(TAG, "unRegisterReceiver: {}", throwable);
        }
    }

    /**
     * 广播接收器.
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Logger.i(TAG, "onReceive: {}", intent.getAction());
                if (Constants.LOGIN_SUCC_ACTION.equals(intent.getAction())) {
                    //登录成功.
                    onLoginSucc(context);
                }
            } catch (Throwable throwable) {
                Logger.e(TAG, "onReceive: {}", throwable);
            }
        }
    };
}
