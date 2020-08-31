package com.duoyue.app.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.duoyue.app.common.data.response.bookshelf.TaskListResp;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.SharePreferenceUtils;

import java.util.List;

public class ExitAppFragment extends DialogFragment {


    private TextView mTv_title, mTv_left, mTv_right, tvShudou;

    private XLinearLayout xLinearLayout;
    private OnExitAppListener listener;
    private AdSiteBean adSiteBean;
    private boolean isClicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(onKeyListener);
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            if (lp != null) {
                lp.gravity = Gravity.BOTTOM;
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                lp.windowAnimations = R.style.DialogAnimation;
            }
        }
        View view = inflater.inflate(R.layout.dialog_exit, container, false);
        mTv_title = view.findViewById(R.id.tv_title);
        xLinearLayout = view.findViewById(R.id.xll_dou);
        mTv_left = view.findViewById(R.id.tv_left);
        mTv_right = view.findViewById(R.id.tv_right);
        mTv_left.setOnClickListener(onClickListener);
        mTv_right.setOnClickListener(onClickListener);
        tvShudou = view.findViewById(R.id.exit_shudou);

        return view;
    }

    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        }
    };

    public void setShudouSize() {
        List<TaskListResp.ListBean> list = SharePreferenceUtils.getList(getContext(), SharePreferenceUtils.TASK_LIST_CACHE);
        if (list == null || list.size() == 0 || !list.contains(new TaskListResp.ListBean(TaskMgr.REWARD_VIDEO_EXIT_TASK))) {//没有下发任务
            setMiddleIsDisplay(View.GONE);
            return;
        }
        int index = list.indexOf(new TaskListResp.ListBean(TaskMgr.REWARD_VIDEO_EXIT_TASK));
        if (index >= 0) {
            tvShudou.setText(String.format(getResources().getString(R.string.exit_dialog_tip), list.get(index).getBookBean()));
        } else {
            setMiddleIsDisplay(View.GONE);
        }
    }

    public boolean showAd() {
        List<TaskListResp.ListBean> list = SharePreferenceUtils.getList(getContext(), SharePreferenceUtils.TASK_LIST_CACHE);
        if (list == null || list.size() == 0 || !list.contains(new TaskListResp.ListBean(TaskMgr.REWARD_VIDEO_EXIT_TASK))) {//没有下发任务
            adSiteBean = null;
            return false;
        }

        adSiteBean = AdConfigManger.getInstance().showAd(getActivity(), Constants.channalCodes[8]);

        if (adSiteBean == null) {
            Logger.e("ad#AdConfig", "退出激励视频没有获取到有效广告");
            return false;
        }
        boolean result = SPUtils.INSTANCE.getBoolean(TaskMgr.getAvailiableTask(TaskMgr.REWARD_VIDEO_EXIT_TASK), false);
        if (!result) {
            adSiteBean = null;
            Logger.e("ad#AdConfig", "后台大佬说不可以显示了");
        } else {
            Logger.e("ad#AdConfig", "后台大佬说还可以显示");
        }
        return result;
    }

    public AdSiteBean getAdSiteBean() {
        return adSiteBean;
    }

    /**
     * 设置中间布局是否显示
     */
    public void setMiddleIsDisplay(int isShow) {
        if (xLinearLayout == null) throw new NullPointerException("此函数要在show函数执行后再调用   否者报错");
        xLinearLayout.setVisibility(isShow);
    }


    /**
     * 设置弹框下面两个按钮文本
     * <p>
     * <p>
     */
    public void setBottomBtnText(String leftText, String rightText) {
        if (mTv_left == null || mTv_right == null)
            throw new NullPointerException("此函数要在show函数执行后再调用   否者报错");
        mTv_left.setText(TextUtils.isEmpty(leftText) ? "确定" : leftText);
        mTv_right.setText(TextUtils.isEmpty(rightText) ? "取消" : rightText);

    }

    /**
     * 设置弹框下面两个按钮文本颜色
     * <p>
     * 此函数要在show函数执行后再调用   否者报错
     */
    public void setBottomBtnTextColor(int leftColor, int rightColor) {
        if (mTv_left == null || mTv_right == null)
            throw new NullPointerException("此函数要在show函数执行后再调用   否者报错");
        mTv_left.setTextColor(leftColor);
        mTv_right.setTextColor(rightColor);
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
                case R.id.tv_left:
                    if (listener != null) listener.onLeft();
                    isClicked = true;
                    dismiss();
                    break;
                case R.id.tv_right:
                    if (listener != null) listener.onRight();
                    isClicked = true;
                    dismiss();
                    break;
            }

        }
    };


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!isClicked) {
            if (listener != null) {
                listener.onDismiss(dialog);
            }
        }

        isClicked = false;
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
