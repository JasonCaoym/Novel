package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.FeedConfigItemBean;
import com.duoyue.app.common.data.response.bookshelf.BookShelfBookInfoResp;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.widget.XFrameLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedConfigRecyclerAdapter extends RecyclerView.Adapter<FeedConfigRecyclerAdapter.FeedConfigViewHolder> {

    /**
     * 日志Tag
     */
    private static final String TAG = "App#FeedConfigRecyclerAdapter";

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 点击事件
     */
    private OnItemClickListener mListener;

    private List<FeedConfigItemBean> mConfigItemBeanList;

    private int selectedPosition = -1;


    public FeedConfigRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mConfigItemBeanList = new ArrayList<>();
    }

    @NonNull
    @Override
    public FeedConfigViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_config_item_view, null);
        return new FeedConfigViewHolder(view);
    }

    public void updateData(List<FeedConfigItemBean> configItemBeanList){
        if(configItemBeanList != null){
            mConfigItemBeanList.clear();
            mConfigItemBeanList.addAll(configItemBeanList);
            notifyDataSetChanged();
        }
    }

    /**
     * 选中item
     * @param position
     */
    public void selectedItem(int position){

        if(selectedPosition != -1){
            mConfigItemBeanList.get(selectedPosition).setSelected(false);
        }
        mConfigItemBeanList.get(position).setSelected(true);
        selectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 获取选中的bean
     */
    public FeedConfigItemBean getSelectedBean(){
        if(selectedPosition != -1){
            return mConfigItemBeanList.get(selectedPosition);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull FeedConfigViewHolder viewHolder, int position) {
        FeedConfigItemBean itemBean = mConfigItemBeanList.get(position);
        viewHolder.tvConfig.setText(itemBean.getContent());
        if(itemBean.isSelected()){
            viewHolder.tvConfig.setBackground(ContextCompat.getDrawable(mContext, R.drawable.question_error_bg_checked));
            viewHolder.tvConfig.setTextColor(ContextCompat.getColor(mContext, R.color.color_FE8B13));
        }else {
            viewHolder.tvConfig.setBackground(ContextCompat.getDrawable(mContext, R.drawable.question_error_bg_uncheck));
            viewHolder.tvConfig.setTextColor(ContextCompat.getColor(mContext, R.color.color_1b1b1b));
        }
    }

    @Override
    public int getItemCount() {
        return mConfigItemBeanList != null ? mConfigItemBeanList.size() : 0;
    }

    /**
     * 设置点击书籍事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    class FeedConfigViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * ItemView
         */
        public View itemView;

        public TextView tvConfig;

        public FeedConfigViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvConfig = itemView.findViewById(R.id.tv_config);

            //设置点击事件.
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }

}
