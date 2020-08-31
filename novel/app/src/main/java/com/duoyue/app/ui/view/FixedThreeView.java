package com.duoyue.app.ui.view;


import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookCityChildChangeBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookThreeBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookCityPresenter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.tools.DataUtils;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;

import java.util.Arrays;
import java.util.List;

/**
 * 1-2-2
 */
public class FixedThreeView extends AbsItemView<BookThreeBean> implements BookCityPresenter.BookMoreView {


    private List<Integer> mIds = Arrays.asList(
            R.id.fixed_module_title,
            R.id.fix_row_1,
            R.id.fix_row_2,
            R.id.fix_row_3,
            R.id.fix_row_4,
            R.id.fix_row_5
    );
    /**
     * 页面Id
     */
    private String mPageId;
    private BookCityPresenter bookCityPresenter;
    private String mPageChannel;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_city_3_layout);
        //获取页面Id.
        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
        mPageChannel = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_CHANNEL)) : "";
        bookCityPresenter = new BookCityPresenter(this);

        for (int index = 0; index < mIds.size(); index++) {
            View view = mItemView.findViewById(mIds.get(index));
            view.setOnClickListener(this);
            view.setTag(index);
        }
    }


    @Override
    public void onClick(View view) {
//        super.onClick(view);
        if (noDoubleListener()) {
            if (mItemData.getBookCityModuleBean().getChildColumns() != null
                    && mItemData.getBookCityModuleBean().getChildColumns().get(0).getBooks() != null
                /* && (Integer) view.getTag() <= mItemData.moduleBean.getBooks().size()*/) {
                int index = (Integer) view.getTag();
                if (index == 0) {
                    // 换一换
                    bookCityPresenter.loadData(mItemData.getBookCityModuleBean());

                    //点击分类更多.
                    FunctionStatsApi.bcCMoreClick(mItemData.getBookCityModuleBean().getId());
                    FuncPageStatsApi.bookCitySwitch(StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0));
                } else {
                    BookCityItemBean bean = mItemData.getBookCityModuleBean().getChildColumns().get(0).getBooks().get((Integer) view.getTag() - 1);
                    ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + bean.getId(), new BaseData(""),
                            PageNameConstants.BOOK_CITY, 4, PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                    //点击分类书籍.
                    FunctionStatsApi.bcCBookClick(mItemData.getBookCityModuleBean().getId(), bean.getId());
                    //判断分类.
                    switch (mItemData.getBookCityModuleBean().getType()) {
                        case 0:
                            //精选
                            FunctionStatsApi.bdFeaturedBookClick(bean.getId());
                            break;
                        case 1:
                            //男生
                            FunctionStatsApi.bdBoyBookClick(bean.getId());
                            break;
                        case 2:
                            //女生
                            FunctionStatsApi.bdGirlBookClick(bean.getId());
                            break;
                    }
                    FuncPageStatsApi.bookCityBookClick(bean.getId(), StringFormat.parseInt(mItemData.getBookCityModuleBean().getId(), 0), PageNameConstants.SOURCE_CAROUSEL + " + " + mItemData.getBookCityModuleBean().getId() + " + " + mPageChannel);
                }
            }
        }

    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (!isDataChanged)return;
        for (int index = 0; index < mIds.size(); index++) {
            View view = mItemView.findViewById(mIds.get(index));
            if (index == 0) {
                if (mItemData.getBookCityModuleBean().getTag().equals("CNXHJN") || mItemData.getBookCityModuleBean().getTag().equals("CNXHJV")) {
                    view.setEnabled(false);
                    view.findViewById(R.id.tv_switch).setVisibility(View.GONE);
                } else {
                    view.setEnabled(true);
                    view.findViewById(R.id.tv_switch).setVisibility(View.VISIBLE);
                }
//                if (mItemData.moduleBean.getBooks().size() < 5 || (mItemData.moduleBean.isLastPosition() && mItemData.moduleBean.getType() == 0)) {
////                    view.setEnabled(false);
////                    view.findViewById(R.id.tv_switch).setVisibility(View.GONE);
//                } else {
////                    view.setEnabled(true);
////                    view.findViewById(R.id.tv_switch).setVisibility(View.VISIBLE);
//                }
                TextView textView = view.findViewById(R.id.module_title);
                textView.setText(mItemData.getBookCityModuleBean().getTitle());
            } else {
                BookCityItemBean data = DataUtils.getItem(mItemData.getBookCityModuleBean().getChildColumns().get(0).getBooks(), index - 1);
                if (data != null) {
                    view.setVisibility(View.VISIBLE);
                    GlideUtils.INSTANCE.loadImage(mActivity, data.getCover(), (ImageView) view.findViewById(R.id.book_cover), GlideUtils.INSTANCE.getBookRadius(), ViewUtils.dp2px(89), ViewUtils.dp2px(122));
                    ((TextView) view.findViewById(R.id.book_name)).setText(data.getName());
                    ((TextView) view.findViewById(R.id.book_author)).setText(data.getAuthorName());
                    ((TextView) view.findViewById(R.id.book_resume)).setText(data.getResume());
                    view.findViewById(R.id.book_grade).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_grade).setVisibility(View.VISIBLE);
                    SpannableString s1 = new SpannableString(String.format("%s分", data.getStar()));
                    s1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s1.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s1.setSpan(new AbsoluteSizeSpan(10, true), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) view.findViewById(R.id.tv_grade)).setText(s1);
                    ((TextView) view.findViewById(R.id.book_count)).setText(String.format("%s万字", data.getWordCount() / 10000));

                    //按更新时间  只有精选显示
                    if (mItemData.getBookCityModuleBean().getType() == 0 && Utils.isStartsWith(mItemData.getBookCityModuleBean().getTag(), "SJ")) {
                        view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.VISIBLE);
                        ((ImageView) view.findViewById(R.id.iv_new_hot_icon)).setImageResource(R.mipmap.new_icon);
                    } else if (mItemData.getBookCityModuleBean().getType() == 0 && Utils.isStartsWith(mItemData.getBookCityModuleBean().getTag(), "RQ")) {
                        view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.VISIBLE);
                        ((ImageView) view.findViewById(R.id.iv_new_hot_icon)).setImageResource(R.mipmap.hot_icon);
                    } else {
                        view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.GONE);
                    }


                    if (data.getCategoryName() == null || data.getCategoryName().isEmpty()) {
                        view.findViewById(R.id.book_category).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.book_category).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.book_category)).setText(data.getCategoryName());
                    }

                    //最后一栏不显示横线
                    if (mItemData.getBookCityModuleBean().isLastPosition()) {
                        mItemView.findViewById(R.id.fix_row_6).setVisibility(View.GONE);
                    } else {
                        mItemView.findViewById(R.id.fix_row_6).setVisibility(View.VISIBLE);
                    }
                    //监听书籍可见.
                    BookExposureMgr.addOnGlobalLayoutListener(mPageId, mItemData.getBookCityModuleBean().getId(), view, data.getId(), data.getName(), Integer.parseInt(mPageChannel), null);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }


    }

    @Override
    public void loadMoreData(BookCityChildChangeBean list) {
        mItemData.getBookCityModuleBean().setChildColumns(list.getChildColumns());
        onSetData(false, true, true);
        bookCityPresenter.addRepeatBookId(Integer.valueOf(mItemData.getBookCityModuleBean().getId()), list);
    }

//    @Override
//    public void loadMoreData(List<BookCityItemBean> list) {
//        mItemData.moduleBean.getChildColumns().get(0).setBooks(list);
//        onSetData(false, true, true);
//        bookCityPresenter.addRepeatBookId(Integer.valueOf(mItemData.moduleBean.getId()), list);
//    }
}
