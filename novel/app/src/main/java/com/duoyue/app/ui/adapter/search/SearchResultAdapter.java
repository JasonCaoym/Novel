package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.SearchKeyWordBean;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {

//    private static final int AUTH_TYPE = 1001;
//    private static final int BOOK_TYPE = 1002;

    private Context mContext;

    private List<SearchKeyWordBean> mList;
    private List<String> mAuthList;

    private View.OnClickListener onClickListener;


    public SearchResultAdapter(Context context, List<SearchKeyWordBean> strings, View.OnClickListener clickListener, List<String> authList) {
        this.mContext = context;
        this.mList = strings;
        this.mAuthList = authList;
        this.onClickListener = clickListener;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (mAuthList != null && !mAuthList.isEmpty() && position < mAuthList.size() && mAuthList.get(position) != null){
//            return AUTH_TYPE;
//        } else{
//            return BOOK_TYPE;
//        }
//    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, viewGroup, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder searchResultViewHolder, int i) {
        searchResultViewHolder.xRelativeLayout.setOnClickListener(onClickListener);
        if (!mAuthList.isEmpty() && i < mAuthList.size()) {
            searchResultViewHolder.textView.setText(Html.fromHtml(mAuthList.get(i)));
            searchResultViewHolder.imageView.setImageResource(R.mipmap.bg_search_auth);
            searchResultViewHolder.mTv_auth.setText("作者");
            searchResultViewHolder.xRelativeLayout.setTag(Html.fromHtml(mAuthList.get(i)).toString());
        } else {
            if (!mAuthList.isEmpty()) i -= 1;
            searchResultViewHolder.textView.setText(Html.fromHtml(mList.get(i).bookName));
            searchResultViewHolder.imageView.setImageResource(R.mipmap.bg_search_book);
            searchResultViewHolder.mTv_auth.setText("小说");
            searchResultViewHolder.xRelativeLayout.setTag(mList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) {
//            if (mAuthList != null && !mAuthList.isEmpty()) {
//                return mList.size() + mAuthList.size();
//            }
            return mList.size();
        }
        return 0;
    }
}
