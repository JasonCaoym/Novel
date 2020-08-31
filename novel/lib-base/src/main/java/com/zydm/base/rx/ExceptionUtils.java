package com.zydm.base.rx;

import com.zydm.base.common.BaseErrorCode;
import com.zydm.base.utils.LogUtils;
import io.reactivex.exceptions.CompositeException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by yan on 2017/3/21.
 */

public class ExceptionUtils {

    private static final String TAG = "ExceptionUtils";

    public static String getExceptionInfo(Throwable ex) {
        Writer errorWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(errorWriter);
        ex.printStackTrace(pw);
        pw.close();
        return errorWriter.toString();
    }

    public static LoadException cast(Throwable error) {
        if (error instanceof LoadException) {
            return (LoadException) error;
        }

        if (error.getCause() instanceof LoadException) {
            return (LoadException) error.getCause();
        }

        if (error instanceof CompositeException) {
            for (Throwable throwable : ((CompositeException) error).getExceptions()) {
                if (throwable instanceof LoadException) {
                    return (LoadException) throwable;
                }
            }
        }

        LogUtils.d(TAG, "cast:", error);
        return new LoadException(BaseErrorCode.UNKNOWN, error);
    }
}
