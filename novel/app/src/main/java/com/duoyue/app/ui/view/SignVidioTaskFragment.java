package com.duoyue.app.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.zydm.base.utils.ViewUtils;

public class SignVidioTaskFragment extends DialogFragment {

    private int mBeans;


    public void setValue(int beans) {
        mBeans = beans;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Utils.dp2px(270);
        lp.height = Utils.dp2px(260);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_sign_vidio_task, container);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Button btOk = view.findViewById(R.id.bt_ok);
        TextView tvContent = view.findViewById(R.id.tv_content);

        SpannableString s1 = new SpannableString(String.format("额外加%s书豆", String.valueOf(mBeans)));
        s1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, s1.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(Utils.dp2px(18), true), 3, s1.length()-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new ForegroundColorSpan(ViewUtils.getColor(R.color.color_FE8B13)), 3, s1.length()-2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvContent.setText(s1);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
}
