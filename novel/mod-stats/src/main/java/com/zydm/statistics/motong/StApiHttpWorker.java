package com.zydm.statistics.motong;


import android.support.annotation.NonNull;
import com.zydm.base.common.BaseErrorCode;
import com.zydm.base.data.label.LabelMgr;
import com.zydm.base.data.net.ApiBaseParam;
import com.zydm.base.data.net.ApiCacheHelper;
import com.zydm.base.rx.ExceptionUtils;
import com.zydm.base.rx.LoadException;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by yan on 2017/3/21.
 */

class StApiHttpWorker {

    private static final String TAG = "ApiHttpWorker";

    private static final String SIGNATURE_VALUE = "mtwl";

    private static final String SIGNATURE = "sign";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MSG = "errorMsg";
    private static final String DATA = "data";

    private static final String API_FAIL = "api_fail";

    //        private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String UTF_8 = "UTF-8";
    private static final String NULL = "null";

    private static final int SECOND_10 = 10000;

    private static final String HTTP_HEAD_FIELD_CACHE_CONTROL = "Cache-Control";
    private static final String HTTP_HEAD_FIELD_DATE = "Date";

    private static final String LAST_MODIFY_TIME = "Last-Modified";

    private StApiRequest mRequest = null;
    private int mErrorCode = BaseErrorCode.UNKNOWN;
    private String mResponseContent = null;
    private String mResponseData = null;
    private boolean mUpdateCache = false;
    private long mLastAccessTime = 0;//the time in milliseconds since January 1, 1970 GMT
    private long mCacheControl = 0; //cache max validity period in seconds
    private long mLastModifyTime = 0; //last cache modify time in milliseconds since January 1, 1970 UTC

    public StApiHttpWorker(StApiRequest req) {
        mRequest = req;
    }

    public void proceed() throws LoadException {
        LogUtils.d(TAG, "@@@ proceed" + this);

        try {
            loadData();
            handleResult();
            if (mRequest.getErrorCode() != 0) {
                throw new LoadException(mRequest.getErrorCode(), mRequest.getErrorMsg(), mRequest.getUrl());
            }
        } catch (Exception exp) {
            throw ExceptionUtils.cast(exp);
        } finally {
        }
    }

    private void loadData() {
        int requestMode = mRequest.getRequestMode();
        switch (requestMode) {
            case StApiRequest.REQ_MODE_LOCAL_CACHE_FIRST:
            case StApiRequest.REQ_MODE_CONFIRM_WITH_SERVER:/*temporarily*/
                if (!loadFromCache()) {
                    loadFromServer();
                }
                break;

            case StApiRequest.REQ_MODE_FORCE_UPDATE:
                if (!loadFromServer()) {
                    loadCacheData();
                }
                break;
        }
    }

    private void handleResult() {
        if (isValidApiResponse()) {
            mRequest.setResponseData(mResponseData);
            if (!mRequest.isLoadFromCache() && mRequest.getUpdateLabel() > 0) {
                LabelMgr.INSTANCE.updateLabel(mRequest.getUpdateLabel());
            }
        } else {
            if (BaseErrorCode.UNKNOWN == mErrorCode || BaseErrorCode.OK == mErrorCode) {
                mErrorCode = BaseErrorCode.NETWORK_INVALID_RESPONSE;
            }
        }

        mRequest.setErrorCode(mErrorCode);

        if (mUpdateCache &&
                mRequest.isCacheEnabled() &&
                BaseErrorCode.OK == mErrorCode) {
            updateCache();
        }
    }

    private boolean loadFromCache() {
        if (!mRequest.isCacheEnabled()) {
            return false;
        }

        LogUtils.d(TAG, "loadFromCache, url: " + mRequest.getUrl());

        loadCacheData();
        return isValidCache();
    }

    private boolean isValidCache() {
        if (null == mResponseContent) {
            return false;
        }
        boolean cacheValid = true;

        try {
            JSONObject jsonObj = new JSONObject(mResponseContent);
            mLastAccessTime = jsonObj.getLong(HTTP_HEAD_FIELD_DATE);
            mCacheControl = jsonObj.getLong(HTTP_HEAD_FIELD_CACHE_CONTROL);
            mLastModifyTime = jsonObj.getLong(LAST_MODIFY_TIME);
        } catch (JSONException e) {
            LogUtils.e(TAG, "invalid cache data");
            return false;
        }

        long expireTime = mLastModifyTime + mCacheControl * 1000;
        long currentTime = System.currentTimeMillis();
        if (currentTime >= expireTime) {
            cacheValid = false;
        } else if (LabelMgr.INSTANCE.isLabelsUpdate(mRequest.getAttentionLabels(), mLastModifyTime)) {
            cacheValid = false;
        }
        return cacheValid;
    }

