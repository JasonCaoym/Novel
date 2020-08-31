package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.RecommendItemBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.ui.BookDetailHotViewHolder;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class BookDetailHotAdapter extends RecyclerView.Adapter<BookDetailHotViewHolder> {

    private Context mContext;

    private List<RecommendItemBean> mList;

    private View.OnClickListener clickListener;

    /**
     * 页面Id(统计曝光用到).
     */
    private String mPageId;

    /**
     * 分类Id(统计曝光用到).
     */
    private String mCategoryId;

    public BookDetailHotAdapter(Context context, String pageId, String bookId, List<RecommendItemBean> beanList, View.OnClickListener onClickListener) {
        this.mContext = context;
        this.mPageId = pageId;
        this.mCategoryId = bookId;
        this.mList = beanList;
        this.clickListener = onClickListener;

    }

    @NonNull
    @Override
    public BookDetailHotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BookDetailHotViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book_detail_hot, viewGroup, false), true);
    }

    @Override
    public void onBindViewHolder(@NonNull BookDetailHotViewHolder bookDetailHotViewHolder, int i) {
        GlideUtils.INSTANCE.loadImage(mContext, mList.get(i).getCover(), bookDetailHotViewHolder.imageView);
        bookDetailHotViewHolder.mTv_name.setText(mList.get(i).getBookName());
        bookDetailHotViewHolder.mTv_author.setText(mList.get(i).getAuthorName());
        if (mList.get(i).getFansNum() < 10000) {
            bookDetailHotViewHolder.mTv_sum.setText(String.valueOf(mList.get(i).getFansNum()));
        } else {
            bookDetailHotViewHolder.mTv_sum.setText(mList.get(i).getFansNum() * 1f / 10000 + "万");
        }
        if (mList.get(i).getState() == 1) {
            bookDetailHotViewHolder.mTv_state.setText(R.string.updating);
        } else if (mList.get(i).getState() == 2) {
            bookDetailHotViewHolder.mTv_state.setText(R.string.finished);
        } else {
            bookDetailHotViewHolder.mTv_state.setText(R.string.stop_update);
        }
        bookDetailHotViewHolder.mTv_desc.setText(mList.get(i).getResume());
        bookDetailHotViewHolder.xRelativeLayout.setOnClickListener(clickListener);
        bookDetailHotViewHolder.xRelativeLayout.setTag(mList.get(i).getBookId());
        //监听书籍可见.
        BookExposureMgr.addOnGlobalLayoutListener(mPageId, mCategoryId, bookDetailHotViewHolder.itemView, mList.get(i).getBookId(), mList.get(i).getBookName());
    }

    @Override
    public int getItemCount() {

        if (this.mList != null && !this.mList.isEmpty()) {
            return this.mList.size();
        }
        return 0;
    }
}
