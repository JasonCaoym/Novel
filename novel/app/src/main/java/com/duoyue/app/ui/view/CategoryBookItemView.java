package com.duoyue.app.ui.view;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.CategoryBookBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.activity.BookDetailCategoryActivity;
import com.duoyue.app.ui.activity.CategoryBookListActivity;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
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
public class CategoryBookItemView extends AbsItemView<CategoryBookBean> {
    /**
     * 页面Id
     */
    private String mPageId;

    /**
     * 模块Id.
     */
    private String mModuleId;

    private BookDetailCategoryActivity bookDetailCategoryActivity;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_item_view);
        //获取页面Id.
        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        //获取模块Id.
        mModuleId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(ListAdapter.EXT_KEY_MODULE_ID)) : "";
        mItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (Utils.isFastClick()) {
            return;
        }
        if (mActivity instanceof CategoryBookListActivity) {
            //点击二级分类书籍.
            FunctionStatsApi.bdCategoryListBookClick(mItemData.getBookId());
        } else if (mActivity instanceof BookDetailCategoryActivity) {
            if (bookDetailCategoryActivity != null) {
                FuncPageStatsApi.bookDetailTaglClick(mItemData.getBookId(), bookDetailCategoryActivity.getTag());
            }
        } else {
            //点击书架分类列表页数据.
            FunctionStatsApi.bcCListBookClick(mModuleId, mItemData.getBookId());
        }
//        ReadActivity.gBookId = 0;
        BookDetailActivity.currBookId = 0;
        ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getBookId(), new BaseData(""),
                PageNameConstants.CATEGORY_DETAIL, 12, "", BookDetailActivity.REQUEST_CODE_READ);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        CategoryBookBean data = mItemData;
        if (data != null) {
            mItemView.setVisibility(View.VISIBLE);
            GlideUtils.INSTANCE.loadImage(mActivity, data.getCover() != null ? data.getCover() : "", (ImageView) mItemView.findViewById(R.id.book_cover), GlideUtils.INSTANCE.getBookRadius());
            ((TextView) mItemView.findViewById(R.id.book_name)).setText(data.getBookName());
            ((TextView) mItemView.findViewById(R.id.book_author)).setText(data.getAuthorName());
            ((TextView) mItemView.findViewById(R.id.book_resume)).setText(data.getResume());
            mItemView.findViewById(R.id.book_count).setVisibility(View.GONE);
            //书籍字数.
            TextView wordCount = mItemView.findViewById(R.id.book_word_count);
            wordCount.setVisibility(View.GONE);

            TextView tvGrade = mItemView.findViewById(R.id.book_grade);
            tvGrade.setTextColor(mActivity.getResources().getColor(R.color.color_898989));
            tvGrade.setBackgroundResource(R.drawable.book_item_category_text_bg);
            tvGrade.setVisibility(View.GONE);

            TextView tvUpdateTime = mItemView.findViewById(R.id.book_category);
            tvUpdateTime.setVisibility(View.GONE);
            switch (data.getType()) {
                case 1: // 人气
                    tvGrade.setVisibility(View.VISIBLE);
                    if (data.getPopularityNum() > 100000000) {
                        tvGrade.setText((data.getPopularityNum() / 100000000) + "亿人气");
                    } else if (data.getPopularityNum() > 10000) {
                        tvGrade.setText((data.getPopularityNum() / 10000) + "万人气");
                    } else {
                        tvGrade.setText(data.getPopularityNum() + "人气");
                    }
                    break;
                case 2: // 更新时间
                    tvUpdateTime.setVisibility(View.VISIBLE);
                    if (data.getState() == 2) {
                        tvUpdateTime.setText(R.string.finished);
                    } else {
                        tvUpdateTime.setText(R.string.updating);
                    }
                    break;
                case 3: // 评分
                    tvGrade.setVisibility(View.VISIBLE);
                    tvGrade.setText(String.format("%s分", data.getStar()));
                    break;
            }
            //字数筛选类型.
            if (data.getWordCountType() > 1) {
                //显示字数.
                wordCount.setVisibility(View.VISIBLE);
                if (data.getWordCount() > 10000) {
                    //字数大于10000的
                    wordCount.setText((data.getWordCount() / 10000) + "万字");
                } else {
                    wordCount.setText(data.getWordCount() + "字");
                }
            }
            //监听书籍可见.
            if (mActivity instanceof BookDetailCategoryActivity) {
                if (bookDetailCategoryActivity == null) {
                    bookDetailCategoryActivity = (BookDetailCategoryActivity) mActivity;
                }
                BookExposureMgr.addOnGlobalLayoutListener(BookExposureMgr.PAGE_ID_TAG_BOOKLIST, bookDetailCategoryActivity.getTag(), mItemView, data.getBookId(), data.getBookName());
            } else {
                BookExposureMgr.addOnGlobalLayoutListener(mPageId, mModuleId, mItemView, data.getBookId(), data.getBookName());
            }

        } else {
            mItemView.setVisibility(View.GONE);
        }
    }
}
