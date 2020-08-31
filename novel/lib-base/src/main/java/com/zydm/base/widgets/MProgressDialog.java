package com.zydm.base.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import com.zydm.base.R;


public class MProgressDialog extends Dialog {

    public MProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.mprogress_dialog);//loading的xml文件
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.horizontalMargin = 20;
        params.alpha = 0.8f;
        params.gravity = Gravity.CENTER;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

}