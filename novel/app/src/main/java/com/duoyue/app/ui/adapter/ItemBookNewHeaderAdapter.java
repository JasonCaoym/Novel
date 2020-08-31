package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.duoyue.app.bean.BookNewHeaderBean;
import com.duoyue.lib.base.widget.XFrameLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.ViewUtils;

import java.text.DecimalFormat;
import java.util.List;

public class ItemBookNewHeaderAdapter extends RecyclerView.Adapter<ItemBookNewHeaderAdapter.ItemBookNewHeaderViewHolder> {

    private Context mContext;
    private List<BookNewHeaderBean> list;

    private final DecimalFormat decimalFormat = new DecimalFormat("##0.0");

    private View.OnClickListener clickListener;

    public ItemBookNewHeaderAdapter(Context context, List<BookNewHeaderBean> bookNewHeaderBeans, View.OnClickListener onClickListener) {
        this.mContext = context;
        this.clickListener = onClickListener;
        this.list = bookNewHeaderBeans;
    }

    @NonNull
    @Override
    public ItemBookNewHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_new_header, viewGroup, false);
        return new ItemBookNewHeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemBookNewHeaderViewHolder itemBookNewHeaderViewHolder, int i) {

        itemBookNewHeaderViewHolder.xFrameLayout.setTag(i);
        itemBookNewHeaderViewHolder.xFrameLayout.setOnClickListener(this.clickListener);
        if (list.get(i).isSelected()) {
            itemBookNewHeaderViewHolder.mRl_list.setBackgroundResource(R.drawable.bg_quan);
            itemBookNewHeaderViewHolder.textView.setBackgroundResource(R.drawable.bg_v2_fe8b13);
            itemBookNewHeaderViewHolder.textView.setTextColor(Color.WHITE);
            itemBookNewHeaderViewHolder.mRl_list.setPadding(ViewUtils.dp2px(3), ViewUtils.dp2px(3), ViewUtils.dp2px(3), ViewUtils.dp2px(3));
        } else {
            itemBookNewHeaderViewHolder.mRl_list.setBackgroundResource(R.drawable.bg_quan_x);
            itemBookNewHeaderViewHolder.textView.setBackgroundResource(R.drawable.bg_v2_ffffff);
            itemBookNewHeaderViewHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.color_FE8B13));
            itemBookNewHeaderViewHolder.mRl_list.setPadding(ViewUtils.dp2px(1), ViewUtils.dp2px(1), ViewUtils.dp2px(1), ViewUtils.dp2px(1));
        }

        if (list.get(i).getDistance() < 1000) {
            itemBookNewHeaderViewHolder.textView.setText((int)list.get(i).getDistance() + "m");
        } else {
            itemBookNewHeaderViewHolder.textView.setText(decimalFormat.format((list.get(i).getDistance() / 1000)) + "km");
        }

        if (!TextUtils.isEmpty(list.get(i).getCover())) {
            Glide.with(mContext)
                    .load(list.get(i).getCover())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.mine_head_icon))
                    .into(itemBookNewHeaderViewHolder.imageView);
        } else {
            itemBookNewHeaderViewHolder.imageView.setImageResource(R.mipmap.mine_head_icon);
        }

    }

    @Override
    public int getItemCount() {
        if (list != null && !list.isEmpty()) return list.size();
        return 0;
    }

    static class ItemBookNewHeaderViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public XFrameLayout xFrameLayout;

        public XRelativeLayout mRl_list;


        public ItemBookNewHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_distance);
            imageView = itemView.findViewById(R.id.iv_icon);
            xFrameLayout = itemView.findViewById(R.id.xfl_book_list);
            mRl_list = itemView.findViewById(R.id.xll_new_book_list);
        }
    }
}
