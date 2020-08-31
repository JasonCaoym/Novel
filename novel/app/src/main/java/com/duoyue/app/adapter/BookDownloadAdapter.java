package com.duoyue.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duoyue.app.bean.BookDownloadChapterBean;
import com.duoyue.app.bean.BookDownloadChapterListBean;
import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.ui.activity.BookDownloadActivity;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class BookDownloadAdapter extends BaseExpandableListAdapter {

    private BookDownloadActivity mActivity;

    private List<BookDownloadChapterListBean> mChapterListBeans;

    private List<BookDownloadChapterBean> selectedChapterBeanList;

    /**
     * 已下载的章节ID
     */
    private List<String> downloadChapterIdList;

    private int pageIndex;

    private String bookId;

    public BookDownloadAdapter(BookDownloadActivity activity, String bookId, List<BookDownloadChapterListBean> chapterListBeans) {
        this.mActivity = activity;
        this.bookId = bookId;
        this.mChapterListBeans = chapterListBeans;
        selectedChapterBeanList = new ArrayList<>();
        downloadChapterIdList = new ArrayList<>();

        init();
    }

    private void init() {

        Observable.create(new ObservableOnSubscribe<BookDownloadDBBean>() {
            @Override
            public void subscribe(ObservableEmitter<BookDownloadDBBean> emitter) throws Exception {

                List<BookDownloadDBBean> downloadDBBeans = BookDownloadHelper.getsInstance().queryDownloadCompleteTask(bookId);

                for (BookDownloadDBBean bookDownloadDBBean : downloadDBBeans) {
                    if (BookDownloadManager.getsInstance().isChapterCached(bookId, String.valueOf(bookDownloadDBBean.chapterId))) {
                        downloadChapterIdList.add(String.valueOf(bookDownloadDBBean.chapterId));
                    } else {
                        //数据库数据为已下载，但是本地文件已被清除，将数据库数据删除
                        BookDownloadHelper.getsInstance().deleteDownloadTask(bookDownloadDBBean);
                    }
                }

                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BookDownloadDBBean>() {
                    @Override
                    public void onNext(BookDownloadDBBean downloadDBBean) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        setData(mChapterListBeans, pageIndex);
                    }
                });
    }

    public void setData(List<BookDownloadChapterListBean> chapterListBeans, int pageIndex) {
        if (chapterListBeans != null) {
            this.pageIndex = pageIndex;
            if (pageIndex == 1) {
                selectedChapterBeanList.clear();
                mActivity.updateSelectedList(selectedChapterBeanList);
            }
            mChapterListBeans = chapterListBeans;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return mChapterListBeans.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChapterListBeans.get(groupPosition).getChapters().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mChapterListBeans.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChapterListBeans.get(groupPosition).getChapters().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_book_download_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ivArrow = convertView.findViewById(R.id.iv_arrow);
            groupViewHolder.tvCahpterInterval = convertView.findViewById(R.id.tv_cahpter_interval);
            groupViewHolder.checkbox = convertView.findViewById(R.id.checkbox);
            groupViewHolder.tvDownload = convertView.findViewById(R.id.tv_download);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            groupViewHolder.ivArrow.setImageResource(R.mipmap.bd_arrow_down);
        } else {
            groupViewHolder.ivArrow.setImageResource(R.mipmap.bd_arrow_right);
        }
        String startTitle = "第" + mChapterListBeans.get(groupPosition).getChapters().get(0).getSeqNum() + "章";
        String endTitle = "第" + mChapterListBeans.get(groupPosition).getChapters().get(mChapterListBeans.get(groupPosition).getChapters().size() - 1).getSeqNum() + "章";
        groupViewHolder.tvCahpterInterval.setText(startTitle + "—" + endTitle);

        if (isAllDownload(mChapterListBeans.get(groupPosition).getChapters())) {
            groupViewHolder.checkbox.setVisibility(View.INVISIBLE);
            groupViewHolder.tvDownload.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.checkbox.setVisibility(View.VISIBLE);
            groupViewHolder.tvDownload.setVisibility(View.GONE);
        }

        groupViewHolder.checkbox.setChecked(mChapterListBeans.get(groupPosition).isChecked());
        groupViewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDownloadChapterListBean listBean = mChapterListBeans.get(groupPosition);
                boolean isChecked = !listBean.isChecked();
                if (isChecked) {
                    for (BookDownloadChapterBean chapterBean : listBean.getChapters()) {
                        chapterBean.setChecked(isDownload(chapterBean) ? false : true);
                        if (chapterBean.isChecked() && !selectedChapterBeanList.contains(chapterBean)) {
                            selectedChapterBeanList.add(chapterBean);
                        }
                    }
                } else {
                    for (BookDownloadChapterBean chapterBean : listBean.getChapters()) {
                        chapterBean.setChecked(false);
                        selectedChapterBeanList.remove(chapterBean);
                    }
                }
                listBean.setChecked(isChecked);
                BookDownloadAdapter.this.notifyDataSetChanged();
                mActivity.updateSelectedList(selectedChapterBeanList);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_book_download_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvCahpterName = convertView.findViewById(R.id.tv_cahpter_name);
            childViewHolder.checkbox = convertView.findViewById(R.id.checkbox);
            childViewHolder.tvDownload = convertView.findViewById(R.id.tv_download);
            childViewHolder.layoutRootView = convertView.findViewById(R.id.layout_root_view);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

//        childViewHolder.checkbox.setOnCheckedChangeListener();
        final BookDownloadChapterBean chapterBean = mChapterListBeans.get(groupPosition).getChapters().get(childPosition);

        childViewHolder.tvCahpterName.setText(chapterBean.getTitle());

        if (isDownload(chapterBean)) {
            childViewHolder.checkbox.setVisibility(View.INVISIBLE);
            childViewHolder.tvDownload.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.checkbox.setVisibility(View.VISIBLE);
            childViewHolder.tvDownload.setVisibility(View.GONE);
        }

        childViewHolder.checkbox.setTag(chapterBean);
        childViewHolder.checkbox.setChecked(chapterBean.isChecked());
        childViewHolder.layoutRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChecked = isDownload(chapterBean) ? false : !chapterBean.isChecked();

                if (isChecked) {
                    chapterBean.setChecked(true);
                    selectedChapterBeanList.add(chapterBean);
                    if (isAllChecked(mChapterListBeans.get(groupPosition).getChapters())) {
                        mChapterListBeans.get(groupPosition).setChecked(true);
                    }
                } else {
                    chapterBean.setChecked(false);
                    selectedChapterBeanList.remove(chapterBean);
                    mChapterListBeans.get(groupPosition).setChecked(false);
                }

                BookDownloadAdapter.this.notifyDataSetChanged();
                mActivity.updateSelectedList(selectedChapterBeanList);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 是否所有子item都被选中了
     *
     * @param chapters
     * @return
     */
    private boolean isAllChecked(List<BookDownloadChapterBean> chapters) {
        for (BookDownloadChapterBean bean : chapters) {
            if (!bean.isChecked() && !isDownload(bean)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否所有子item都已下载
     *
     * @param chapters
     * @return
     */
    private boolean isAllDownload(List<BookDownloadChapterBean> chapters) {
        for (BookDownloadChapterBean bean : chapters) {
            if (!isDownload(bean)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 该章节是否已下载
     *
     * @param bean
     * @return
     */
    private boolean isDownload(BookDownloadChapterBean bean) {
        return downloadChapterIdList.contains(String.valueOf(bean.getId()));
    }

    static class GroupViewHolder {
        ImageView ivArrow;
        TextView tvCahpterInterval;
        CheckBox checkbox;
        TextView tvDownload;
    }

    static class ChildViewHolder {
        LinearLayout layoutRootView;
        TextView tvCahpterName;
        CheckBox checkbox;
        TextView tvDownload;
    }

}
