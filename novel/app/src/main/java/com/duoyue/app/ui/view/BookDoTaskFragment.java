package com.duoyue.app.ui.view;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.app.event.GoTaskCenterEvent;
import com.duoyue.app.ui.fragment.ReadSleepFragment;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.zydm.base.utils.ViewUtils;
import org.greenrobot.eventbus.EventBus;

public class BookDoTaskFragment extends DialogFragment {

    private String mContent;
    private int mBeans;
    private Handler handler = new Handler();
    private boolean isShow;
    private boolean isPouse;
    private View rootView;
    private ViewGroup doubleLayout;
    private TextView tvContentTip, tvRewardAd, tvRewardTip;
    private TextView tvSum, tvAdd;
    private boolean showAd;
    private ReadSleepFragment.OnExitAppListener listener;
    private Resources res;
    private int mTastId;
    private ImageView ivClose, ivDou;
    private View mainLayoutView;


    public void setValue(String content, int beans, boolean showAd, int taskId) {
        mContent = content;
        mBeans = beans;
        this.showAd = showAd;
        mTastId = taskId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        res = getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        rootView = inflater.inflate(R.layout.dialog_do_task, container, false);
        initView(rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
    }

    private void initView(View view) {
        doubleLayout = view.findViewById(R.id.dialog_task_double_layout);
        tvSum = view.findViewById(R.id.tv_sum);
        tvAdd = view.findViewById(R.id.tv_add);
        ivDou = view.findViewById(R.id.dialog_task_dou);
        TextView tvContent = view.findViewById(R.id.tv_content);
        if (mBeans != 0) {
            tvSum.setText(String.valueOf(mBeans));
        } else {
            tvSum.setText("");
        }
        tvContent.setText(mContent);

        view.findViewById(R.id.dialog_task_reward).setOnClickListener(onClickListener);
        mainLayoutView = view.findViewById(R.id.dialog_task_layout);
        ivClose = view.findViewById(R.id.dialog_task_close);
        tvRewardTip = view.findViewById(R.id.dialog_task_reward_tip);
        ivClose.setOnClickListener(onClickListener);
        tvContentTip = view.findViewById(R.id.tv_content);
        tvRewardAd = view.findViewById(R.id.dialog_task_reward);

        doubleLayout = view.findViewById(R.id.dialog_task_double_layout);
        if (showAd) {
            doubleLayout.setVisibility(View.VISIBLE);
        } else {
            doubleLayout.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (handler == null) return;
                    if (isShow) {
                        dismiss();
                    } else {
                        isPouse = true;
                    }
                }
            }, 2000);
        }
    }

    public void setClickListener(ReadSleepFragment.OnExitAppListener listener) {
        this.listener = listener;
    }

    public void updateNightMode(boolean isNightMode) {
        if (isNightMode) {
            ivClose.setImageResource(R.mipmap.icon_read_close_night);
            mainLayoutView.setBackgroundColor(res.getColor(R.color.color_202020));
            tvAdd.setTextColor(res.getColor(R.color.standard_red_main_light));
            tvSum.setTextColor(res.getColor(R.color.standard_red_main_light));
            ivDou.setImageResource(R.mipmap.icon_dou_night);
            tvContentTip.setTextColor(res.getColor(R.color.color_56585C));
            tvRewardAd.setTextColor(res.getColor(R.color.color_EECFBA));
            tvRewardAd.setBackgroundResource(R.drawable.ad_round_main_light_bg);
//            StringFormat.setTextViewColor(tvRewardTip, res.getColor(R.color.standard_red_main_light), 3, 7);
        } else {
            ivClose.setImageResource(R.mipmap.icon_read_close);
            mainLayoutView.setBackgroundColor(res.getColor(R.color.white));
            tvAdd.setTextColor(res.getColor(R.color.standard_red_main_color_c1));
            tvSum.setTextColor(res.getColor(R.color.standard_red_main_color_c1));
            ivDou.setImageResource(R.mipmap.icon_doudou);
            tvContentTip.setTextColor(res.getColor(R.color.color_202020));
            tvRewardAd.setTextColor(res.getColor(R.color.white));
            tvRewardAd.setBackgroundResource(R.drawable.ad_round_main_bg);
//            StringFormat.setTextViewColor(tvRewardTip, res.getColor(R.color.standard_red_main_color_c1), 3, 7);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_task_close:
                    if (listener != null) {
                        listener.onLeft();
                    }
                    dismiss();
                    break;
                case R.id.dialog_task_reward:
                    if (listener != null) {
                        listener.onRight();
                    }
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        isShow = true;
        if (isPouse) {
            isPouse = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 100);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDismiss(getDialog());
        }
        if (mTastId == TaskMgr.REWARD_VIDEO_EXIT_TASK) {
            EventBus.getDefault().post(new GoTaskCenterEvent());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.isDestroyed() || manager.isStateSaved()) {
            return;
        }

        if (isAdded()) {
            manager.beginTransaction().remove(this).commit();
        }
        super.show(manager, tag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
