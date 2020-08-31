package com.zydm.base.widgets.refreshview;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by yan on 2017/3/1.
 */

public class PullOnGestureListener extends GestureDetector.SimpleOnGestureListener {

    private boolean mOnScrollReturn;

    @Override
    public boolean onDown(MotionEvent e) {
        mOnScrollReturn = false;
        return super.onDown(e);
    }

    @Override
    public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mOnScrollReturn = onScroll2(e1, e2, distanceX, distanceY);
        return mOnScrollReturn;
    }

    public boolean onScroll2(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean isOnScrollReturn() {
        return mOnScrollReturn;
    }
}
