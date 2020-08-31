package com.duoyue.app.ui.view;


import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;

/**
 * 单个书籍
 */
public class BookCityItemView extends AbsItemView<BookCityItemBean> {


    private String mPageChannel;
    private String mModelId;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_item_view);
        //获取模块Id.
        mModelId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(ListAdapter.EXT_KEY_MODULE_ID)) : "";
        mPageChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";
        mItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        //点击书架分类列表页数据.
        FunctionStatsApi.bcCListBookClick(mModelId, mItemData.getId());

        ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""),
                PageNameConstants.BOOK_CITY, 4, PageNameConstants.SOURCE_CAROUSEL + " + " + mModelId +" + " + mPageChannel);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (!isDataChanged)return;
        BookCityItemBean data = mItemData;
        if (data != null) {
            mItemView.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImage(mActivity, data.getCover(), (ImageView) mItemView.findViewById(R.id.book_cover), GlideUtils.INSTANCE.getBookRadius(), ViewUtils.dp2px(89), ViewUtils.dp2px(122));
            ((TextView) mItemView.findViewById(R.id.book_name)).setText(data.getName());
            ((TextView) mItemView.findViewById(R.id.book_author)).setText(data.getAuthorName());
            ((TextView) mItemView.findViewById(R.id.book_resume)).setText(data.getResume());
            mItemView.findViewById(R.id.book_grade).setVisibility(View.GONE);
//            ((TextView) mItemView.findViewById(R.id.book_grade)).setText(String.format("%s分", data.getStar()));
            mItemView.findViewById(R.id.tv_grade).setVisibility(View.VISIBLE);
            SpannableString s1 = new SpannableString(String.format("%s分", data.getStar()));
            s1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s1.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s1.setSpan(new AbsoluteSizeSpan(10, true), s1.length()-1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) mItemView.findViewById(R.id.tv_grade)).setText(s1);
            ((TextView) mItemView.findViewById(R.id.book_count)).setText(String.format("%s万字", data.getWordCount() / 10000));
            if (data.getCategoryName() == null || data.getCategoryName().isEmpty()) {
                mItemView.findViewById(R.id.book_category).setVisibility(View.GONE);
            } else {
                mItemView.findViewById(R.id.book_category).setVisibility(View.VISIBLE);
                ((TextView) mItemView.findViewById(R.id.book_category)).setText(data.getCategoryName());
            }
        } else {
            mItemView.setVisibility(View.GONE);
        }
    }
}

