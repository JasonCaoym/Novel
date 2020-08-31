package com.duoyue.app.ui.fragment;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.text.TextTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.ViewUtils;

public class ReadSleepFragment extends DialogFragment {


    private TextView mTv_right, tvTitle;
//    private ImageView ivICon, ivClose;
    private OnExitAppListener listener;
    private View mainLayoutView;
    private Resources res;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        res = getResources();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(ViewUtils.dp2px(50), 0, ViewUtils.dp2px(50), ViewUtils.dp2px(80));
            WindowManager.LayoutParams lp = window.getAttributes();
            if (lp != null) {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
        }
        View view = inflater.inflate(R.layout.dialog_read_sleep, container, false);
        mainLayoutView = view.findViewById(R.id.read_sleep_layout);
//        ivClose = view.findViewById(R.id.read_sleep_close);
//        ivClose.setOnClickListener(onClickListener);
//        ivICon = view.findViewById(R.id.read_sleep_icon);
        tvTitle = view.findViewById(R.id.read_sleep_title);
        mTv_right = view.findViewById(R.id.tv_right);
        mTv_right.setOnClickListener(onClickListener);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
    }

    public void updateNightMode(boolean isNightMode) {
        if (isNightMode) {
//            ivClose.setImageResource(R.mipmap.icon_read_close_night);
//            ivICon.setImageResource(R.mipmap.read_sleep_night_bg);
            mainLayoutView.setBackgroundColor(res.getColor(R.color.color_202020));
            tvTitle.setTextColor(res.getColor(R.color.color_797C81));
//            StringFormat.setTextViewColor(rewardVideoEntry, res.getColor(R.color.standard_red_main_light), 4, 10);
            mTv_right.setTextColor(res.getColor(R.color.color_EECFBA));
            mTv_right.setBackgroundResource(R.drawable.ad_round_main_light_bg);
        } else {
//            ivClose.setImageResource(R.mipmap.icon_read_close);
//            ivICon.setImageResource(R.mipmap.bg_read_sleep);
            mainLayoutView.setBackgroundColor(res.getColor(R.color.white));
            tvTitle.setTextColor(res.getColor(R.color.color_1b1b1b));
//            StringFormat.setTextViewColor(rewardVideoEntry, res.getColor(R.color.standard_red_main_color_c1), 4, 10);
            mTv_right.setTextColor(res.getColor(R.color.white));
            mTv_right.setBackgroundResource(R.drawable.ad_round_main_bg);
        }
    }

    /**
     * 点击弹框下面两个按钮回调
     */
    public void setOnExitAppListener(OnExitAppListener onExitAppListener) {
        this.listener = onExitAppListener;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                /*case R.id.read_sleep_close:
                    if (listener != null) {
                        listener.onLeft();
                    }
                    break;*/
                case R.id.tv_right:
                    if (listener != null) {
                        listener.onRight();
                    }
                    break;
            }

        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) listener.onDismiss(dialog);
    }

    public interface OnExitAppListener {
        //左按钮回调
        void onLeft();

        //右按钮回调
        void onRight();

        //弹框消失回调
        void onDismiss(DialogInterface dialogInterface);

    }
}
