package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.duoyue.app.bean.BookNewBookInfoBean;
import com.duoyue.app.bean.BookNewHeaderBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class BookNewListHeaderV2Adapter extends RecyclerView.Adapter<BookNewListHeaderV2ViewHolder> {

    private Context mContext;

    private List<BookNewHeaderBean> list;

    private View.OnClickListener onClickListener;

    public BookNewListHeaderV2Adapter(Context context, List<BookNewHeaderBean> bookNewHeaderBeanList, View.OnClickListener clickListener) {
        this.mContext = context;
        this.list = bookNewHeaderBeanList;
        this.onClickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null && list.get(position).isLastData()) {
            return 102;
        } else {
            return 103;
        }
    }

    @NonNull
    @Override
    public BookNewListHeaderV2ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 102) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_more_go, viewGroup, false);
            return new BookNewListHeaderV2ViewHolder(view);
        } else {
            View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_book_new_list_near, viewGroup, false);
            return new BookNewListHeaderV2ViewHolder(view2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BookNewListHeaderV2ViewHolder bookNewListHeaderV2ViewHolder, int i) {
        if (getItemViewType(i) == 103) {
            BookNewHeaderBean bookNewHeaderBean = list.get(i);
            if (bookNewHeaderBean != null) {
                if (!TextUtils.isEmpty(bookNewHeaderBean.getCover())) {
                    Glide.with(mContext)
                            .load(bookNewHeaderBean.getCover())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.mine_head_icon))
                            .into(bookNewListHeaderV2ViewHolder.mIv_icon2);
                } else {
                    bookNewListHeaderV2ViewHolder.mIv_icon2.setImageResource(R.mipmap.mine_head_icon);
                }
                bookNewListHeaderV2ViewHolder.mTv_name.setText(bookNewHeaderBean.getNickName());
                if (list.get(i).getDistance() < 1000) {
                    bookNewListHeaderV2ViewHolder.mTv_mi.setText((int) list.get(i).getDistance() + "m");
                } else {
                    bookNewListHeaderV2ViewHolder.mTv_mi.setText(String.format("%.1f", list.get(i).getDistance() / 1000) + "km");
                }
                bookNewListHeaderV2ViewHolder.mIv_sex.setImageResource(bookNewHeaderBean.getSex() == 1 ? R.mipmap.bg_man : R.mipmap.bg_woman);
                bookNewListHeaderV2ViewHolder.mTv_read.setText(bookNewHeaderBean.getLastReadTime());
                BookNewBookInfoBean bookNewBookInfoBean = bookNewHeaderBean.getBookInfo();
                if (bookNewBookInfoBean != null) {
                    GlideUtils.INSTANCE.loadImage(mContext, bookNewBookInfoBean.getCover(), bookNewListHeaderV2ViewHolder.mIv_icon);
                    bookNewListHeaderV2ViewHolder.mTv_book_name.setText(bookNewBookInfoBean.getName());
                    bookNewListHeaderV2ViewHolder.mTv_desc.setText
                            (bookNewBookInfoBean.getResume());
                    String state = "";
                    switch (bookNewBookInfoBean.getState()) {
                        case 1:
                            state = "连载中";
                            break;
                        case 2:
                            state = "已完结";
                            break;
                        case 3:
                            state = "断更";
                            break;
                    }
                    state += " · " + String.format("%s万字", bookNewBookInfoBean.getWordCount() / 10000) + " · " + bookNewBookInfoBean.getCatName();
                    bookNewListHeaderV2ViewHolder.mTv_state.setText(state);

                    boolean isSave = BookShelfPresenter.isAdded("" + bookNewBookInfoBean.getBookId());
                    bookNewListHeaderV2ViewHolder.mTv_join.setBackground(ContextCompat.getDrawable(mContext, isSave ? R.drawable.btn_sign_in_16 : R.drawable.btn_sign_in_15));
                    bookNewListHeaderV2ViewHolder.mTv_join.setCompoundDrawablesWithIntrinsicBounds(isSave ? ContextCompat.getDrawable(mContext, R.mipmap.bg_book_list_join_shape) : ContextCompat.getDrawable(mContext, R.mipmap.bg_book_list_join), null, null, null);
                    bookNewListHeaderV2ViewHolder.mTv_join.setText(isSave ? R.string.already_in_shelf_x : R.string.btn_add_shelf);
                    bookNewListHeaderV2ViewHolder.mTv_join.setTextColor(isSave ? ContextCompat.getColor(mContext, R.color.color_b2b2b2) : ContextCompat.getColor(mContext, R.color.color_FE8B13));
                    bookNewListHeaderV2ViewHolder.mTv_join.setOnClickListener(isSave ? null : onClickListener);
                    bookNewListHeaderV2ViewHolder.mTv_join.setTag(i);
                    bookNewListHeaderV2ViewHolder.mTv_all_read.setOnClickListener(onClickListener);
                    bookNewListHeaderV2ViewHolder.mTv_all_read.setTag(bookNewBookInfoBean.getBookId());
                    bookNewListHeaderV2ViewHolder.mXrl_one.setTag(bookNewBookInfoBean.getBookId());
                    bookNewListHeaderV2ViewHolder.mXrl_two.setTag(bookNewBookInfoBean.getBookId());
                    bookNewListHeaderV2ViewHolder.mXrl_one.setOnClickListener(onClickListener);
                    bookNewListHeaderV2ViewHolder.mXrl_two.setOnClickListener(onClickListener);
                    BookExposureMgr.addOnGlobalLayoutListener(FunPageStatsConstants.NEAR_READER_EXPOSE, FunPageStatsConstants.NEAR_READER_EXPOSE, bookNewListHeaderV2ViewHolder.mXrl_one, bookNewBookInfoBean.getBookId(), "", 0, null);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list != null && !list.isEmpty()) return list.size();
        return 0;
    }
}
