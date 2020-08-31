package com.duoyue.lib.base.http;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: fanwentao
 * @function:
 * @date: 2018/4/16
 */
public class HttpUtils {

    private OkHttpClient mOkHttpClient;
    private static HttpUtils mInstance;
    private final static long CONNECT_TIMEOUT = 30;//超时时间，秒
    private final static long READ_TIMEOUT = 30;//读取时间，秒
    private final static long WRITE_TIMEOUT = 30;//写入时间，秒


    /**
     * @param url        下载链接
     * @param startIndex 下载起始位置
     * @param callback   回调
     * @throws IOException
     */
    public void downloadFileByRange(String url, long startIndex, Callback callback) throws IOException {
        // 创建一个Request
        // 设置分段下载的头信息。 Range:做分段数据请求,断点续传指示下载的区间。格式: Range bytes=0-1024或者bytes:0-1024
        Request request = new Request.Builder().header("RANGE", "bytes=" + startIndex + "-")
                .url(url)
                .build();
        doAsync(request, callback);
    }

    public void getContentLength(String url, Callback callback) throws IOException {
        // 创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        doAsync(request, callback);
    }


    /**
     * 同步GET请求
     */
    public void doGetSync(String url) throws IOException {
        //创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        doSync(request);
    }

    /**
     * 异步请求
     */
    private void doAsync(Request request, Callback callback) throws IOException {
        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        call.enqueue(callback);
    }

    /**
     * 同步请求
     */
    private Response doSync(Request request) throws IOException {
        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        return call.execute();
    }

    /**
     * @return HttpUtil实例对象
     */
    public static HttpUtils getInstance() {
        if (null == mInstance) {
            synchronized (HttpUtils.class) {
                if (null == mInstance) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 构造方法,配置OkHttpClient
     */
    private HttpUtils() {
        //创建okHttpClient对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }
}