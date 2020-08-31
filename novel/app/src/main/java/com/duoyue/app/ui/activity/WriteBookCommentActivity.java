package com.duoyue.app.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.CommentListBean;
import com.duoyue.app.bean.RecommendBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.presenter.BookCommentPresenter;
import com.duoyue.app.ui.view.BookDetailsView;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.RouterPath;
import org.jetbrains.annotations.Nullable;

public class WriteBookCommentActivity extends BaseActivity {

    public static final String BOOK_ID = "bookId";

    private static final int FIFTEEN = 15;

    private long bookId;

    private RatingBar mRatingBar;

    private EditText editText;

    private TextView textView;

    private SimpleDialog simpleDialog;

    private int mRating = 5;

    private BookCommentPresenter bookCommentPresenter;

    private View view;
    private String source;
    private String prevPageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_book_comment);

        initViews();
        initData();
    }


    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            mRating = (int) ratingBar.getRating();
        }
    };

    void initViews() {
        setToolBarLayout(ViewUtils.getString(R.string.comment_the_book));
        mRatingBar = findViewById(R.id.book_comment_item_ratingbar);
        view = findViewById(R.id.view_loading);
        mRatingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
        editText = findViewById(R.id.et_comment);
        editText.addTextChangedListener(textWatcher);
        textView = findViewById(R.id.toolbar_right_tv);
        textView.setText(ViewUtils.getString(R.string.send_comment));

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_comment_start);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRatingBar.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        lp.height = bmp.getHeight();
        mRatingBar.setLayoutParams(lp);
        bmp.recycle();


        textView.setOnClickListener(onClickListener);
    }

    void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getStringExtra(RouterPath.INSTANCE.KEY_SOURCE);
            prevPageId = intent.getStringExtra(RouterPath.INSTANCE.KEY_PARENT_ID);
            bookId = intent.getLongExtra(BOOK_ID, -1);
            bookCommentPresenter = new BookCommentPresenter(detailsView);
        }
    }

    public String getCurrPageId() {
        return PageNameConstants.SEND_COMMENT;
    }

    private BookDetailsView detailsView = new BookDetailsView() {
        @Override
        public void showLoading() {

        }

        @Override
        public void dismissLoading() {

        }

        @Override
        public void showEmpty() {

        }

        @Override
        public void showNetworkError() {
            ToastUtils.showLimited("评论出错，请稍等再试!");
        }

        @Override
        public Activity getActivity() {
            return null;
        }

        @Override
        public void showPage(BookDetailBean bookDetailBean) {

        }

        @Override
        public void showAdPage(Object adObject) {

        }

        @Override
        public void showRecommend(RecommendBean recommendBean) {

        }

        @Override
        public void showComment(CommentListBean commentList) {

        }

        @Override
        public void loadFirstChapterData(String data, String title) {

        }

        @Override
        public void loadOtherReadData(RecommendBean recommendBean) {

        }

        @Override
        public void loadSaveComment() {
            view.setVisibility(View.GONE);
            ToastUtils.showLimited("评论成功");
            setResult(1003);
            finish();
        }

        @Override
        public void showDownloadDialog(ChapterDownloadOptionResp resp) {

        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
//
                textView.setTextColor(ContextCompat.getColor(WriteBookCommentActivity.this, R.color.text_color_898989));
//                textView.setTag(false);
            } else {
                textView.setTextColor(ContextCompat.getColor(WriteBookCommentActivity.this, R.color.black));
//                textView.setTag(true);
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toolbar_right_tv:
                    if (editText.getText().toString().trim().length() > FIFTEEN) {
                        if (editText.getText().toString().trim().length() > 500) {
                            ToastUtils.showLimited("书评不可多于500字哦");
                        } else {
                            view.setVisibility(View.VISIBLE);
                            bookCommentPresenter.loadComment(editText.getText().toString().trim(), bookId, mRating);
                            //点击发送书评按钮.
                            FunctionStatsApi.bdSendBookReviewClick(bookId);
                            FuncPageStatsApi.bookDetailSendComment(bookId,prevPageId, source);
                        }
                    } else {
                        if (editText.getText().toString().trim().length() == 0) {
                            ToastUtils.showLimited("评论内容不能为空");
                        } else {
                            if (simpleDialog != null) simpleDialog = null;

                            simpleDialog = new SimpleDialog.Builder(WriteBookCommentActivity.this).
                                    setCanceledOnTouchOutside(false).
                                    setTitle("为了鼓励有益的分享，书评字数需要多于15字才能发布").
                                    setPositiveButton(R.string.write_again, clickListener).create();
                            simpleDialog.show();
                        }
                    }
                    break;
            }
        }
    };

    private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleDialog != null) {
            simpleDialog.dismiss();
            simpleDialog = null;
        }

    }


    @Override
    public void onBackPressed() {
        initDialog();
    }

    void initDialog() {
        if (simpleDialog != null) simpleDialog = null;

        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            finish();
        } else {
            simpleDialog = new SimpleDialog.Builder(this).setCanceledOnTouchOutside(false).setTitle("返回将放弃写书评哦").setPositiveButton("继续写", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).create();
            //显示Dialog.
            simpleDialog.show();
        }

    }
}
