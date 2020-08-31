package com.zydm.base.widgets;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.duoyue.lib.base.log.Logger;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.share.platform.ShareUtil;
import com.share.platform.share.ShareListener;
import com.share.platform.share.SharePlatform;
import com.sina.weibo.sdk.WbSdk;
import com.zydm.base.R;
import com.zydm.base.utils.BitmapUtils;
import com.zydm.base.utils.ToastUtils;


/**
 * 底部分享弹出框
 */
public class BottomShareDialog extends BottomBaseDialog<BottomShareDialog> implements View.OnClickListener {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BottomShareDialog";
    private Context context;
    private TextView cancelTxt;
    private TextView wxTxt;
    private TextView chatMomentsTxt;
    private TextView qqTxt;
    private TextView wbTxt;
    private TextView qqZoneTxt;
    private TextView copyLinkTxt;
    private ShareListener mShareListener;
    private String title;
    private String content;
    private String targetUrl;
    private Bitmap bitmap;
    /**
     * 大图
     */
    private Bitmap bigBitmap;
    private ProgressDialog mProgressDialog;
    private ShareClickListener listener;
    private ShareResultListener mShareResultListener;

    public static final int SHARE_ERROR = -1;
    public static final int SHARE_CANCEL = 0;
    public static final int SHARE_SUCCESS = 1;


    public interface ShareClickListener {
        void onClick(int type);
    }

    public interface ShareResultListener {

        void onShare(int shareResult);
    }

    public BottomShareDialog(Context context, ShareClickListener clickListener) {
        super(context);
        this.context = context;
        listener = clickListener;
    }

    public BottomShareDialog(Context context, ShareClickListener clickListener, ShareResultListener shareResultListener) {
        super(context);
        this.context = context;
        listener = clickListener;
        this.mShareResultListener = shareResultListener;
    }

    /**
     * 设置分享内容数据
     *
     * @param title
     * @param content
     * @param thumbBitmap
     * @param targetUrl
     */
    public void setShareData(String title, String content, Bitmap thumbBitmap, Bitmap bigBitmap, String targetUrl) {
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
        this.bitmap = thumbBitmap;
        this.bigBitmap = bigBitmap;
    }

