package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duoyue.app.bean.SearchResultBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.ui.adapter.search.SearchResultListViewHolder;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class SearchResultWorksListAdapter extends RecyclerView.Adapter<SearchResultListViewHolder> {

    private Context mContext;
    private List<SearchResultBean> mList;
    private View.OnClickListener onClickListener;
    private boolean mPrve;

    public SearchResultWorksListAdapter(Context context, List<SearchResultBean> searchResultBeans, View.OnClickListener clickListener, boolean prve) {
        this.mContext = context;
        this.onClickListener = clickListener;
        this.mList = searchResultBeans;
        this.mPrve = prve;
    }

    @NonNull
    @Override
    public SearchResultListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_list_result, viewGroup, false);
        return new SearchResultListViewHolder(view, 103);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultListViewHolder searchResultListViewHolder, int i) {
        if (mList.get(i).getBookName() != null) {
            searchResultListViewHolder.mTv_name.setText(Html.fromHtml(mList.get(i).getBookName()));
        }
        if (mList.get(i).getResume() != null) {
            searchResultListViewHolder.mTv_desc.setText(Html.fromHtml(mList.get(i).getResume()));
        }

        if (mList.get(i).getAuthor() != null) {
            searchResultListViewHolder.mTv_author.setText(Html.fromHtml(mList.get(i).getAuthor()));
        }

        if (mList.get(i).getBookCover() != null) {
            GlideUtils.INSTANCE.loadImage(mContext, mList.get(i).getBookCover(), searchResultListViewHolder.imageView, GlideUtils.INSTANCE.getBookRadius());
        }
        searchResultListViewHolder.mTv_zi.setText((int) (mList.get(i).getWordCount() * 1f / 10000) + "万字");
        searchResultListViewHolder.mTv_fen.setText(mList.get(i).getStar() + "分");
        searchResultListViewHolder.mTv_title.setText(mList.get(i).getCategory());
        searchResultListViewHolder.xRelativeLayout.setOnClickListener(onClickListener);
        searchResultListViewHolder.xRelativeLayout.setTag(mList.get(i).getBookId());
        if (i == 0) {
            searchResultListViewHolder.view.setVisibility(View.VISIBLE);
        } else {
            searchResultListViewHolder.view.setVisibility(View.GONE);
        }
        BookExposureMgr.addOnGlobalLayoutListener(BookExposureMgr.SEARCH_AUTH_RESULT, mPrve ? FunPageStatsConstants.SEARCH_SHOW : FunPageStatsConstants.SEARCH_RESULT, searchResultListViewHolder.xRelativeLayout, mList.get(i).getBookId(), mList.get(i).getBookName(), 0, null);
    }

    @Override
    public int getItemCount() {
        if (this.mList != null && !this.mList.isEmpty()) return this.mList.size();
        return 0;
    }
}
