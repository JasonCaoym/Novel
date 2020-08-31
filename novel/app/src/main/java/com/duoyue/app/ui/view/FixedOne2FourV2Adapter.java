//package com.duoyue.app.ui.view;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.PagerSnapHelper;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.duoyue.app.bean.BookCityModuleBean;
//import com.duoyue.mianfei.xiaoshuo.R;
//
//public class FixedOne2FourV2Adapter extends RecyclerView.Adapter<FixedOne2FourV2ViewHolder> {
//
//
//    private Context mContext;
//    private BookCityModuleBean bookCityModuleBean;
//
//    private View.OnClickListener onClickListener;
//
//    public FixedOne2FourV2Adapter(Context context, View.OnClickListener clickListener) {
//        this.mContext = context;
//        this.onClickListener = clickListener;
//    }
//
//    public void setData(BookCityModuleBean bookCityModuleBean) {
//        this.bookCityModuleBean = bookCityModuleBean;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public FixedOne2FourV2ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.book_ciry_v2, viewGroup, false);
//        return new FixedOne2FourV2ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FixedOne2FourV2ViewHolder fixedOne2FourV2ViewHolder, int i) {
//
////        if (i == 0) {
////            FixedOne2ColumnAdapter fixedOne2ColumnAdapter = new FixedOne2ColumnAdapter(bookCityModuleBean.getChildColumns());
////            fixedOne2FourV2ViewHolder.mRv_more.setAdapter(fixedOne2ColumnAdapter);
////        } else {
////            FixedOne2ItemColumnAdapter fixedOne2ItemColumnAdapter = new FixedOne2ItemColumnAdapter(bookCityModuleBean.getChildColumns(),mContext,onClickListener,bookCityModuleBean.getType(),bookCityModuleBean.getTag());
////            fixedOne2FourV2ViewHolder.recyclerView.setAdapter(fixedOne2ItemColumnAdapter);
////
////        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return 2;
//    }
//}
