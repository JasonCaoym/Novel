package com.duoyue.app.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.duoyue.app.ui.adapter.CatalogueAdapter;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.ui.catalogue.CatalogueActivity;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;

import java.util.ArrayList;

/**
 * 目录章节
 */
public class DirectoryDialog extends DialogFragment {


    private ArrayList<CatalogueActivity.GroupItem> mGroups;

    public void setOnItemClickListener(CatalogueAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    private CatalogueAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }


    public void setData(ArrayList<CatalogueActivity.GroupItem> mGroups) {
        this.mGroups = mGroups;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Utils.dp2px(getContext(), 315);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.directory_section, container);
        final RecyclerView recycleView = view.findViewById(R.id.recycle_view);
        final CatalogueAdapter adapter = new CatalogueAdapter(getContext(), mGroups);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recycleView.addItemDecoration(new ListLineDecoration());
        recycleView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CatalogueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                for (int j = 0; j < mGroups.size(); j++) {
                    if (j == postion) {
                        mGroups.get(j).setReadGroup(true);
                    } else {
                        mGroups.get(j).setReadGroup(false);
                    }
                }
                adapter.notifyDataSetChanged();
                mOnItemClickListener.onItemClick(view, postion);
                dismiss();
            }
        });

        //解决recycleView分割线超出圆角部分的问题
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    View viewByPosition = linearManager.findViewByPosition(lastItemPosition);
                    int height = viewByPosition.getTop();
                    if (height < Utils.dp2px(getContext(),364) && height > Utils.dp2px(getContext(),354)) {
                        View viewByPosition1 = linearManager.findViewByPosition(lastItemPosition);
                        View view1 = viewByPosition1.findViewById(R.id.view_devider);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.dp2px(getContext(),1));
                        params.leftMargin = Utils.dp2px(getContext(),10);
                        params.rightMargin = Utils.dp2px(getContext(),10);
                        view1.setLayoutParams(params);
                    }else {
                        View viewByPosition1 = linearManager.findViewByPosition(lastItemPosition);
                        View view1 = viewByPosition1.findViewById(R.id.view_devider);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.dp2px(getContext(),1));
                        params.leftMargin = Utils.dp2px(getContext(),0);
                        params.rightMargin = Utils.dp2px(getContext(),0);
                        view1.setLayoutParams(params);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
