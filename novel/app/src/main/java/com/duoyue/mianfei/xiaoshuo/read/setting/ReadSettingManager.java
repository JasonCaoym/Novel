package com.duoyue.mianfei.xiaoshuo.read.setting;


import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.page.PageMode;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.ViewUtils;


public class ReadSettingManager {
    public static final int READ_BG_0 = 0;
    public static final int READ_BG_1 = 1;
    public static final int READ_BG_2 = 2;
    public static final int READ_BG_3 = 3;
    public static final int NIGHT_MODE = 6;


    //-1 表示牛皮纸  表示牛皮纸  表示牛皮纸 重要的事情说三遍
    // (区分颜色和图片。不可能把颜色和图片归为一类，颜色值为color，数字 eg：-1 代表图片 ，后期有可能有新的图片，预留功能)
    public int[] colorBg = {-1, -2, R.color.read_page_bg_01,
            R.color.read_page_bg_03, R.color.read_page_bg_04,};
    //-1 表示牛皮纸  表示牛皮纸  表示牛皮纸 重要的事情说三遍
    public int[] colorTip = {-1, -2, R.color.read_page_tip_01,
            R.color.read_page_tip_03, R.color.read_page_tip_04};
    // 底部广告背景颜色
    public int[] bottomAdBgColor = {0xFFCBBC9E, 0xFFCBBC9E, 0xFFE3DEE2,
            0xFFC6DAC7, 0xFFBECEDD, 0xFF1C2029};
    // 底部广告字体颜色
    public int[] bottomAdTxtColor = {0xFF998E77, 0xFF998E77, 0xFF998E77,
            0xFFA2B3A3, 0xFF9AA6B3, 0xFF262C38};
    // 字体行距
    public float[] textSpaceArray = {1f, 0.68f, 0.3f};
    // 息屏时间
    public long[] offScreenArray = {60_000L, 300_000, Long.MAX_VALUE - 1};

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_page_mode";
    public static final String SHARED_READ_SCREEN_OFF_MODE = "shared_read_off_screen_mode";
    public static final String SHARED_READ_LINE_SPACE = "shared_read_line_space";


    private static volatile ReadSettingManager sInstance;

    public static ReadSettingManager getInstance() {
        if (sInstance == null) {
            synchronized (ReadSettingManager.class) {
                if (sInstance == null) {
                    sInstance = new ReadSettingManager();
                }
            }
        }
        return sInstance;
    }

    private ReadSettingManager() {
    }

    public void setReadBackground(int theme) {
        SPUtils.INSTANCE.putInt(SHARED_READ_BG, theme);
    }

    public void setBrightness(int progress) {
        SPUtils.INSTANCE.putInt(SHARED_READ_BRIGHTNESS, progress);
    }

    public void setAutoBrightness(boolean isAuto) {
        SPUtils.INSTANCE.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public void setTextSize(int textSize) {
        SPUtils.INSTANCE.putInt(SHARED_READ_TEXT_SIZE, textSize);
    }

    public void setPageMode(PageMode mode) {
        Logger.e("pagemode", "设置page mode : " + mode.ordinal());
        SPUtils.INSTANCE.putInt(SHARED_READ_PAGE_MODE, mode.ordinal());
    }

    public PageMode getPageMode() {
        int mode = SPUtils.INSTANCE.getInt(SHARED_READ_PAGE_MODE, PageMode.COVER.ordinal());
        Logger.e("pagemode", "获取page mode : " + mode);
        return PageMode.values()[mode];
    }

    public void setScreenOffMode(int position) {
        SPUtils.INSTANCE.putInt(SHARED_READ_SCREEN_OFF_MODE, position);
    }

    public int getScreenOffMode() {
        return SPUtils.INSTANCE.getInt(SHARED_READ_SCREEN_OFF_MODE, 1);
    }

    public void setLineSpace(int position) {
        SPUtils.INSTANCE.putInt(SHARED_READ_LINE_SPACE, position);
    }

    public int getLineSpace() {
        return SPUtils.INSTANCE.getInt(SHARED_READ_LINE_SPACE, 1);
    }

    public void setNightMode(boolean isNight) {
        SPUtils.INSTANCE.putBoolean(SHARED_READ_NIGHT_MODE, isNight);
    }

    public int getBrightness() {
        return SPUtils.INSTANCE.getInt(SHARED_READ_BRIGHTNESS, -1);
    }

    public boolean isBrightnessAuto() {
        return SPUtils.INSTANCE.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, true);
    }

    public int getTextSize() {
        return SPUtils.INSTANCE.getInt(SHARED_READ_TEXT_SIZE, ViewUtils.dp2px(18.5f));
    }

    public int getReadBgTheme() {
        return SPUtils.INSTANCE.getInt(SHARED_READ_BG, READ_BG_0);
    }

    public boolean isNightMode() {
        return SPUtils.INSTANCE.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        SPUtils.INSTANCE.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return SPUtils.INSTANCE.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        SPUtils.INSTANCE.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return SPUtils.INSTANCE.getBoolean(SHARED_READ_FULL_SCREEN, false);
    }
}
