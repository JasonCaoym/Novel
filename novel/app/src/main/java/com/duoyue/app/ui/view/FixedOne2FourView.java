//package com.duoyue.app.ui.view;
//
//
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.StyleSpan;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.duoyue.app.bean.BookCityItemBean;
//import com.duoyue.app.bean.BookCityListBean;
//import com.duoyue.app.common.mgr.BookExposureMgr;
//import com.duoyue.app.presenter.BookCityPresenter;
//import com.duoyue.lib.base.format.StringFormat;
//import com.duoyue.mianfei.xiaoshuo.R;
//import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
//import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
//import com.duoyue.mod.stats.FuncPageStatsApi;
//import com.duoyue.mod.stats.FunctionStatsApi;
//import com.duoyue.mod.stats.common.PageNameConstants;
//import com.zydm.base.data.tools.DataUtils;
//import com.zydm.base.ui.item.AbsItemView;
//import com.zydm.base.utils.GlideUtils;
//import com.zzdm.ad.router.BaseData;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 1-4横排
// */
//public class FixedOne2FourView extends AbsItemView<BookCityListBean.BookOne2FourBean> implements BookCityPresenter.BookMoreView {
//
//
//    private List<Integer> mIds = Arrays.asList(
//            R.id.fixed_module_title,
//            R.id.fixed_module_1,
//            R.id.fixed_module_2,
//            R.id.fixed_module_3,
//            R.id.fixed_module_4,
//            R.id.fixed_module_5
//    );
//    /**
//     * 页面Id
//     */
//    private String mPageId;
//
//    private BookCityPresenter bookCityPresenter;
//
//    @Override
//    public void onCreate() {
//        setContentView(R.layout.book_city_1_4_line_layout);
//        //获取页面Id.
//        mPageId = getMAdapter() != null ? StringFormat.toString(getMAdapter().getExtParam(BookExposureMgr.PAGE_ID_KEY)) : "";
//        bookCityPresenter = new BookCityPresenter(this);
//        for (int index = 0; index < mIds.size(); index++) {
//            View view = mItemView.findViewById(mIds.get(index));
//            view.setOnClickListener(this);
//            view.setTag(index);
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
////        super.onClick(view);
//        if (noDoubleListener()) {
//            if (mItemData.moduleBean.getBooks() != null/* && (Integer) view.getTag() <= mItemData.moduleBean.getBooks().size()*/) {
//                int index = (Integer) view.getTag();
//                if (index == 0) {
//                    // 换一换
//                    bookCityPresenter.loadData(mItemData.moduleBean);
//                    //点击分类更多.
//                    FunctionStatsApi.bcCMoreClick(mItemData.moduleBean.getId());
//                    FuncPageStatsApi.bookCitySwitch(StringFormat.parseInt(mItemData.moduleBean.getId(), 0));
//                } else {
//                    BookCityItemBean bean = mItemData.moduleBean.getBooks().get(index - 1);
//                    ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + bean.getId(), new BaseData(""),
//                            PageNameConstants.BOOK_CITY, 4, "");
//                    //点击分类书籍.
//                    FunctionStatsApi.bcCBookClick(mItemData.moduleBean.getId(), bean.getId());
//                    //判断分类.
//                    switch (mItemData.moduleBean.getType()) {
//                        case 0:
//                            //精选
//                            FunctionStatsApi.bdFeaturedBookClick(bean.getId());
//                            break;
//                        case 1:
//                            //男生
//                            FunctionStatsApi.bdBoyBookClick(bean.getId());
//                            break;
//                        case 2:
//                            //女生
//                            FunctionStatsApi.bdGirlBookClick(bean.getId());
//                            break;
//                    }
//                    FuncPageStatsApi.bookCityBookClick(bean.getId(), StringFormat.parseInt(mItemData.moduleBean.getId(), 0));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
//        for (int index = 0; index < mIds.size(); index++) {
//            View view = mItemView.findViewById(mIds.get(index));
//            if (index == 0) {
//                TextView textView = view.findViewById(R.id.module_title);
//                textView.setText(mItemData.moduleBean.getTitle());
//            } else {
//                BookCityItemBean data = DataUtils.getItem(mItemData.moduleBean.getBooks(), index - 1);
//                if (data != null) {
//                    //防止出现少于五本书的情况
//                    view.setVisibility(View.VISIBLE);
//                    GlideUtils.INSTANCE.loadImage(mActivity, data.getCover(), (ImageView) view.findViewById(R.id.book_cover), GlideUtils.INSTANCE.getBookRadius());
//                    ((TextView) view.findViewById(R.id.book_name)).setText(data.getName());
//                    if (index == 1) {
//                        ((TextView) view.findViewById(R.id.book_resume)).setText(data.getResume());
//                        ((TextView) view.findViewById(R.id.book_author)).setText(data.getAuthorName());
//                        ((TextView) view.findViewById(R.id.book_count)).setText(String.format("%s万字", data.getWordCount() / 10000));
//                        view.findViewById(R.id.book_grade).setVisibility(View.GONE);
////                        ((TextView) view.findViewById(R.id.book_grade)).setText(String.format("%s分", data.getStar()));
//                        view.findViewById(R.id.tv_grade).setVisibility(View.VISIBLE);
//
//                        SpannableString s1 = new SpannableString(String.format("%s分", data.getStar()));
//                        s1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        s1.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        s1.setSpan(new AbsoluteSizeSpan(10, true), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        ((TextView) view.findViewById(R.id.tv_grade)).setText(s1);
//
//                        if (data.getCategoryName() == null || data.getCategoryName().isEmpty()) {
//                            view.findViewById(R.id.book_category).setVisibility(View.GONE);
//                        } else {
//                            view.findViewById(R.id.book_category).setVisibility(View.VISIBLE);
//                            ((TextView) view.findViewById(R.id.book_category)).setText(data.getCategoryName());
//                        }
//                        //按更新时间  只有精选显示
//                        if (mItemData.moduleBean.getType() == 0 && Utils.isStartsWith(mItemData.moduleBean.getTag(), "SJ")) {
//                            view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.VISIBLE);
//                            ((ImageView) view.findViewById(R.id.iv_new_hot_icon)).setImageResource(R.mipmap.new_icon);
//                        } else if (mItemData.moduleBean.getType() == 0 && Utils.isStartsWith(mItemData.moduleBean.getTag(), "RQ")) {
//                            view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.VISIBLE);
//                            ((ImageView) view.findViewById(R.id.iv_new_hot_icon)).setImageResource(R.mipmap.hot_icon);
//                        } else {
//                            view.findViewById(R.id.iv_new_hot_icon).setVisibility(View.GONE);
//                        }
//                    }
//                    //监听书籍可见.
//                    BookExposureMgr.addOnGlobalLayoutListener(mPageId, mItemData.moduleBean.getId(), view, data.getId(), data.getName());
//                } else {
//                    // 只能一本书的情况  不需要占位隐藏
//                    if (mItemData.moduleBean.getBooks().size() == 1) {
//                        view.setVisibility(View.GONE);
//                    } else {
//                        view.setVisibility(View.INVISIBLE);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void loadMoreData(List<BookCityItemBean> list) {
//        mItemData.moduleBean.setBooks(list);
//        onSetData(false, true, true);
//        bookCityPresenter.addRepeatBookId(Integer.valueOf(mItemData.moduleBean.getId()), list);
//    }
//}
