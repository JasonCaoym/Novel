package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class RankingBooksListAdapter extends RecyclerView.Adapter<RankingBooksListViewHolder> {

    private Context mContext;

    private List<BookChildColumnsBean> mList;

    private View.OnClickListener mClick;

    private RankingItemBooksAdapter rankingItemBooksAdapter;
    private String mPageId;

    public RankingBooksListAdapter(Context context, List<BookChildColumnsBean> listBeanList, View.OnClickListener onClickListener,
                                   String pageId) {
        this.mContext = context;
        this.mList = listBeanList;
        this.mClick = onClickListener;
        this.mPageId = pageId;
    }

    @NonNull
    @Override
    public RankingBooksListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rank, viewGroup, false);
        return new RankingBooksListViewHolder(view, true);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingBooksListViewHolder rankingBooksListViewHolder, int i) {
        switch (i) {
            case 0:
                rankingBooksListViewHolder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_top));
                break;
            case 1:
                rankingBooksListViewHolder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_top_v2));
                break;
//            case 2:
//                rankingBooksListViewHolder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_top_v3));
//                break;
            default:
                rankingBooksListViewHolder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_top_v3));
                break;
        }
        rankingBooksListViewHolder.imageView.setOnClickListener(mClick);
        rankingBooksListViewHolder.imageView.setTag(mList.get(i).getClassId());
        rankingBooksListViewHolder.mTv_rank_name.setText(mList.get(i).getChildColumnName());
//        rankingBooksListViewHolder.mTv_rank_desc.setText(mList.get(i).getDesc());
//        ViewGroup.LayoutParams params = rankingBooksListViewHolder.cardView.getLayoutParams();
//        params.width = ViewUtils.dp2px(310);
//        rankingBooksListViewHolder.cardView.setLayoutParams(params);
//
//        XRelativeLayout.LayoutParams imageViewLayoutParams = (XRelativeLayout.LayoutParams) rankingBooksListViewHolder.imageView.getLayoutParams();
//        imageViewLayoutParams.width = ViewUtils.dp2px(310);
//        rankingBooksListViewHolder.imageView.setLayoutParams(imageViewLayoutParams);

        if (mList.get(i).getBooks() != null && !mList.get(i).getBooks().isEmpty()) {
            BookCityItemBean bookCityItemBean = mList.get(i).getBooks().get(0);

            switch (mList.get(i).getColumnType()) {
//            1:人气榜 2:连载榜 3:完结榜
                case 1:
                    if (bookCityItemBean.getRealWeekCollect() >= 100000000) {
                        rankingBooksListViewHolder.mTv_word_count.setText(bookCityItemBean.getRealWeekCollect() / 100000000 + "万");
                    } else if (bookCityItemBean.getRealWeekCollect() >= 10000) {
                        rankingBooksListViewHolder.mTv_word_count.setText((int) (bookCityItemBean.getRealWeekCollect() * 1f / 10000f) + "万");
                    } else {
                        rankingBooksListViewHolder.mTv_word_count.setText(String.valueOf(bookCityItemBean.getRealWeekCollect()));
                    }
                    rankingBooksListViewHolder.mTv_rank.setText("热度");
                    break;
//                case 2:
//                    if (bookCityItemBean.getRealWeekRead() >= 100000000) {
//                        rankingBooksListViewHolder.mTv_word_count.setText(bookCityItemBean.getRealWeekRead() / 100000000 + "万");
//                    } else if (bookCityItemBean.getRealWeekRead() >= 10000) {
//                        rankingBooksListViewHolder.mTv_word_count.setText((int) (bookCityItemBean.getRealWeekRead() * 1f / 10000f) + "万");
//                    } else {
//                        rankingBooksListViewHolder.mTv_word_count.setText(bookCityItemBean.getRealWeekRead() + "");
//                    }
//                    rankingBooksListViewHolder.mTv_rank.setText("人在读");
//                    break;
                case 3:
                case 2:
                    rankingBooksListViewHolder.mTv_word_count.setText((int) (bookCityItemBean.getWordCount() * 1f / 10000) + "万");
                    rankingBooksListViewHolder.mTv_rank.setText("字");
                    break;
            }

            GlideUtils.INSTANCE.loadImage(mContext, bookCityItemBean.getCover(), rankingBooksListViewHolder.mIn_icon, ViewUtils.dp2px(57), ViewUtils.dp2px(75));
            rankingBooksListViewHolder.mIn_icon.setOnClickListener(mClick);
            rankingBooksListViewHolder.mIn_icon.setTag(R.id.xll_tag, bookCityItemBean.getId());

            rankingBooksListViewHolder.mTv_book_name.setText(bookCityItemBean.getName());
            rankingBooksListViewHolder.mTv_book_name.setOnClickListener(mClick);
            rankingBooksListViewHolder.mTv_book_name.setTag(R.id.xll_tag, bookCityItemBean.getId());

            rankingBooksListViewHolder.mTv_desc.setText(bookCityItemBean.getResume());
            rankingBooksListViewHolder.mTv_desc.setOnClickListener(mClick);
            rankingBooksListViewHolder.mTv_desc.setTag(R.id.xll_tag, bookCityItemBean.getId());

            if (rankingItemBooksAdapter != null) {
                rankingItemBooksAdapter = null;
            }
            rankingItemBooksAdapter = new RankingItemBooksAdapter(mContext, mList.get(i).getBooks().subList(1, mList.get(i).getBooks().size()), mClick, mList.get(i).getColumnType());
            rankingBooksListViewHolder.recyclerView.setAdapter(rankingItemBooksAdapter);
        }
    }

    @Override
    public int getItemCount() {
        if (this.mList != null && !this.mList.isEmpty()) {
            return this.mList.size() > 3 ? 3 : this.mList.size();
        }
        return 0;
    }
}
