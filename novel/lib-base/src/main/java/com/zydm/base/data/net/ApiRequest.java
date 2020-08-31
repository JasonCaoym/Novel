package com.zydm.base.data.net;

import com.zydm.base.common.BaseErrorCode;
import com.zydm.base.common.Constants;
import com.zydm.base.data.base.MtMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {

    /**
     * 数据加载模式: 遵守http缓存协商机制, 优先加载本地缓存, 本地缓存过期则访问服务器
     */
    public static final int REQ_MODE_LOCAL_CACHE_FIRST = 100;
    /**
     * /* 数据加载模式: 遵守http缓存协商机制, 向服务器确认本地缓存是否有效,收到304返回码则使用本地缓存
     */
    public static final int REQ_MODE_CONFIRM_WITH_SERVER = 101;
    /**
     * 数据加载模式: 强制更新数据,无视缓存协商机制
     */
    public static final int REQ_MODE_FORCE_UPDATE = 102;

    public static final int REQ_METHOD_GET = 1;
    public static final int REQ_METHOD_POST = 2;
    private int mUpdateLabel = Constants.NEGATIVE_ONE_NUM;
    private int[] mAttentionLabels;
    private String mFullUrl = null;
    private Type mRespType;
    private int mCacheExpire = -1;
    private final HashMap<String, String> mParams = new HashMap<>();
    private int mRequestMethod = REQ_METHOD_POST;

    private int mRequestMode = REQ_MODE_LOCAL_CACHE_FIRST;
    private boolean mLoadFromCache = true;
    private int mErrorCode = BaseErrorCode.UNKNOWN;
    private String mErrorMsg = "";
    private String mResponseData = null;
    private boolean mIsNeedAddTokenParam = true;
    private boolean mIsExternalReq = false;

    public ApiRequest(String fullUrl, ApiConfigs configs, MtMap<String, String> fields, Type respType) {
        this.mFullUrl = fullUrl;
        mRespType = respType;
        if (configs != null) {
            this.mCacheExpire = configs.expTime();
            this.mUpdateLabel = configs.updateLabel();
            this.mAttentionLabels = configs.attentionLabels();
            this.mIsNeedAddTokenParam = configs.isNeedAddTokenParam();
        }
        mParams.putAll(fields);
    }

    private ApiRequest(String url) {
        this.mFullUrl = url;
    }

    public static ApiRequest createExternalGetRequest(String url) {
        ApiRequest request = new ApiRequest(url);
        request.mIsExternalReq = true;
        request.mRequestMethod = REQ_METHOD_GET;
        return request;
    }

    public static ApiRequest createExternalPostRequest(String url, MtMap<String, String> params) {
        ApiRequest request = new ApiRequest(url);
        request.mIsExternalReq = true;
        request.mRequestMethod = REQ_METHOD_POST;
        request.addParams(params);
        return request;
    }

    public void reset() {
        mRequestMode = REQ_MODE_LOCAL_CACHE_FIRST;
        mLoadFromCache = true;
        mErrorCode = BaseErrorCode.UNKNOWN;
        mErrorMsg = "";
        mResponseData = null;
    }

    public boolean isExternalReq() {
        return mIsExternalReq;
    }

    protected String getUrl() {
        return mFullUrl;
    }

    public String getParam(String key) {
        return mParams.get(key);
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    public void addParams(Map<String, String> params) {
        mParams.putAll(params);
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public String getResponseData() {
        return mResponseData;
    }

    /**
     * @param method default REQ_METHOD_GET
     * @see #REQ_METHOD_GET
     * @see #REQ_METHOD_POST
     */
    public void setRequestMethod(int method) {
        if (REQ_METHOD_POST == method) {
            mRequestMethod = method;
        }
    }

    /**
     * @param mode default REQ_MODE_LOCAL_CACHE_FIRST
     * @see #REQ_MODE_LOCAL_CACHE_FIRST
     * @see #REQ_MODE_CONFIRM_WITH_SERVER
     * @see #REQ_MODE_FORCE_UPDATE
     */
    public void setRequestMode(int mode) {
        mRequestMode = mode;
    }

    public boolean isLoadFromCache() {
        return mLoadFromCache;
    }

    protected int getRequestMethod() {
        return mRequestMethod;
    }

    public int getCacheExpire() {
        return mCacheExpire;
    }

    protected int getRequestMode() {
        return mRequestMode;
    }

    protected HashMap<String, String> getParams() {
        return mParams;
    }

    protected boolean isCacheEnabled() {
        return mCacheExpire > 0;
    }

    protected void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    protected void setErrorMsg(String errorMsg) {
        mErrorMsg = errorMsg;
    }

    protected void setResponseData(String responseData) {
        mResponseData = responseData;
    }

    protected void setDataSource(boolean fromCache) {
        mLoadFromCache = fromCache;
    }

    public int getUpdateLabel() {
        return mUpdateLabel;
    }

    public int[] getAttentionLabels() {
        return mAttentionLabels;
    }

    public Type getRespType() {
        return mRespType;
    }

    public boolean isNeedAddTokenParam() {
        return mIsNeedAddTokenParam;
    }
}
