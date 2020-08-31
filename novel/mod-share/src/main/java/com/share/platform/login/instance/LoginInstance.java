package com.share.platform.login.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.share.platform.login.LoginListener;
import com.share.platform.login.result.BaseToken;

public abstract class LoginInstance {

    public LoginInstance(Activity activity, LoginListener listener, boolean fetchUserInfo) {

    }

    public abstract void doLogin(Activity activity, LoginListener listener, boolean fetchUserInfo);

    public abstract void fetchUserInfo(BaseToken token);

    public abstract void handleResult(int requestCode, int resultCode, Intent data);

    public abstract boolean isInstall(Context context);

    public abstract void recycle();
}
