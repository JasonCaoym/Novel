package com.duoyue.app.ui.view;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.bean.BookCityChildChangeBean;
import com.duoyue.app.bean.BookNBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookCityPresenter;
import com.duoyue.app.ui.adapter.FixedSixAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.item.AbsItemView;
import com.zzdm.ad.router.BaseData;

/**
 * 1-4横排
 */
public class FixedSixView extends AbsItemView<BookNBean> implements BookCityPresenter.BookMoreView {

    private RecyclerView mRv_book_list;

    private FixedSixAdapter fixedSixAdapter;

    /**
     * 页面Id
     */
    private String mPageId;

    private BookCityPresenter bookCityPresenter;

    private TextView textView, mRv_title;
    private String mPageChannel;


    @Override
    public void onCreate() {
        setContentView(R.layout.book_n_line_layout);
        //获取页面Id.
        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        mPageChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";
        bookCityPresenter = new BookCityPresenter(this);
        mRv_title = mItemView.findViewById(R.id.module_title);
        textView = mItemView.findViewById(R.id.tv_switch);
        textView.setOnClickListener(this);
        mRv_book_list = mItemView.findViewById(R.id.rv_all_list);
        mRv_book_list.setHasFixedSize(true);
        mRv_book_list.setNestedScrollingEnabled(false);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mActivity, 3);
        mRv_book_list.setLayoutManager(linearLayoutManager);
        fixedSixAdapter = new FixedSixAdapter(mActivity, this, mPageId, mPageChannel);
        mRv_book_list.setAdapter(fixedSixAdapter);
    }

    @Override
    public void onClick(View view) {
        if (noDoubleListener()) {
            switch (view.getId()) {
                case R.id.tv_switch:
                    // 换一换
                    bookCityPresenter.loadData(mItemData.getBookCityModuleBean());
                    //点击分类更多.
                    FunctionStatsApi.bcCMoreClick(mItemData.getBookCityModuleBean().getId());
                    FuncPageStatsApi.bookCitySwitch(StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0));
                    break;

//                case R.id.cv:
//                case R.id.tv_name:
//                    long id = (long) view.getTag();
//                    ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + id, new BaseData(""),
//                            PageNameConstants.BOOK_CITY, 4, PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
//                    //点击分类书籍.
//                    FunctionStatsApi.bcCBookClick(mItemData.getBookCityModuleBean().getId(), id);
//                    //判断分类.
//                    switch (mItemData.getBookCityModuleBean().getType()) {
//                        case 0:
//                            //精选
//                            FunctionStatsApi.bdFeaturedBookClick(id);
//                            break;
//                        case 1:
//                            //男生
//                            FunctionStatsApi.bdBoyBookClick(id);
//                            break;
//                        case 2:
//                            //女生
//                            FunctionStatsApi.bdGirlBookClick(id);
//                            break;
//                    }
//                    FuncPageStatsApi.bookCityBookClick(id, StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0), PageNameConstants.SOURCE_CAROUSEL+ " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
//                    break;

                case R.id.tv_read:
                case R.id.cv:
                case R.id.tv_name:
                    long bookid = (long) view.getTag();
                    com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper.INSTANCE.gotoReadForResult(mActivity, String.valueOf(bookid), new BaseData("书城"), PageNameConstants.BOOK_CITY, PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel, 999);
                    FuncPageStatsApi.bookCityTryRead(StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0), PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                    break;
            }
        }
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            mRv_title.setText(mItemData.getBookCityModuleBean().getTitle());
            fixedSixAdapter.setData(mItemData.getBookCityModuleBean().getChildColumns().get(0).getBooks(), mItemData.getBookCityModuleBean().getId());
        }
    }

    @Override
    public void loadMoreData(BookCityChildChangeBean list) {
        mItemData.getBookCityModuleBean().setChildColumns(list.getChildColumns());
        onSetData(false, true, true);
        bookCityPresenter.addRepeatBookId(Integer.valueOf(mItemData.getBookCityModuleBean().getId()), list);
    }

}
