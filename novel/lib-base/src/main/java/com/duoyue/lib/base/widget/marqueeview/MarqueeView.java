package com.duoyue.lib.base.widget.marqueeview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.zydm.base.R;
import java.util.ArrayList;
import java.util.List;


/**
 * @author caoym
 * @data 2019/5/29  9:29
 */
public class MarqueeView extends ViewFlipper {

    private int interval = 3000;
    private boolean hasSetAnimDuration = false;
    private int animDuration = 1000;
    private int textSize = 14;
    private int textColor = 0xffffffff;
    private boolean singleLine = false;

    private int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;

    private boolean hasSetDirection = false;
    private int direction = DIRECTION_BOTTOM_TO_TOP;
    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = 2;
    private static final int DIRECTION_LEFT_TO_RIGHT = 3;

    @AnimRes
    private int inAnimResId = R.anim.marquee_anim_bottom_in;
    @AnimRes
    private int outAnimResId = R.anim.marquee_anim_top_out;

    private int position;
    private List<? extends CharSequence> notices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnScrollListener onScrollListener;

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CMarqueeViewStyle, defStyleAttr, 0);

        interval = typedArray.getInteger(R.styleable.CMarqueeViewStyle_mvInterval, interval);
        hasSetAnimDuration = typedArray.hasValue(R.styleable.CMarqueeViewStyle_mvAnimDuration);
        animDuration = typedArray.getInteger(R.styleable.CMarqueeViewStyle_mvAnimDuration, animDuration);
        singleLine = typedArray.getBoolean(R.styleable.CMarqueeViewStyle_mvSingleLine, false);
        if (typedArray.hasValue(R.styleable.CMarqueeViewStyle_mvTextSize)) {
            textSize = (int) typedArray.getDimension(R.styleable.CMarqueeViewStyle_mvTextSize, textSize);
            textSize = Utils.px2sp(context, textSize);
        }
        textColor = typedArray.getColor(R.styleable.CMarqueeViewStyle_mvTextColor, textColor);

        int gravityType = typedArray.getInt(R.styleable.CMarqueeViewStyle_mvGravity, GRAVITY_LEFT);
        switch (gravityType) {
            case GRAVITY_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }

        hasSetDirection = typedArray.hasValue(R.styleable.CMarqueeViewStyle_mvDirection);
        direction = typedArray.getInt(R.styleable.CMarqueeViewStyle_mvDirection, direction);
        if (hasSetDirection) {
            switch (direction) {
                case DIRECTION_BOTTOM_TO_TOP:
                    inAnimResId = R.anim.marquee_anim_bottom_in;
                    outAnimResId = R.anim.marquee_anim_top_out;
                    break;
                case DIRECTION_TOP_TO_BOTTOM:
                    inAnimResId = R.anim.marquee_anim_top_in;
                    outAnimResId = R.anim.marquee_anim_bottom_out;
                    break;
                case DIRECTION_RIGHT_TO_LEFT:
                    inAnimResId = R.anim.marquee_anim_right_in;
                    outAnimResId = R.anim.marquee_anim_left_out;
                    break;
                case DIRECTION_LEFT_TO_RIGHT:
                    inAnimResId = R.anim.marquee_anim_left_in;
                    outAnimResId = R.anim.marquee_anim_right_out;
                    break;
            }
        } else {
            inAnimResId = R.anim.marquee_anim_bottom_in;
            outAnimResId = R.anim.marquee_anim_top_out;
        }

        typedArray.recycle();
        setFlipInterval(interval);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice 字符串
     */
    public void startWithText(String notice) {
        startWithText(notice, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice       字符串
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    @SuppressWarnings("deprecation")
    public void startWithText(final String notice, final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        if (TextUtils.isEmpty(notice)) return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                startWithFixedWidth(notice, inAnimResId, outAnimResID);
            }
        });
    }

    /**
     * 根据字符串和宽度，启动翻页公告
     *
     * @param notice 字符串
     */
    private void startWithFixedWidth(String notice, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        int noticeLength = notice.length();
        int width = Utils.px2dip(getContext(), getWidth());
        if (width == 0) {
            throw new RuntimeException("Please set the width of MarqueeView !");
        }
        int limit = width / textSize;
        List list = new ArrayList();

        if (noticeLength <= limit) {
            list.add(notice);
        } else {
            int size = noticeLength / limit + (noticeLength % limit != 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int startIndex = i * limit;
                int endIndex = ((i + 1) * limit >= noticeLength ? noticeLength : (i + 1) * limit);
                list.add(notice.substring(startIndex, endIndex));
            }
        }

        if (notices == null) notices = new ArrayList<>();
        notices.clear();
        notices.addAll(list);
        postStart(inAnimResId, outAnimResID);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices 字符串列表
     */
    public void startWithList(List<? extends CharSequence> notices) {
        startWithList(notices, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices      字符串列表
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    public void startWithList(List<? extends CharSequence> notices, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        if (Utils.isEmpty(notices)) return;
        setNotices(notices);
        postStart(inAnimResId, outAnimResID);
    }

    private void postStart(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        post(new Runnable() {
            @Override
            public void run() {
                start(inAnimResId, outAnimResID);
            }
        });
    }

    private void start(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        removeAllViews();
        clearAnimation();

        position = 0;
        addView(createTextView(notices.get(position)));

        if (notices.size() > 1) {
            setInAndOutAnimation(inAnimResId, outAnimResID);
            startFlipping();
        }

        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (onScrollListener != null)
                    {
                        onScrollListener.onScrollStateChanged(position, null);
                    }
                    position++;
                    if (position >= notices.size()) {
                        position = 0;
                    }
                    TextView view = createTextView(notices.get(position));
                    if (view.getParent() == null) {
                        addView(view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private TextView createTextView(CharSequence text) {
        TextView textView = (TextView) getChildAt((getDisplayedChild() + 1) % 3);
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setGravity(gravity);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setSingleLine(singleLine);
            textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getPosition(), (TextView) v);
                }
            }
        });
        textView.setText(text);
        textView.setTag(position);
        return textView;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<? extends CharSequence> getNotices() {
        return notices;
    }

    public void setNotices(List<? extends CharSequence> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    /**
     * 滚动切换事件
     */
    public interface OnScrollListener {
        void onScrollStateChanged(int position, TextView textView);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        if (hasSetAnimDuration) inAnim.setDuration(animDuration);
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        if (hasSetAnimDuration) outAnim.setDuration(animDuration);
        setOutAnimation(outAnim);
    }

    /**
     * 工具类
     */
    private static class Utils
    {
        public static <T> boolean notEmpty(List<T> list) {
            return !isEmpty(list);
        }

        public static <T> boolean isEmpty(List<T> list) {
            if (list == null || list.size() == 0) {
                return true;
            }
            return false;
        }

        // 将px值转换为dip或dp值
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        // 将dip或dp值转换为px值
        public static int dip2px(Context context, float dipValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }

        // 将px值转换为sp值
        public static int px2sp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }

        // 将sp值转换为px值
        public static int sp2px(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }

        // 屏幕宽度（像素）
        public static int getWindowWidth(Activity context) {
            DisplayMetrics metric = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return metric.widthPixels;
        }

        // 屏幕高度（像素）
        public static int getWindowHeight(Activity context) {
            DisplayMetrics metric = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return metric.heightPixels;
        }

        // 根据Unicode编码判断中文汉字和符号
        private static boolean isChinese(char c) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
                return true;
            }
            return false;
        }

        // 判断中文汉字和符号
        public static boolean isChinese(String strName) {
            char[] ch = strName.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    return true;
                }
            }
            return false;
        }
    }
}