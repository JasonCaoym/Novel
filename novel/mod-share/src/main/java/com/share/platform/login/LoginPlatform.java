package com.share.platform.login;

import android.support.annotation.IntDef;

import java.lang.annotation.*;

public class LoginPlatform {

    @Documented
    @IntDef({QQ, WX, WEIBO})
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    public @interface Platform {

    }

    public static final int QQ = 1;

    public static final int WX = 3;

    public static final int WEIBO = 5;
}
