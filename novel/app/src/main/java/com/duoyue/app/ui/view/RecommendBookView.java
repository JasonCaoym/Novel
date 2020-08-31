//package com.duoyue.app.ui.view;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Parcelable;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.duoyue.app.bean.BookCityItemBean;
//import com.duoyue.app.bean.BookCityListBean;
//import com.duoyue.app.bean.RecommendBean;
//import com.duoyue.app.bean.RecommendItemBean;
//import com.duoyue.app.ui.activity.BookMoreActivity;
//import com.duoyue.mianfei.xiaoshuo.R;
//import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
//import com.zydm.base.data.tools.DataUtils;
//import com.zydm.base.ui.activity.BaseActivity;
//import com.zydm.base.ui.item.AbsItemView;
//import com.zydm.base.utils.GlideUtils;
//import com.zydm.base.utils.ViewUtils;
//import com.zzdm.ad.router.BaseData;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 4
// */
//public class RecommendBookView implements View.OnClickListener {
//
//
//    private RecommendBean mRecommendBean;
//    private Activity mActivity;
//
//    private List<Integer> mIds = Arrays.asList(
//            R.id.fixed_horizontal_1,
//            R.id.fixed_horizontal_2,
//            R.id.fixed_horizontal_3,
//            R.id.fixed_horizontal_4
//    );
//
//    public void update(Activity activity, View rootView, RecommendBean recommendBean, View.OnClickListener switchListener) {
//        mActivity = activity;
//        mRecommendBean = recommendBean;
//        if (mRecommendBean != null && mRecommendBean.getBookList() != null && mRecommendBean.getBookList().size() > 0) {
//            rootView.setVisibility(View.VISIBLE);
//            ((TextView)rootView.findViewById(R.id.module_title)).setText("同类热门书");
//            TextView tvSwitch = rootView.findViewById(R.id.module_more);
//            tvSwitch.setText("换一换");
//            ImageView ivSwitch = rootView.findViewById(R.id.module_right_icon);
//            ivSwitch.setImageResource(R.mipmap.icon_switch);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivSwitch.getLayoutParams();
//            params.height = ViewUtils.dp2px(12);
//            params.width = ViewUtils.dp2px(12);
//            ivSwitch.setLayoutParams(params);
//            rootView.findViewById(R.id.show_more).setOnClickListener(switchListener);
//
//            for (int index = 0; index < mIds.size(); index++) {
//                View view = rootView.findViewById(mIds.get(index));
//                view.setOnClickListener(this);
//                view.setTag(index);
//            }
//
//            updateVIew(rootView, recommendBean);
//        } else {
//            rootView.setVisibility(View.GONE);
//        }
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        RecommendItemBean bean = mRecommendBean.getBookList().get((Integer) view.getTag());
//        ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + bean.getBookId(), new BaseData(""));
//    }
//
//    private void updateVIew(View rootView, RecommendBean recommendBean) {
//        for (int index = 0; index < mIds.size(); index++) {
//            View view = rootView.findViewById(mIds.get(index));
//            RecommendItemBean data = DataUtils.getItem(recommendBean.getBookList(), index);
//            if (data != null) {
//                view.setVisibility(View.VISIBLE);
//                GlideUtils.INSTANCE.loadImage(mActivity, data.getCover(), (ImageView) view.findViewById(R.id.book_cover));
//                ((TextView) view.findViewById(R.id.book_name)).setText(data.getBookName());
//                ((TextView) view.findViewById(R.id.book_author)).setText(data.getAuthorName());
//            } else {
//                view.setVisibility(View.GONE);
//            }
//        }
//    }
//}
