package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.app.Application;
import android.util.Log;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.io.IOUtil;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;
import okhttp3.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

public class BookDetailLoadUtils {

    private static volatile BookDetailLoadUtils bookDetailLoadUtils;

    private static final OkHttpClient.Builder builder = new OkHttpClient.Builder();

    private static final int TIME_DAY = 1000 * 24 * 60 * 60;
    //缓存时间
    private static final int CACHE_TIME = 24 * 60 * 60;
    //缓存大小
    private static final int CACHE_SIZE = 10 * 1024 * 1024;
    //缓存目录 /data/data/com.duoyue.mianfei.xiaoshuo/cache/image_manager_disk_cache  /data/data/com.duoyue.mianfei.xiaoshuo/cache/duoyue_book_detail_first_chapter
//    public static final String CACHE_PATH = BaseContext.getContext().getCacheDir().getAbsoluteFile() + "/duoyue_book_detail_first_chapter";
//    private static final String CACHE_IMAGE_PATH = BaseContext.getContext().getCacheDir().getAbsoluteFile() + "/image_manager_disk_cache";
    //缓存时间
    private static final int CACHE_CLEAN = 7;

    public static String getCacheRootPath() {
        Application application = BaseApplication.context.globalContext;
        try {
            File cacheFile = application.getCacheDir();
            if (cacheFile == null) {
                cacheFile = application.getFilesDir();
            }
            if (cacheFile == null) {
                cacheFile = application.getExternalCacheDir();
            }
            if (cacheFile != null) {
                return cacheFile.getAbsolutePath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.e("error", "缓存文件路径获取失败: " + ex.getMessage());
        }
        return "";
    }

    public static String getCachePath() {
        return getCacheRootPath() + "/duoyue_book_detail_first_chapter";
    }

    public static String getCacheImagePath() {
        return getCacheRootPath() + "/image_manager_disk_cache";
    }

    public static BookDetailLoadUtils getInstance() {
        if (bookDetailLoadUtils == null) {
            synchronized (BookDetailLoadUtils.class) {
                if (bookDetailLoadUtils == null) {
                    bookDetailLoadUtils = new BookDetailLoadUtils();
                    File file = new File(getCachePath());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Cache cache = new Cache(file, CACHE_SIZE);
                    builder.cache(cache);
                    builder.addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response.newBuilder()
                                    .header("Cache-Control", "private, max-age=" + CACHE_TIME)
                                    .removeHeader("Pragma")
                                    .build();
                        }
                    });
                }
            }
        }
        return bookDetailLoadUtils;
    }

    public String loadDetailContent(String url) throws Exception {
        final Request request = new Request.Builder().url(url).build();

        Response response = builder.build().newCall(request).execute();
        return decompress(response.body().bytes());
    }

    String decompress(byte[] compress) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(compress);
        InflaterInputStream iis = new InflaterInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c;
        byte[] buf = new byte[1024];
        try {
            while (true) {
                c = iis.read(buf);
                if (c == -1) break;
                baos.write(buf, 0, c);
            }
            baos.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtil.close(bais, iis, baos);
        }

        return baos.toString("utf-8");
    }

    public static void cleanCacheData(String path) {
        final File file = new File(path);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (!f.isFile()) break;
            long timeDifference = (System.currentTimeMillis() - f.lastModified()) / TIME_DAY;
            if (timeDifference >= CACHE_CLEAN) {
                f.delete();
            }
        }
        //递归调用
        if (path.equals(getCachePath())) {
            cleanCacheData(getCacheImagePath());
        } else {
            return;
        }
        Log.i("cleanCacheData", "清理缓存成功");
    }
}
