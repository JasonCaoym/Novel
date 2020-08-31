package com.duoyue.app.ui.view;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.notification.NotificationsUtils;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.utils.SharePreferenceUtils;

public class NotificationPermissionDialogFragment extends BaseFragmentDialog implements View.OnClickListener {

    private String mCurrPageId;

    @Override
    public void initData() {
    }

    public void setCurrPageId(String currPageId) {
        mCurrPageId = currPageId;
    }

    @Override
    public void initView(View view) {
        TextView tvSure = view.findViewById(R.id.tv_sure);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        tvSure.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        SharePreferenceUtils.putLong(getContext(), SharePreferenceUtils.DIALOG_SHOW_FIRST_TIME_EVERY_DAY, System.currentTimeMillis());
        FuncPageStatsApi.notificationPermissionOut(mCurrPageId);
    }

    @Override
    public int getWidth() {
        return Utils.dp2px(275);
    }

    @Override
    public int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getInflateLayout() {
        return R.layout.dialog_notify_permission;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public int getAnimation() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                FuncPageStatsApi.notificationPermissionClick(mCurrPageId);
                toSetting();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    /**
     * 去系统设置开启通知权限
     */
    private void toSetting() {
        NotificationsUtils.toPermiddionSetting(getContext());
        dismiss();
    }



}
