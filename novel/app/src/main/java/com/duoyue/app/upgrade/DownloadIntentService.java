package com.duoyue.app.upgrade;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.duoyue.app.upgrade.download.DownloadManager;
import com.duoyue.app.upgrade.download.*;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.devices.UtilSharedPreferences;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Description 负责下载apk,通知栏显示下载进度
 */

public class DownloadIntentService extends IntentService {
    private static final String TAG = "App#DownloadIntentService";

    private Notification mNotification;
    private static final int NOTIFY_ID = 1000;
    /**
     * 数据下载最大值
     */
    private static final int TYPE_MAXNUM = 100;
    private NotificationManager mNotificationManager;
    /**
     * 下载包安装路径
     */
    private String rootPath;
    private String fileName;
    private UpgradeMsgBean upgradeMsgBean;
    private boolean isInstalling;
    private boolean isDownLoading;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadIntentService(String name) {
        super(name);
    }

    public DownloadIntentService() {
        super("download");
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadInfo bean = (DownloadInfo) msg.obj;
            if (bean == null) {
                return;
            }
            long fileSize = bean.getFileSize();
            long completeSize = bean.getCompleteSize();
            if (fileSize <= 0 || isInstalling) {
                return;
            }
            int pecent = (int) (100 * completeSize / fileSize);
            if (pecent == 100) {
                // 下载完毕
                isInstalling = true;
                FuncPageStatsApi.updateCompleteDownload();
                UpgradeApkInstallUtil.getInstance(getApplicationContext()).installApk(getApplicationContext());
                cancelNotification();
                stopSelf();// 停掉服务自身
                UtilSharedPreferences.saveBooleanData(getApplicationContext(), UtilSharedPreferences.KEY_IS_DOWNLOADING, false);
                return;
            }
            switch (msg.what) {
//                case DownState.FINISH:
//                    // 下载完毕
//                    UpgradeApkInstallUtil.getInstance(getApplicationContext()).installApk(getApplicationContext());
//                    cancelNotification();
//                    stopSelf();// 停掉服务自身
//                    break;
                case DownState.DOWNLOAD:
                    RemoteViews contentview = mNotification.contentView;
                    contentview.setTextViewText(R.id.version_notification_text, pecent + "%");
                    contentview.setProgressBar(R.id.version_notification_progress, TYPE_MAXNUM, pecent, false);
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
                case DownState.PAUSE:
                    break;
                case DownState.FILEERROR:
                case DownState.NETERROR:
                    cancelNotification();
                    stopSelf();// 停掉服务自身
                    Logger.e(TAG, "下载出错");
                    UtilSharedPreferences.saveBooleanData(getApplicationContext(), UtilSharedPreferences.KEY_IS_DOWNLOADING, false);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        rootPath = UpgradeApkInstallUtil.getInstance(this).getApkStorePath(this);
        fileName = UpgradeApkInstallUtil.getInstance(this).getApkName();
        upgradeMsgBean = UpgradeMsgUtils.getUpgradeMsg(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 判断是否之前就已经下载完成
        if (UpgradeApkInstallUtil.getInstance(this).isFileDonwloadFinished(this, upgradeMsgBean.getAppVersionCode())) {
            UpgradeApkInstallUtil.getInstance(this).installApk(this);
            return;
        }
        if (intent != null && !isDownLoading) {
            String apkUrl = intent.getStringExtra("apkUrl");
            if (TextUtils.isEmpty(apkUrl) || !Tools.isNetAvailable(this)) {
                return;
            }
            isDownLoading = true;
            Logger.e(TAG, "开始下载");
            FuncPageStatsApi.updateDialogstartDownload();
            UtilSharedPreferences.saveBooleanData(getApplication(), UtilSharedPreferences.KEY_IS_DOWNLOADING, true);
            downloadFile(rootPath + File.separator + fileName);
        } else {
            Logger.e(TAG, "已经在下载了");
        }
    }

    /**
     * 处理下载前的逻辑判断
     */
    private void downloadFile(String filePath) {
        DownloadInfo downLoadBean = DaoDownloadManager.getInstance(this).getInfos(fileName);
        File apkFile = new File(filePath);
        if (downLoadBean == null) {
            // 清除未知的同名apk包
            if (apkFile.exists()) {
                apkFile.delete();
//                Logger.e(TAG, "DownloadIntentService 存在老版本apk，删除，重新下载");
            }
            startDownloadData(null, this, mHandler, upgradeMsgBean);
        } else { // 已经下载过了
            int state = downLoadBean.getDownState();
            int currVersionCode = Tools.getVersionCode(this);
            if (downLoadBean.getFileSize()  > 0) {
//                Logger.e(TAG, "DownloadIntentService 数据库已存在该版本地址信息，上次下载:"
//                        + downLoadBean.getCompleteSize() * 100 / downLoadBean.getFileSize() + " % ");
            }
            // 数据库版本信息存在，但是apk已经不存在了，则清除数据库信息，重新下载最新版本
            if (!apkFile.exists()) {
//                Logger.e(TAG, "DownloadIntentService 数据库存在apk信息，但是apk不存在，重新下载");
                DaoDownloadManager.getInstance(this).delete(fileName);
                startDownloadData(null, this, mHandler, upgradeMsgBean);
                return;
            } else
            // 判断数据库中的版本信息，跟当前需要下载的信息比较
            if (upgradeMsgBean != null && upgradeMsgBean.getAppVersionCode() > downLoadBean.getVersionCode()) {
                DaoDownloadManager.getInstance(this).delete(fileName);
                apkFile.delete();
                startDownloadData(null, this, mHandler, upgradeMsgBean);
            } else {
//            // 判断是否是之前未下载完成的文件
                try {
                    FileInputStream inputStream = new FileInputStream(apkFile);
//                    Logger.e(TAG, "APK文件大小:" + inputStream.available()
//                            + ", 数据库存在apk已下载大小:" + downLoadBean.getCompleteSize()
//                            + ", 数据库存在apk文件大小:" + downLoadBean.getFileSize());
                    if (inputStream.available() != downLoadBean.getCompleteSize()) {
//                    apkFile.delete();
//                    downLoadBean.setCompleteSize(0);
//                        Logger.e(TAG, "APK文件大小和数据库存在apk已下载大小不符，进行校正");
                        downLoadBean.setCompleteSize(inputStream.available());
                    }
                    inputStream.close();
                } catch (Exception e) {
                    Logger.e(TAG, "APK文件解析出错：" + e.getMessage());
                }
                if (upgradeMsgBean != null && upgradeMsgBean.getAppVersionCode() > currVersionCode) {
                    switch (state) {
                        case DownState.FINISH:
                            UpgradeApkInstallUtil.getInstance(this).installApk(this);
                            break;
                        case DownState.PAUSE:
                            break;
                        case DownState.DOWNLOAD:
                        case DownState.FILEERROR:
                        case DownState.NETERROR:
                        case DownState.LINKING:
                        default:
                            if (Tools.isNetAvailable(this)) {
                                startDownloadData(downLoadBean, this, mHandler, upgradeMsgBean);
                            }
                            break;
                    }
                } else {
                    DaoDownloadManager.getInstance(this).delete(fileName);
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }
                    UpgradeMsgUtils.clearUpdateMsg(this);
                }
            }
        }
    }

    /**
     * 启动下载线程
     * @param downLoadBean
     * @param context
     * @param handler
     * @param upgradeMsgBean
     */
    public void startDownloadData(DownloadInfo downLoadBean, Context context, Handler handler, UpgradeMsgBean upgradeMsgBean) {
        setUpNotification(0);
        if (downLoadBean != null) {
            downLoadBean.setDownState(DownState.WAITING);
        } else {
            downLoadBean = new DownloadInfo();
            downLoadBean.setUrl(upgradeMsgBean.getDownloadUrl());
            downLoadBean.setCompleteSize(0);
            downLoadBean.setVersionCode(upgradeMsgBean.getAppVersionCode());
            downLoadBean.setVersionName(upgradeMsgBean.getAppVersionName());
            downLoadBean.setDownState(DownState.WAITING);
            downLoadBean.setFileName(fileName);

            downLoadBean.setDownPath(rootPath);
        }
        DownTask task = new DownTask(downLoadBean, context, handler);
        DownloadManager.getInstance().insertTask(task);
        DaoDownloadManager.getInstance(context).saveInfos(downLoadBean);
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFY_ID);
        }
    }

    /**
     * 创建通知
     */
    private void setUpNotification(int progress) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                getString(R.string.default_notification_channel_id));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher).build();
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_version);
        mRemoteViews.setTextViewText(R.id.version_notification_title, getString(R.string.downloading));
        Intent intent = new Intent(this, Activity.class);
        mBuilder.setContent(mRemoteViews)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setTicker(getString(R.string.downloading));
        mBuilder.setProgress(100, progress, true);
        mBuilder.setOngoing(false);
        mNotification = mBuilder.build();
        mNotification.contentView = mRemoteViews;
    }

    @Override
    public void onDestroy() {
        DownloadManager.getInstance().removeTask(upgradeMsgBean.getDownloadUrl());
        super.onDestroy();
    }
}
