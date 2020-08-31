package com.duoyue.lib.base.customshare;


import android.content.Context;
import android.graphics.BitmapFactory;
import com.zydm.base.widgets.BottomShareDialog;

public class CustomShareManger {
    /**
     * 日志Tag
     */
    private static final String TAG = "Share#CustomShareManger";

    private static class Inner {
        static final CustomShareManger INSTANCE = new CustomShareManger();
    }

    private CustomShareManger() {
    }

    public static CustomShareManger getInstance() {
        return CustomShareManger.Inner.INSTANCE;
    }

    /**
     * 文本分享
     *
     * @param context
     * @param title   主题
     * @param content 内容
     * @parm thumbUrlOrPath 显示图片
     * @parm targetUrl 显示的链接
     */
    public void shareWithText(Context context, String title, String content, int bigBitamp, int thumbBitmap,
                              String targetUrl, BottomShareDialog.ShareClickListener listener) {
        BottomShareDialog customBaseDialog = new BottomShareDialog(context, listener);
        customBaseDialog.setShareData(title, content, BitmapFactory.decodeResource(context.getResources(), thumbBitmap, null),
                BitmapFactory.decodeResource(context.getResources(), bigBitamp, null), targetUrl);
        customBaseDialog.onCreateView();
        customBaseDialog.setUiBeforShow();
        //点击空白区域能不能退出
        customBaseDialog.setCanceledOnTouchOutside(true);
        //按返回键能不能退出
        customBaseDialog.setCancelable(true);
        customBaseDialog.show();
    }

    /**
     * 文本分享
     *
     * @param context
     * @param title   主题
     * @param content 内容
     * @parm thumbUrlOrPath 显示图片
     * @parm targetUrl 显示的链接
     */
    public BottomShareDialog shareWithText(Context context, String title, String content, int bigBitamp, int thumbBitmap,
                              String targetUrl, BottomShareDialog.ShareClickListener listener, BottomShareDialog.ShareResultListener shareResultListener) {
        BottomShareDialog customBaseDialog = new BottomShareDialog(context, listener,shareResultListener);
        customBaseDialog.setShareData(title, content, BitmapFactory.decodeResource(context.getResources(), thumbBitmap, null),
                BitmapFactory.decodeResource(context.getResources(), bigBitamp, null), targetUrl);
        customBaseDialog.onCreateView();
        customBaseDialog.setUiBeforShow();
        //点击空白区域能不能退出
        customBaseDialog.setCanceledOnTouchOutside(true);
        //按返回键能不能退出
        customBaseDialog.setCancelable(true);
        customBaseDialog.show();
        return customBaseDialog;
    }

    /**
     * 分享书籍
     * @param context
     * @param title
     * @param content
     * @param bigBitamp
     * @param cover
     * @param targetUrl
     */
    public void shareBookWithText(Context context, String title, String content, int bigBitamp, String cover,
                                  String targetUrl, BottomShareDialog.ShareClickListener listener) {
        BottomShareDialog customBaseDialog = new BottomShareDialog(context, listener);
        customBaseDialog.setShareData(title, content, cover,
                BitmapFactory.decodeResource(context.getResources(), bigBitamp, null), targetUrl);
        customBaseDialog.onCreateView();
        customBaseDialog.setUiBeforShow();
        //点击空白区域能不能退出
        customBaseDialog.setCanceledOnTouchOutside(true);
        //按返回键能不能退出
        customBaseDialog.setCancelable(true);
        customBaseDialog.show();
    }


    /**
     * 分享书籍
     * @param context
     * @param title
     * @param content
     * @param bigBitamp
     * @param cover
     * @param targetUrl
     */
    public void shareBookWithText(Context context, String title, String content, int bigBitamp, String cover,
                                  String targetUrl, BottomShareDialog.ShareClickListener listener, BottomShareDialog.ShareResultListener shareResultListener) {
        BottomShareDialog customBaseDialog = new BottomShareDialog(context, listener,shareResultListener);
        customBaseDialog.setShareData(title, content, cover,
                BitmapFactory.decodeResource(context.getResources(), bigBitamp, null), targetUrl);
        customBaseDialog.onCreateView();
        customBaseDialog.setUiBeforShow();
        //点击空白区域能不能退出
        customBaseDialog.setCanceledOnTouchOutside(true);
        //按返回键能不能退出
        customBaseDialog.setCancelable(true);
        customBaseDialog.show();
    }
}
