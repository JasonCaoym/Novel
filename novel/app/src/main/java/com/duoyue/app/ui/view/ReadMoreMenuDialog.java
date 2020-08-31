package com.duoyue.app.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.*;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;

/**
 * 目录章节
 */
public class ReadMoreMenuDialog extends DialogFragment implements View.OnClickListener {

    private OnItemClickListener listener;


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick((Integer) v.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int postion);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        lp.width = Utils.dp2px(getContext(),250);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.RIGHT | Gravity.TOP;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_read_more_menu, container);

        view.findViewById(R.id.dialog_read_more_book).setTag(1);
        view.findViewById(R.id.dialog_read_more_book).setOnClickListener(this);
        view.findViewById(R.id.dialog_read_more_share).setTag(2);
        view.findViewById(R.id.dialog_read_more_share).setOnClickListener(this);
        view.findViewById(R.id.dialog_read_more_error).setTag(3);
        view.findViewById(R.id.dialog_read_more_error).setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
