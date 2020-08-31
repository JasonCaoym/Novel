package com.zydm.base.rx;

import com.zydm.base.common.BaseErrorCode;
import com.zydm.base.common.Constants;
import com.zydm.base.utils.StringUtils;

/**
 * Created by yan on 2017/4/14.
 */

public class LoadException extends Exception {

    private int mErrorCode;
    private String mErrorMsg = Constants.EMPTY;
    private boolean mIsIntercepted = false;
    public Object mTag;

    public LoadException() {
        this(BaseErrorCode.OK, Constants.EMPTY, "");
    }

    public LoadException(int errorCode, String errorMsg, String url) {
        super(errorCode + ":" + errorMsg + " url:" + url);
        mErrorCode = errorCode;
        mErrorMsg = errorMsg;
    }

    public LoadException(int errorCode, Throwable cause) {
        super(cause.getMessage(), cause);
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void interceptAll() {
        mIsIntercepted = true;
    }

    public boolean intercept() {
        if (!isErrorMsgEmpty()) {
            return false;
        }
        mIsIntercepted = true;
        return true;
    }

    public boolean isErrorMsgEmpty() {
        return StringUtils.isBlank(mErrorMsg) || mErrorMsg.equalsIgnoreCase("null");
    }

    public boolean isIntercepted() {
        return mIsIntercepted;
    }
}
