package com.duoyue.app.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.ui.activity.BaseActivity;

public class VideoEnterDialog extends Dialog {

    private static final String TAG = "MTDialog";
    protected BaseActivity mActivity;

    private View.OnClickListener videoOnClickListener;
    private View.OnClickListener ruleOnClickListener;
    private int freeDuration;


    public VideoEnterDialog(Context context, int freeDuration, View.OnClickListener videoOnClickListener,
                            View.OnClickListener ruleOnClickListener) {
        super(context, R.style.Dialog);
        mActivity = (BaseActivity) context;
        this.videoOnClickListener = videoOnClickListener;
        this.ruleOnClickListener = ruleOnClickListener;
        this.freeDuration = freeDuration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_video_enter);
        findViewById(R.id.dialog_video_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((TextView)findViewById(R.id.dialog_content)).setText(String.format(
                mActivity.getResources().getString(R.string.read_video_enter_tip), freeDuration));
        findViewById(R.id.dialog_video_play).setOnClickListener(videoOnClickListener);
        ((TextView)findViewById(R.id.dialog_video_play)).setText(
                String.format("看视频免%s分钟广告", freeDuration));
        findViewById(R.id.dialog_video_rule).setOnClickListener(ruleOnClickListener);
    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (!isShowing()) {
            return;
        }
        super.dismiss();
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.inflate(layoutResID, null);
        setContentView(contentView);
    }

}
