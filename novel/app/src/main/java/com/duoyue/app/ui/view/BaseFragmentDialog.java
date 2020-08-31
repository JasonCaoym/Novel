package com.duoyue.app.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;

public abstract class BaseFragmentDialog extends DialogFragment {

    public boolean isShow;

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
        lp.width = getWidth();
        lp.height = getHeight();
        lp.windowAnimations = getAnimation();
        lp.gravity = getGravity();
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(getInflateLayout(), container);
        initView(view);
        initData();
        return view;
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 初始化view
     * @param view
     */
    public abstract void initView(View view);

    /**
     * 默认屏幕宽
     * @return
     */
    public int getWidth() {
        return Utils.getScreenWidth();
    }

    /**
     * 默认wrap_content
     * @return
     */
    public int getHeight(){
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 默认底部
     *
     * @return
     */
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    /**
     * 默认底部动画
     *
     * @return
     */
    public int getAnimation() {
        return R.style.DialogAnimation;
    }

    /**
     * 设置layoutView
     * @return
     */
    public abstract int getInflateLayout();


    @Override
    public void onResume() {
        super.onResume();
        isShow = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
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
    }
}
