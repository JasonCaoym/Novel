package com.duoyue.app.ui.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookMoreItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.StringUtils;
import com.zzdm.ad.router.BaseData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BookMoreView extends AbsItemView<BookMoreItemBean> {

    @Override
    public void onCreate() {
        setContentView(R.layout.book_item_view);
        mItemView.setOnClickListener(this);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        GlideUtils.INSTANCE.loadImage(mActivity, mItemData.getCover(), ((ImageView) mItemView.findViewById(R.id.book_cover)));
        ((TextView) mItemView.findViewById(R.id.book_resume)).setText(mItemData.getResume());
        ((TextView) mItemView.findViewById(R.id.book_author)).setText(mItemData.getAuthorName());
        ((TextView) mItemView.findViewById(R.id.book_name)).setText(mItemData.getName());

        ((TextView)mItemView.findViewById(R.id.book_count)).setText(String.format("%s万字", mItemData.getWordCount() / 10000));

        String categoryName = mItemData.getCategoryName();
        if (StringUtils.isBlank(categoryName)) {
            mItemView.findViewById(R.id.book_category).setVisibility( View.GONE);
        } else {
            mItemView.findViewById(R.id.book_category).setVisibility(View.VISIBLE);
            ((TextView) mItemView.findViewById(R.id.book_category)).setText(categoryName);
        }
    }

    @Nullable
    @Override
    public void onClick(@NotNull View view) {
        super.onClick(view);

//        ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + mItemData.getId(), new BaseData(""), 2, "");
    }
}
