package com.duoyue.mod.ad.api;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import com.duoyue.lib.base.app.http.HttpClient;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.NetType;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToutiaoAdReport extends Service {

    private static final String TAG = "ad#ToutiaoAdReport";
    // 测试链接： http://192.168.0.85:42111/
    private static final String BASE_URL = "http://cdpinfoapi.37xh.cn/";
    // 正式链接： http://cdpinfoapi.37xh.cn:42111/
    private static final String TOKEN_URL = "duoyue/api/getAccessToken";
    private static final String REPORT_URL = "duoyue/api/eventReport";
    private static final String APP_ROOT = "novel";
    private String token;
    private AtomicBoolean isRegistered = new AtomicBoolean(false);
    private AtomicBoolean isRequesting = new AtomicBoolean(true);

    public ToutiaoAdReport() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startRequest();
    }

    public static boolean hasUpload(Context context) {
        boolean isUpload = SPUtils.INSTANCE.getBoolean(SPUtils.INSTANCE.getSHARED_IS_UPLOAD_TAOTIAO(), false);
        if (!isUpload) {
            // 判断文件
            /*if (!TextUtils.isEmpty(getApkStorePath(context))) {
                File uploadFile = new File(getApkStorePath(context), ".config");
                if (!uploadFile.exists()) {
                    createConfigFile(context, uploadFile);
                    return false;
                } else {
                    Logger.i(TAG, "文件存在，已上报过了");
                    SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getSHARED_IS_UPLOAD_TAOTIAO(), true);
                    return true;
                }
            } else*/ {
                return false;
            }
        } else {
            Logger.i(TAG, "配置存在，已上报过了");
            // 同步文件
            /*if (!TextUtils.isEmpty(getApkStorePath(context))) {
                File uploadFile = new File(getApkStorePath(context), ".config");
                if (!uploadFile.exists()) {
                    createConfigFile(context, uploadFile);
                }
            }*/
            return true;
        }
    }

    private static void createConfigFile(Context context, File uploadFile) {
        if (PhoneUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
            uploadFile.createNewFile();
            FileWriter fileWriter = new FileWriter(uploadFile);
            fileWriter.write("true");
            fileWriter.flush();
            fileWriter.close();
            Logger.i(TAG, "头条配置文件创建成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static String getApkStorePath(Context context) {
        String savePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_ROOT;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return savePath;
    }

    public void startRequest() {
        if (PhoneUtil.isNetworkAvailable(this)) {
            ZExecutorService.getInstance().execute(tokenRunnable);
        } else {
            isRequesting.set(false);
            initNetBroadcast();
        }
    }

    private void initNetBroadcast() {
        if (!isRegistered.get()) {
            isRegistered.set(true);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mReceiver, intentFilter);
            Logger.i(TAG, "网络监听已注册");
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (PhoneUtil.isNetworkAvailable(context) && !isRequesting.get()) {
                    if (TextUtils.isEmpty(token)) {
                        ZExecutorService.getInstance().execute(tokenRunnable);
                    } else {
                        ZExecutorService.getInstance().execute(reportRunnable);
                    }
                } else {
                    Logger.i(TAG, "没有网络或已经上传成功");
                }
            }
        }
    };

    private Runnable tokenRunnable = new Runnable() {
        @Override
        public void run() {
            getAccessToken();
        }
    };

    private Runnable reportRunnable = new Runnable() {
        @Override
        public void run() {
            reportAdInfo();
        }
    };

    private OkHttpClient buildHttpClient() {
        return HttpClient.getInstance();
    }

    private void getAccessToken() {
        isRequesting.set(true);
        OkHttpClient client = buildHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + TOKEN_URL);
        builder.post(new RequestBody() {

            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json;charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try {
                    SysLoginModel loginModel = new SysLoginModel("duoyue", "duoyue");
                    sink.write(JsonUtils.toJson(loginModel).getBytes());
                } catch (Throwable throwable) {
                    throw new IOException(throwable.getMessage(), throwable.getCause());
                }
            }
        });
        Call call = client.newCall(builder.build());
        Response response = null;
        try {
            response = call.execute();
            int code = response.code();
            switch (code) {
                case 200:
                    String body = response.body().string();
                    ResponseResult result = JsonUtils.parseJson(body, ResponseResult.class);
                    Logger.i(TAG, "头条广告token获取成功");
                    if (result != null) {
                        token = result.data;
                        reportAdInfo();
                        Logger.i(TAG, "token : " + result.data);
                    } else {
                        isRequesting.set(false);
                        initNetBroadcast();
                    }
                break;
                default:
                    isRequesting.set(false);
                    initNetBroadcast();
                    Logger.e(TAG, "头条广告token获取失败: code = "  + response.code() + ", msg = " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            isRequesting.set(false);
            initNetBroadcast();
            Logger.e(TAG, "头条广告token获取失败 ： " + response);
        }
    }

    private void reportAdInfo() {
        isRequesting.set(true);
        OkHttpClient client = buildHttpClient();
        Request.Builder builder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + REPORT_URL).newBuilder();
        urlBuilder.addQueryParameter("accessToken", token);
        urlBuilder.addQueryParameter("event_id", "0");
        urlBuilder.addQueryParameter("timestamp", TimeUtils.formatDateTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
        if (PhoneUtil.getNetworkType(getApplicationContext()) == NetType.WIFI) {
            urlBuilder.addQueryParameter("ip", PhoneUtil.getWifiIP(getApplicationContext()));
        } else {
            urlBuilder.addQueryParameter("ip", PhoneUtil.getMobileIP());
        }
        UserInfo info = UserManager.getInstance().getUserInfo();
        if (info != null) {
            urlBuilder.addQueryParameter("uid", info.uid);
        }
        urlBuilder.addQueryParameter("channel_id", "0");
        urlBuilder.addQueryParameter("mac", PhoneUtil.getMac(getApplicationContext()));
        urlBuilder.addQueryParameter("os", "0");
        urlBuilder.addQueryParameter("andriod_id", PhoneUtil.getAndroidID(getApplicationContext()));
        if (!TextUtils.isEmpty(PhoneUtil.getIMEI_1(getApplicationContext()))) {
            urlBuilder.addQueryParameter("imei", PhoneUtil.getIMEI_1(getApplicationContext()));
        }
        if (!TextUtils.isEmpty(PhoneUtil.getIMEI_2(getApplicationContext()))) {
            urlBuilder.addQueryParameter("imei1", PhoneUtil.getIMEI_2(getApplicationContext()));
        }
        urlBuilder.addQueryParameter("device_id", PhoneUtil.getModel());
        builder.url(urlBuilder.build());

        builder.get();
        Call call = client.newCall(builder.build());
        Response response = null;
        try {
            response = call.execute();
            int code = response.code();
            String body = response.body().string();
            ResponseResult result = JsonUtils.parseJson(body, ResponseResult.class);
            switch (code) {
                case 200:
                    Logger.i(TAG, "头条广告上报取成功");
                    SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getSHARED_IS_UPLOAD_TAOTIAO(), true);
                    stopSelf();
                    break;
                default:
                    isRequesting.set(false);
                    initNetBroadcast();
                    Logger.e(TAG, "头条广告上报失败: code = "  + response.code() + ", msg = " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            isRequesting.set(false);
            initNetBroadcast();
            Logger.e(TAG, "头条广告上报失败 ： " + response);
        }
    }


    private static class ResponseResult {
        String status;
        String data;
        String msg;

        @Override
        public String toString() {
            return "ResponseResult{" +
                    "status='" + status + '\'' +
                    ", data='" + data + '\'' +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    private static class SysLoginModel {
        private String username;
        private String password;

        public SysLoginModel(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered.get()) {
            unregisterReceiver(mReceiver);
            Logger.i(TAG, "网络监听已注销");
        }
    }

}
