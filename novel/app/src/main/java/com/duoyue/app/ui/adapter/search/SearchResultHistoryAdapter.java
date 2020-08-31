package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class SearchResultHistoryAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {


    private Context mContext;
    private List<String> mList;

    private View.OnClickListener onClickListener;


    public SearchResultHistoryAdapter(Context context, List<String> strings, View.OnClickListener clickListener) {
        this.mContext = context;
        this.mList = strings;
        this.onClickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_history_result, viewGroup, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder searchResultViewHolder, int i) {

        searchResultViewHolder.xRelativeLayout.setOnClickListener(onClickListener);
        searchResultViewHolder.xRelativeLayout.setTag(mList.get(i));
        searchResultViewHolder.textView.setText(Html.fromHtml(mList.get(i)).toString());
    }

    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) return mList.size();
        return 0;
    }
}
