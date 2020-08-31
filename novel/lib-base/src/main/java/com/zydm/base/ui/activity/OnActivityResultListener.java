package com.zydm.base.ui.activity;

import android.content.Intent;

/**
 * Created by YinJiaYan on 2017/5/23.
 */

public interface OnActivityResultListener {

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
