package com.duoyue.app.ui.view;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.duoyue.app.bean.BookCityMenuBean;
import com.duoyue.app.bean.BookCityMenuItemBean;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.ui.activity.TaskWebViewActivity;
import com.duoyue.app.ui.adapter.EntrancesV2Adapter;
import com.duoyue.app.ui.fragment.NewCategoryActivity;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntrancesV2View extends AbsItemView<BookCityMenuBean> {


    private RecyclerView recyclerView;

    private EntrancesV2Adapter entrancesV2Adapter;

    private List<BookCityMenuItemBean> menuItemBeanList;
    private int mType;
    private int mIconType;

    @Override
    public void onCreate() {
        setContentView(R.layout.item_entrances_v2);
        recyclerView = mItemView.findViewById(R.id.rc_icon_list);
        menuItemBeanList = new ArrayList<>();
        entrancesV2Adapter = new EntrancesV2Adapter(mActivity, menuItemBeanList, this);
        recyclerView.setAdapter(entrancesV2Adapter);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            if (mItemData.getIconList() != null && !mItemData.getIconList().isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                menuItemBeanList.clear();
                menuItemBeanList.addAll(mItemData.getIconList());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, menuItemBeanList.size());
                recyclerView.setLayoutManager(gridLayoutManager);
                entrancesV2Adapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    //1:榜单 2:精品 3:新书 4:完结 5:分类 6:一级分类 7:书单
    @Override
    public void onClick(@NotNull View view) {
        super.onClick(view);
        BookCityMenuItemBean bookCityMenuItemBean = (BookCityMenuItemBean) view.getTag();
        switch (bookCityMenuItemBean.getShowType()) {
            case 1:
                ActivityHelper.INSTANCE.gotoRank(mActivity);
                //点击榜单.
                FunctionStatsApi.bcRankIconClick();
//                FuncPageStatsApi.bookCityIconClick(1);
                break;

            case 2:
                ActivityHelper.INSTANCE.gotoJingXuan(mActivity, String.valueOf(mType));
                //点击精选.
                FunctionStatsApi.bcFeaturedIconClick();
//                FuncPageStatsApi.bookCityIconClick(2);
                break;

            case 3:
                ActivityHelper.INSTANCE.gotoBookList(mActivity, ViewUtils.getString(R.string.entrances_new), 4,
                        AdConstants.Position.BOOK_NEWS, PageNameConstants.BOOK_CITY, PageNameConstants.BOOKSTORE_NEW, mItemData.getType(), String.valueOf(mType));
                //点击新书.
                FunctionStatsApi.bcNewIconClick();
//                FuncPageStatsApi.bookCityIconClick(3);
                break;

            case 4:
                ActivityHelper.INSTANCE.gotoBookList(mActivity, ViewUtils.getString(R.string.entrances_complete), 3,
                        AdConstants.Position.BOOK_FINISH, PageNameConstants.BOOK_CITY, PageNameConstants.BOOKSTORE_FINISH, mItemData.getType(), String.valueOf(mType));
                //点击完结.
                FunctionStatsApi.bcCompleteIconClick();
//                FuncPageStatsApi.bookCityIconClick(4);
                break;
            case 5:
                Intent intent = new Intent(mActivity, NewCategoryActivity.class);
                mActivity.startActivity(intent);
//                EventBus.getDefault().post(new TabSwitchEvent(HomeActivity.CATEGORY));
                //点击分类.
                FunctionStatsApi.bcCategoryIconClick();
//                FuncPageStatsApi.bookCityIconClick(5);
                break;
            case 6:
                //一级分类
                CategoryBean categoryBean = new CategoryBean();
                categoryBean.setId(String.valueOf(bookCityMenuItemBean.getCatId()));
                categoryBean.setName(bookCityMenuItemBean.getShowName());
                categoryBean.setSex(mItemData.getType());
                if (bookCityMenuItemBean.getTag() != null)
                    categoryBean.setTags(bookCityMenuItemBean.getTag());
                categoryBean.setSubCategories(bookCityMenuItemBean.getSubCategories());
                ActivityHelper.INSTANCE.gotoCategoryBookList(mActivity, categoryBean);
                break;

            case 7:
                Intent intent2 = new Intent(mActivity, TaskWebViewActivity.class);
                String url2 = TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLIST)) ? "http://taskcenter.duoy ueapp.com/readList/" : AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLIST);
                intent2.putExtra("url", url2 + "?" + getLoginInfo());
                mActivity.startActivity(intent2);
                break;

        }


        switch (mItemData.getType()) {
            case 0:
                if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                    mType = 1;
                    mIconType = menuItemBeanList.indexOf(bookCityMenuItemBean) + 1;
                } else {
                    switch (menuItemBeanList.indexOf(bookCityMenuItemBean)) {
                        case 0:
                            mIconType = 16;
                            break;
                        case 1:
                            mIconType = 17;
                            break;
                        case 2:
                            mIconType = 18;
                            break;
                        case 3:
                            mIconType = 19;
                            break;
                    }
                    mType = 2;
                }
                break;
            case 1:
                switch (menuItemBeanList.indexOf(bookCityMenuItemBean)) {
                    case 0:
                        mIconType = 6;
                        break;
                    case 1:
                        mIconType = 7;
                        break;
                    case 2:
                        mIconType = 8;
                        break;
                    case 3:
                        mIconType = 9;
                        break;
                }
                mType = 3;
                break;
            case 2:
                switch (menuItemBeanList.indexOf(bookCityMenuItemBean)) {
                    case 0:
                        mIconType = 11;
                        break;
                    case 1:
                        mIconType = 12;
                        break;
                    case 2:
                        mIconType = 13;
                        break;
                    case 3:
                        mIconType = 14;
                        break;
                }
                mType = 4;
                break;
        }
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(mIconType, "", "BOOKSTORE", String.valueOf(mType), "SCICON", "");
    }

    /**
     * 获取参数
     */
    private String getLoginInfo() {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        Boolean isLogin;
        if (userInfo == null) return "";
        isLogin = userInfo.type != 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("uid=").append(userInfo.uid)
                .append("&version=").append(PhoneStatusManager.getInstance().getAppVersionName())
                .append("&appId=").append(Constants.APP_ID)
                .append("&channelCode=").append(PhoneStatusManager.getInstance().getAppChannel())
                .append("&isLogin=").append(isLogin)
                .append("&token=").append(userInfo.token)
                .append("&mid=").append(UserManager.getInstance().getMid());

        return stringBuilder.toString();
    }
}
