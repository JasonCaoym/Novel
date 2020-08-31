package com.duoyue.app.ui.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.duoyue.app.bean.BookBagCompleteBean;
import com.duoyue.app.bean.BookNewUserBagStatusesBean;
import com.duoyue.app.bean.BookPeopleNewBean;
import com.duoyue.app.presenter.BookNewPersonGiftBagPresenter;
import com.duoyue.app.ui.adapter.BookNewPersonGiftBagAdapter;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.ui.item.AbsItemView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookNewPersonGiftBagView extends AbsItemView<BookPeopleNewBean> implements BookNewPersonGiftBagPresenter.PageView {

    private RecyclerView recyclerView;
    private XLinearLayout xLinearLayout;

    private BookNewPersonGiftBagAdapter bookNewPersonGiftBagAdapter;

    private List<BookNewUserBagStatusesBean> bookNewUserBagStatusesBeans;

    private BookNewPersonGiftBagPresenter bookNewPersonGiftBagPresenter;

    private int mIndex = -1;

    private View mView_line;

    @Override
    public void onCreate() {
        setContentView(R.layout.book_new_person_gift_bag);
        recyclerView = mItemView.findViewById(R.id.rv_people_new);
        xLinearLayout = mItemView.findViewById(R.id.xll_new_people);
        mView_line = mItemView.findViewById(R.id.fix_row_6);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        new StartSnapHelper().attachToRecyclerView(recyclerView);
        bookNewUserBagStatusesBeans = new ArrayList<>();
        bookNewPersonGiftBagPresenter = new BookNewPersonGiftBagPresenter(this);
        bookNewPersonGiftBagAdapter = new BookNewPersonGiftBagAdapter(mActivity, bookNewUserBagStatusesBeans, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            if (mItemData.getNewUserBagStatuses() != null) {
                xLinearLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mView_line.setVisibility(View.VISIBLE);

                bookNewUserBagStatusesBeans.clear();
                bookNewUserBagStatusesBeans.addAll(mItemData.getNewUserBagStatuses());
                recyclerView.setAdapter(bookNewPersonGiftBagAdapter);
                recyclerView.scrollToPosition(initTodayIndex());
            } else {
                xLinearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                mView_line.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(@NotNull View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.text_bg:
                mIndex = (int) view.getTag();
                bookNewPersonGiftBagPresenter.loadData();
                FuncPageStatsApi.bookCityRedPageClick(mIndex + 1);
                break;
        }
    }

    int initTodayIndex() {
        int index = -1;
        for (BookNewUserBagStatusesBean book : bookNewUserBagStatusesBeans) {
            if (book.getIsToday() == 1) {
                index = bookNewUserBagStatusesBeans.indexOf(book);
                break;
            }
        }
        return index;
    }


    @Override
    public void onLoadData(BookBagCompleteBean bookBagCompleteBean) {
        if (mIndex != -1) {
            bookNewUserBagStatusesBeans.get(mIndex).setStatus(3);
            bookNewPersonGiftBagAdapter.notifyItemChanged(mIndex);
        }
    }

    @Override
    public void onLoadErrorData() {

    }
}
