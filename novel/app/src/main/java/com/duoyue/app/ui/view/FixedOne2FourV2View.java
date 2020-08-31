package com.duoyue.app.ui.view;


import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.app.bean.BookCityChildChangeBean;
import com.duoyue.app.bean.BookOne2FourBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookCityPresenter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.zydm.base.ui.item.AbsItemView;
import com.zzdm.ad.router.BaseData;

import java.util.List;

/**
 * 1-4横排
 */
public class FixedOne2FourV2View extends AbsItemView<BookOne2FourBean> implements BookCityPresenter.BookMoreView {


    private RecyclerView mRv_title;

    private RecyclerView mRv_book_list;
    /**
     * 页面Id
     */
    private String mPageId;

    private BookCityPresenter bookCityPresenter;

    private TextView textView;


    private FixedOne2ColumnAdapter fixedOne2ColumnAdapter;
    private FixedOne2ItemColumnAdapter fixedOne2ItemColumnAdapter;
    private int mIndex;


    private LinearLayoutManager linearLayoutManager;

    private int mSelected;
    private String mPageChannel;


    @Override
    public void onCreate() {
        setContentView(R.layout.book_city_1_4_v2_line_layout);
        //获取页面Id.
        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        mPageChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";
        bookCityPresenter = new BookCityPresenter(this);
        mRv_title = mItemView.findViewById(R.id.rv_title_module);
        mRv_title.setHasFixedSize(true);
        mRv_title.setNestedScrollingEnabled(false);
        textView = mItemView.findViewById(R.id.tv_switch);
        textView.setOnClickListener(this);
        mRv_book_list = mItemView.findViewById(R.id.rv_all_list);
        mRv_book_list.setHasFixedSize(true);
        mRv_book_list.setNestedScrollingEnabled(false);

        fixedOne2ColumnAdapter = new FixedOne2ColumnAdapter(mActivity, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 4);
        mRv_title.setLayoutManager(gridLayoutManager);
        mRv_title.setAdapter(fixedOne2ColumnAdapter);
        fixedOne2ItemColumnAdapter = new FixedOne2ItemColumnAdapter();
        mRv_book_list.setAdapter(fixedOne2ItemColumnAdapter);
        mRv_book_list.addOnScrollListener(onScrollListener);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv_book_list.setLayoutManager(linearLayoutManager);
        new PagerSnapHelper().attachToRecyclerView(mRv_book_list);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_column:
                if (mSelected != (int) view.getTag()) {
                    mRv_book_list.setTag(view.getTag());
                    mRv_book_list.smoothScrollToPosition((int) view.getTag());
                    mSelected = (int) view.getTag();
                    if (mItemData.getBookCityModuleBean() != null) {
                        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow((int) view.getTag() + 1, "", "BOOKSTORE", mItemData.getBookCityModuleBean().getId(), "LFTAB", "");
                    }
                }
                break;

            case R.id.tv_switch:
                // 换一换
                if (noDoubleListener()) {
                    bookCityPresenter.loadData(mItemData.getBookCityModuleBean());

                    //点击分类更多.
                    FunctionStatsApi.bcCMoreClick(mItemData.getBookCityModuleBean().getId());
                    FuncPageStatsApi.bookCitySwitch(StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0));
                }
                break;

            case R.id.xll_item_two:
                if (noDoubleListener()) {
                    long id = (long) view.getTag();
                    ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + id, new BaseData(""),
                            PageNameConstants.BOOK_CITY, 4, PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                    //点击分类书籍.
                    FunctionStatsApi.bcCBookClick(mItemData.getBookCityModuleBean().getId(), id);
                    //判断分类.
                    switch (mItemData.getBookCityModuleBean().getType()) {
                        case 0:
                            //精选
                            FunctionStatsApi.bdFeaturedBookClick(id);
                            break;
                        case 1:
                            //男生
                            FunctionStatsApi.bdBoyBookClick(id);
                            break;
                        case 2:
                            //女生
                            FunctionStatsApi.bdGirlBookClick(id);
                            break;
                    }
                    FuncPageStatsApi.bookCityBookClick(id, StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0), PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                    break;
                }
        }
    }


    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            if (!mItemData.isFirst()) {
                mRv_book_list.scrollToPosition(0);
                mRv_title.scrollToPosition(0);
                mRv_book_list.setTag(0);
                mSelected = 0;
            }
            fixedOne2ItemColumnAdapter.setClassId(mPageId, Integer.valueOf(mItemData.getBookCityModuleBean().getId()));

            fixedOne2ColumnAdapter.setData(mItemData.getBookCityModuleBean().getChildColumns());
            fixedOne2ItemColumnAdapter.setData(mItemData.getBookCityModuleBean().getChildColumns(), mActivity, this, mItemData.getBookCityModuleBean().getType(), mItemData.getBookCityModuleBean().getTag(), mPageChannel);
            if (!mItemData.isFirst()) {
                mItemData.setFirst(!mItemData.isFirst());
//            mRv_book_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    List<BookCityItemBean> bookCityItemBeans = mItemData.getBookCityModuleBean().getChildColumns().get(0).getBooks();
//                    for (BookCityItemBean b : bookCityItemBeans) {
//                        BookExposureMgr.addOnGlobalLayoutListener(mPageId, String.valueOf(mItemData.getBookCityModuleBean().getChildColumns().get(0).getClassId()), linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()), b.getId(), b.getName(), Integer.parseInt(mPageChannel), null);
//                    }
//                    mRv_book_list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });
            }

            if (mItemData.getBookCityModuleBean().isLastPosition()) {
                mItemView.findViewById(R.id.fix_row_6).setVisibility(View.GONE);
            } else {
                mItemView.findViewById(R.id.fix_row_6).setVisibility(View.VISIBLE);
            }
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition() == 2) {
                    mIndex = linearLayoutManager.findFirstVisibleItemPosition() + 1;
                } else {
                    if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                        mIndex = 0;
                    } else {
                        mIndex = linearLayoutManager.findLastVisibleItemPosition();
                    }
                }
                mRv_book_list.setTag(mIndex);
                initSelected(mItemData.getBookCityModuleBean().getChildColumns());
            }
        }
    };

    @Override
    public void loadMoreData(BookCityChildChangeBean list) {
        mRv_title.smoothScrollToPosition(mRv_book_list.getTag() == null ? 0 : (int) mRv_book_list.getTag());
        initSelected(list.getChildColumns());
        bookCityPresenter.addRepeatBookId(Integer.valueOf(mItemData.getBookCityModuleBean().getId()), list);

    }

    void initSelected(List<BookChildColumnsBean> beans) {
//        for (BookCityItemBean bookCityItemBean : beans.get((int) mRv_book_list.getTag()).getBooks()) {
//            BookExposureMgr.addOnGlobalLayoutListener(mPageId, String.valueOf(beans.get((int) mRv_book_list.getTag()).getClassId()), linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()), bookCityItemBean.getId(), bookCityItemBean.getName(), Integer.parseInt(mPageChannel), null);
//        }
        for (BookChildColumnsBean b : beans) {
            b.setIndex(mRv_book_list.getTag() == null ? 0 : (int) mRv_book_list.getTag());
        }
        mItemData.getBookCityModuleBean().setChildColumns(beans);
        onSetData(false, true, true);

    }

}
