package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import java.util.List;

public class FixedSixAdapter extends RecyclerView.Adapter<FixedSixViewHolder> {
    private final String pageChannel;
    private List<BookCityItemBean> mList;
    private Context mContext;

    private View.OnClickListener clickListener;

    private String mPage;
    private String mClassid;

    public FixedSixAdapter(Context context, View.OnClickListener onClickListener, String pageid, String pageChannel) {
        this.mContext = context;
        this.clickListener = onClickListener;
        this.mPage = pageid;
        this.pageChannel = pageChannel;

    }

    public void setData(List<BookCityItemBean> bookCityItemBeans, String cid) {
        this.mList = bookCityItemBeans;
        this.mClassid = cid;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FixedSixViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fixed_six, viewGroup, false);
        return new FixedSixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedSixViewHolder fixedSixViewHolder, int i) {
        BookCityItemBean data = mList.get(i);

        GlideUtils.INSTANCE.loadImage(mContext, data.getCover(), fixedSixViewHolder.imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.dp2px(134));
        fixedSixViewHolder.textView.setText(data.getName());
        fixedSixViewHolder.cardView.setOnClickListener(clickListener);
        fixedSixViewHolder.cardView.setTag(data.getId());
        fixedSixViewHolder.textView.setOnClickListener(clickListener);
        fixedSixViewHolder.textView.setTag(data.getId());
        fixedSixViewHolder.mTv_read.setOnClickListener(clickListener);
        fixedSixViewHolder.mTv_read.setTag(data.getId());
        BookExposureMgr.addOnGlobalLayoutListener(mPage, String.valueOf(mClassid), fixedSixViewHolder.itemView, data.getId(), data.getName(), Integer.parseInt(pageChannel), null);

    }

    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) return mList.size();
        return 0;
    }
}
