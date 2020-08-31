package com.duoyue.app.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class FixedOne2FourAdapter extends RecyclerView.Adapter<FixedOne2FourViewHolder> {


    private final String channel;
    private List<BookCityItemBean> mList;
    private Context mContext;

    private View.OnClickListener clickListener;

    private int mType;
    private String mTag;

    private String mPage;
    private int mClassid;

    public FixedOne2FourAdapter(Context context, View.OnClickListener onClickListener, String channel) {
        this.mContext = context;
        this.clickListener = onClickListener;
        this.channel = channel;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FixedOne2FourViewHolder.ITEM_ONE;
        } else {
            return FixedOne2FourViewHolder.ITEM_TWO;
        }

    }

    public void setData(List<BookCityItemBean> bookCityItemBeans, int type, String tag) {
        this.mList = bookCityItemBeans;
        this.mType = type;
        this.mTag = tag;
        notifyDataSetChanged();
    }

    public void setPageId(String page, int classid) {
        this.mPage = page;
        this.mClassid = classid;

    }

    @NonNull
    @Override
    public FixedOne2FourViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == FixedOne2FourViewHolder.ITEM_ONE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.book_item_v2_view, viewGroup, false);
            return new FixedOne2FourViewHolder(view, i);
        } else {
            View view2 = LayoutInflater.from(mContext).inflate(R.layout.books_horizontal_v2layout_item, viewGroup, false);
            return new FixedOne2FourViewHolder(view2, i);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull FixedOne2FourViewHolder fixedOne2FourViewHolder, int i) {
        BookCityItemBean data = mList.get(i);
        if (data == null) return;
        fixedOne2FourViewHolder.textView.setText(data.getName());
        fixedOne2FourViewHolder.xRelativeLayout.setOnClickListener(clickListener);
        fixedOne2FourViewHolder.xRelativeLayout.setTag(data.getId());
        if (getItemViewType(i) == FixedOne2FourViewHolder.ITEM_ONE) {
            GlideUtils.INSTANCE.loadImage(mContext, data.getCover(), fixedOne2FourViewHolder.imageView, ViewUtils.dp2px(89), ViewUtils.dp2px(122));
            fixedOne2FourViewHolder.mTv_resume.setText(data.getResume());
            fixedOne2FourViewHolder.mTv_author.setText(data.getAuthorName());
            fixedOne2FourViewHolder.mTv_count.setText(String.format("%s万字", data.getWordCount() / 10000));
            fixedOne2FourViewHolder.mTv_grade.setVisibility(View.GONE);
            fixedOne2FourViewHolder.mTv_grade_x.setVisibility(View.VISIBLE);
            SpannableString s1 = new SpannableString(String.format("%s分", data.getStar()));
            s1.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s1.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s1.setSpan(new AbsoluteSizeSpan(10, true), s1.length() - 1, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fixedOne2FourViewHolder.mTv_grade_x.setText(s1);

            if (data.getCategoryName() == null || data.getCategoryName().isEmpty()) {
                fixedOne2FourViewHolder.mTv_category.setVisibility(View.GONE);
            } else {
                fixedOne2FourViewHolder.mTv_category.setVisibility(View.VISIBLE);
                fixedOne2FourViewHolder.mTv_category.setText(data.getCategoryName());
            }
            //按更新时间  只有精选显示
            if (mType == 0 &&
                    (TextUtils.equals(mTag, "SJJN") || TextUtils.equals(mTag, "SJJV"))) {
                fixedOne2FourViewHolder.mIv_hot.setVisibility(View.VISIBLE);
                fixedOne2FourViewHolder.mIv_hot.setImageResource(R.mipmap.new_icon);
            } else if (mType == 0 &&
                    (TextUtils.equals(mTag, "RQJN") || TextUtils.equals(mTag, "RQJV"))) {
                fixedOne2FourViewHolder.mIv_hot.setVisibility(View.VISIBLE);
                fixedOne2FourViewHolder.mIv_hot.setImageResource(R.mipmap.hot_icon);
            } else {
                fixedOne2FourViewHolder.mIv_hot.setVisibility(View.GONE);
            }
        } else if (getItemViewType(i) == FixedOne2FourViewHolder.ITEM_TWO) {
            GlideUtils.INSTANCE.loadImage(mContext, data.getCover(), fixedOne2FourViewHolder.imageView, ViewUtils.dp2px(76), ViewUtils.dp2px(103));
        }
        if (mPage != null && mClassid != 0) {
            BookExposureMgr.addOnGlobalLayoutListener(mPage, String.valueOf(mClassid), fixedOne2FourViewHolder.xRelativeLayout, data.getId(), data.getName(), Integer.parseInt(channel), null);
        }
    }


    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) return mList.size();
        return 0;
    }
}
