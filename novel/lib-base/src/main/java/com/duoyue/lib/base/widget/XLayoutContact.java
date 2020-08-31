package com.duoyue.lib.base.widget;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public interface XLayoutContact
{
    interface XViewParams
    {
        ViewGroup.MarginLayoutParams getMarginParams();

        Map<Integer, String> getXAttrs();

        void setXAttrs(Map<Integer, String> attrs);
    }

    interface XViewAdapter
    {
        void updateXAttrs(XViewParams params, AttributeSet attrs);

        void updateView(View view, XViewParams params);
    }
}
