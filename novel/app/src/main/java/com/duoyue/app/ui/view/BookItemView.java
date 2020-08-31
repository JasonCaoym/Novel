package com.duoyue.app.ui.view;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.router.BaseData;

/**
 * 单个书籍
 */
public class BookItemView extends AbsItemView<BookCityItemBean> {

    /**
     * 页面Id
     */
    private String mPageId;

    /**
     * 分类Id.
     */
    private String mCategoryId;
    private String parentId;
    private int modelId;
    private String mChannel;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_item_view);
        mItemView.setOnClickListener(this);
        //获取页面Id.
        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        //获取分类Id.
        mCategoryId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(ListAdapter.EXT_KEY_MODULE_ID)) : "";
        parentId = getMAdapter() != null ? getMAdapter().getExtParam(ListAdapter.EXT_KEY_PARENT_ID) : "";
        modelId = getMAdapter() != null ? StringFormat.parseInt(getMAdapter().getExtParam(ListAdapter.EXT_KEY_MODEL_ID), 0) : 0;
        mChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";
        Logger.d("BookItemView", "onCreate: pageId = " + mPageId);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (Utils.isFastClick()) {
            return;
        }

        if (mPageId.equals(BookExposureMgr.BOOK_CITY_BOUTIQUE)) {//书城精品
            FuncPageStatsApi.boutiqueClick(mItemData.getId());
            ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""), parentId, modelId, PageNameConstants.BOOK_CITY_SELECT_RECOMMEND);
        } else if (mPageId.equals(BookExposureMgr.BOOK_CITY_NEW_BOOK)) {//书城新书
            FuncPageStatsApi.newBookClick(mItemData.getId());
            ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""), parentId, modelId, PageNameConstants.BOOK_CITY_NEW_RECOMMEND);
        } else if (mPageId.equals(BookExposureMgr.BOOK_CITY_FINISH)) {//书城完结
            FuncPageStatsApi.finishClick(mItemData.getId());
            ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""), parentId, modelId, PageNameConstants.BOOK_CITY_FINISH_RECOMMEND);
        } else {
            //点击书城分类列表页数据.
            FunctionStatsApi.bcCListBookClick(StringFormat.isEmpty(mItemData.getCategoryId()) ? mCategoryId : mItemData.getCategoryId(), mItemData.getId());
            ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""), parentId, modelId, "");
        }
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        BookCityItemBean data = mItemData;
        if (data != null) {
            mItemView.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImage(mActivity, data.getCover(), (ImageView) mItemView.findViewById(R.id.book_cover), GlideUtils.INSTANCE.getBookRadius());
            //书籍名称.
            ((TextView) mItemView.findViewById(R.id.book_name)).setText(data.getName());
            //书籍作者.
            ((TextView) mItemView.findViewById(R.id.book_author)).setText(data.getAuthorName());
            ((TextView) mItemView.findViewById(R.id.book_resume)).setText(data.getResume());
            ((TextView) mItemView.findViewById(R.id.book_grade)).setText(String.format("%s分", data.getStar()));
            ((TextView) mItemView.findViewById(R.id.book_count)).setText(String.format("%s万字", data.getWordCount() / 10000));
            if (data.getCategoryName() == null || data.getCategoryName().isEmpty()) {
                mItemView.findViewById(R.id.book_category).setVisibility(View.GONE);
            } else {
                mItemView.findViewById(R.id.book_category).setVisibility(View.VISIBLE);
                ((TextView) mItemView.findViewById(R.id.book_category)).setText(data.getCategoryName());
            }
            Logger.d("BookItemView", "onSetData: pageId = " + mPageId);
            //监听书籍可见.
            BookExposureMgr.addOnGlobalLayoutListener(mPageId,
                    StringFormat.isEmpty(StringFormat.isEmpty(mItemData.getCategoryId()) ? mCategoryId : mItemData.getCategoryId())
                            ? "0" : (StringFormat.isEmpty(mItemData.getCategoryId()) ? mCategoryId : mItemData.getCategoryId()),
                    mItemView, data.getId(), data.getName(),Integer.parseInt(mChannel),null);
        } else {
            mItemView.setVisibility(View.GONE);
        }
    }
}
