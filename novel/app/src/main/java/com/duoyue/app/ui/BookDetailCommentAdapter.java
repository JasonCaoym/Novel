package com.duoyue.app.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.CommentItemBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class  BookDetailCommentAdapter extends RecyclerView.Adapter<BookDetailCommentViewHolder> {


    private List<CommentItemBean> commentItemBeans;
    private Context mContext;

    public BookDetailCommentAdapter(Context context, List<CommentItemBean> itemBeans) {
        this.mContext = context;
        this.commentItemBeans = itemBeans;
    }



    @NonNull
    @Override
    public BookDetailCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.book_comment_item, viewGroup, false);
        return new BookDetailCommentViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookDetailCommentViewHolder bookDetailCommentViewHolder, int i) {
        final CommentItemBean commentBean = commentItemBeans.get(i);
        if (!TextUtils.isEmpty(commentBean.getAvatar())) {
            GlideUtils.INSTANCE.loadCircleImage(mContext, commentBean.getAvatar(), bookDetailCommentViewHolder.ivLogo, R.mipmap.icon_book_detail_default);
        }
        if (!TextUtils.isEmpty(commentBean.getNick())) {
            bookDetailCommentViewHolder.tvNick.setText(commentBean.getNick());
        }
        //点评为空，没有点评
        if (commentBean.getVote() == 0) {
            bookDetailCommentViewHolder.ratingBar.setVisibility(View.INVISIBLE);
            bookDetailCommentViewHolder.mTv_comment.setVisibility(View.INVISIBLE);
        } else {
            bookDetailCommentViewHolder.ratingBar.setRating(commentBean.getVote());
        }

        if (!TextUtils.isEmpty(commentBean.getContent())) {
            bookDetailCommentViewHolder.tvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bookDetailCommentViewHolder.tvContent.setText(commentBean.getContent());
                    bookDetailCommentViewHolder.moreLayout.setTag(commentBean.getContent());

                    bookDetailCommentViewHolder.tvContent.setVisibility(View.VISIBLE);
                    if (bookDetailCommentViewHolder.tvContent.getLineCount() > 3) {
                        bookDetailCommentViewHolder.tvContent.setMaxLines(3);
                        bookDetailCommentViewHolder.moreLayout.setVisibility(View.VISIBLE);
                        bookDetailCommentViewHolder.moreLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bookDetailCommentViewHolder.tvContent.getLineCount() > 3) {
                                    bookDetailCommentViewHolder.tvContent.setMaxLines(3);
                                    int lineEndIndex = bookDetailCommentViewHolder.tvContent.getLayout().getLineEnd(2);
                                    String text = bookDetailCommentViewHolder.tvContent.getText().subSequence(0, lineEndIndex - 5) + "...";
                                    bookDetailCommentViewHolder.tvContent.setText(text);
                                } else {
                                    bookDetailCommentViewHolder.tvContent.setMaxLines(Integer.MAX_VALUE / 2);
                                    bookDetailCommentViewHolder.tvContent.setText((String) v.getTag());
                                }
                                bookDetailCommentViewHolder.moreLayout.setVisibility(View.GONE);
                            }
                        });
                        int lineEndIndex = bookDetailCommentViewHolder.tvContent.getLayout().getLineEnd(2);
                        if (lineEndIndex < 4) {
                            lineEndIndex = 4;
                        }
                        String text = bookDetailCommentViewHolder.tvContent.getText().subSequence(0, lineEndIndex - 4) + "...";
                        bookDetailCommentViewHolder.tvContent.setText(text);
                    } else {
                        bookDetailCommentViewHolder.moreLayout.setVisibility(View.GONE);
                    }
                }
            }, 100);

        } else {
            bookDetailCommentViewHolder.moreLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (commentItemBeans != null && !commentItemBeans.isEmpty()) {
            return commentItemBeans.size() > 3 ? 3 : commentItemBeans.size();
        }
        return 0;
    }
}
