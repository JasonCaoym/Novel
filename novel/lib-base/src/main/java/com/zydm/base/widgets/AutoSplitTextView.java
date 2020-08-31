package com.zydm.base.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class AutoSplitTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "AutoSplitTextView";
    private boolean mEnabled = true;
    private int mOldAutoSplitWidth = 0;

    public AutoSplitTextView(Context context) {
        super(context);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAutoSplitEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = getMeasuredWidth();
//        LogUtils.d(TAG, this.hashCode() + " onMeasure:" + measuredWidth + " mOldAutoSplitWidth:" + mOldAutoSplitWidth);
        if (measuredWidth > 0 && mEnabled && measuredWidth != mOldAutoSplitWidth) {
            String newText = autoSplitText(this);
            if (!TextUtils.isEmpty(newText)) {
                super.setText(newText);
                mOldAutoSplitWidth = measuredWidth;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mOldAutoSplitWidth = 0;
    }

    private String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString(); //原始文本
        if (TextUtils.isEmpty(rawText)) {
            return rawText;
        }
//        LogUtils.d(TAG, this.hashCode() + " autoSplitText rawText:" + rawText);
        final Paint tvPaint = tv.getPaint(); //paint，包含字体等信息
        final float tvWidth = tv.getMeasuredWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控件可用宽度

        //将原始文本按行拆分
        String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }
//        LogUtils.d(TAG, this.hashCode() + " autoSplitText result:" + sbNewText);
        return sbNewText.toString();
    }
}