    private void loadCacheData() {
        String url = mRequest.getUrl();
        HashMap<String, String> params = getCacheParams();
        mResponseContent = ApiCacheHelper.getCacheData(url, params);
    }

    @NonNull
    private HashMap<String, String> getCacheParams() {
        HashMap<String, String> params = new HashMap<>(mRequest.getParams());
        if (mRequest.getAttentionLabels() == null) {
            return params;
        }

        for (int label : mRequest.getAttentionLabels()) {
            if (label > LabelMgr.INSTANCE.getFORCE_CLEAR_CACHE_START()) {
                params.put("label" + label, String.valueOf(LabelMgr.INSTANCE.getLabelTime(label)));
            }
        }
        return params;
    }

    private void updateCache() {
        LogUtils.d(TAG, "updateCache()");
        try {
            JSONObject jsonObj = new JSONObject(mResponseContent);

            jsonObj.put(HTTP_HEAD_FIELD_DATE, mLastAccessTime);

            if (mCacheControl > 0 && mLastModifyTime > 0) {
                jsonObj.put(HTTP_HEAD_FIELD_CACHE_CONTROL, mCacheControl);
                jsonObj.put(LAST_MODIFY_TIME, mLastModifyTime);
            } else {
                jsonObj.put(HTTP_HEAD_FIELD_CACHE_CONTROL, mRequest.getCacheExpire());
                jsonObj.put(LAST_MODIFY_TIME, System.currentTimeMillis());
            }

            String cacheData = jsonObj.toString();
            String url = mRequest.getUrl();
            HashMap<String, String> params = mRequest.getParams();
            ApiCacheHelper.storeCacheData(url, params, cacheData);
        } catch (JSONException e) {
        }
    }

    private HttpURLConnection doGetRequest() {
        URL url = createApiUrl();
        if (null == url) {
            return null;
        }
        LogUtils.d(TAG, "GET: " + url.toString());

        HttpURLConnection conn = createConnection(url);
        if (null == conn) {
            return null;
        }
        try {
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
        } catch (ProtocolException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
            conn = null;
        }
        return conn;
    }

    private HttpURLConnection doPostRequest() {
        URL url = createApiUrl();
        if (null == url) {
            return null;
        }

        LogUtils.d(TAG, "POST: " + url.toString());
        HttpURLConnection conn = createConnection(url);
        if (null == conn) {
            return null;
        }

        OutputStream outputSteam = null;
        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            byte[] reqParams = createRequestParams().getBytes(UTF_8);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(reqParams.length));

