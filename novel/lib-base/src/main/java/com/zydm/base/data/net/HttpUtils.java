package com.zydm.base.data.net;

import android.support.annotation.NonNull;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.utils.LogUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2017/7/10
 * @ChangeList:
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    private static final String HTTPS = "https";
    private static final int SECOND_10 = 10000;

    public static MtMap<String, String> proceedRequest(String url) throws Exception {
        return JsonUtils.parseJson(getResponseContent(doGetRequest(url)));
    }

    private static HttpURLConnection doGetRequest(String originalUrl) throws Exception {
        URL url = new URL(originalUrl);
        if (null == url) {
            return null;
        }
        LogUtils.d(TAG, "GET: " + url.toString());

        HttpURLConnection conn = createConnection(url);
        if (null == conn) {
            return null;
        }
        conn.setRequestMethod("GET");
        conn.setDoOutput(false);
        return conn;
    }

    private static HttpURLConnection createConnection(URL url) throws Exception {
        HttpURLConnection conn = null;
        if (url.getProtocol().equalsIgnoreCase(HTTPS)) {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MtTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MtHostnameVerifier());
            conn = (HttpsURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setConnectTimeout(SECOND_10);
        conn.setReadTimeout(SECOND_10);
        conn.setDoInput(true);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        return conn;
    }

    public static String getResponseContent(URLConnection conn) {
        InputStream is = null;
        InputStream finalInputStream = null;
        BufferedReader bufferReader = null;
        try {
            is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            if ("gzip".equals(conn.getContentEncoding())) {
                finalInputStream = new GZIPInputStream(is);
            } else {
                finalInputStream = is;
            }
            bufferReader = new BufferedReader(new InputStreamReader(finalInputStream));

            String line = "";
            while (null != (line = bufferReader.readLine())) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
        } catch (Exception e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
        } finally {
            try {
                if (null != bufferReader) {
                    bufferReader.close();
                }
                if (null != finalInputStream) {
                    finalInputStream.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }

        return "api_fail";
    }

    public static HttpURLConnection doPostRequest(@NonNull String originalUrl, @NonNull byte[] requestUTF8Body, @NonNull Map<String, String> headers) throws Exception {
        URL url = new URL(originalUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        conn.setConnectTimeout(SECOND_10);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.connect();

        OutputStream stream = conn.getOutputStream();
        stream.write(requestUTF8Body);
        stream.flush();
        stream.close();
        return conn;
    }
}
