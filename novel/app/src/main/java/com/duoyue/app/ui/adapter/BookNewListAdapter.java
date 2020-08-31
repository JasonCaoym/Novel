package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookNewBookBean;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class BookNewListAdapter extends RecyclerView.Adapter<BookNewListAdapter.BookNewListViewHolder> {

    private static final int ONE_TYPE = 1;
    private static final int TWO_TYPE = 2;
    private static final int THREE_TYPE = 3;

    private Context mContext;

    private List<BookNewBookBean> newBookBeans;

    private View.OnClickListener onClickListener;

    public BookNewListAdapter(Context context, List<BookNewBookBean> bookNewBookBeans, View.OnClickListener clickListener) {
        this.mContext = context;
        this.newBookBeans = bookNewBookBeans;
        this.onClickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1) {
            return ONE_TYPE;
        } else if (position == newBookBeans.size()) {
            return THREE_TYPE;
        } else {
            return TWO_TYPE;
        }
    }

    @NonNull
    @Override
    public BookNewListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ONE_TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_new_list_one, viewGroup, false);
            return new BookNewListViewHolder(view, i, this.onClickListener);
        } else if (i == THREE_TYPE) {
            View view3 = LayoutInflater.from(mContext).inflate(R.layout.item_more_go, viewGroup, false);
            return new BookNewListViewHolder(view3, i, this.onClickListener);
        } else {
            View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_book_new_list_three, viewGroup, false);
            return new BookNewListViewHolder(view2, i, this.onClickListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final BookNewListViewHolder bookNewListViewHolder, int i) {
        if (getItemViewType(i) == ONE_TYPE) {
            bookNewListViewHolder.mTv_one.setText(newBookBeans.get(i).getName());
            XLinearLayout.LayoutParams layoutParams = (XLinearLayout.LayoutParams) bookNewListViewHolder.xRelativeLayout.getLayoutParams();
            if (i == 0) {
                bookNewListViewHolder.xRelativeLayout.setBackgroundResource(R.mipmap.bg_book_list_blue);
                layoutParams.leftMargin = ViewUtils.dp2px(16);
                layoutParams.rightMargin = ViewUtils.dp2px(7.5f);
                bookNewListViewHolder.xRelativeLayout.setLayoutParams(layoutParams);
            } else {
                bookNewListViewHolder.xRelativeLayout.setBackgroundResource(R.mipmap.bg_book_list_yellow);
                layoutParams.leftMargin = ViewUtils.dp2px(7.5f);
                layoutParams.rightMargin = ViewUtils.dp2px(16);
                bookNewListViewHolder.xRelativeLayout.setLayoutParams(layoutParams);
            }
            bookNewListViewHolder.xRelativeLayout.setTag(i);
            bookNewListViewHolder.xRelativeLayout.setOnClickListener(onClickListener);
            initImage(bookNewListViewHolder, this.newBookBeans.get(i).getIconList());
            FuncPageStatsApi.nearBookListExp(String.valueOf(newBookBeans.get(i).getId()));
        } else if (getItemViewType(i) == TWO_TYPE) {
            bookNewListViewHolder.mRv_item_two.setTag(i);
            bookNewListViewHolder.mRv_item_two.setOnClickListener(onClickListener);
            bookNewListViewHolder.mTv_book_list_name.setText(newBookBeans.get(i).getName());
            bookNewListViewHolder.mTv_desc.setText(newBookBeans.get(i).getResume());
            bookNewListViewHolder.mTv_author.setText(newBookBeans.get(i).getUserName() + " · " + newBookBeans.get(i).getBookNum() + "本");
            if (TextUtils.isEmpty(newBookBeans.get(i).getUserIcon())) {
                bookNewListViewHolder.mIv_icon.setImageResource(R.mipmap.icon_book_detail_default);
            } else {
                GlideUtils.INSTANCE.loadImage(mContext, newBookBeans.get(i).getUserIcon(), bookNewListViewHolder.mIv_icon);
            }
            initImage(bookNewListViewHolder, this.newBookBeans.get(i).getIconList());

//            if (i == newBookBeans.size() - 1) {
//                bookNewListViewHolder.view.setVisibility(View.GONE);
//            } else {
//                bookNewListViewHolder.view.setVisibility(View.VISIBLE);
//            }
            FuncPageStatsApi.nearBookListExp(String.valueOf(newBookBeans.get(i).getId()));
        }
    }

    void initImage(final BookNewListViewHolder bookNewListViewHolder, final List<String> list) {
        if (list == null) {
            bookNewListViewHolder.mIv_center.setVisibility(View.INVISIBLE);
            bookNewListViewHolder.mIv_two.setVisibility(View.INVISIBLE);
            bookNewListViewHolder.mIv_one.setVisibility(View.INVISIBLE);
        } else {
            if (!TextUtils.isEmpty(list.get(0))) {
                bookNewListViewHolder.mIv_center.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImage(mContext, list.get(0), bookNewListViewHolder.mIv_center, GlideUtils.INSTANCE.getBookRadius());
            } else {
                bookNewListViewHolder.mIv_center.setVisibility(View.INVISIBLE);
            }
            if (list.size() > 1 && !TextUtils.isEmpty(list.get(1))) {
                bookNewListViewHolder.mIv_two.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImage(mContext, list.get(1), bookNewListViewHolder.mIv_two, GlideUtils.INSTANCE.getBookRadius());
            } else {
                bookNewListViewHolder.mIv_two.setVisibility(View.INVISIBLE);
            }
            if (list.size() > 2 && !TextUtils.isEmpty(list.get(2))) {
                bookNewListViewHolder.mIv_one.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImage(mContext, list.get(2), bookNewListViewHolder.mIv_one, GlideUtils.INSTANCE.getBookRadius());
            } else {
                bookNewListViewHolder.mIv_one.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (this.newBookBeans != null && !this.newBookBeans.isEmpty()) return this.newBookBeans.size() + 1;
        return 0;
    }

    static class BookNewListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTv_one, mTv_book_list_name, mTv_desc, mTv_author;
        public XRelativeLayout xRelativeLayout, mRv_item_two;
        public ImageView mIv_one, mIv_two, mIv_center, mIv_icon;

        public View view;

        public BookNewListViewHolder(@NonNull View itemView, int type, View.OnClickListener onClickListener) {
            super(itemView);
            if (type == ONE_TYPE) {
                mTv_one = itemView.findViewById(R.id.tv_one);
                xRelativeLayout = itemView.findViewById(R.id.xrl_bg);
                mIv_one = itemView.findViewById(R.id.iv_one);
                mIv_two = itemView.findViewById(R.id.iv_two);
                mIv_center = itemView.findViewById(R.id.iv_center);
            } else if (type == THREE_TYPE) {
                mTv_one = itemView.findViewById(R.id.tv_go_more);
                mTv_one.setOnClickListener(onClickListener);
            } else {
                mTv_book_list_name = itemView.findViewById(R.id.tv_book_list_name);
                mTv_desc = itemView.findViewById(R.id.tv_desc);
                mTv_author = itemView.findViewById(R.id.tv_author);
                mIv_icon = itemView.findViewById(R.id.iv_icon);
                mIv_one = itemView.findViewById(R.id.iv_icon_one);
                mIv_two = itemView.findViewById(R.id.iv_icon_two);
                mIv_center = itemView.findViewById(R.id.iv_icon_three);
                view = itemView.findViewById(R.id.fix_row_6);
                mRv_item_two = itemView.findViewById(R.id.xll_item_two);
            }
        }
    }
}
