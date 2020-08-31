package com.duoyue.mianfei.xiaoshuo.read.utils;

import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.io.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.InflaterInputStream;

public class ChapterContentLoadUtils {

    public static final int LOAD_CONTENT_TYPE_READ = 1;
    public static final int LOAD_CONTENT_TYPE_DOWNLOAD = 2;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    /**
     * 加载章节内容
     * @param url
     * @param type  1 阅读  2 下载
     * @return
     * @throws Exception
     */
    public static String loadContent(String url, int type) throws Exception {
        String uid = "";
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if(userInfo != null){
            uid = userInfo.uid;
        }

        String sUrl = url + "?uid="+uid+"&type="+type;

        final Request request = new Request.Builder().url(sUrl).build();
        Response response = okHttpClient.newCall(request).execute();
        return decompress(response.body().bytes());
    }

    private static String decompress(byte[] compress) throws Exception {
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
}
