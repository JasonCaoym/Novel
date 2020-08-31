package com.duoyue.app.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import com.duoyue.lib.base.widget.XRelativeLayout;

/**
 * 书籍分类页面的悬浮分类标题控件
 * @author wangtian
 * @date 2019/05/27
 */
public class CategoryBookRelativeLayout extends XRelativeLayout {

    //分类悬浮控件是否正在显示
    private boolean isShowing;

    //显示动画
    private ObjectAnimator displayAnimator;
    //隐藏动画
    private ObjectAnimator hideAnimator;

    public CategoryBookRelativeLayout(Context context) {
        super(context);
    }

    public CategoryBookRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryBookRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 显示
     */
    public void display(){
        if (isShowing) {
            return;
        }
        this.setTranslationY(0);
        isShowing = true;
    }

    /**
     * 隐藏
     */
    public void hide(){
        if (!isShowing) {
            return;
        }
        this.setTranslationY(-this.getHeight());
        isShowing = false;
    }

    /**
     * 显示，带动画效果
     */
    public void displayAnimator(Animator.AnimatorListener listener){
        if(displayAnimator == null){
            displayAnimator = ObjectAnimator.ofFloat(this, "translationY", -this.getHeight(), 0);
            displayAnimator.setDuration(200);
        }
        if(listener != null){
            displayAnimator.addListener(listener);
        }
        displayAnimator.start();
    }

    public void hideAnimator(Animator.AnimatorListener listener){
        if(hideAnimator == null){
            hideAnimator = ObjectAnimator.ofFloat(this, "translationY", 0, -this.getHeight());
            hideAnimator.setDuration(200);
        }
        if(listener != null){
            hideAnimator.addListener(listener);
        }
        hideAnimator.start();
    }
}
