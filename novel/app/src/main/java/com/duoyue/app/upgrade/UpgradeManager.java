package com.duoyue.app.upgrade;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import com.duoyue.app.common.mgr.LauncherDialogMgr;
import com.duoyue.app.event.GetUpdateFinishEvent;
import com.duoyue.app.upgrade.download.DaoDownloadManager;
import com.duoyue.app.upgrade.download.UpgradeMsgBean;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.UpgradeDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.mine.MineManager;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.google.gson.Gson;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static com.zydm.base.common.BaseApplication.context;

/**
 *  管理apk升级
 * @author: fanwentao
 *
 */
public class UpgradeManager {
    private static final String TAG = "App#UpgradeManager";

    public static final String LAST_CLEAR_UPGRADE_DIALOG_TIME = "last_clear_upgrade_dialog_time";

    private static final String FORCE_UPDATE_VERSION_CODE = "UpgradeManager.force_update_version_code";
    private static final String IS_FORCE_UPGRADE_TAG = "is_force_upgrade_tag";

    public static final String IS_UPGRADE_REQUEST_FINISHED = "is_upgrade_request_finished";

    private Context mContext;
    private static UpgradeManager mUpgradeManager;
    private boolean checkOnHome = true;


    public boolean isCheckOnHome() {
        return checkOnHome;
    }

    public void setCheckOnHome(boolean checkOnHome) {
        this.checkOnHome = checkOnHome;
    }

    /**
     * 创建管理器单例
     *
     * @param context 上下文
     * @return UpgradeManager
     */
    public static UpgradeManager getInstance(Context context) {
        if (mUpgradeManager == null) {
            synchronized (UpgradeManager.class) {
                if (mUpgradeManager == null) {
                    mUpgradeManager = new UpgradeManager(context.getApplicationContext());
                }
            }
        }
        return mUpgradeManager;
    }

    private UpgradeManager(Context context) {
        mContext = context;
        initChannelNotification();
    }

