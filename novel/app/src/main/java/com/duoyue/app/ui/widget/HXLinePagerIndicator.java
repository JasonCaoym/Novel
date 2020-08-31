package com.duoyue.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import com.duoyue.mianfei.xiaoshuo.R;

public class HXLinePagerIndicator extends LinePagerIndicatorEx {
    private Context mContext;
    public HXLinePagerIndicator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LinearGradient lg = new LinearGradient(getLineRect().left, getLineRect().top, getLineRect().right,
                getLineRect().bottom, new int[]{mContext.getResources().getColor(R.color.standard_red_main_color_c1),
                mContext.getResources().getColor(R.color.standard_red_main_color_bright)}, null, LinearGradient.TileMode.CLAMP);
        getPaint().setShader(lg);
        canvas.drawRoundRect(getLineRect(), getRoundRadius(), getRoundRadius(), getPaint());
    }
}