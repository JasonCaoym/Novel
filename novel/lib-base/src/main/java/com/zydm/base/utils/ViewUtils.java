package com.zydm.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.TextView;
import com.zydm.base.common.BaseApplication;

import java.io.ByteArrayOutputStream;

public class ViewUtils {

    private static final int MIN_WVGA_HEIGHT = 700;
    private static final int WVGA_HEIGHT = 800;
    private static final int MIN_HD_HEIGHT = 1180;
    private static final int HD_HEIGHT = 1280;
    public static final int ONE_DIVIDER_HEIGHT = dp2px(1);
    public static final int TEN_DIVIDER_HEIGHT = dp2px(10);

    public static View inflateView(Activity activity, int layoutId) {
        return LayoutInflater.from(activity).inflate(layoutId, null);
    }

    public static View inflateView(Activity activity, int layoutId, ViewGroup root) {
        return LayoutInflater.from(activity).inflate(layoutId, root, false);
    }

    public static int getDimenPx(int resId) {

        return getResources().getDimensionPixelSize(resId);
    }

    public static Resources getResources() {

        return getApplication().getResources();
    }

    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(getApplication(), resId);
    }

    public static int[] getPhonePixels() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int curWidth = metrics.widthPixels;
        int curHeight = metrics.heightPixels;
        if (curHeight >= MIN_WVGA_HEIGHT && curHeight <= WVGA_HEIGHT) {
            curHeight = WVGA_HEIGHT;
        }
        if (curHeight >= MIN_HD_HEIGHT && curHeight <= HD_HEIGHT) {
            curHeight = HD_HEIGHT;
        }
        return new int[]{curWidth, curHeight};
    }

    public static void setViewVisible(View view, boolean show) {

        if (view == null) {
            return;
        }
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void setViewVisibleOrInvisible(View view, boolean show) {

        if (view == null) {
            return;
        }
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setViewVisibility(boolean visible, View... views) {

        if (views == null || views.length == 0) {
            return;
        }
        if (visible) {
            for (View v : views) {
                v.setVisibility(View.VISIBLE);
            }
        } else {
            for (View v : views) {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void setViewClickable(boolean clickable, View... views) {

        if (views == null || views.length == 0) {
            return;
        }
        if (clickable) {
            for (View v : views) {
                v.setClickable(true);
            }
        } else {
            for (View v : views) {
                v.setClickable(false);
            }
        }
    }

    public static int[] getResourceArray(int arrayId) {

        TypedArray array = getResources().obtainTypedArray(arrayId);
        int len = array.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            resIds[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return resIds;
    }

    public static String getString(int stringId) {

        return getApplication().getString(stringId);
    }

    public static String getString(int resId, Object... formatArgs) {

        return getResources().getString(resId, formatArgs);
    }

    public static int getColor(int colorId) {

//        return getResources().getColor(colorId);
        return ContextCompat.getColor(getApplication(), colorId);
    }

    private static Application getApplication() {
        return BaseApplication.context.globalContext;
    }

    public static int getInteger(int integerId) {

        return getResources().getInteger(integerId);
    }

    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int getDensityDpi() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }

    public static int dp2px(float dpValue) {

        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float px) {

        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * @param view
     * @return listview实际滚动距离
     */
    public static int getListViewScrollY(AbsListView view) {

        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = view.getHeight();
        }
        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    public static void setPaddingLeft(View view, int paddingLeft) {
        if (null == view) {
            return;
        }
        view.setPadding(paddingLeft, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setPaddingTop(View view, int paddingTop) {
        if (null == view) {
            return;
        }
        view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setPaddingBottom(View view, int paddingBottom) {
        if (null == view) {
            return;
        }
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), paddingBottom);
    }

    public static int computeDistanceWithPhoneWidth(int denominator, int member) {

        float ratio = (float) denominator / (float) member;
        return (int) (ViewUtils.getPhonePixels()[0] / ratio + 0.5f);
    }

    public static int computeDistanceWithPhoneHeight(int denominator, int member) {

        float ratio = (float) denominator / (float) member;
        return (int) (ViewUtils.getPhonePixels()[1] / ratio + 0.5f);
    }

    /**
     * 适配小屏幕，以屏幕高800 为界限，800以下member取二分之一
     */
    public static int computeDistanceWithPhoneHeightAdapterLdpi(int denominator, int member) {
        float ratio = getAdapterLdpiRatio(denominator, member);
        return (int) (ViewUtils.getPhonePixels()[1] / ratio + 0.5f);
    }

    public static int computeRatioWithAdapterLdpi(int originalLength, int denominator, int member) {
        float ratio = getAdapterLdpiRatio(denominator, member);
        return (int) (originalLength / ratio + 0.5f);
    }

    private static float getAdapterLdpiRatio(float denominator, int member) {
        float member2 = member * 0.65f;
        Log.d("ldpi", " member2 : " + member2);
        float realMember = getPhonePixels()[1] <= 800 ? member2 : member;
        Log.d("ldpi", " realMember : " + realMember);
        return denominator / realMember;
    }

    public static int computeRatio(int originalLength, int denominator, int member) {
        float ratio = (float) denominator / (float) member;
        return (int) (originalLength / ratio + 0.5f);
    }

    public static int computeRatio(int originalLength, float ratio) {

        return (int) (originalLength / ratio + 0.5f);
    }

    public static void setLetterSpacing(TextView textView, float letterSpacing) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setLetterSpacing(letterSpacing);
        }
    }

    public static void setMarginTop(View view, int marginTop) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setMarginBottom(View view, int marginBottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
        view.setLayoutParams(params);
    }

    public static RectF getViewRectInParent(View view) {
        return new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    public static Drawable getTextViewTopDrawable(TextView textView) {
        if (null == textView) return null;
        return textView.getCompoundDrawables()[1];
    }

    public static boolean isInScrollViewTop(View view) {
        return ViewCompat.canScrollVertically(view, 1) && !ViewCompat.canScrollVertically(view, -1);
    }

    public static boolean isInScrollViewBottom(View view) {
        return !ViewCompat.canScrollVertically(view, 1) && ViewCompat.canScrollVertically(view, -1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void blur(Activity activity, Bitmap bkg, View view) {
        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);

        RenderScript rs = RenderScript.create(activity);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        rs.destroy();
    }

    public static void clearViewStatus(View v) {
        if (null == v) {
            return;
        }
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
    }

    public static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * @param p0 起始点
     * @param p1 曲线顶点
     * @param p2 终止点
     * @return t对应的点
     */
    public static PointF calculateBezierControlPoint(PointF p0, PointF p1, PointF p2) {
        PointF point = new PointF();
        // 曲线运动到的比例，0-1之间。因为这里起始点和终点配合成为等腰三角形，所以使用0.5这里表示运动到一半
        float temp = 0.5f;
        point.x = (float) ((p1.x - Math.pow(temp, 2) * (p0.x + p2.x)) / (2 * Math.pow(temp, 2)));
        point.y = (float) ((p1.y - Math.pow(temp, 2) * (p0.y + p2.y)) / (2 * Math.pow(temp, 2)));
        return point;
    }

    /**
     * - @param t  曲线此时运动到的长度比例，0-1之间
     * - @param p0 起始点
     * - @param p1 控制点
     * - @param p2 终止点
     * - @return 曲线此时的点
     */
    public static PointF CalculateBezierPointForQuadratic(float t, PointF p0, PointF p1, PointF p2) {
        PointF point = new PointF();
        float temp = 1 - t;
        point.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x;
        point.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y;
        return point;
    }

    public static void scale(View cardInfoLayout, float startX, float endX, float startY, float endY, float pivotX, float pivotY) {
        ScaleAnimation animation = new ScaleAnimation(startX, endX, startY, endY,
                Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        cardInfoLayout.setAnimation(animation);
        animation.start();
    }

    public static Rect transform(RectF rectF) {
        return new Rect(transform(rectF.left), transform(rectF.top), transform(rectF.right), transform(rectF.bottom));
    }

    private static int transform(double value) {
        return (int) (value + 0.5);
    }

    public static int parseColor(String format, int defaultColor) {
        try {
            return Color.parseColor(format);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static void setViewSize(View view, int width, int height) {
        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
            view.setLayoutParams(params);
            return;
        }

        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setViewHeight(View view, int height) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(params);
            return;
        }
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setViewWidth(View view, int width) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            return;
        }
        params.width = width;
        view.setLayoutParams(params);
    }

    public static void setAndroidMWindowsBarTextDark(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        Window window = activity.getWindow();
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (visibility == window.getDecorView().getSystemUiVisibility()) {
            return;
        }
        window.getDecorView().setSystemUiVisibility(visibility);
    }

    public static void setAndroidMWindowsBarTextWhite(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        Window window = activity.getWindow();
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (visibility == window.getDecorView().getSystemUiVisibility()) {
            return;
        }
        window.getDecorView().setSystemUiVisibility(visibility);
    }
}
