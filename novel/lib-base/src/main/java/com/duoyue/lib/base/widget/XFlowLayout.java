package com.duoyue.lib.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.duoyue.lib.base.FlowXLayout;

import java.util.Map;

public class XFlowLayout extends FlowXLayout {

    private XLayoutContact.XViewAdapter adapter;

    public XFlowLayout(Context context) {
        this(context, null);
    }

    public XFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        adapter = new XLayoutAdapter(context);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        XFlowLayout.XLayoutParams params = new XFlowLayout.XLayoutParams(getContext(), attrs);
        adapter.updateXAttrs(params, attrs);
        return params;
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (params instanceof XFrameLayout.XLayoutParams) {
            adapter.updateView(child, (XLayoutContact.XViewParams) params);
        }
        super.addView(child, params);
    }

    static class XLayoutParams extends FrameLayout.LayoutParams implements XLayoutContact.XViewParams {
        Map<Integer, String> xAttrs;

        private XLayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public MarginLayoutParams getMarginParams() {
            return this;
        }

        @Override
        public Map<Integer, String> getXAttrs() {
            return xAttrs;
        }

        @Override
        public void setXAttrs(Map<Integer, String> attrs) {
            xAttrs = attrs;
        }
    }
}
