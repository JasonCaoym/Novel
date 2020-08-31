package com.duoyue.mianfei.xiaoshuo.read.setting;

import android.app.Activity;
import android.graphics.Rect;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.utils.ViewUtils;

public class BrightnessMgr implements View.OnClickListener {


    public static final float SYSTEM_LIGHT = -0.1f;

    private Activity mActivity;
    private SeekBar mSeekBar;
    private CheckBox mSystemLightCheckBox;
    private boolean mIsSystemLight = true;
    private String prevPageId;
    private String source;

    public BrightnessMgr(Activity activity, View root, String prevPageId, String source) {
        this.prevPageId = prevPageId;
        this.source = source;
        init(activity, root);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mIsSystemLight) {
                mIsSystemLight = false;
                mSystemLightCheckBox.setChecked(mIsSystemLight);
            }
            updateBrightness();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ReadSettingManager.getInstance().setBrightness(seekBar.getProgress());
            //调节亮度.
            FunctionStatsApi.rBrightnessClick();
            FuncPageStatsApi.readLightChanged(prevPageId, source);
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mIsSystemLight = isChecked;
            updateBrightness();
            ReadSettingManager.getInstance().setAutoBrightness(mIsSystemLight);
            if (isChecked) {
                mSystemLightCheckBox.setTextColor(ViewUtils.getColor(ReadSettingManager.getInstance().isNightMode() ?
                        R.color.standard_red_main_light : R.color.standard_red_main_color_c1));
                //点击系统亮度切换.
                FunctionStatsApi.rBrightnessSysClick();
                FuncPageStatsApi.readLightSys(prevPageId, source);
            } else {
                mSystemLightCheckBox.setTextColor(ViewUtils.getColor(R.color.color_898989));
            }
        }
    };

    private void init(Activity activity, View root) {
        mActivity = activity;
        mSeekBar = root.findViewById(R.id.read_setting_sb_brightness);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect seekRect = new Rect();
                mSeekBar.getHitRect(seekRect);

                if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
                    float y = seekRect.top + seekRect.height() / 2;
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return mSeekBar.onTouchEvent(me);
                }
                return false;
            }
        });
        mSystemLightCheckBox = root.findViewById(R.id.read_setting_cb_brightness_auto);
        initSystemCheckBok();
        initSeekBar();

        root.findViewById(R.id.read_setting_iv_brightness_minus).setOnClickListener(this);
        root.findViewById(R.id.read_setting_iv_brightness_plus).setOnClickListener(this);
        updateBrightness();
    }

    private void initSystemCheckBok() {
        mIsSystemLight = ReadSettingManager.getInstance().isBrightnessAuto();
        mSystemLightCheckBox.setChecked(mIsSystemLight);
        mSystemLightCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    private void initSeekBar() {
        int progress = ReadSettingManager.getInstance().getBrightness();
        if (progress < 0) {
            float screenBrightness = getBrightness();
            progress = (int) (screenBrightness * 100);
        }
        mSeekBar.setMax(100);
        mSeekBar.setProgress(progress);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    private void updateBrightness() {
        float brightness = mIsSystemLight ? SYSTEM_LIGHT : mSeekBar.getProgress() / 100f;
        changeScreenBrightness(brightness);
    }

    private void changeScreenBrightness(float screenBrightness) {
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = screenBrightness;
        window.setAttributes(lp);
    }

    private float getBrightness() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        float screenBrightness = lp.screenBrightness;
        if (screenBrightness < 0) {
            try {
                int sysBrightness = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                screenBrightness = sysBrightness / 255f;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return screenBrightness;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_setting_iv_brightness_minus) {
            mSeekBar.setProgress(0);
            ReadSettingManager.getInstance().setBrightness(0);
            //调节亮度.
            FunctionStatsApi.rBrightnessClick();
        } else if (v.getId() == R.id.read_setting_iv_brightness_plus) {
            mSeekBar.setProgress(mSeekBar.getMax());
            ReadSettingManager.getInstance().setBrightness(mSeekBar.getMax());
            //调节亮度.
            FunctionStatsApi.rBrightnessClick();
        }
        FuncPageStatsApi.readLightChanged(prevPageId, source);
    }
}
