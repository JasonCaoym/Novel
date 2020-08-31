package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookNewUserBagStatusesBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class BookNewPersonGiftBagAdapter extends RecyclerView.Adapter<BookNewPersonGiftBagViewHolder> {

    private Context mContext;
    private List<BookNewUserBagStatusesBean> mList;

    private View.OnClickListener onClickListener;

    public BookNewPersonGiftBagAdapter(Context context, List<BookNewUserBagStatusesBean> bookNewUserBagStatusesBeanList, View.OnClickListener clickListener) {
        this.mList = bookNewUserBagStatusesBeanList;
        this.mContext = context;
        this.onClickListener = clickListener;
    }

    @NonNull
    @Override
    public BookNewPersonGiftBagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_new_person_gift_bag, viewGroup, false);
        return new BookNewPersonGiftBagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookNewPersonGiftBagViewHolder bookNewPersonGiftBagViewHolder, int i) {
        BookNewUserBagStatusesBean bookNewUserBagStatusesBean = mList.get(i);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) bookNewPersonGiftBagViewHolder.itemView.getLayoutParams();
        if (i == 0) {
            layoutParams.leftMargin = ViewUtils.dp2px(9);
            layoutParams.rightMargin = ViewUtils.dp2px(0);
        } else if (i == mList.size() - 1) {
            layoutParams.leftMargin = ViewUtils.dp2px(0);
            layoutParams.rightMargin = ViewUtils.dp2px(9);

        } else {
            layoutParams.leftMargin = ViewUtils.dp2px(0);
            layoutParams.rightMargin = ViewUtils.dp2px(0);
        }
        bookNewPersonGiftBagViewHolder.itemView.setLayoutParams(layoutParams);
        bookNewPersonGiftBagViewHolder.mTv_title.setText(bookNewUserBagStatusesBean.getDesc());
        GlideUtils.INSTANCE.loadGiftImage(mContext, bookNewUserBagStatusesBean.getNewIconUrl(), bookNewPersonGiftBagViewHolder.imageView);
        if (bookNewUserBagStatusesBean.getDate() != null) {
            String time = bookNewUserBagStatusesBean.getDate().substring(bookNewUserBagStatusesBean.getDate().indexOf("-") + 1);
            if (bookNewUserBagStatusesBean.getIsToday() == 0) {
                bookNewPersonGiftBagViewHolder.mText_time.setText(time);
            } else {
                bookNewPersonGiftBagViewHolder.mText_time.setText(time + "（仅今日可领）");
            }

            switch (bookNewUserBagStatusesBean.getStatus()) {
                case 1:
                    if (bookNewUserBagStatusesBean.getIsToday() == 0) {
                        bookNewPersonGiftBagViewHolder.textView.setVisibility(View.VISIBLE);
                        bookNewPersonGiftBagViewHolder.mText_bg.setVisibility(View.GONE);
                        bookNewPersonGiftBagViewHolder.mIv_triangle.setVisibility(View.GONE);
                        bookNewPersonGiftBagViewHolder.textView.setText("未开始");
                    } else {
                        bookNewPersonGiftBagViewHolder.textView.setVisibility(View.GONE);
                        bookNewPersonGiftBagViewHolder.mText_bg.setVisibility(View.VISIBLE);
                        bookNewPersonGiftBagViewHolder.mIv_triangle.setVisibility(View.VISIBLE);
                        bookNewPersonGiftBagViewHolder.mText_bg.setText("再阅读" + bookNewUserBagStatusesBean.getRemainingTime() + "分钟可领取");
                    }

                    break;
                case 2:
                    bookNewPersonGiftBagViewHolder.textView.setVisibility(View.GONE);
                    bookNewPersonGiftBagViewHolder.mText_bg.setVisibility(View.VISIBLE);
                    bookNewPersonGiftBagViewHolder.mIv_triangle.setVisibility(View.VISIBLE);

                    if (bookNewUserBagStatusesBean.getRemainingTime() == 0) {
                        bookNewPersonGiftBagViewHolder.mText_bg.setText("立即领取");
                        bookNewPersonGiftBagViewHolder.mText_bg.setOnClickListener(onClickListener);
                        bookNewPersonGiftBagViewHolder.mText_bg.setTag(i);
                    } else {
                        bookNewPersonGiftBagViewHolder.mText_bg.setText("再阅读" + bookNewUserBagStatusesBean.getRemainingTime() + "分钟可领取");
                    }

                    break;
                case 3:
                    bookNewPersonGiftBagViewHolder.textView.setVisibility(View.VISIBLE);
                    bookNewPersonGiftBagViewHolder.mText_bg.setVisibility(View.GONE);
                    bookNewPersonGiftBagViewHolder.mIv_triangle.setVisibility(View.GONE);
                    bookNewPersonGiftBagViewHolder.textView.setText("已领取");
                    break;
                case 4:
                    bookNewPersonGiftBagViewHolder.textView.setVisibility(View.VISIBLE);
                    bookNewPersonGiftBagViewHolder.mText_bg.setVisibility(View.GONE);
                    bookNewPersonGiftBagViewHolder.mIv_triangle.setVisibility(View.GONE);
                    bookNewPersonGiftBagViewHolder.textView.setText("已失效");
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) return mList.size();
        return 0;
    }
}
