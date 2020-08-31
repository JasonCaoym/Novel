package com.duoyue.app.ui.view;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.duoyue.app.bean.BannerBean;
import com.duoyue.app.bean.BookBannerItemBean;
import com.duoyue.app.bean.BookBannerListBean;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.router.BaseData;

import java.util.ArrayList;
import java.util.List;

public class BookBannerView extends AbsItemView<BookBannerListBean> {

    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookBannerView";

    private XCustomBanner mBanner;

    /**
     * 账号性别.
     */
    private int mSex;

    /**
     * 分类类型(1:精选男;2:精选女;3:男生;4:女生).
     */
    private String mCategoryType;
    private List<Long> bookIdList = new ArrayList<>();

    @Override
    public void onCreate() {
        setContentView(R.layout.book_city_banner_layout);
        initBanner();
    }

    private void initBanner() {
        mBanner = mItemView.findViewById(R.id.banner);
        mBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                try {
                    //获取Banner对象.
                    Object bannerObj = mBanner.getItemData(i);
                    BannerBean bannerBean = bannerObj != null && bannerObj instanceof BannerBean ? (BannerBean) bannerObj : null;
                    if (bannerBean == null) {
                        return;
                    }
                    long bookId = bannerBean.getBanner().getType() == 1 ? bannerBean.getBanner().getBookId() : -bannerBean.getBanner().getPopId();
                    if (!bookIdList.contains(bookId)) {
                        bookIdList.add(bookId);
                        //调用Banner曝光节点(1:精选男;2:精选女;3:男生;4:女生).
                        FunctionStatsApi.cBannerExposure(mCategoryType, bannerBean.getBanner().getType() == 1 ? bannerBean.getBanner().getBookId() : -bannerBean.getBanner().getPopId());
                        FuncPageStatsApi.bookCityBannerShow(bookId, StringFormat.parseInt(mCategoryType, 1), PageNameConstants.SOURCE_BANNER + " + " + bannerBean.getBanner().getPopId() + " + " + mCategoryType);
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onPageSelected: {}, {}", i, throwable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Glide.get(mBanner.getContext()).clearMemory();
            }
        });
        //banner设置方法全部调用完毕时最后调用
        //加载广告图片
        mBanner.loadImage(new XCustomBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XCustomBanner banner, Object model, View view, int position) {
                GlideUtils.INSTANCE.loadFixImage(mActivity, ((BannerBean) model).getBanner().getCover(), (ImageView) view, GlideUtils.INSTANCE.getBookRadius());
            }
        });
        mBanner.setOnItemClickListener(new XCustomBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XCustomBanner banner, Object model, View view, int position) {
                BannerBean bannerBean = (BannerBean) model;
                switch (bannerBean.getBanner().getType()) {
                    case 1: // 书籍
                        long bookId = bannerBean.getBanner().getBookId();
                        ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + bookId, new BaseData(""),
                                PageNameConstants.BOOK_CITY, 3, PageNameConstants.SOURCE_BANNER  + " + " + bannerBean.getBanner().getPopId() + " + " + mCategoryType);
                        //上报Banner点击.
                        FunctionStatsApi.cBannerClick(mCategoryType, bookId);
                        FuncPageStatsApi.bookCityBannerClick(bookId, StringFormat.parseInt(mCategoryType, 1), PageNameConstants.SOURCE_BANNER  + " + " + bannerBean.getBanner().getPopId() + " + " + mCategoryType);
                        break;
                    case 2: // H5
                        //跳转类型.
                        ActivityHelper.INSTANCE.gotoWeb(mActivity, bannerBean.getBanner().getLink());
                        //上报Banner点击.
                        FunctionStatsApi.cBannerClick(mCategoryType, -bannerBean.getBanner().getPopId());
                        FuncPageStatsApi.bookCityBannerClick(-bannerBean.getBanner().getPopId(), StringFormat.parseInt(mCategoryType, 1), PageNameConstants.SOURCE_BANNER  + " + " + bannerBean.getBanner().getPopId() + " + " + mCategoryType);
                        break;
                }
            }
        });
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (!isDataChanged) {
            return;
        }
        //初始化数据.
        initData();

        List<BannerBean> urlList = new ArrayList<>();
        List<BookBannerItemBean> itemBeans = mItemData.getList();
        if (itemBeans == null) {
            mBanner.setVisibility(View.GONE);
        } else {
            mBanner.setVisibility(View.VISIBLE);
            for (BookBannerItemBean bannerItemBean : itemBeans) {
                if (!TextUtils.isEmpty(bannerItemBean.getCover())) {
                    urlList.add(new BannerBean(bannerItemBean));
                }
            }

            mBanner.setBannerData(urlList);
            mBanner.startAutoPlay();

            Glide.get(mBanner.getContext()).clearMemory();
        }

    }

    /**
     * 设置通过onVisibilityChanged方法监听到展示时, 是否启动播放.
     *
     * @param visibleStartPaly
     */
    public void setVisibleStartPaly(boolean visibleStartPaly) {
        if (mBanner != null) {
            mBanner.setVisibleStartPaly(visibleStartPaly);
        }
    }

    /**
     * 启动播放.
     */
    public void startPlay() {
        if (mBanner != null) {
            mBanner.startAutoPlay();
        }
    }

    /**
     * 停止播放.
     */
    public void stopPlay() {
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    /**
     * 初始化数据.
     */
    private void initData() {
        //获取设置的性别.
        mSex = StartGuideMgr.getChooseSex();
        if (mItemData == null) {
            return;
        }
        switch (mItemData.getType()) {
            case 0:
                //精选.
                if (mSex == StartGuideMgr.SEX_WOMAN) {
                    //精选女
                    mCategoryType = "2";
                } else {
                    //精选男
                    mCategoryType = "1";
                }
                break;
            case 1:
                //男生
                mCategoryType = "3";
                break;
            case 2:
                //女生
                mCategoryType = "4";
                break;
        }
    }
}
