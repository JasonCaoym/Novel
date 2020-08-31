package com.duoyue.app.ui.view;


import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.BookNewBookInfoBean;
import com.duoyue.app.bean.BookNewHeaderBean;
import com.duoyue.app.bean.BookNewListHeaderBean;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.event.BookListHeaderEvent;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.adapter.BookNewListItemDecoration;
import com.duoyue.app.ui.adapter.ItemBookNewHeaderAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.data.dao.ShelfEvent;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.item.AbsItemView;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class BookNewListHeaderView extends AbsItemView<BookNewListHeaderBean> implements BookShelfHelper.ShelfDaoObserver {

    private TextView mTv_user_name, mTv_book_name, mTv_desc, mTv_state, mTv_all_read, mTv_join;

    private ImageView imageView;

    private RecyclerView recyclerView;

    private ItemBookNewHeaderAdapter itemBookNewHeaderAdapter;

    private List<BookNewHeaderBean> bookNewHeaderBeans;

    private BookNewHeaderBean bean;
    private BookNewBookInfoBean bookNewHeaderBean;

    private ImageView view_line;

    private LinearLayoutManager linearLayoutManager;

    private ObjectAnimator objectAnimator;

    @Override
    public void onCreate() {
        setContentView(R.layout.item_book_new_list_header);

        initViews();
        BookShelfHelper.getsInstance().addObserver(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDestroyEvent(BookListHeaderEvent event) {
        BookShelfHelper.getsInstance().removeObserver(this);
        EventBus.getDefault().unregister(this);
    }

    private void initViews() {
        mTv_user_name = mItemView.findViewById(R.id.tv_user_name);
        view_line = mItemView.findViewById(R.id.view_line);
        mTv_book_name = mItemView.findViewById(R.id.tv_book_name);
        mTv_book_name.setOnClickListener(this);
        recyclerView = mItemView.findViewById(R.id.list_book_new);

        mTv_desc = mItemView.findViewById(R.id.tv_desc);
        mTv_desc.setOnClickListener(this);
        mTv_state = mItemView.findViewById(R.id.tv_state);
        mTv_state.setOnClickListener(this);
        mTv_all_read = mItemView.findViewById(R.id.tv_all_read);
        mTv_all_read.setOnClickListener(this);
        mTv_join = mItemView.findViewById(R.id.tv_join);
        imageView = mItemView.findViewById(R.id.iv_icon);
        imageView.setOnClickListener(this);
        bookNewHeaderBeans = new ArrayList<>();
        itemBookNewHeaderAdapter = new ItemBookNewHeaderAdapter(mActivity, bookNewHeaderBeans, this);
        recyclerView.setAdapter(itemBookNewHeaderAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        new StartSnapHelper().attachToRecyclerView(recyclerView);
        BookNewListItemDecoration spaceItemDecoration = new BookNewListItemDecoration();
        recyclerView.addItemDecoration(spaceItemDecoration);

    }

    @Override
    public void onClick(View view) {
        if (Utils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.xfl_book_list:
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                    objectAnimator = null;
                }
                objectAnimator = ObjectAnimator.ofFloat(view_line, "translationX", view_line.getTranslationX(), view.getLeft());
                objectAnimator.setDuration(200).start();
                bean.setSelected(false);
                itemBookNewHeaderAdapter.notifyItemChanged(bookNewHeaderBeans.indexOf(bean));
                initNearBook((int) view.getTag());
                itemBookNewHeaderAdapter.notifyItemChanged((int) view.getTag());
                break;
            case R.id.tv_all_read:
                com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper.INSTANCE.gotoReadForResult(mActivity, String.valueOf((int) view.getTag()), new BaseData("发现-附近书友进入阅读器"), PageNameConstants.NEARREAD, PageNameConstants.NEAR_READ_BOOK, 999);
                FuncPageStatsApi.nearGoReadClick(bookNewHeaderBean.getBookId());
                break;
            case R.id.tv_join:
                addToBookshelf();
                FuncPageStatsApi.nearAddshelfClick(bookNewHeaderBean.getBookId());
                break;
            case R.id.iv_icon:
            case R.id.tv_book_name:
            case R.id.tv_desc:
            case R.id.tv_state:
                ActivityHelper.INSTANCE.gotoBookDetails(mActivity, "" + bookNewHeaderBean.getBookId(), new BaseData(""),
                        PageNameConstants.NEARREAD, 24, PageNameConstants.NEAR_READ_BOOK);
                FuncPageStatsApi.nearBookClick(bookNewHeaderBean.getBookId());
                break;
        }


    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            bookNewHeaderBeans.clear();
            bookNewHeaderBeans.addAll(mItemData.getList());
            initNearBook(0);
            itemBookNewHeaderAdapter.notifyItemRangeInserted(0, bookNewHeaderBeans.size());
            recyclerView.smoothScrollToPosition(0);
            ObjectAnimator.ofFloat(view_line, "translationX", view_line.getTranslationX(), ViewUtils.dp2px(16)).setDuration(200).start();
        }
    }

    private void addToBookshelf() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return BookShelfPresenter.addFindBookShelf(bookNewHeaderBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(s)) {
                    //添加书架成功.
                    StatisHelper.onEvent().subscription(bookNewHeaderBean.getName(), "发现页加入书架");
                    ToastUtils.showLimited(R.string.add_shelf_success);
                    disableAddButton(true);
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(s);
                }
            }
        });
    }

    void initNearBook(int index) {
        bean = mItemData.getList().get(index);
        if (bean == null) return;
        bean.setSelected(true);
        bookNewHeaderBean = bean.getBookInfo();
        if (bookNewHeaderBean == null) return;
        bookNewHeaderBeans.set(index, bean);
        mTv_all_read.setTag(bookNewHeaderBean.getBookId());
        if (TextUtils.isEmpty(bean.getNickName())) {
            mTv_book_name.setVisibility(View.GONE);
        } else {
            mTv_book_name.setVisibility(View.VISIBLE);
            mTv_book_name.setText(bookNewHeaderBean.getName());
        }
        mTv_desc.setText(bookNewHeaderBean.getResume());
        mTv_user_name.setText(TextUtils.isEmpty(mItemData.getList().get(index).getNickName()) ? "" : mItemData.getList().get(index).getNickName() + " 正在读");
        GlideUtils.INSTANCE.loadImage(mActivity, bookNewHeaderBean.getCover(), imageView);
        String state = "";
        switch (bookNewHeaderBean.getState()) {
            case 1:
                state = "连载中";
                break;
            case 2:
                state = "已完结";
                break;
            case 3:
                state = "断更";
                break;
        }
        state += " · " + String.format("%s万字", bookNewHeaderBean.getWordCount() / 10000) + " · " + bookNewHeaderBean.getCatName();
        mTv_state.setText(state);

        disableAddButton(BookShelfPresenter.isAdded("" + bookNewHeaderBean.getBookId()));

        FuncPageStatsApi.nearReadBookExp(bookNewHeaderBean.getBookId());
    }

    private void disableAddButton(boolean isSave) {
        mTv_join.setBackground(ContextCompat.getDrawable(mActivity, isSave ? R.drawable.btn_sign_in_16 : R.drawable.btn_sign_in_15));
        mTv_join.setCompoundDrawablesWithIntrinsicBounds(isSave ? ContextCompat.getDrawable(mActivity, R.mipmap.bg_book_list_join_shape) : ContextCompat.getDrawable(mActivity, R.mipmap.bg_book_list_join), null, null, null);
        mTv_join.setText(isSave ? R.string.already_in_shelf_x : R.string.btn_add_shelf);
        mTv_join.setTextColor(isSave ? ContextCompat.getColor(mActivity, R.color.color_b2b2b2) : ContextCompat.getColor(mActivity, R.color.color_FE8B13));
        mTv_join.setOnClickListener(isSave ? null : this);
    }

    @Override
    public void onShelfChange(@NonNull ShelfEvent event) {
        if (event.mType == ShelfEvent.TYPE_ADD || event.mType == ShelfEvent.TYPE_REMOVE) {
            //获取书架书籍信息.
            BookShelfBean bookShelfBean = !StringFormat.isEmpty(event.mChangeList) ? event.mChangeList.get(0) : null;
            if (bookNewHeaderBean == null) return;
            if (bookShelfBean != null && !StringUtils.isEmpty(bookShelfBean.getBookId()) && bookShelfBean.getBookId().equals(String.valueOf(bookNewHeaderBean.getBookId()))) {
                //修改当前按钮为已添加书架状态.
                disableAddButton(ShelfEvent.TYPE_ADD == event.mType);
            }
        }
    }

}
