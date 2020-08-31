package com.duoyue.lib.base.font;

import android.content.Context;
import android.graphics.Typeface;

public class TitleTypeface {

    private Typeface typeFace;
    private static TitleTypeface INSTANCE;

    public static void initTypeface(Context context) {
        INSTANCE = new TitleTypeface(context);
    }

    private TitleTypeface(Context context) {
        typeFace = Typeface.createFromAsset(context.getAssets(), "SourceHanSerifCN-Bold.ttf");
    }

    public static Typeface getTypeFace(Context context) {
        if (INSTANCE == null) {
            initTypeface(context);
        }
        return INSTANCE.typeFace;
    }
}
