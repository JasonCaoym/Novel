package com.duoyue.app.ui.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import com.duoyue.app.ui.view.CategoryBookRelativeLayout;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;

/**
 * 书籍分类页面的嵌套滚动效果的自定义Behavior
 *
 * @author wangtian
 * @date 2019/05/27
 */
public class CategoryBookBehavior extends CoordinatorLayout.Behavior<View> {

    private static final String TAG = "APP#CategoryBookBehavior";

    private CategoryBookRelativeLayout floatingLayout;

    public CategoryBookBehavior() {

    }

    public CategoryBookBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof PullToRefreshLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {

//        Logger.d(TAG, "dependency.getY()： " + dependency.getY());

        if (floatingLayout == null && child instanceof CategoryBookRelativeLayout) {
            floatingLayout = (CategoryBookRelativeLayout) child;
            floatingLayout.setTranslationY(-child.getHeight());
            return true;
        }

        if (dependency.getY() <= floatingLayout.getHeight() + floatingLayout.getHeight() / 5) {
            //分类控件已被顶出页面，要显示悬浮窗
            floatingLayout.display();
        } else {
            //分类控件在页面以内，要隐藏悬浮窗
            floatingLayout.hide();
        }

        return true;
    }
}


