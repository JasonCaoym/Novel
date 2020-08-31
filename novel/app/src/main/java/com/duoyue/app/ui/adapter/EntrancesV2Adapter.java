package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookCityMenuItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class EntrancesV2Adapter extends RecyclerView.Adapter<EntrancesV2ViewHolder> {


    private Context mContext;
    private List<BookCityMenuItemBean> menuItemBeanList;

    private View.OnClickListener clickListener;

    public EntrancesV2Adapter(Context context, List<BookCityMenuItemBean> list, View.OnClickListener onClickListener) {
        this.mContext = context;
        this.menuItemBeanList = list;
        this.clickListener = onClickListener;
    }

    @NonNull
    @Override
    public EntrancesV2ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_icon, viewGroup, false);
        return new EntrancesV2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrancesV2ViewHolder entrancesV2ViewHolder, int i) {
        entrancesV2ViewHolder.textView.setText(menuItemBeanList.get(i).getShowName());
        GlideUtils.INSTANCE.loadImage(mContext, menuItemBeanList.get(i).getPic(), entrancesV2ViewHolder.imageView);
        entrancesV2ViewHolder.xRelativeLayout.setOnClickListener(clickListener);
        entrancesV2ViewHolder.xRelativeLayout.setTag(menuItemBeanList.get(i));
    }

    @Override
    public int getItemCount() {
        if (menuItemBeanList != null && !menuItemBeanList.isEmpty()) return menuItemBeanList.size();
        return 0;
    }
}