    /**
     * 添加channel Id
     */
    public void initChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Resources resources = mContext.getResources();
            NotificationManager notificationManager =
                    mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(
                    new NotificationChannel(resources.getString(R.string.default_notification_channel_id),
                            resources.getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_LOW));
            notificationManager.createNotificationChannel(
                    new NotificationChannel(resources.getString(R.string.default_notification_update_channel_id),
                            resources.getString(R.string.default_notification_channel_update_name), NotificationManager.IMPORTANCE_LOW));

        }
    }

    public void startBackgroundCheck(final FragmentActivity activity, final String currPageId) {
        boolean isForceUpdate = BaseApplication.context.globalContext.getSharedPreferences(AdConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE).getBoolean(IS_FORCE_UPGRADE_TAG, true);
        SPUtils.INSTANCE.putBoolean(IS_UPGRADE_REQUEST_FINISHED, false);
        Logger.e("App#", "开始请求版本更新");
        // 强更的话每次进来都弹框
        if (!isForceUpdate) {
            if (!isNeedBackgroundCheck()) {
                SPUtils.INSTANCE.putBoolean(IS_UPGRADE_REQUEST_FINISHED, true);
                startOtherDialogRequest(activity,currPageId);
                Logger.e("App#", "完成请求版本更新： ");
                return;
            }
        }

        startCheck(activity, new OnUpgradeCheckListener() {

            @Override
            public boolean onCheckFinish(int code, UpgradeMsgBean data) {
                SPUtils.INSTANCE.putBoolean(IS_UPGRADE_REQUEST_FINISHED, true);
                Logger.e("App#", "完成请求版本更新： ");
                if (code == 0 && data != null && data.getAppVersionCode() > PhoneStatusManager.getInstance().getAppVersionCode()) {
                    showForceUpgradeDialog(activity, data);
                } else {
                    resetCheckComplete();
                    startOtherDialogRequest(activity,currPageId);
                }
                EventBus.getDefault().post(new GetUpdateFinishEvent());
                return true;
            }
        });
    }

    /**
     * 判断是否应该显示小说口令
     * @param context
     * @return
     */
    public static boolean upgradeRequestFinished(Context context) {
        // 版本更新请求完，同时没有版本更新
        Logger.e("App#", "判断请求版本更新是否完成： " + SPUtils.INSTANCE.getBoolean(IS_UPGRADE_REQUEST_FINISHED, false));
        Logger.e("App#", "判断是否有新版： " + UpgradeMsgUtils.isHasUpdateInfo(context));
        return SPUtils.INSTANCE.getBoolean(IS_UPGRADE_REQUEST_FINISHED, false) && !UpgradeMsgUtils.isHasUpdateInfo(context);
    }

    private void startOtherDialogRequest(FragmentActivity activity, String currPageId) {
        LauncherDialogMgr.requestData(activity,currPageId);
    }

    public void startManualCheck(final Activity activity) {
        startCheck(activity, new OnUpgradeCheckListener() {
            @Override
            public boolean onCheckFinish(int code, UpgradeMsgBean data) {
                if (code != 0 || data == null) {
                    resetCheckComplete();
                    ToastUtils.showLimited(R.string.mine_was_last_update);
                    return true;
                }
                if (data.getAppVersionCode() > PhoneStatusManager.getInstance().getAppVersionCode()) {
                    showForceUpgradeDialog(activity, data);
                } else {
                    resetCheckComplete();
                    ToastUtils.showLimited(R.string.mine_was_last_update);
                }
                EventBus.getDefault().post(new GetUpdateFinishEvent());
                return true;
            }
        });
    }

    private void startCheck(Context context, OnUpgradeCheckListener listener) {
        upgradeRequest(context, listener);
    }

    private class UpgradeObserver extends DisposableObserver<JsonResponse<UpgradeMsgBean>> {

        private OnUpgradeCheckListener listener;
        private Context context;

        public UpgradeObserver(Context context, OnUpgradeCheckListener listener) {
            this.listener = listener;
            this.context = context;
        }

        @Override
        public void onNext(JsonResponse<UpgradeMsgBean> response) {
            if (response.status == 1) {
                Logger.e(TAG, "upgrade : " + new Gson().toJson(response.data));
                updateUpgradeDialogTime();
                if (response.data != null && !StringFormat.isEmpty(response.data.getDownloadUrl())) {
                    UpgradeMsgBean upgradeBean = response.data;
                    if (upgradeBean.getIsForceUpdate() == 0) {
                        BaseApplication.context.globalContext.getSharedPreferences(AdConstants.PREFERENCE_NAME,
                                Context.MODE_PRIVATE).edit().putBoolean(IS_FORCE_UPGRADE_TAG, true).apply();
                        SPUtils.INSTANCE.putInt(FORCE_UPDATE_VERSION_CODE, upgradeBean.getAppVersionCode());
                    } else {
                        BaseApplication.context.globalContext.getSharedPreferences(AdConstants.PREFERENCE_NAME,
                                Context.MODE_PRIVATE).edit().putBoolean(IS_FORCE_UPGRADE_TAG, false).apply();
                    }
                    UpgradeMsgUtils.storeUpdateInfo(context, true);
                    UpgradeMsgUtils.storeUpdateMsg(context, new Gson().toJson(upgradeBean));
                    Logger.e("App#", "有版本更新： " + UpgradeMsgUtils.isHasUpdateInfo(context));
                    if (listener != null) {
                        listener.onCheckFinish(0, upgradeBean);
                    }
                    Logger.i(TAG, "onNext: " + new Gson().toJson(response));
                } else {
                    Logger.e("App#", "没有版本更新： " + UpgradeMsgUtils.isHasUpdateInfo(context));
                    if (listener != null) {
                        listener.onCheckFinish(1, null);
                    }
                    UpgradeMsgUtils.clearUpdateMsg(context);
                }
            } else {
                Logger.e("App#", "没有版本更新： " + UpgradeMsgUtils.isHasUpdateInfo(context));
                if (listener != null) {
                    listener.onCheckFinish(0, null);
                }
                UpgradeMsgUtils.clearUpdateMsg(context);
            }
        }

        @Override
        public void onError(Throwable e) {
            Logger.e(TAG, "error : " + e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    }

    private void upgradeRequest(Context context, OnUpgradeCheckListener listener) {
        UpgradeRequest request = new UpgradeRequest();
        request.appVersionCode = PhoneStatusManager.getInstance().getAppVersionCode();
        new JsonPost.AsyncPost<UpgradeMsgBean>()
                .setRequest(request)
                .setResponseType(UpgradeMsgBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .post(new UpgradeObserver(context, listener));
    }

    private boolean isNeedBackgroundCheck() {
        int forceVersionCode = SPUtils.INSTANCE.getInt(FORCE_UPDATE_VERSION_CODE, -1);
        return isShowDialogExpired() || forceVersionCode > PhoneStatusManager.getInstance().getAppVersionCode();
    }

    private boolean isShowDialogExpired() {
        long lastShowTime = getUpgradeDialogTime();
        return TimeUtils.isPastDay(lastShowTime);
    }

    private static long getUpgradeDialogTime() {
        return SPUtils.INSTANCE.getLong(LAST_CLEAR_UPGRADE_DIALOG_TIME, Constants.ZERO_NUM);
    }

    private static void updateUpgradeDialogTime() {
        SPUtils.INSTANCE.putLong(LAST_CLEAR_UPGRADE_DIALOG_TIME, System.currentTimeMillis());
    }

    public static void clearUpgradeDialogTime() {
        SPUtils.INSTANCE.putLong(LAST_CLEAR_UPGRADE_DIALOG_TIME, Constants.ZERO_NUM);
    }

    private void showForceUpgradeDialog(final Activity activity, final UpgradeMsgBean data) {
        if (!context.isOnForeground()) {
            return;
        }
        StatisHelper.onEvent().strongUpgrade();
        UpgradeDialog.Builder builder = new UpgradeDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setSize(String.format("%.1f", data.getSize() / 1024f / 1024f));
        builder.setMessage(data.getDesc());
        builder.setVersion(data.getAppVersionName());
        builder.setPositiveButton(R.string.upgrade_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StatisHelper.onEvent().strongNowUpgrade();
                FuncPageStatsApi.updateDialogClickOk();
                UpgradeManager.getInstance(activity).checkUpdate();
                dialog.cancel();
            }
        });
        if (data.getIsForceUpdate() != 0) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FuncPageStatsApi.updateDialogClickCancel();
                    dialog.cancel();
                }
            });
        }
        builder.create().show();
        FuncPageStatsApi.updateDialogExpose();
    }

    public void resetCheckComplete() {
        MineManager.isCheckComplete = true;
    }

    private void downloadApk(String apkUrl) {
        Intent intent = new Intent(mContext, DownloadIntentService.class);
        intent.putExtra("apkUrl", apkUrl);
        mContext.startService(intent);

//        // 使用系统下载
//        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(apkUrl));
//        //设置什么网络情况下可以下载
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//        //设置通知栏的标题
//        request.setTitle("下载");
//        //设置通知栏的message
//        request.setDescription("今日头条正在下载.....");
//        //设置漫游状态下是否可以下载
//        request.setAllowedOverRoaming(false);
//        //设置文件存放目录
//        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,"novel.apk");
//        //获取系统服务
//        DownloadManager downloadManager= (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        //进行下载
//        downloadManager.enqueue(request);
    }

    /**
     * 检查是否有新版本
     *
     */
    public void checkUpdate() {
        // 判断是否有版本更新消息
        if (UpgradeMsgUtils.isHasUpdateInfo(mContext)) {
            UpgradeMsgBean upgradeMsgBean = UpgradeMsgUtils.getUpgradeMsg(mContext);
            if (upgradeMsgBean == null) {
                UpgradeMsgUtils.clearUpdateMsg(mContext);
                return;
            }
            Logger.e("TAG", "版本升级json: " + new Gson().toJson(upgradeMsgBean));
            // 没网且没有下载完，则不显示升级框
            if (!Tools.isNetAvailable(mContext) && !UpgradeApkInstallUtil.getInstance(mContext)
                    .isFileDonwloadFinished(mContext, upgradeMsgBean.getAppVersionCode())) {
                return;
            }
            // 判断是否已经安装最新版本
            int versionCode = Tools.getVersionCode(mContext);
            if (versionCode >= upgradeMsgBean.getAppVersionCode()) {
                UpgradeMsgUtils.clearUpdateMsg(mContext);
                File file = new File(UpgradeApkInstallUtil.getInstance(mContext).getApkObsPath());
                Logger.e(TAG, "apk路径： "  + file.getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                    Logger.e(TAG, "UpgradeManager 删除apk文件");
                }
                DaoDownloadManager.getInstance(mContext).delete(file.getName());
            }
            downloadApk(upgradeMsgBean.getDownloadUrl());
        }
    }

}
