package com.duoyue.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ToastUtils;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 阅读历史Adapter
 * @author caoym
 * @data 2019/4/16  14:10
 */
public class ReadHistoryAdapter extends BaseAdapter implements View.OnClickListener
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#ReadHistoryAdapter";

    /**
     * 每页书籍数量.
     */
    public static final int PAGE_COUNT = 20;

    /**
     * Activity
     */
    private Activity mActivity;

    /**
     * LayoutInflater
     */
    private LayoutInflater mInflater;

    /**
     * 要展示的数据列表.
     */
    private List<BookRecordBean> mDataList;

    /**
     * 书架书籍Id列表
     */
    private List<String> mBookShelfBookIdList;

    /**
     * 构造方法
     */
    public ReadHistoryAdapter(Activity activity, List<BookRecordBean> dataList)
    {
        mActivity = activity;
        //获取LayoutInflater对象.
        mInflater = (LayoutInflater) BaseContext.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //查询所有的书架书籍信息集合.
        mBookShelfBookIdList = BookShelfPresenter.getBookShelfBookIdList();
        mDataList = dataList;
    }

    /**
     * 移除阅读历史记录.
     * @param bookId
     */
    public void removeData(String bookId)
    {
        if (StringFormat.isEmpty(bookId))
        {
            return;
        }
        boolean isRefresh = false;
        for (int index = mDataList.size() - 1; index >= 0; index--)
        {
            if (bookId.equals(mDataList.get(index).getBookId()))
            {
                mDataList.remove(index);
                isRefresh = true;
            }
        }
        if (isRefresh)
        {
            notifyDataSetChanged();
        }
    }

    /**
     * 清理书架书籍信息列表
     */
    public void clearAllData()
    {
        if (mDataList != null && !mDataList.isEmpty())
        {
            mDataList.clear();
            //刷下ListView.
            notifyDataSetChanged();
        }
    }

    /**
     * 添加阅读历史记录数据.
     * @param dataList
     */
    public void addAllData(List<BookRecordBean> dataList)
    {
        if (dataList == null || dataList.isEmpty())
        {
            return;
        }
        if (mDataList == null)
        {
            mDataList = new ArrayList<>();
        }
        mDataList.addAll(dataList);
        //刷下ListView.
        notifyDataSetChanged();
    }

    /**
     * 更新阅读历史记录
     * @param bookRecordBean
     */
    public void updateReadHistory(BookRecordBean bookRecordBean)
    {
        if (StringFormat.isEmpty(mDataList) || bookRecordBean == null)
        {
            return;
        }
        for (BookRecordBean recordBean : mDataList)
        {
            if (bookRecordBean.getBookId().equalsIgnoreCase(recordBean.bookId))
            {
                //最后阅读时间.
                recordBean.setLastRead(bookRecordBean.getLastRead());
                //阅读章节.
                recordBean.setSeqNum(bookRecordBean.getSeqNum());
                //阅读章节名称.
                recordBean.setChapterTitle(bookRecordBean.getChapterTitle());
                //刷新Adapter.
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 添加书籍到书架, 阅读历史记录操作按钮.
     * @param bookId
     */
    public void addBookShelf(String bookId)
    {
        if (mDataList == null || mDataList.isEmpty() || StringFormat.isEmpty(bookId))
        {
            return;
        }
        if (mBookShelfBookIdList == null)
        {
            mBookShelfBookIdList = new ArrayList<>();
        }
        mBookShelfBookIdList.add(bookId);

        boolean isRefresh = false;
        for (BookRecordBean bookRecordBean : mDataList)
        {
            if (bookId.equals(bookRecordBean.getBookId()))
            {
                isRefresh = true;
                break;
            }
        }
        if (isRefresh)
        {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount()
    {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public BookRecordBean getItem(int position)
    {
        return mDataList != null && mDataList.size() > position ? mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取阅读历史记录数据.
        BookRecordBean bookRecordBean = getItem(position);
        if (bookRecordBean == null)
        {
            return  convertView;
        }
        ViewHolder itemViewHolder;
        if (convertView == null || convertView.getTag() == null)
        {
            itemViewHolder = new ViewHolder();
            //加载Item对应View.
            convertView = mInflater.inflate(R.layout.rh_item_view, null);
            //书籍封面.
            itemViewHolder.coverImageView = convertView.findViewById(R.id.rh_bookcover_imageview);
            //书籍名称.
            itemViewHolder.nameTextView = convertView.findViewById(R.id.rh_bookname_textview);
            //阅读到的章节.
            itemViewHolder.readChapterTextView = convertView.findViewById(R.id.rh_read_chapter_textview);
            //最近阅读时间.
            itemViewHolder.readTimeTextView = convertView.findViewById(R.id.rh_read_time_textview);
            //添加到书架按钮.
            itemViewHolder.addShelfBtn = convertView.findViewById(R.id.rh_add_shelf_btn);
            //设置点击事件.
            itemViewHolder.addShelfBtn.setOnClickListener(this);
            //继续阅读按钮.
            itemViewHolder.readBtn = convertView.findViewById(R.id.rh_read_btn);
            convertView.setTag(itemViewHolder);
        } else
        {
            itemViewHolder = (ViewHolder) convertView.getTag();
        }
        //书籍封面.
        GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), bookRecordBean.getBookCover(), itemViewHolder.coverImageView, GlideUtils.INSTANCE.getBookRadius());
        //书籍名称.
        itemViewHolder.nameTextView.setText(bookRecordBean.getBookName());
        //阅读到的章节.
        itemViewHolder.readChapterTextView.setText(bookRecordBean.getChapterTitle());
        //最近阅读时间.
        itemViewHolder.readTimeTextView.setText(TimeTool.timeToData(bookRecordBean.getLastRead(), TimeTool.DATE_FORMAT_FULL_02));
        //判断是否为书架书籍.
        if (mBookShelfBookIdList != null && mBookShelfBookIdList.contains(bookRecordBean.getBookId()))
        {
            //书架书籍.
            itemViewHolder.addShelfBtn.setVisibility(View.GONE);
            //显示继续阅读按钮.
            itemViewHolder.readBtn.setVisibility(View.VISIBLE);
            itemViewHolder.readBtn.setTag(bookRecordBean);
        } else
        {
            //非书架书籍.
            itemViewHolder.addShelfBtn.setVisibility(View.VISIBLE);
            itemViewHolder.addShelfBtn.setTag(bookRecordBean);
            //隐藏继续阅读按钮.
            itemViewHolder.readBtn.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void onClick(final View view)
    {
        switch (view.getId())
        {
            case R.id.rh_add_shelf_btn:
                //添加到书架.
                Single.fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        try {
                            //调用添加到书架接口.
                            return BookShelfPresenter.addBookShelf((BookRecordBean) view.getTag());
                        } catch (Throwable throwable)
                        {
                            Logger.e(TAG, "onClick: {}, {}", view, throwable);
                            return "添加到书架失败, 稍后再试";
                        }
                    }
                }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        if (ReadHistoryMgr.HTTP_OK.equals(o))
                        {
                            //添加书架成功.
                            ToastUtils.showLimited(R.string.add_shelf_success);
                        } else
                        {
                            //添加书架失败.
                            ToastUtils.showLimited(o);
                        }
                    }
                });
                break;
        }
    }

    class ViewHolder
    {
        /**
         * 书籍封面图.
         */
        public ImageView coverImageView;

        /**
         * 书籍名称.
         */
        public TextView nameTextView;

        /**
         * 阅读到的章节.
         */
        public TextView readChapterTextView;

        /**
         * 最近阅读时间
         */
        public TextView readTimeTextView;

        /**
         * 添加到书架按钮.
         */
        public Button addShelfBtn;

        /**
         * 继续阅读按钮.
         */
        public Button readBtn;
    }
}
