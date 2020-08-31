package com.duoyue.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duoyue.app.bean.CommentItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class BookCommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommentItemBean> dataList = new ArrayList<>();

    public BookCommentAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<CommentItemBean> list) {
        if (list != null) {
            dataList.clear();
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < dataList.size()) {
            return dataList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_comment_item, parent, false);
            viewHolder = new CommentViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommentViewHolder) convertView.getTag();
        }
        viewHolder.bindData(mContext, dataList.get(position));
        return convertView;
    }

    public static class CommentViewHolder {

        public ImageView ivLogo;
        public TextView tvNick;
        public RatingBar ratingBar;
        public TextView tvContent;
        public View moreLayout;
        public RelativeLayout moreParentView;
        public TextView mTv_comment;

        public CommentViewHolder(View rootView) {
            ivLogo = rootView.findViewById(R.id.book_comment_item_logo);
            tvNick = rootView.findViewById(R.id.book_comment_item_nick);
            ratingBar = rootView.findViewById(R.id.book_comment_item_ratingbar);
            mTv_comment = rootView.findViewById(R.id.tv_comment);
            tvContent = rootView.findViewById(R.id.book_comment_item_comment);
            moreParentView = rootView.findViewById(R.id.booke_detail_resume_layout);
            moreLayout = rootView.findViewById(R.id.book_detail_open);
            moreLayout.setVisibility(View.VISIBLE);


            moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvContent.getLineCount() > 3) {
                        tvContent.setMaxLines(3);
                        int lineEndIndex = tvContent.getLayout().getLineEnd(2);
                        String text = tvContent.getText().subSequence(0, lineEndIndex - 5) + "...";
                        tvContent.setText(text);
                    } else {
                        tvContent.setMaxLines(Integer.MAX_VALUE / 2);
                        tvContent.setText((String) v.getTag());
                    }
                    moreLayout.setVisibility(View.GONE);
                }
            });
        }

        public void bindData(Context context, final CommentItemBean commentBean) {
            if (!TextUtils.isEmpty(commentBean.getAvatar())) {
                GlideUtils.INSTANCE.loadCircleImage(context, commentBean.getAvatar(), ivLogo, R.mipmap.icon_book_detail_default);
            }
            if (!TextUtils.isEmpty(commentBean.getNick())) {
                tvNick.setText(commentBean.getNick());
            }
            //点评为空，没有点评
            if (commentBean.getVote() == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
                mTv_comment.setVisibility(View.INVISIBLE);
            } else {
                ratingBar.setRating(commentBean.getVote());
            }


            if (!TextUtils.isEmpty(commentBean.getContent())) {
                moreLayout.setTag(commentBean.getContent());
                tvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//
                        tvContent.setText(commentBean.getContent());
                        tvContent.setVisibility(View.VISIBLE);
                        if (tvContent.getLineCount() > 3) {
                            int lines = tvContent.getLineCount();
                            tvContent.setMaxLines(3);
                            moreLayout.setVisibility(View.VISIBLE);

                            int lineEndIndex = tvContent.getLayout().getLineEnd(2);
                            if (lineEndIndex < 4) {
                                lineEndIndex = 4;
                            }
                            String text = tvContent.getText().subSequence(0, lineEndIndex - 4) + "...";
                            tvContent.setText(text);
                            tvContent.setMaxLines(lines);
                        } else {
                            moreLayout.setVisibility(View.GONE);
                        }
                    }
                }, 100);
            } else {
                moreLayout.setVisibility(View.GONE);
            }
        }

    }
}
