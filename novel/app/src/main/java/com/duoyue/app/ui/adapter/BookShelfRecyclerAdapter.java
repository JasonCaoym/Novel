package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.duoyue.app.common.data.response.bookshelf.BookShelfBookInfoResp;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.event.UpdateEvent;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.widget.XFrameLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookShelfRecyclerAdapter extends RecyclerView.Adapter<BookShelfRecyclerAdapter.BookShelfViewHolder> {

    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookShelfRecyclerAdapter";

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 添加书籍BookId
     */
    public static final long ADD_BOOK_BOOKID = -10001;

    /**
     * 点击书籍事件.
     */
    private View.OnClickListener mClickBookListener;

    /**
     * 长按书籍事件.
     */
    private View.OnLongClickListener mLongClickBookListener;

    /**
     * Touch事件.
     */
    private View.OnTouchListener mTouchListener;

    /**
     * 书籍数据列表.
     */
    private List<BookShelfBookInfoResp> mBookInfoList;

    /**
     * 要展示的数据列表(书籍+书城入口).
     */
    private List<BookShelfBookInfoResp> mDataList;

    /**
     * 入口信息对象.
     */
    private BookShelfBookInfoResp mAddBookEntranceInfo;

    /**
     * 是否为编辑状态.
     */
    private boolean isEditMode;

    /**
     * 拉取到的最新章节数信息.
     */
    private JSONObject mPullChapterJSONObj;

    /**
     * 选中的BookId列表
     */
    private List<Long> mSelectedBookIdList;

    private String mPageId = BookExposureMgr.BOOK_SHELF;

    /**
     * 通知栏追更提醒
     */
    private UpdateEvent updateEvent;

    public BookShelfRecyclerAdapter(Context context) {
        this.mContext = context;
        //创建书城入口.
        mAddBookEntranceInfo = new BookShelfBookInfoResp();
        mAddBookEntranceInfo.setBookId(ADD_BOOK_BOOKID);
    }

    @NonNull
    @Override
    public BookShelfViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bs_book_item_view, null);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(lp);
        return new BookShelfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookShelfViewHolder viewHolder, int position) {

        BookShelfBookInfoResp bookShelfInfoResp = mDataList.get(position);
        //保存书籍信息.
        viewHolder.itemView.setTag(bookShelfInfoResp);

        //判断是否为编辑状态.
        if (isEditMode) {
            viewHolder.checkBox.setChecked(mSelectedBookIdList != null && mSelectedBookIdList.contains(bookShelfInfoResp.getBookId()));
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            //非编辑状态, 隐藏CheckBox.
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        //判断是否为收藏书籍入口.
        if (bookShelfInfoResp.getBookId() == ADD_BOOK_BOOKID) {
            //隐藏书籍名称等信息.
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.bookmarkTextView.setVisibility(View.GONE);
            viewHolder.nameTextView.setVisibility(View.GONE);
            viewHolder.statusTextView.setVisibility(View.GONE);
            viewHolder.markingPointImageView.setVisibility(View.GONE);
            //取消Glide加载.
            GlideUtils.INSTANCE.clear(BaseContext.getContext(), viewHolder.coverImageView);
            //设置封面图片.
            viewHolder.coverImageView.setImageResource(R.mipmap.add_book_v2);
            viewHolder.ivTopping.setVisibility(View.GONE);
        } else {
            //显示书籍名称等信息.
            showView(viewHolder.bookmarkTextView);
            showView(viewHolder.nameTextView);
            showView(viewHolder.statusTextView);
            //清空缓存图片.
//                viewHolder.coverImageView.setBackground(null);
            //书籍封面.
            GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), bookShelfInfoResp.getBookCover(), viewHolder.coverImageView, GlideUtils.INSTANCE.getBookRadius(), ViewUtils.dp2px(93), ViewUtils.dp2px(126));
            //书籍名称
            viewHolder.nameTextView.setText(bookShelfInfoResp.getBookName());

            viewHolder.nameTextView.setTextColor(BaseContext.getContext().getResources().getColor(R.color.book_shelf_top_text));

            if (bookShelfInfoResp.getToppingTime() != 0) {
                viewHolder.ivTopping.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivTopping.setVisibility(View.GONE);
            }
            //更新书籍状态.
            updateBookState(viewHolder, bookShelfInfoResp);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    /**
     * 修改书架书籍数据.
     *
     * @param isReset  是否重置
     * @param isRemove 是否为删除书籍
     * @param dataList
     */
    public void updateData(boolean isReset, boolean isRemove, List<BookShelfBookInfoResp> dataList) {
        if (isRemove) {
            //设置为非编辑状态.
            isEditMode = false;
        }
        if (isReset) {
            //重置数据.
            if (mBookInfoList != null) {
                mBookInfoList.clear();
            }
        }
        if (!StringFormat.isEmpty(dataList)) {
            if (mBookInfoList == null) {
                mBookInfoList = new ArrayList<>(dataList);
            } else {
                mBookInfoList.addAll(dataList);
            }
        }
        if (mBookInfoList != null) {
            mDataList = new ArrayList<>(mBookInfoList);
        } else {
            mDataList = new ArrayList<>();
        }
        //判断总书籍量, 如果小于9个, 则需要添加收藏书籍入口
        if (mBookInfoList != null && mBookInfoList.size() > 0 && mBookInfoList.size() < 9) {
            mDataList.add(mAddBookEntranceInfo);
        }
        //获取拉取到的最新书籍章节数信息.
        mPullChapterJSONObj = BookShelfPresenter.getPullChapter();
        //刷下ListView.
        notifyDataSetChanged();
    }

    /**
     * 设置点击书籍事件
     *
     * @param listener
     */
    public void setClickBookListener(View.OnClickListener listener) {
        mClickBookListener = listener;
    }

    /**
     * 设置长按书籍事件
     *
     * @param listener
     */
    public void setLongClickBookListener(View.OnLongClickListener listener) {
        mLongClickBookListener = listener;
    }

    /**
     * 设置OnTouch事件.
     *
     * @param listener
     */
    public void setOnTouchListener(View.OnTouchListener listener) {
        mTouchListener = listener;
    }

    /**
     * 更新编辑状态
     *
     * @param isEditMode
     */
    public void updateEditMode(boolean isEditMode) {
        if (this.isEditMode == isEditMode) {
            return;
        }
        this.isEditMode = isEditMode;
        if (mSelectedBookIdList != null) {
            mSelectedBookIdList.clear();
        }
        //编辑状态下不显示添加书籍按钮
        if (isEditMode) {
            if (mDataList != null && mDataList.size() > 0 && mDataList.get(mDataList.size() - 1).getBookId() == ADD_BOOK_BOOKID) {
                mDataList.remove(mDataList.size() - 1);
            }
        } else if (mBookInfoList != null && mBookInfoList.size() > 0 && mBookInfoList.size() < 9
                && mDataList.get(mDataList.size() - 1).getBookId() != ADD_BOOK_BOOKID) {
            //判断总书籍量, 如果小于9个, 则需要添加收藏书籍入口
            mDataList.add(mAddBookEntranceInfo);
        }
        //刷新Adapter.
        notifyDataSetChanged();
    }

    /**
     * 全选或取消全选.
     */
    public void selectAll(boolean isSelectAll) {
        if (!StringFormat.isEmpty(mDataList)) {
            if (isSelectAll) {
                if (mSelectedBookIdList == null) {
                    mSelectedBookIdList = new ArrayList<>();
                } else {
                    mSelectedBookIdList.clear();
                }
                //选中状态.
                for (BookShelfBookInfoResp bookInfoResp : mDataList) {
                    mSelectedBookIdList.add(bookInfoResp.getBookId());
                }
            } else {
                //取消全选.
                if (mSelectedBookIdList != null) {
                    mSelectedBookIdList.clear();
                }
            }
        }
        //刷新Adapter.
        notifyDataSetChanged();
    }

    /**
     * 获取书籍数量.
     *
     * @return
     */
    public int getBookCount() {
        if (!StringFormat.isEmpty(mDataList)) {
            //需要减去书城入口Item.
            return mDataList.size() - (mDataList.get(mDataList.size() - 1).getBookId() == ADD_BOOK_BOOKID ? 1 : 0);
        }
        return 0;
    }

    /**
     * CheckBox状态变化.
     *
     * @param bookId    书籍Id
     * @param isChecked 是否为选中状态(true:选中;false:非选中)
     */
    public void onCheckedChange(Long bookId, boolean isChecked) {
        if (isChecked) {
            //选中状态.
            if (mSelectedBookIdList == null) {
                mSelectedBookIdList = new ArrayList<>();
            }
            if (!mSelectedBookIdList.contains(bookId)) {
                mSelectedBookIdList.add(bookId);
            }
        } else {
            //取消选中状态.
            if (mSelectedBookIdList != null) {
                mSelectedBookIdList.remove(bookId);
            }
        }
    }

    /**
     * 显示指定View.
     *
     * @param view
     */
    private void showView(View view) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新书籍状态.
     *
     * @param viewHolder
     * @param bookInfoResp
     */
    private void updateBookState(BookShelfViewHolder viewHolder, BookShelfBookInfoResp bookInfoResp) {
        if (viewHolder.bookmarkTextView == null) {
            return;
        }
        //隐藏书签View.
        viewHolder.bookmarkTextView.setVisibility(View.GONE);
        //隐藏标记点.
        viewHolder.markingPointImageView.setVisibility(View.GONE);
        if (bookInfoResp == null) {
            return;
        }
//        FuncPageStatsApi.bookCityBookClick(bookInfoResp.getBookId(), StringFormat.parseInt(bookInfoResp..getId(), 0));
        BookExposureMgr.addOnGlobalLayoutListener(mPageId, "0", viewHolder.itemView, bookInfoResp.getBookId(), bookInfoResp.getBookName());

        //获取连载/完结状态
        StringBuilder statusStr = new StringBuilder();
        //书籍状态(1:更新中;2:已完结;3:断更).
        if (bookInfoResp.getState() == 1 || bookInfoResp.getState() == 3) {
            //未完待续.
            statusStr.append(ViewUtils.getString(R.string.on_progress)).append("•");
        } else {
            //已完结.
            statusStr.append(ViewUtils.getString(R.string.category_finish)).append("•");
        }

        //判断是否为推荐书籍(1:普通书籍;2:推荐书籍)..
        if (bookInfoResp.getType() == 2) {
            //设置为推荐标签.
            viewHolder.bookmarkTextView.setVisibility(View.VISIBLE);
            //设置背景为蓝色.
            viewHolder.bookmarkTextView.setBackgroundResource(R.mipmap.bg_blue);
            //设置文本为推荐.
            viewHolder.getBookmarkTextView.setText(R.string.recommend);
            //设置未阅读状态.
            viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.unread)).toString());
            //显示标记点.
            viewHolder.markingPointImageView.setVisibility(View.VISIBLE);


            return;
        }
        if (bookInfoResp.getLastReadChapter() <= 0) {
            //未阅读.
            viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.unread)).toString());
            //显示标记点.
            viewHolder.markingPointImageView.setVisibility(View.VISIBLE);
        } else if (bookInfoResp.getLastChapter() > getPullChapter(bookInfoResp.getBookId(), bookInfoResp.getLastPushChapter())) {
            //显示更新至多少章节, 设置为更新标签.
            viewHolder.bookmarkTextView.setVisibility(View.VISIBLE);
            //设置背景为橙色.
            viewHolder.bookmarkTextView.setBackgroundResource(R.mipmap.bg_yellow);
            //设置文本为更新.
            viewHolder.getBookmarkTextView.setText(R.string.update);
            //设置状态.
            viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.update_chapter, bookInfoResp.getLastChapter())).toString());
            if (updateEvent == null) {

                updateEvent = new UpdateEvent();
                updateEvent.setBookid(bookInfoResp.getBookId());
                updateEvent.setBookName(bookInfoResp.getBookName());
                updateEvent.setIcon(bookInfoResp.getBookCover());
                updateEvent.setChapter(bookInfoResp.getLastChapter());
                EventBus.getDefault().post(updateEvent);
            }
        } else if (bookInfoResp.getLastChapter() > bookInfoResp.getLastReadChapter()) {
            //多少章节未阅读.
            viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.chapter_unread, bookInfoResp.getLastChapter() - bookInfoResp.getLastReadChapter())).toString());
        } else {
            //书籍状态(1:更新中;2:已完结;3:断更).
            if (bookInfoResp.getState() == 1 || bookInfoResp.getState() == 3) {
                //未完待续.
                viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.to_be_continued)).toString());
            } else {
                //已完结.
                viewHolder.statusTextView.setText(statusStr.append(ViewUtils.getString(R.string.have_finished_reading)).toString());
            }
        }
    }

    /**
     * 根据BookId获取对应拉取章节数.
     *
     * @param bookId
     * @param newChapter 服务端下发最近一次下发的章节数.
     * @return
     */
    private int getPullChapter(long bookId, int newChapter) {
        if (mPullChapterJSONObj == null || bookId <= 0) {
            return newChapter;
        }
        return Math.max(mPullChapterJSONObj.optInt(String.valueOf(bookId), 0), newChapter);
    }

    class BookShelfViewHolder extends RecyclerView.ViewHolder {
        /**
         * ItemView
         */
        public View itemView;

        /**
         * CheckBox
         */
        private CheckBox checkBox;

        /**
         * 书籍封面图.
         */
        public ImageView coverImageView;

        /**
         * 书籍标签(未阅读、X章未读、未完待续、已看完)
         */
        public XFrameLayout bookmarkTextView;

        public TextView getBookmarkTextView;

        /**
         * 书籍名称.
         */
        public TextView nameTextView;

        /**
         * 书籍状态.
         */
        public TextView statusTextView;

        /**
         * 标记点
         */
        public ImageView markingPointImageView;

        /**
         * 置顶标识
         */
        public ImageView ivTopping;

        public BookShelfViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            //选择控件.
            checkBox = itemView.findViewById(R.id.bs_book_checkbox);
            //书籍封面图.
            coverImageView = itemView.findViewById(R.id.bs_book_cover);
            //书籍标签(未阅读、X章未读、未完待续、已看完)
            bookmarkTextView = itemView.findViewById(R.id.bs_bookmark_textview);
            getBookmarkTextView = itemView.findViewById(R.id.bs_bookmark_textview_v2);
            //书籍名称.
            nameTextView = itemView.findViewById(R.id.bs_book_name);
            //书籍状态.
            statusTextView = itemView.findViewById(R.id.bs_book_status);
            //标记点
            markingPointImageView = itemView.findViewById(R.id.bs_marking_point);
            //置顶标识
            ivTopping = itemView.findViewById(R.id.iv_topping);

            //设置点击事件.
            itemView.setOnClickListener(mClickBookListener);
            //设置长按事件.
            itemView.setOnLongClickListener(mLongClickBookListener);
            //设置Touch事件.
            itemView.setOnTouchListener(mTouchListener);
        }
    }

}
