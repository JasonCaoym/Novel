package com.duoyue.app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;

/**
 * 收起/展开View
 * https://blog.csdn.net/qq_19714505/article/details/71216308
 * @author caoym
 * @data 2019/10/30  18:50
 */
public class ExpandView extends FrameLayout
{
    /**
     * 展开动画
     */
    private Animation mExpandAnimation;

    /**
     * 收起动画
     */
    private Animation mCollapseAnimation;

    /**
     * 是否为展开状态
     */
    private boolean mIsExpand;

    public ExpandView(Context context)
    {
        this(context, null);
    }

    public ExpandView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ExpandView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initExpandView();
    }

    /**
     * 初始化View.
     */
    private void initExpandView()
    {
        mExpandAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_expand);
        mExpandAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }
            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
            @Override
            public void onAnimationEnd(Animation animation)
            {
                setVisibility(View.VISIBLE);
            }
        });
        mCollapseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_collapse);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }
            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
            @Override
            public void onAnimationEnd(Animation animation)
            {
                setVisibility(View.GONE);
            }
        });
    }

    /**
     * 收起
     */
    public void onCollapse()
    {
        if (mIsExpand)
        {
            mIsExpand = false;
            clearAnimation();
            startAnimation(mCollapseAnimation);
        }
    }

    /**
     * 展开
     */
    public void onExpand()
    {
        if (!mIsExpand)
        {
            mIsExpand = true;
            clearAnimation();
            startAnimation(mExpandAnimation);
        }
    }

    /**
     * 判断是否为展开状态.
     * @return
     */
    public boolean isExpand()
    {
        return mIsExpand;
    }

    /**
     * 添加展开的View.
     * @param expandView
     */
    public void addExpandView(View expandView)
    {
        removeAllViews();
        addView(expandView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}