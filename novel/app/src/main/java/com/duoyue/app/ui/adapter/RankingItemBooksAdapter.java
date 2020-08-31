package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class RankingItemBooksAdapter extends RecyclerView.Adapter<RankingBooksListViewHolder> {

    private Context mContext;

    private List<BookCityItemBean> bookCityItemBeanList;

    private View.OnClickListener listener;

    private int mColumnType;

    public RankingItemBooksAdapter(Context context, List<BookCityItemBean> bookCityItemBeans, View.OnClickListener onClickListener, int columntype) {
        this.mContext = context;
        this.bookCityItemBeanList = bookCityItemBeans;
        this.listener = onClickListener;
        this.mColumnType = columntype;
    }

    @NonNull
    @Override
    public RankingBooksListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rank_book, viewGroup, false);
        return new RankingBooksListViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingBooksListViewHolder rankingBooksListViewHolder, int i) {
        BookCityItemBean bookCityItemBean = bookCityItemBeanList.get(i);
        rankingBooksListViewHolder.mTv_book_name.setText(bookCityItemBean.getName());
        switch (i) {
            case 0:
                rankingBooksListViewHolder.mTv_word_count.setBackgroundResource(R.mipmap.icon_rank_second);
                break;
            case 1:
                rankingBooksListViewHolder.mTv_word_count.setBackgroundResource(R.mipmap.icon_rank_third);
                break;
            default:
                rankingBooksListViewHolder.mTv_word_count.setBackgroundResource(R.mipmap.icon_rank_five);
                break;
        }
        switch (mColumnType) {
//            1:人气榜 2:飙升榜 3:完结榜
            case 1:
                if (bookCityItemBean.getRealWeekCollect() >= 100000000) {
                    rankingBooksListViewHolder.nTv_count.setText(bookCityItemBean.getRealWeekCollect() / 100000000 + "万");
                } else if (bookCityItemBean.getRealWeekCollect() >= 10000) {
                    rankingBooksListViewHolder.nTv_count.setText((int) (bookCityItemBean.getRealWeekCollect() * 1f / 10000f) + "万");
                } else {
                    rankingBooksListViewHolder.nTv_count.setText(String.valueOf(bookCityItemBean.getRealWeekCollect()));
                }
                rankingBooksListViewHolder.mTv_rank.setText("热度");
                break;
//            case 2:
//                if (bookCityItemBean.getRealWeekRead() >= 100000000) {
//                    rankingBooksListViewHolder.nTv_count.setText(bookCityItemBean.getRealWeekRead() / 100000000 + "万");
//                } else if (bookCityItemBean.getRealWeekRead() >= 10000) {
//                    rankingBooksListViewHolder.nTv_count.setText((int) (bookCityItemBean.getRealWeekRead() * 1f / 10000f) + "万");
//                } else {
//                    rankingBooksListViewHolder.nTv_count.setText(bookCityItemBean.getRealWeekRead() + "");
//                }
//                rankingBooksListViewHolder.mTv_rank.setText("人在读");
//                break;
            case 3:
            case 2:
                rankingBooksListViewHolder.nTv_count.setText((int) (bookCityItemBean.getWordCount() * 1f / 10000) + "万");
                rankingBooksListViewHolder.mTv_rank.setText("字");
                break;
        }
        rankingBooksListViewHolder.mTv_word_count.setText(String.valueOf(i + 2));
        rankingBooksListViewHolder.xRelativeLayout.setOnClickListener(listener);
        rankingBooksListViewHolder.xRelativeLayout.setTag(R.id.xll_tag, bookCityItemBeanList.get(i).getId());
        GlideUtils.INSTANCE.loadImage(mContext, bookCityItemBeanList.get(i).getCover(), rankingBooksListViewHolder.mIv_icon, ViewUtils.dp2px(50), ViewUtils.dp2px(67));
    }

    @Override
    public int getItemCount() {
        if (bookCityItemBeanList != null && !bookCityItemBeanList.isEmpty()) return bookCityItemBeanList.size();
        return 0;
    }
}
