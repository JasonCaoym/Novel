package com.duoyue.app.ui.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookRankingColumnBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.ui.activity.BookRankActivity;
import com.duoyue.app.ui.adapter.RankingBooksListAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*排行榜*/
public class RankingBooksListView extends AbsItemView<BookRankingColumnBean> {

    private RecyclerView mTv_list;
    private RankingBooksListAdapter rankingBooksListAdapter;

    private LinearLayoutManager linearLayoutManager;

    private boolean isScoller;

    private XRelativeLayout view;

    private List<BookChildColumnsBean> mList;

    private int mIndex = 0;
    private String mPageId;
    private List<Integer> typeList = new ArrayList<>();
    private String mPageChannel;

    @Override
    public void onCreate() {

        setContentView(R.layout.book_ranking_books_list_layout);

        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        mPageChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";

        mTv_list = findView(R.id.rv_rank);
        mTv_list.setHasFixedSize(true);
        mTv_list.setNestedScrollingEnabled(false);
        new PagerSnapHelper().attachToRecyclerView(mTv_list);
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTv_list.addItemDecoration(new SpaceItemDecoration(ViewUtils.dp2px(10)));
        mTv_list.setLayoutManager(linearLayoutManager);
        mTv_list.addOnScrollListener(onScrollListener);
        rankingBooksListAdapter = new RankingBooksListAdapter(mActivity, mList, this, mPageId);
        mTv_list.setAdapter(rankingBooksListAdapter);
        mItemView.setId(R.id.tag_item);
        linearLayoutManager.setInitialPrefetchItemCount(3);
        mTv_list.setItemViewCacheSize(3);
    }


    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            mList.clear();
            mList.addAll(mItemData.getBookCityModuleBean().getChildColumns());
//            xLinearLayout.setTag(mItemData.getBookCityModuleBean().getChildColumns().get(0).getClassId());
//            if (mTv_list.getAdapter() == null) {
//
//            } else {
//                mTv_list.smoothScrollToPosition(0);
//            }
            rankingBooksListAdapter.notifyItemRangeChanged(0, mList.size() - 1);
            mTv_list.scrollToPosition(0);
//            mTv_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    int itemWidth = linearLayoutManager.findViewByPosition(linearLayoutManager.findLastVisibleItemPosition()).getWidth();
//                    for (BookChildColumnsBean booksListBean : mList) {
//
//                        booksListBean.setWidth(itemWidth);
//                    }
//                    rankingBooksListAdapter.notifyItemRangeChanged(0, mList.size() - 1);
//                    mTv_list.smoothScrollToPosition(0);
//                    mTv_list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });
            typeList.clear();
            typeList.add(mList.get(0).getClassId());
            List<Long> bookIdList = new ArrayList<>();
            BookChildColumnsBean book = mList.get(mIndex);
            if (book.getBooks() != null) {
                for (BookCityItemBean itemBean : book.getBooks()) {
//                FuncPageStatsApi.bookCityRankShow(itemBean.getId(), mList.get(0).getClassId());
                    bookIdList.add(itemBean.getId());
                }
                BookExposureMgr.addOnGlobalLayoutListener(mPageId, "rank",
                        mItemView, 0, "book city rank", mList.get(0).getClassId(), bookIdList);
            }
        }
    }

    @Override
    public void onClick(@NotNull View view) {
        if (noDoubleListener()) {
            switch (view.getId()) {
                case R.id.iv_bg:
                    Intent intent = new Intent(mActivity, BookRankActivity.class);
                    intent.putExtra(BookRankActivity.CLASSID, (int) view.getTag());
                    mActivity.startActivity(intent);
                    FuncPageStatsApi.bookCityRankMore();
                    break;
                case R.id.book_cover:
                case R.id.tv_book_name:
                case R.id.tv_desc:
                case R.id.xll_rank:
                    ActivityHelper.INSTANCE.gotoBookDetails(mActivity, String.valueOf(view.getTag(R.id.xll_tag)), new BaseData("书城页排行榜"),
                            PageNameConstants.BOOK_CITY, 4, PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                    FuncPageStatsApi.bookCityRankClick((long) view.getTag(R.id.xll_tag), mList.get(mIndex).getClassId());
                    break;
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
                Integer classId = mList.get(mIndex).getClassId();
                PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(classId, "", PageNameConstants.RANK, String.valueOf(classId),
                        FunPageStatsConstants.CATEGORY_RANK_MALE_SHOW, "");
                if (!typeList.contains(classId)) {
                    typeList.add(classId);
                    BookChildColumnsBean book = mList.get(mIndex);
                    if (book.getBooks() != null) {
                        for (BookCityItemBean itemBean : book.getBooks()) {
                            FuncPageStatsApi.bookCityRankShow(itemBean.getId(), classId);
                        }
                    }

                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    };

//
//    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(final @NonNull RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (view == null && newState == RecyclerView.SCROLL_STATE_IDLE ) {
//                if (mDx > 0) {
//                    if (mDx >= height / 4) {
//                        view = (XRelativeLayout) linearLayoutManager.findViewByPosition(linearLayoutManager.findLastVisibleItemPosition());
//                        if (linearLayoutManager.findLastVisibleItemPosition() == 1) {
//                            mSlidingDistance = view.getLeft() - ViewUtils.dp2px(25);
//                            recyclerView.smoothScrollBy(mSlidingDistance, 0);
//
//                        } else {
//                            mSlidingDistance = view.getLeft() - ViewUtils.dp2px(10);
//                            recyclerView.smoothScrollBy(mSlidingDistance, 0);
//                        }
//                        recyclerView.setTag(false);
//                        xLinearLayout.setTag(view.getTag());
//
//                    } else {
//                        mSlidingDistance = -mDx;
//                        recyclerView.smoothScrollBy(mSlidingDistance, 0);
//                        recyclerView.setTag(false);
//                    }
//                } else {
//                    if (recyclerView.getTag() == null) {
//                        if (mDx <= -(height / 4)) {
//                            view = (XRelativeLayout) linearLayoutManager.findViewByPosition(linearLayoutManager.findLastVisibleItemPosition() - 1);
//                            mSlidingDistance = view.getLeft() - ViewUtils.dp2px(25);
//                            recyclerView.smoothScrollBy(mSlidingDistance, 0);
////                            if (linearLayoutManager.findLastVisibleItemPosition() == 1  || linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount()-1) {
////                            } else {
////                                recyclerView.smoothScrollBy(view.getLeft() - ViewUtils.dp2px(10), 0);
////                            }
//                            xLinearLayout.setTag(view.getTag());
//                        } else {
//                            mSlidingDistance = Math.abs(mDx);
//                            recyclerView.smoothScrollBy(mSlidingDistance, 0);
//                        }
//
//                    }
//                }
//            } else if (view != null && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
////                Log.i("Interpolator", recyclerView.getScrollState() + "---->");
////                if (recyclerView.getScrollState()!=2){
//                Log.i("Interpolator", view.getLeft() + "---->");
//                    view = null;
//                    mDx = 0;
//                    recyclerView.setTag(null);
////                }
//
//            } else {
//
//            }
//        }
//
//        @Override
//        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            mDx += dx;
////                if (dx > 0) {
//////                isSlidingToLeft = dx > 0;
////                } else {
////                    mDx -= dx;
////                }
//
//        }
//    };

}
