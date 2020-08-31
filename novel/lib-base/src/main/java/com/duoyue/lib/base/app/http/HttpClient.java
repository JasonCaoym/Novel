package com.duoyue.lib.base.app.http;

import com.duoyue.lib.base.log.Logger;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * 单例模式封装OkHttpClient
 * 作为全局变量使用,这样可以复用线程池和连接池
 *
 * @author wangtian
 * @date 2019/05/25
 */
public class HttpClient {

    private static final String TAG = "Base#HttpClient";

    private static OkHttpClient okHttpClient;

    private HttpClient() {

    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (HttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .build();
                    Logger.i(TAG, "okHttpClient= ", okHttpClient);
                }
            }
        }
        return okHttpClient;
    }

}