            outputSteam = conn.getOutputStream();
            outputSteam.write(reqParams);
            outputSteam.flush();
        } catch (SocketTimeoutException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
            mErrorCode = BaseErrorCode.NETWORK_CONNETED_TIMEOUT;
            conn = null;
        } catch (IOException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
            conn = null;
        } finally {
            try {
                if (null != outputSteam) {
                    outputSteam.close();
                }
            } catch (IOException e) {
            }
        }
        return conn;
    }

    private boolean loadFromServer() {
            /*
            PhoneStatusManager phone = PhoneStatusManager.getInstance();
            if (!phone.hasNetworkConnected()) {
                mErrorCode = LocalErrorCode.NETWORK_DISCONNECTION;
                return false;
            }
            */

        LogUtils.i(TAG, "loadFromServer");
        HttpURLConnection conn = null;
        if (StApiRequest.REQ_METHOD_POST == mRequest.getRequestMethod()) {
            conn = doPostRequest();
        } else {
            conn = doGetRequest();
        }
        if (null == conn) {
            if (BaseErrorCode.UNKNOWN == mErrorCode) {
                mErrorCode = BaseErrorCode.NETWORK_ERROR;
            }
            LogUtils.i(TAG, "mErrorCode: " + mErrorCode);
            return false;
        }

        try {
            int response = conn.getResponseCode();
            LogUtils.i(TAG, "http response code: " + response);
            if (response == HttpURLConnection.HTTP_OK) {
                String outputData = getResponseContent(conn);
                if (API_FAIL.equals(outputData)) {
                    mErrorCode = BaseErrorCode.NETWORK_ERROR;
                    return false;
                } else {
                    extractHeadFields(conn);
                    mResponseContent = outputData;
                    mUpdateCache = true;
                    mErrorCode = BaseErrorCode.OK;
                    mRequest.setDataSource(false);
                    return true;
                }
            } else if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    /*Use cache data has been loaded in loadFromCache called by run()*/
                return true;
            }
        } catch (SocketTimeoutException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
            mErrorCode = BaseErrorCode.NETWORK_CONNETED_TIMEOUT;
        } catch (IOException e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
            mErrorCode = BaseErrorCode.NETWORK_ERROR;
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            LogUtils.i(TAG, "finally mErrorCode: " + mErrorCode);
        }
        return false;
    }

    private void extractHeadFields(URLConnection conn) {
        mCacheControl = 0;
        mLastAccessTime = conn.getDate();
        mLastModifyTime = conn.getLastModified();
        String field = conn.getHeaderField(HTTP_HEAD_FIELD_CACHE_CONTROL);
        if (null != field && field.startsWith("max-age=")) {
            String cacheControl = field.substring(8, field.length());
            try {
                mCacheControl = Long.parseLong(cacheControl);
            } catch (NumberFormatException e) {
            }
        }
    }

    private String getResponseContent(URLConnection conn) {
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

        return API_FAIL;
    }

    private HttpURLConnection createConnection(URL url) {
        HttpURLConnection conn = null;
        try {
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
            conn.setIfModifiedSince(mLastAccessTime);
            conn.setRequestProperty("Accept-Encoding", "gzip");
        } catch (Exception e) {
            LogUtils.e(TAG, e.getLocalizedMessage(), e);
        }
        return conn;
    }

    private URL createApiUrl() {
        String fullUrl = mRequest.getUrl();

        if (StApiRequest.REQ_METHOD_GET == mRequest.getRequestMethod() && !mRequest.isExternalReq()) {
            String params = createRequestParams();
            fullUrl = fullUrl + "?" + params;
        }
        try {
            return new URL(fullUrl);
        } catch (MalformedURLException exp) {
            LogUtils.e(TAG, exp.getLocalizedMessage(), exp);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String createRequestParams() {
        Map<String, String> params = (Map<String, String>) mRequest.getParams().clone();
        LogUtils.d(TAG, "params: " + params);
        if (!mRequest.isExternalReq()) {
            ApiBaseParam.addCommonParam(params);
        }
        params.put("package", "com.motong.ebk");

        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(getUTF8Code(entry.getValue()))
                        .append("&");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } catch (Exception e) {
        }
        LogUtils.d(TAG, "all params: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String getUTF8Code(String value) {
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return NULL;
        }
    }

    private boolean isValidApiResponse() {
        if (mRequest.isExternalReq()) {
            mErrorCode = BaseErrorCode.OK;
            mResponseData = mResponseContent;
            return true;
        }
        if (null == mResponseContent) {
            return false;
        }
        boolean isLoadFromCache = mRequest.isLoadFromCache();
        try {
            JSONObject jsonObj = new JSONObject(mResponseContent);
            LogUtils.d(TAG, "url=" + mRequest.getUrl() + "  isLoadFromCache = " + isLoadFromCache + "    response:" + jsonObj.toString(2));
            mErrorCode = jsonObj.getInt(ERROR_CODE);
            mResponseData = jsonObj.optString(DATA);
            if (!jsonObj.isNull(ERROR_MSG)) {
                mRequest.setErrorMsg(jsonObj.optString(ERROR_MSG));
            }

            if (BaseErrorCode.OK != mErrorCode) {
                return false;
            }
            if (!SIGNATURE_VALUE.equals(jsonObj.getString(SIGNATURE))) {
                mErrorCode = BaseErrorCode.UNKNOWN;
                return false;
            }
            if (mRequest.getRespType() != null && StringUtils.isBlank(mResponseData)) {
                mErrorCode = BaseErrorCode.DATA_EMPTY;
                return false;
            }
            return true;
        } catch (JSONException e) {
            LogUtils.w(TAG, "url=" + mRequest.getUrl() + " invalid response:" + mResponseContent, e);
        }
        return false;
    }

}
