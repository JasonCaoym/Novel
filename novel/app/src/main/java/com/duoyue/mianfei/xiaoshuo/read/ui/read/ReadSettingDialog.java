package com.duoyue.mianfei.xiaoshuo.read.ui.read;

import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.page.AbsPageLoader;
import com.duoyue.mianfei.xiaoshuo.read.setting.BrightnessMgr;
import com.duoyue.mianfei.xiaoshuo.read.setting.ReadSettingManager;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.data.dao.ReadBgBean;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ItemListenerAdapter;
import com.zydm.base.ui.item.RecyclerAdapter;
import com.zydm.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class ReadSettingDialog extends Dialog {

    private static final int FONT_SIZE_STEP = ViewUtils.dp2px(1.5f);
    private static final int FONT_MIN_SIZE = ViewUtils.dp2px(12);
    private static final int FONT_MAX_SIZE = ViewUtils.dp2px(30);
    ImageView mIvBrightnessMinus;
    SeekBar mSbBrightness;
    ImageView mIvBrightnessPlus;
    CheckBox mCbBrightnessAuto;
    TextView mTvFontMinus;
    TextView mTvFontPlus;
    RecyclerView mRvBg;

    private RecyclerAdapter mReadBgAdapter;
    private ReadSettingManager mSettingManager;
    private AbsPageLoader mAbsPageLoader;
    private ReadActivity mActivity;

    private int mTextSize;
    private int mReadBgTheme;
    private List<ReadBgBean> mReadBgBeans = new ArrayList<>();
    private TextView mFont;
    private TextView mBright;
    private TextView mBg;
    private View mDialogRoot;
    private BrightnessMgr mBrightnessMgr;

    /**
     * 选中的背景色索引位置.
     */
    private int mSelectedBgPosition;

    private String prevPageId;
    private String source;

    public ReadSettingDialog(@NonNull ReadActivity activity, AbsPageLoader mAbsPageLoader, String prevPageId, String source) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mAbsPageLoader = mAbsPageLoader;
        this.prevPageId = prevPageId;
        this.source = source;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        findView();
        initData();
        setStyle();
        setUpWindow();
        initWidget();
        initClick();
    }

    private void findView() {
        mIvBrightnessMinus = findViewById(R.id.read_setting_iv_brightness_minus);
        mSbBrightness = findViewById(R.id.read_setting_sb_brightness);
        mIvBrightnessPlus = findViewById(R.id.read_setting_iv_brightness_plus);
        mCbBrightnessAuto = findViewById(R.id.read_setting_cb_brightness_auto);
        mTvFontMinus = findViewById(R.id.read_setting_tv_font_minus);
        mTvFontPlus = findViewById(R.id.read_setting_tv_font_plus);
        mRvBg = findViewById(R.id.read_setting_rv_bg);
        mFont = findViewById(R.id.font);
        mBright = findViewById(R.id.bright);
        mBg = findViewById(R.id.bg);
        mDialogRoot = findViewById(R.id.read_setting_ll_menu);
    }

    private void setStyle() {
        if (mAbsPageLoader.isNightMode()) {
            mTvFontMinus.setBackground(ViewUtils.getDrawable(R.drawable.shape_btn_read_setting_night));
            mTvFontMinus.setTextColor(ViewUtils.getColor(R.color.standard_black_fourth_level_color_c6));
            mTvFontPlus.setBackground(ViewUtils.getDrawable(R.drawable.shape_btn_read_setting_night));
            mTvFontPlus.setTextColor(ViewUtils.getColor(R.color.standard_black_fourth_level_color_c6));
            mFont.setTextColor(ViewUtils.getColor(R.color.standard_black_third_level_color_c5));
            mBg.setTextColor(ViewUtils.getColor(R.color.standard_black_third_level_color_c5));
            mBright.setTextColor(ViewUtils.getColor(R.color.standard_black_third_level_color_c5));
            mDialogRoot.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_night));
            mCbBrightnessAuto.setBackground(ViewUtils.getDrawable(R.drawable.selector_btn_read_setting_bg_night));
            mCbBrightnessAuto.setTextColor(mSettingManager.isBrightnessAuto() ? ViewUtils.getColor(R.color.standard_red_main_light) : ViewUtils.getColor(R.color.color_898989));
        } else {
            mTvFontMinus.setBackground(ViewUtils.getDrawable(R.drawable.shape_btn_read_setting_normal));
            mTvFontMinus.setTextColor(ViewUtils.getColor(R.color.standard_black_second_level_color_c4));
            mTvFontPlus.setBackground(ViewUtils.getDrawable(R.drawable.shape_btn_read_setting_normal));
            mTvFontPlus.setTextColor(ViewUtils.getColor(R.color.standard_black_second_level_color_c4));
            mFont.setTextColor(ViewUtils.getColor(R.color.standard_black_first_level_color_c3));
            mBg.setTextColor(ViewUtils.getColor(R.color.standard_black_first_level_color_c3));
            mBright.setTextColor(ViewUtils.getColor(R.color.standard_black_first_level_color_c3));
            mDialogRoot.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_day));
            mCbBrightnessAuto.setBackground(ViewUtils.getDrawable(R.drawable.selector_btn_read_setting_bg));
            mCbBrightnessAuto.setTextColor(mSettingManager.isBrightnessAuto() ?
                    ViewUtils.getColor(R.color.standard_red_main_color_c1) : ViewUtils.getColor(R.color.color_898989));
        }
    }

    @Override
    public void show() {
        setFullScreen(true);
        super.show();
        setStyle();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setFullScreen(false);
    }

    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    public void setFullScreen(boolean full) {
        /*WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (full) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        mTextSize = mSettingManager.getTextSize();
        mReadBgTheme = mSettingManager.getReadBgTheme();
    }

    private void initWidget() {
        mBrightnessMgr = new BrightnessMgr(mActivity, findViewById(R.id.bright_layout), prevPageId, source);
        setUpAdapter();
    }

    private void setUpAdapter() {
        setReadBg(ReadSettingManager.getInstance().getReadBgTheme());
        mReadBgAdapter = new AdapterBuilder().putItemClass(ReadBgHolder.class, getItemListener()).builderRecyclerAdapter(mActivity);
        mRvBg.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRvBg.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = com.zydm.base.utils.ViewUtils.dp2px(17);
            }
        });
        mRvBg.setAdapter(mReadBgAdapter);
        mReadBgAdapter.setData(mReadBgBeans);
    }

    @NonNull
    private ItemListenerAdapter<ReadBgHolder> getItemListener() {
        return new ItemListenerAdapter<ReadBgHolder>() {
            @Override
            public void onClick(ReadBgHolder readBgHolder, View v) {
                int position = readBgHolder.getMPosition();
                Logger.e("ad#setting", "点击的位置：" + position);
                mSettingManager.setReadBackground(position);
                mActivity.toDayMode();
                mAbsPageLoader.setBgColor(position);
                for (int i = 0; i < mReadBgBeans.size(); i++) {
                    ReadBgBean readBgBean = mReadBgBeans.get(i);
                    if (i == position) {
                        readBgBean.setSelect(true);
                    } else {
                        readBgBean.setSelect(false);
                    }
                }
                mReadBgAdapter.notifyDataSetChanged();
                //判断是否与上次设置的背景色相同.
                if (mSelectedBgPosition != position) {
                    mSelectedBgPosition = position;

                    switch (position) {
                        case 0:
                            //设置背景颜色.
                            FunctionStatsApi.rBackground1Click();
                            FuncPageStatsApi.readBg1(prevPageId, source);
                            break;
                        case 1:
                            //设置背景颜色.
                            FunctionStatsApi.rBackground2Click();
                            FuncPageStatsApi.readBg2(prevPageId, source);
                            break;
                        case 2:
                            //设置背景颜色.
                            FunctionStatsApi.rBackground3Click();
                            FuncPageStatsApi.readBg3(prevPageId, source);
                            break;
                        case 3:
                            //设置背景颜色.
                            FunctionStatsApi.rBackground4Click();
                            FuncPageStatsApi.readBg4(prevPageId, source);
                            break;
                    }
                }
            }
        };
    }

    private void setReadBg(int selectPos) {
        //设置默认选中背景色索引位置.
        mSelectedBgPosition = selectPos;

        int[] colorBg = ReadSettingManager.getInstance().colorBg;
        for (int i = 0; i < colorBg.length; i++) {
            ReadBgBean readBgBean = new ReadBgBean();
            readBgBean.setBgColor(colorBg[i]);
            if (i == selectPos) {
                readBgBean.setSelect(true);
            } else {
                readBgBean.setSelect(false);
            }
            mReadBgBeans.add(readBgBean);
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        mTvFontMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextSize -= FONT_SIZE_STEP;
                if (mTextSize < FONT_MIN_SIZE) {
                    mTextSize = FONT_MIN_SIZE;
                    return;
                }
                mAbsPageLoader.setTextSize(mTextSize);
                //减少字体大小.
                FunctionStatsApi.rReduceFontClick();
                FuncPageStatsApi.readFontDec(prevPageId, source);
            }
        });

        mTvFontPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextSize += FONT_SIZE_STEP;
                if (mTextSize > FONT_MAX_SIZE) {
                    mTextSize = FONT_MAX_SIZE;
                    return;
                }
                mAbsPageLoader.setTextSize(mTextSize);
                //加大字体大小.
                FunctionStatsApi.rIncreaseFontClick();
                FuncPageStatsApi.readFontInc(prevPageId, source);
            }
        });
    }
}
