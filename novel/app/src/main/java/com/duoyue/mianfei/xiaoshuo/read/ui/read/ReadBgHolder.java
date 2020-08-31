package com.duoyue.mianfei.xiaoshuo.read.ui.read;

import android.view.View;
import android.widget.ImageView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.setting.ReadSettingManager;
import com.zydm.base.data.dao.ReadBgBean;
import com.zydm.base.ui.item.AbsItemView;


public class ReadBgHolder extends AbsItemView<ReadBgBean> {

    private ImageView mBg;

    @Override
    public void onCreate() {
        setContentView(R.layout.item_read_bg);
        mBg = findView(R.id.read_bg_view);
        mItemView.setOnClickListener(this);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        ReadBgBean readBgBean = getMItemData();
        if (readBgBean.getBgColor() == -1) {
//            if (ReadSettingManager.getInstance().getPageMode() == PageMode.SCROLL) {
//                mBg.setVisibility(View.GONE);
//            } else {
                mBg.setVisibility(View.VISIBLE);
//            }
            //牛皮纸  牛皮纸  牛皮纸
            if (readBgBean.isSelect()) {
                mBg.setImageResource(R.mipmap.read_bg_paper_sel);
            } else {
                mBg.setImageResource(R.mipmap.read_bg_paper_nor);
            }
        } else if (readBgBean.getBgColor() == -2) {
            mBg.setVisibility(View.VISIBLE);
            if (readBgBean.isSelect()) {
                mBg.setImageResource(R.mipmap.read_bg_paper_light_sel);
            } else {
                mBg.setImageResource(R.mipmap.read_bg_paper_light);
            }
        } else {
            mBg.setVisibility(View.VISIBLE);
            if (readBgBean.isSelect()) {
                if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[2]) {
                    mBg.setImageResource(R.mipmap.read_bg_gray_sel);
                } else if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[3]) {
                    mBg.setImageResource(R.mipmap.read_bg_green_sel);
                } else if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[4]) {
                    mBg.setImageResource(R.mipmap.read_bg_blue_sel);
                }
            } else {
                if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[2]) {
                    mBg.setImageResource(R.mipmap.read_bg_gray_nor);
                } else if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[3]) {
                    mBg.setImageResource(R.mipmap.read_bg_green_nor);
                } else if (readBgBean.getBgColor() == ReadSettingManager.getInstance().colorBg[4]) {
                    mBg.setImageResource(R.mipmap.read_bg_blue_nor);
                }
            }
        }
    }
}
