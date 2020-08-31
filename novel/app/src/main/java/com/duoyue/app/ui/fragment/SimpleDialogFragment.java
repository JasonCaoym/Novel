package com.duoyue.app.ui.fragment;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.ViewUtils;

public class SimpleDialogFragment extends DialogFragment {


    private TextView tvTitle, tvMsg, tvCancel, tvConfirm;
    private View mainLayout;
    private OnExitAppListener listener;
    private Resources res;
    private int title;
    private String message;
    private int confirmStr;
    private int cancelStr;
    private int messageColor;
    private int messageStart;
    private int messageEnd;

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
        View view = inflater.inflate(R.layout.dialog_simple_frag, container, false);
        mainLayout = view.findViewById(R.id.fragment_parent_layout);
        tvTitle = view.findViewById(R.id.fragment_simple_title);
        if (title != 0) {
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        tvMsg = view.findViewById(R.id.fragment_simple_message);
        if (!TextUtils.isEmpty(message)) {
            tvMsg.setText(message);
            if (messageColor != 0 && messageStart >= 0 && messageEnd < message.length() && messageStart < messageEnd) {
                StringFormat.setTextViewColor(tvMsg, messageColor, messageStart, messageEnd);
            }
        } else {
            tvMsg.setVisibility(View.GONE);
        }
        tvCancel = view.findViewById(R.id.fragment_simple_cancel);
        tvCancel.setOnClickListener(onClickListener);
        tvConfirm = view.findViewById(R.id.fragment_simple_confirm);
        tvConfirm.setOnClickListener(onClickListener);
        if (confirmStr != 0) {
            tvConfirm.setText(confirmStr);
        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
    }

    public SimpleDialogFragment message(String msg) {
        message = msg;
        return this;
    }

    public SimpleDialogFragment messageWithColor(String msg, int color, int start, int end) {
        message = msg;
        messageColor = color;
        messageStart = start;
        messageEnd = end;
        return this;
    }

    public SimpleDialogFragment title(int titleRes) {
        title = titleRes;
        return this;
    }

    public SimpleDialogFragment confirm(int stringRes) {
        confirmStr = stringRes;
        return this;
    }

    public void updateNightMode(boolean isNightMode) {
        if (isNightMode) {
            tvTitle.setTextColor(res.getColor(R.color.color_797C81));
            tvMsg.setTextColor(res.getColor(R.color.color_56585C));
            StringFormat.setTextViewColor(tvMsg, res.getColor(R.color.standard_red_main_light), 5, 8);
            tvConfirm.setBackgroundResource(R.drawable.ad_round_main_light_bg);
        } else {
            tvTitle.setTextColor(res.getColor(R.color.color_1b1b1b));
            tvMsg.setBackgroundColor(res.getColor(R.color.color_666666));
            StringFormat.setTextViewColor(tvMsg, res.getColor(R.color.standard_red_main_color_c1), 5, 8);
            tvConfirm.setBackgroundResource(R.drawable.ad_round_main_bg);
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
                case R.id.fragment_simple_cancel:
                    if (listener != null) {
                        listener.onCancel(getDialog());
                    } else {
                        getDialog().cancel();
                    }
                    break;
                case R.id.fragment_simple_confirm:
                    if (listener != null) {
                        listener.onConfirm(getDialog());
                    } else {
                        getDialog().cancel();
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
        void onCancel(DialogInterface dialog);

        //右按钮回调
        void onConfirm(DialogInterface dialog);

        void onDismiss(DialogInterface dialog);
    }
}
