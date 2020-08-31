package com.zzdm.tinker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import com.duoyue.lib.base.http.HttpUtils;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zzdm.tinker.util.TinkerManager;
import okhttp3.Response;

import java.io.*;

public class TinkerPatchDownloadService extends IntentService {

    private static String patchName = "patch_signed_7zip.apk";
    private static String TAG = "tinker#TinkerPatchDownloadService";


    public TinkerPatchDownloadService() {
        this("tinker-download");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TinkerPatchDownloadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static  void installPatch(Context context) {
        File patchFile = new File(getPatchPath(context) + File.separator + patchName);
        if (patchFile.exists()) {
            Logger.e(TAG, "补丁存在，进行安装");
            TinkerManager.updatePatch(context.getApplicationContext(), getPatchPath(context) + File.separator + patchName);
            return;
        }
    }

    public static String getPatchPath(Context context) {
        String savePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            savePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            savePath = context.getCacheDir().getAbsolutePath();
        }
        File file = new File(savePath);
        if (file == null) {
            savePath = context.getFilesDir().getAbsolutePath();
            file = new File(savePath);
        }
        if (file!= null && !file.exists()) {
            file.mkdirs();
        }
        Logger.e(TAG, "补丁保存路径是： " + savePath);
        return savePath;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        Logger.e(TAG, "下载url是：" + url);
        if (!TextUtils.isEmpty(url)) {
            try {
                File patchFile = new File(getPatchPath(this) + File.separator + patchName);
                if (patchFile.exists()) {
                    Logger.e(TAG, "补丁存在，进行安装");
                    TinkerManager.updatePatch(getApplicationContext(), getPatchPath(this) + File.separator + patchName);
                    return;
                }

                downloadFile(getApplicationContext(), url, getPatchPath(this), patchName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void downloadFile(final Context context, final String downloadUrl, final String filePath, final String fileName) {
        try{
            File file1 = new File(filePath);
            if(!file1.exists()){
                file1.mkdirs();
            }
            download(context,filePath + File.separator + fileName, downloadUrl, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(final Context context, final String savePath, final String url, final long startIndex) throws IOException {
        final File cacheFile = new File(savePath);
        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");
        HttpUtils.getInstance().downloadFileByRange(url, startIndex, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Logger.e(TAG, "补丁下载失败download: error = " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.code() != 206) {// 206：请求部分资源成功码，表示服务器支持断点续传
                    Logger.e(TAG, "补丁下载失败download: code = " + response.code());
                    return;
                }
                InputStream is = response.body().byteStream();// 获取流
                RandomAccessFile tmpAccessFile = new RandomAccessFile(cacheFile, "rw");// 获取前面已创建的文件.
                tmpAccessFile.seek(startIndex);// 文件写入的开始位置.
                /*  将网络流中的文件写入本地*/
                byte[] buffer = new byte[1024 << 5];
                int length = -1;
                try {
                    while ((length = is.read(buffer)) > 0) {//读取流
                        tmpAccessFile.write(buffer, 0, length);
                    }
                    TinkerManager.updatePatch(context, savePath);
                    FuncPageStatsApi.hotUpdateCompleteDownload();
                } catch (Exception e) {
                } finally {
                    close(cacheAccessFile);
                    close(is);
                    close(response.body());
                }
            }
        });
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