    /**
     * 设置分享内容数据
     * <p>
     * 异步加载书籍封面cover的bitmap
     *
     * @param title
     * @param content
     * @param cover
     * @param targetUrl
     */
    public void setShareData(final String title, final String content, String cover, final Bitmap bigBitmap, final String targetUrl) {
        Glide.with(context).asBitmap().load(cover).into(new SimpleTarget<Bitmap>(150, 150) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                //微信朋友圈只支持小于32K大小的图片，所以这里需要做压缩
                byte[] output = BitmapUtils.bmpToByteArray(resource, 30);
                Bitmap b = BitmapFactory.decodeByteArray(output, 0, output.length);
                setShareData(title, content, b, bigBitmap, targetUrl);
            }
        });
    }

    //该方法用来出来数据初始化代码
    @Override
    public View onCreateView() {
        //填充弹窗布局
        View inflate = View.inflate(context, R.layout.dialog_bottom_share, null);
        cancelTxt = inflate.findViewById(R.id.cancel_txt);
        wxTxt = inflate.findViewById(R.id.wx_txt);
        chatMomentsTxt = inflate.findViewById(R.id.wechat_moments_txt);
        qqTxt = inflate.findViewById(R.id.qq_txt);
        wbTxt = inflate.findViewById(R.id.wb_txt);
        qqZoneTxt = inflate.findViewById(R.id.zone_txt);
        copyLinkTxt = inflate.findViewById(R.id.copy_link_txt);
        mShareListener = new ShareListener() {
            @Override
            public void shareSuccess() {
                if (mShareResultListener != null)mShareResultListener.onShare(SHARE_SUCCESS);
                    ToastUtils.show(R.string.share_suc);
                mProgressDialog.dismiss();
            }

            @Override
            public void shareFailure(Exception e) {
                if (mShareResultListener != null)mShareResultListener.onShare(SHARE_ERROR);
                Log.d(TAG, e.getMessage());
                mProgressDialog.dismiss();
                ToastUtils.show(e.getMessage());
                ToastUtils.show(R.string.share_fail);
            }

            @Override
            public void shareCancel() {
                if (mShareResultListener != null)mShareResultListener.onShare(SHARE_CANCEL);
                ToastUtils.show(R.string.cancel_share);
                mProgressDialog.dismiss();

            }
        };
        return inflate;
    }

    //该方法用来处理逻辑代码
    @Override
    public void setUiBeforShow() {
        //点击弹窗相应位置，处理相关逻辑。
        wxTxt.setOnClickListener(this);
        chatMomentsTxt.setOnClickListener(this);
        qqTxt.setOnClickListener(this);
        wbTxt.setOnClickListener(this);
        qqZoneTxt.setOnClickListener(this);
        copyLinkTxt.setOnClickListener(this);
        //点×关闭弹框的代码
        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭弹框的代码
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (bitmap == null) {
            return;
        }

        int type = 0;
        dismiss();
        if (v.getId() == R.id.copy_link_txt) {
            //复制链接
            copyLink();
            type = 6;
        } else {

            showProcessingPage(context.getResources().getString(R.string.share_loading));

            if (v.getId() == R.id.wx_txt) {         //微信
                shareWx();
                type = 1;
            } else if (v.getId() == R.id.wechat_moments_txt) { //微信朋友圈
                shareWxMechat();
                type = 2;
            } else if (v.getId() == R.id.wb_txt) {  //微博
                shareWeiBo();
                type = 3;
            } else if (v.getId() == R.id.qq_txt) { //QQ
                shareQQ();
                type = 4;
            } else if (v.getId() == R.id.zone_txt) { //QQ空间
                shareQQZone();
                type = 5;
            }
        }
        if (listener != null) {
            listener.onClick(type);
        }
    }

    /**
     * 微信分享
     */
    public void shareWx() {
        //判断微信是否已安装.
        if (!ShareUtil.isWeiXinInstalled(context)) {
            //微信未安装.
            ToastUtils.show(R.string.no_install_wechat);
            mProgressDialog.dismiss();
            return;
        }
        ShareUtil.shareMedia(context, mProgressDialog, SharePlatform.WX, title, content, targetUrl, bitmap, mShareListener);
    }

    /**
     * 微信朋友圈分享
     */
    public void shareWxMechat() {
        //判断微信是否已安装.
        if (!ShareUtil.isWeiXinInstalled(context)) {
            //微信未安装.
            ToastUtils.show(R.string.no_install_wechat);
            mProgressDialog.dismiss();
            return;
        }
        ShareUtil.shareMedia(context, mProgressDialog, SharePlatform.WX_TIMELINE, title, content, targetUrl, bitmap, mShareListener);
    }

    /**
     * 微博分享
     */
    public void shareWeiBo() {
        if (!WbSdk.isWbInstall(context)) {
            //微博未安装.
            ToastUtils.show(R.string.no_install_weibo);
            mProgressDialog.dismiss();
            return;
        }

        String summary = title + "——" + content + "#" + context.getResources().getString(R.string.app_name) + "#" + targetUrl;
        ShareUtil.shareMedia(context, mProgressDialog, SharePlatform.WEIBO, title, summary, targetUrl, bigBitmap, mShareListener);
    }

    /**
     * qq分享
     */
    public void shareQQ() {
        if (!ShareUtil.isQQInstalled(context)) {
            //QQ未安装.
            ToastUtils.show(R.string.no_install_qq);
            mProgressDialog.dismiss();
            return;
        }
        ShareUtil.shareMedia(context, mProgressDialog, SharePlatform.QQ, title, content, targetUrl, bitmap, mShareListener);
    }

    /**
     * 空间分享
     */
    public void shareQQZone() {
        if (!ShareUtil.isQQInstalled(context)) {
            //QQ未安装.
            ToastUtils.show(R.string.no_install_qq);
            mProgressDialog.dismiss();
            return;
        }
        ShareUtil.shareMedia(context, mProgressDialog, SharePlatform.QZONE, title, content, targetUrl, bitmap, mShareListener);
    }

    /**
     * 复制链接
     */
    public void copyLink() {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", title + targetUrl);
        cm.setPrimaryClip(mClipData);
        ToastUtils.show("已复制，快去粘贴分享吧");
    }

    /**
     * 显示处理进度页面.
     *
     * @param msg 加载页面提示信息
     */
    private void showProcessingPage(String msg) {
        try {
            mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_DARK);
            // 设置对话框参数
            mProgressDialog.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.loading_bar));
            mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();
            WindowManager.LayoutParams params = mProgressDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.horizontalMargin = 20;
            params.alpha = 0.8f;
            params.gravity = Gravity.CENTER;
            mProgressDialog.getWindow().setAttributes(params);
        } catch (Throwable throwable) {
            Logger.e(TAG, "showProcessingPage: {}, {}", msg, throwable);
        }
    }

}