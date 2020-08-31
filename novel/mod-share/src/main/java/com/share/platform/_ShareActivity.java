package com.share.platform;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by shaohui on 2016/11/19.
 */

public class _ShareActivity extends Activity {

    private int mType;
    private boolean isNew;
    //定义一个标示isCancle用于判断是否是第一次调用onResume
    private static Dialog mDialog;
    private static final String TYPE = "share_activity_type";

    public static void newInstance(Context context, int type) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, _ShareActivity.class);
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }


    public static void newInstance(Context context, int type, Dialog dialog) {
        mDialog = dialog;
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, _ShareActivity.class);
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareLogger.i(ShareLogger.INFO.ACTIVITY_CREATE);
        isNew = true;
        // init data
        mType = getIntent().getIntExtra(TYPE, 0);
        if (mType == ShareUtil.TYPE) {
            // 分享
            ShareUtil.action(this);
        } else if (mType == LoginUtil.TYPE) {
            // 登录
            LoginUtil.action(this);
        } else {
            // handle 微信回调
            LoginUtil.handleResult(-1, -1, getIntent());
            ShareUtil.handleResult(-1, -1, getIntent());
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShareLogger.i(ShareLogger.INFO.ACTIVITY_RESUME);
        if (isNew) {
            isNew = false;
        } else {
            if (null != mDialog) {
                mDialog.dismiss();
            }
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ShareLogger.i(ShareLogger.INFO.ACTIVITY_NEW_INTENT);
        handleCallBack(0, 0, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareLogger.i(ShareLogger.INFO.ACTIVITY_RESULT);
        handleCallBack(requestCode, resultCode, data);

    }

    // 处理回调
    private void handleCallBack(int requestCode, int resultCode, Intent data) {
        if (mType == LoginUtil.TYPE) {
            LoginUtil.handleResult(requestCode, resultCode, data);
        } else if (mType == ShareUtil.TYPE) {
            if (null != mDialog) {
                mDialog.dismiss();
            }
            ShareUtil.handleResult(requestCode, resultCode, data);
        }
        finish();
    }
}
