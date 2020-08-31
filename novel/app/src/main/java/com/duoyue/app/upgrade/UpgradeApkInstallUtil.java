package com.duoyue.app.upgrade;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import com.duoyue.app.upgrade.download.DaoDownloadManager;
import com.duoyue.app.upgrade.download.DownloadInfo;
import com.duoyue.app.upgrade.download.UpgradeMsgBean;
import com.duoyue.lib.base.log.Logger;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author fanwentao
 * @Description 升级apk安装信息管理
 */

public class UpgradeApkInstallUtil {

    private final static String TAG = "App#Upgrade";
    private final static String APP_ROOT = "novel";
    private static final String APK_NAME = "novel.apk";

    private String rootPath;
    private String filePath;
    private String fileName;
    private static UpgradeApkInstallUtil INSTANCE;


    private UpgradeApkInstallUtil(Context context) {
        rootPath = getApkStorePath(context);
        fileName = APK_NAME;
        filePath = rootPath + File.separator + fileName;
    }

    /**
     * 创建APK安装信息单例
     *
     * @param context 上下文
     * @return UpgradeApkInstallUtil
     */
    public static UpgradeApkInstallUtil getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UpgradeApkInstallUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UpgradeApkInstallUtil(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取apk文件名称
     *
     * @return
     */
    public String getApkName() {
        return fileName;
    }

    /**
     * 获取apk文件路径
     *
     * @param context
     * @return
     */
    public String getApkStorePath(Context context) {
        String savePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && context.getExternalFilesDir(null) != null) {
            savePath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + APP_ROOT;
        } else {
            savePath = context.getFilesDir().getAbsolutePath() + File.separator + APP_ROOT;
        }
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Logger.e(TAG, "存储路径：" + savePath);
        return savePath;

    }

    public String getApkObsPath() {
        return filePath;
    }

    /**
     * 判断apk是否下载完成
     *
     * @return
     */
    public boolean isFileDonwloadFinished(Context context, int versionCode) {
        File apkRootPath = new File(rootPath);
        File apkFile = new File(rootPath, fileName);
        DownloadInfo downLoadBean = DaoDownloadManager.getInstance(context).getInfos(fileName);
        Logger.e("App#", "需要升级都版本：" +  versionCode + "，本地数据库数据：" + new Gson().toJson(downLoadBean));
        if (downLoadBean == null || !apkRootPath.exists() || !apkFile.exists() || downLoadBean.getCompleteSize() == 0 || versionCode > downLoadBean.getVersionCode()) {
            return false;
        }

        if (downLoadBean != null) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(apkFile);
                Logger.e("App#", "APK文件大小:" + inputStream.available()
                        + ", 数据库存在apk已下载大小:" + downLoadBean.getCompleteSize()
                        + "， apk文件大小:" + downLoadBean.getFileSize());
                if (inputStream.available() == downLoadBean.getFileSize()
                        && downLoadBean.getFileSize() == downLoadBean.getCompleteSize()) {
                    Logger.e("App#", "文件已下载，直接安装");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    /**
     * 安装apk，同时校验MD5值
     */
    public void installApk(Context context) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        //此处不能删除, 否则会导致每次都需要重新下载Apk文件. V1.3.0  20191127
        //DaoDownloadManager.getInstance(context).delete(fileName);
        UpgradeMsgBean upgradeMsgBean = UpgradeMsgUtils.getUpgradeMsg(context);
        // 校验APK MD5值，判断是否非法文件
        if (upgradeMsgBean != null && !TextUtils.isEmpty(upgradeMsgBean.getMd5())
            && !upgradeMsgBean.getMd5().equalsIgnoreCase("null")) {
            try {
                String fileMd5 = Tools.getFileMD5(apkfile);
                if (!upgradeMsgBean.getMd5().equalsIgnoreCase(fileMd5)) {
                    Logger.e(TAG, "apk md5 校验失败， md5不正确");
                    apkfile.delete();
                    DaoDownloadManager.getInstance(context).delete(apkfile.getName());
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "apk md5 校验失败： " + e.getMessage());
            }
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            try {
                String[] command = {"chmod", "777", apkfile.getAbsolutePath()};
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Logger.e(TAG, "包名是：" + context.getPackageName());
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apkfile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if (componentName != null) {
                context.startActivity(intent);
            } else {
                Logger.e(TAG, "找不到安装界面");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.e(TAG, "apk安装失败..." + ex.getMessage());
        }
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Logger.d(TAG, "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }

}
