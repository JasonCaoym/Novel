package com.zydm.base.utils;

import android.graphics.Color;

public class ColorGradientUtils {

    public static final int MAX_PROGRESS = 1;
    private static final int MAX_ALPHA = 0xFF;

    public static int getColor(float progress, int startColor, int endColor) {
        int color;
        if (progress == MAX_PROGRESS) {
            color = endColor;
        } else {
            int alphaGap = Color.alpha(startColor) - Color.alpha(endColor);
            int alpha = (int) (alphaGap * progress);
            int redGap = Color.red(startColor) - Color.red(endColor);
            int red = (int) (redGap * progress);
            int greenGap = Color.green(startColor) - Color.green(endColor);
            int green = (int) (greenGap * progress);
            int blueGap = Color.blue(startColor) - Color.blue(endColor);
            int blue = (int) (blueGap * progress);
            color = startColor - Color.argb(alpha, red, green, blue);
        }
        return color;
    }

    public static int changeColorAlpha(int color, float progress) {
        return (color & 0xFFFFFF) | ((int) progress * MAX_ALPHA << 24);
    }
}
