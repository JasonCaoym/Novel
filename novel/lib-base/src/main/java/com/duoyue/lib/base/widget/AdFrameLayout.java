package com.duoyue.lib.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.zydm.base.tools.TooFastChecker;

public class AdFrameLayout extends FrameLayout implements View.OnTouchListener {

    private boolean isDisabled;
    public AdFrameLayout(Context context) {
        super(context);
    }

    public AdFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && isDisabled) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
