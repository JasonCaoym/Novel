package com.duoyue.lib.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zydm.base.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class XLayoutAdapter implements XLayoutContact.XViewAdapter
{
    private static final int BASE_SIZE = 750;

    private static final int ID_LAYOUT_SIZE = 1000;
    private static final int ID_LAYOUT_MIN = 1001;
    private static final int ID_LAYOUT_MAX = 1002;
    private static final int ID_LAYOUT_MARGIN = 1003;
    private static final int ID_LAYOUT_PADDING = 1004;
    private static final int ID_TEXT_SIZE = 1005;
    private static final int ID_TEXT_DRAWABLE_BOUNDS = 1006;
    private static final int ID_TEXT_DRAWABLE_PADDING = 1007;
    private static final int ID_SHAPE_RADIUS = 1008;

    private Context mContext;
    private Pattern mPattern;
    private int mScreenSize;

    XLayoutAdapter(Context context)
    {
        mContext = context;
        mPattern = Pattern.compile("([\\d]+|#),?");
        mScreenSize = getScreenSize();
    }

    private int getScreenSize()
    {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return Math.min(metrics.widthPixels, metrics.heightPixels);
    }

    @Override
    public void updateXAttrs(XLayoutContact.XViewParams params, AttributeSet attrs)
    {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.XView);
        if (array.length() > 0)
        {
            Map<Integer, String> map = new HashMap<>();
            put(map, ID_LAYOUT_SIZE, array.getString(R.styleable.XView_x_layout_size));
            put(map, ID_LAYOUT_MIN, array.getString(R.styleable.XView_x_layout_min));
            put(map, ID_LAYOUT_MAX, array.getString(R.styleable.XView_x_layout_max));
            put(map, ID_LAYOUT_MARGIN, array.getString(R.styleable.XView_x_layout_margin));
            put(map, ID_LAYOUT_PADDING, array.getString(R.styleable.XView_x_layout_padding));
            put(map, ID_TEXT_SIZE, array.getString(R.styleable.XView_x_text_size));
            put(map, ID_TEXT_DRAWABLE_BOUNDS, array.getString(R.styleable.XView_x_text_drawable_bounds));
            put(map, ID_TEXT_DRAWABLE_PADDING, array.getString(R.styleable.XView_x_text_drawable_padding));
            put(map, ID_SHAPE_RADIUS, array.getString(R.styleable.XView_x_shape_radius));
            params.setXAttrs(map);
        }
        array.recycle();
    }

    private void put(Map<Integer, String> map, Integer id, String value)
    {
        if (value != null)
        {
            map.put(id, value);
        }
    }

    @Override
    public void updateView(View view, XLayoutContact.XViewParams params)
    {
        Map<Integer, String> xAttrs = params.getXAttrs();
        if (xAttrs != null)
        {
            for (Map.Entry<Integer, String> entry : xAttrs.entrySet())
            {
                List<Float> list = parseValue(entry.getValue());
                switch (entry.getKey())
                {
                    case ID_LAYOUT_SIZE:
                        updateLayoutSize(params.getMarginParams(), list);
                        break;
                    case ID_LAYOUT_MIN:
                        updateLayoutMin(view, list);
                        break;
                    case ID_LAYOUT_MAX:
                        updateLayoutMax(view, list);
                        break;
                    case ID_LAYOUT_MARGIN:
                        updateLayoutMargin(params.getMarginParams(), list);
                        break;
                    case ID_LAYOUT_PADDING:
                        updateLayoutPadding(view, list);
                        break;
                    case ID_TEXT_SIZE:
                        updateTextSize(view, list);
                        break;
                    case ID_TEXT_DRAWABLE_BOUNDS:
                        updateTextDrawableBounds(view, list);
                        break;
                    case ID_TEXT_DRAWABLE_PADDING:
                        updateTextDrawablePadding(view, list);
                        break;
                    case ID_SHAPE_RADIUS:
                        updateShapeRadius(view, list);
                        break;
                }
            }
        }
    }

    private void updateLayoutSize(ViewGroup.MarginLayoutParams params, List<Float> list)
    {
        if (list.size() == 2)
        {
            Float width = list.get(0);
            Float height = list.get(1);
            params.width = width == null ? params.width : width.intValue();
            params.height = height == null ? params.height : height.intValue();
        }
    }

    private void updateLayoutMin(View view, List<Float> list)
    {
        if (list.size() == 2)
        {
            Float minWidth = list.get(0);
            Float minHeight = list.get(1);
            if (minWidth != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setMinWidth(minWidth.intValue());
                }
            }
            if (minHeight != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setMinHeight(minHeight.intValue());
                }
            }
        }
    }

    private void updateLayoutMax(View view, List<Float> list)
    {
        Float maxWidth = list.get(0);
        Float maxHeight = list.get(1);
        if (list.size() == 2)
        {
            if (maxWidth != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setMaxWidth(maxWidth.intValue());
                } else if (view instanceof ImageView)
                {
                    ((ImageView) view).setMaxWidth(maxWidth.intValue());
                }
            }
            if (maxHeight != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setMaxHeight(maxHeight.intValue());
                } else if (view instanceof ImageView)
                {
                    ((ImageView) view).setMaxHeight(maxHeight.intValue());
                }
            }
        }
    }

    private void updateLayoutMargin(ViewGroup.MarginLayoutParams params, List<Float> list)
    {
        if (list.size() == 4)
        {
            Float marginLeft = list.get(0);
            Float marginTop = list.get(1);
            Float marginRight = list.get(2);
            Float marginBottom = list.get(3);
            params.leftMargin = marginLeft == null ? params.leftMargin : marginLeft.intValue();
            params.topMargin = marginTop == null ? params.topMargin : marginTop.intValue();
            params.rightMargin = marginRight == null ? params.rightMargin : marginRight.intValue();
            params.bottomMargin = marginBottom == null ? params.bottomMargin : marginBottom.intValue();
        }
    }

    private void updateLayoutPadding(View view, List<Float> list)
    {
        if (list.size() == 4)
        {
            Float paddingLeft = list.get(0);
            Float paddingTop = list.get(1);
            Float paddingRight = list.get(2);
            Float paddingBottom = list.get(3);
            int paddingL = paddingLeft == null ? view.getPaddingLeft() : paddingLeft.intValue();
            int paddingT = paddingTop == null ? view.getPaddingTop() : paddingTop.intValue();
            int paddingR = paddingRight == null ? view.getPaddingRight() : paddingRight.intValue();
            int paddingB = paddingBottom == null ? view.getPaddingBottom() : paddingBottom.intValue();
            view.setPadding(paddingL, paddingT, paddingR, paddingB);
        }
    }

    private void updateTextSize(View view, List<Float> list)
    {
        if (list.size() == 1)
        {
            Float textSize = list.get(0);
            if (textSize != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
            }
        }
    }

    private void updateTextDrawableBounds(View view, List<Float> list)
    {
        if (list.size() == 4)
        {
            Float left = list.get(0);
            Float top = list.get(1);
            Float right = list.get(2);
            Float bottom = list.get(3);
            if (view instanceof TextView)
            {
                if (left != null && top != null && right != null && bottom != null)
                {
                    Drawable[] drawables = ((TextView) view).getCompoundDrawables();
                    for (Drawable drawable : drawables)
                    {
                        if (drawable != null)
                        {
                            drawable.setBounds(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
                        }
                    }
                }
            }
        }
    }

    private void updateTextDrawablePadding(View view, List<Float> list)
    {
        if (list.size() == 1)
        {
            Float padding = list.get(0);
            if (padding != null)
            {
                if (view instanceof TextView)
                {
                    ((TextView) view).setCompoundDrawablePadding(padding.intValue());
                }
            }
        }
    }

    private void updateShapeRadius(View view, List<Float> list)
    {
        if (list.size() == 1)
        {
            Float size = list.get(0);
            if (size != null)
            {
                Drawable drawable = view.getBackground();
                if (drawable instanceof GradientDrawable)
                {
                    ((GradientDrawable) drawable).setCornerRadius(size);
                }
            }
        }
    }

    private List<Float> parseValue(String value)
    {
        List<Float> list = new ArrayList<>();
        Matcher matcher = mPattern.matcher(value);
        while (matcher.find())
        {
            list.add(parseSize(matcher.group(1)));
        }
        return list;
    }

    private Float parseSize(String value)
    {
        try
        {
            return Float.parseFloat(value) * mScreenSize / BASE_SIZE;
        } catch (Throwable throwable)
        {
            //ignore
        }
        return null;
    }
}
