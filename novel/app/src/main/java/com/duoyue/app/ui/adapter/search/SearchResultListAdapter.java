package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duoyue.app.bean.SearchResultAuthBean;
import com.duoyue.app.bean.SearchResultBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListViewHolder> {

    private Context mContext;

    private List<Object> mList;
    private View.OnClickListener listener;

//    private SearchResultAuthBean searchResultAuthBean;

    private String title;

    public SearchResultListAdapter(Context context, View.OnClickListener onClickListener, List<Object> searchResultBeans, String string) {
        this.listener = onClickListener;
        this.mContext = context;
        this.mList = searchResultBeans;
        this.title = string;
    }

//    public void setData(SearchResultAuthBean authBeanList) {
//        this.searchResultAuthBean = authBeanList;
//    }
//
//    public SearchResultAuthBean getSearchResultAuthBean() {
//        return this.searchResultAuthBean;
//    }

    @Override
    public int getItemViewType(int position) {
        Object object = mList.get(position);
        if (object instanceof SearchResultAuthBean) {
            return 102;
        } else if (object instanceof SearchResultBean) {
            int i = 0;
            SearchResultBean resultBean = (SearchResultBean) object;
            if (resultBean.getType() == 102) {
                i = 103;
            } else if (resultBean.getType() == 103) {
                i = 104;
            }
            return i;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 102) {
            View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_search_auth_result, viewGroup, false);
            return new SearchResultListViewHolder(view2, i);
        } else if (i == 104) {
            View view3 = LayoutInflater.from(mContext).inflate(R.layout.item_recommd_book_list_result, viewGroup, false);
            return new SearchResultListViewHolder(view3, i);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_list_result, viewGroup, false);
            return new SearchResultListViewHolder(view, i);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultListViewHolder searchResultListViewHolder, int i) {
        if (getItemViewType(i) == 102) {
            SearchResultAuthBean searchResultAuthBean = (SearchResultAuthBean) mList.get(i);
            if (searchResultAuthBean == null) return;
            searchResultListViewHolder.mTv_name.setText(Html.fromHtml(searchResultAuthBean.getAuthName()));
            searchResultListViewHolder.mTv_desc.setText(searchResultAuthBean.getTotalSize() + "部作品");
            searchResultListViewHolder.xLinearLayout.setOnClickListener(listener);
            searchResultListViewHolder.xLinearLayout.setTag(searchResultAuthBean.getAuthName());
        } else if (getItemViewType(i) == 104) {
            SearchResultBean searchResultBean = (SearchResultBean) mList.get(i);
            if (searchResultBean == null) return;
            searchResultListViewHolder.mTv_name.setText((TextUtils.isEmpty(title) ? "" : title));
            searchResultListViewHolder.mTv_sum.setText("》" + searchResultBean.getRandom() + "%的用户在看");
            if (null != searchResultBean.getSearchRecommdBookBeans()) {
                SearchRecommdBookAdapter searchRecommdBookAdapter = new SearchRecommdBookAdapter(mContext, searchResultBean.getSearchRecommdBookBeans(), listener);
                searchResultListViewHolder.recyclerView.setAdapter(searchRecommdBookAdapter);
            }
        } else if (getItemViewType(i) == 103) {
            SearchResultBean searchResultBean = (SearchResultBean) mList.get(i);
            if (searchResultBean == null) return;
            BookExposureMgr.addOnGlobalLayoutListener(FunPageStatsConstants.EP_SEARCH_RESULT, FunPageStatsConstants.EP_SEARCH_RESULT, searchResultListViewHolder.xRelativeLayout, searchResultBean.getBookId(), "", 0, null);

            if (searchResultBean.getBookName() != null) {
                searchResultListViewHolder.mTv_name.setText(Html.fromHtml(searchResultBean.getBookName()));
            }
            if (searchResultBean.getResume() != null) {
                searchResultListViewHolder.mTv_desc.setText(Html.fromHtml(searchResultBean.getResume()));
            }

            if (searchResultBean.getAuthor() != null) {
                searchResultListViewHolder.mTv_author.setText(Html.fromHtml(searchResultBean.getAuthor()));
            }

            if (searchResultBean.getBookCover() != null) {
                GlideUtils.INSTANCE.loadImage(mContext, searchResultBean.getBookCover(), searchResultListViewHolder.imageView, GlideUtils.INSTANCE.getBookRadius());
            }
            searchResultListViewHolder.mTv_zi.setText((int) (searchResultBean.getWordCount() * 1f / 10000) + "万字");
            searchResultListViewHolder.mTv_fen.setText(searchResultBean.getStar() + "分");
            searchResultListViewHolder.mTv_title.setText(searchResultBean.getCategory());
            searchResultListViewHolder.xRelativeLayout.setOnClickListener(listener);
            searchResultListViewHolder.xRelativeLayout.setTag(searchResultBean.getBookId());
            if (i == 0) {
                searchResultListViewHolder.view.setVisibility(View.VISIBLE);
            } else {
                searchResultListViewHolder.view.setVisibility(View.GONE);
            }

        }
    }

//    private void TvOverFlowed(final TextView textView, final String string) {
//        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                double w0 = textView.getWidth();//控件宽度
//                double w1 = textView.getPaint().measureText(string);//文本宽度
//                if (w1 >= w0) {
//                    textView.setText(w1 / w0 + "--");
//                } else {
//                    textView.setText(string);
//                }
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) {
//            if (this.searchResultAuthBean != null) return this.mList.size() + 1;
            return mList.size();
        }
        return 0;
    }
}
