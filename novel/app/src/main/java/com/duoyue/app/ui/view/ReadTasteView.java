package com.duoyue.app.ui.view;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.duoyue.app.bean.BannerBean;
import com.duoyue.app.bean.BookBannerItemBean;
import com.duoyue.app.bean.BookBannerListBean;
import com.duoyue.app.bean.BookReadTasteBean;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.common.mgr.UserLoginMgr;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.router.BaseData;

import java.util.ArrayList;
import java.util.List;

public class ReadTasteView extends AbsItemView<BookReadTasteBean> {


    private XRelativeLayout rlReadTaste;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_city_read_taste_layout);
        initView();
    }

    private void initView() {
        rlReadTaste = mItemView.findViewById(R.id.rl_read_taste);
        rlReadTaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //阅读口味.
                StartGuideMgr.showGuidePage((FragmentActivity) mActivity, true);
                //点击阅读口味入口.
                FunctionStatsApi.mReadTasteClick();
                FuncPageStatsApi.bookCityTaste();
            }
        });
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        ImageView icon = mItemView.findViewById(R.id.iv_love_icon);
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null || userInfo.type == LoginPresenter.USER_TYPE_TOURIST || StringFormat.isEmpty(userInfo.headImg)) {//未登录
            icon.setImageResource(R.mipmap.loving_heart);
        } else {
            GlideUtils.INSTANCE.loadImage(mActivity, userInfo.headImg, icon);
        }
    }

}
