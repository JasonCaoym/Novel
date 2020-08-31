package com.duoyue.lib.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.zydm.base.tools.TooFastChecker;

public class AdShadeViewGroup extends XRelativeLayout implements View.OnTouchListener {

    private TooFastChecker tooFastChecker = new TooFastChecker(2000);

    public AdShadeViewGroup(Context context) {
        super(context);
    }

    public AdShadeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdShadeViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP && tooFastChecker.isTooFast()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
