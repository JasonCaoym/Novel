package com.duoyue.app.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class FixedOne2FourColumnAdapter extends RecyclerView.Adapter<FixedOne2FourColumnViewHolder> {






    private Context mContext;

    public FixedOne2FourColumnAdapter(Context context){
        this.mContext = context;
    }



    @NonNull
    @Override
    public FixedOne2FourColumnViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LinearLayout.inflate(mContext, R.layout.item_column_title,viewGroup);
        return new FixedOne2FourColumnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedOne2FourColumnViewHolder fixedOne2FourColumnViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
