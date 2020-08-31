package com.duoyue.app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.RecomHotItemBean;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.utils.GlideUtils;
import com.zzdm.ad.router.BaseData;

import java.util.ArrayList;
import java.util.List;

public class RecomHotAdapter extends RecyclerView.Adapter<RecomHotAdapter.HotViewHolder> {

    private Activity mContext;
    private List<RecomHotItemBean> list;
    private String sourceStats;

    public RecomHotAdapter(Activity context, String sourceStats) {
        mContext = context;
        this.sourceStats = sourceStats;
        list = new ArrayList<>();
    }

    public void setList(List<RecomHotItemBean> data){
        if (data != null) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public HotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HotViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recom_hot_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HotViewHolder hotViewHolder, int i) {
        hotViewHolder.bindData(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HotViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView ivCover;
        private TextView tvBookName;
        private TextView tvResume;
        private TextView tvAuthor;
        private TextView tvFans;
        private TextView tvState;

        public HotViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivCover = itemView.findViewById(R.id.book_hot_cover);
            tvBookName = itemView.findViewById(R.id.book_hot_title);
            tvResume = itemView.findViewById(R.id.book_hot_resume);
            tvAuthor = itemView.findViewById(R.id.book_hot_author);
            tvFans = itemView.findViewById(R.id.book_hot_fans);
            tvState = itemView.findViewById(R.id.book_hot_state);

        }

        public void bindData(RecomHotItemBean data) {
            if (!StringFormat.isEmpty(data.getCover())) {
                GlideUtils.INSTANCE.loadImage(mContext, data.getCover(), ivCover, GlideUtils.INSTANCE.getBookRadius());
            } else {
                GlideUtils.INSTANCE.loadImage(mContext, R.drawable.book_bg, ivCover, GlideUtils.INSTANCE.getBookRadius());
            }
            if (!StringFormat.isEmpty(data.getBookName())) {
                tvBookName.setText(data.getBookName());
            } else {
                tvBookName.setText("");
            }
            if (!StringFormat.isEmpty(data.getCover())) {
                tvResume.setText(data.getResume());
            } else {
                tvResume.setText("");
            }
            if (!StringFormat.isEmpty(data.getAuthorName())) {
                tvAuthor.setText(data.getAuthorName());
            } else {
                tvAuthor.setText("");
            }
            //   setTextViewColor
            String fansStr = "" + data.getFansNum();
            if (data.getFansNum() >= 100000000) {
                fansStr =  String.format( "%.1f亿",data.getFansNum() * 1f / 100000000f);
            } else if (data.getFansNum() >= 10000) {
                fansStr = String.format( "%.1f万",data.getFansNum() * 1f / 10000f);
            }
            fansStr = String.format("%s人在读", fansStr);
            tvFans.setText(fansStr);
            StringFormat.setTextViewColor(tvFans, mContext.getResources().getColor(R.color.standard_red_main_color_c1), 0, fansStr.length() - 3);
            if (data.getState() == 2) {
                tvState.setText(R.string.finished);
            } else {
                tvState.setText(R.string.updating);
            }
            itemView.setTag(data);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long bookId = ((RecomHotItemBean)v.getTag()).getBookId();
                    ActivityHelper.INSTANCE.gotoBookDetails(mContext, "" + bookId, new BaseData("阅读器末尾热门推荐"), PageNameConstants.READER_END, 13, sourceStats);
                    //添加点击推荐书籍.
                    FunctionStatsApi.readRecommendBookClick(bookId);
//                    FuncPageStatsApi.readEndBookClick(sourceStats);
                    FuncPageStatsApi.readEndBookClick(bookId,sourceStats);
                }
            });
        }
    }
}
