package com.zydm.base.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

/**
 * Created by YinJiaYan on 2017/6/3.
 */

public abstract class AbsDialogViewHolder extends AbsViewHolder {

    private Dialog mDialog;
    private boolean mIsCancelable = true;
    private Activity mActivity;

    public final void setContentViewTo(Activity activity, Dialog dialog) {
        mActivity = activity;
        dialog.setContentView(getContentView(activity, dialog));
    }

    public final View getContentView(Activity activity, Dialog dialog) {
        mActivity = activity;
        mDialog = dialog;
        View view = createContentView(activity);
        onInitDialog(activity, dialog);
        return view;
    }

    protected void onInitDialog(Activity activity, Dialog dialog) {

    }

    protected abstract View createContentView(Activity activity);

    protected void dismissDialog() {
        if (mDialog == null
                || !mDialog.isShowing()
                || mActivity.isFinishing()) {
            return;
        }
        mDialog.dismiss();
    }

    protected void cancelDialog() {
        if (mDialog == null || !mIsCancelable) {
            return;
        }
        mDialog.cancel();
    }

    public void setCancelable(boolean cancelable) {
        mIsCancelable = cancelable;
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }
}
