package com.zydm.base.common;

public class BaseErrorCode {

    public static final int DATA_EMPTY = -200;
    public static final int UNKNOWN = -1;

    public static final int OK = 0;

    public static final int NETWORK_DISCONNECTION = 1;
    public static final int NETWORK_CONNETED_TIMEOUT = 2;
    public static final int NETWORK_ERROR = 3;
    public static final int NETWORK_INVALID_RESPONSE = 4;

    public static final int TASK_BUSY = 10;

    public static final int UNLOGIN = 10001;
    public static final int ACCOUNT_TOKEN_FAIL = 20004;     //token过期了

    public static boolean isNetWorkError(int errorCode) {
        return errorCode >= NETWORK_DISCONNECTION && errorCode < NETWORK_INVALID_RESPONSE;
    }

    public static boolean isNeedReLogin(int errorCode) {
        return UNLOGIN == errorCode || ACCOUNT_TOKEN_FAIL == errorCode;
    }

    public static boolean isDefaultHandle(int errorCode) {
        return isNeedReLogin(errorCode) || isNetWorkError(errorCode);
    }
}
