package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.duoyue.app.adapter.BookCommentAdapter;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.CommentItemBean;
import com.duoyue.app.bean.CommentListBean;
import com.duoyue.app.bean.RecommendBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.presenter.BookCommentPresenter;
import com.duoyue.app.ui.view.BookDetailsView;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zzdm.ad.router.RouterPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookCommentActivity extends BaseActivity implements BookDetailsView {

    private BookCommentAdapter commentAdapter;
    private ListView commentListView;
    private BookDetailActivity.CommentPresenter presenter;
    protected PromptLayoutHelper mPromptLayoutHelper;
    protected PullToRefreshLayout mPullLayout;

    private List<CommentItemBean> commentList = new ArrayList<>();

    private long bookId;
    private int pageIndex = 1;

    private int mResult = -1;

    private UserManager userManager;
    private String source;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getBookId();
        initViews();
        presenter = new BookCommentPresenter(this);
        presenter.loadData(bookId, pageIndex);
    }

    private void getBookId() {
        source = getIntent().getStringExtra(RouterPath.INSTANCE.KEY_SOURCE);
        bookId = getIntent().getLongExtra(RouterPath.INSTANCE.KEY_BOOK_ID, 0);
        userManager = UserManager.getInstance();
    }

    private void initViews() {
        initPullLayout();
        setToolBarLayout(R.string.book_review_more);
        findViewById(R.id.iv_write_comments_icon).setOnClickListener(this);
        commentListView = findViewById(R.id.list_view);
        commentAdapter = new BookCommentAdapter(this);
        commentListView.setAdapter(commentAdapter);
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.COMMENT_LIST;
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_write_comments_icon:

                //进入评论页面.
                if (userManager.getUserInfo().type == 1) {
                    Intent intent1 = new Intent(BookCommentActivity.this, LoginActivity.class);
                    startActivityForResult(intent1, 1009);
                } else {
                    Intent intent = new Intent(this, WriteBookCommentActivity.class);
                    intent.putExtra(RouterPath.INSTANCE.KEY_BOOK_ID, bookId);
                    intent.putExtra(RouterPath.INSTANCE.KEY_SOURCE, source);
                    intent.putExtra(RouterPath.INSTANCE.KEY_PARENT_ID, PageNameConstants.COMMENT_LIST);
                    startActivityForResult(intent, 1005);
                    FunctionStatsApi.bdWriteBookReview("2", bookId);
                    FuncPageStatsApi.bookDetailEditComment(PageNameConstants.BOOK_DETAIL, PageNameConstants.COMMENT_LIST, 2, source);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1005 && resultCode == 1003) {
            commentList.clear();
            pageIndex = 1;
            presenter.loadData(bookId, pageIndex);
            mResult = 1003;
        }  if (requestCode == 1009 && resultCode == 1008) {
            mResult = 1008;
        }
    }

    @Override
    public void finish() {
        if (mResult != -1) {
            setResult(mResult);
        }
        super.finish();
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        getPromptLayoutHelper().hide();
    }

    @Override
    public void showEmpty() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    @Override
    public void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                presenter.loadData(bookId, pageIndex);
            }
        });
    }

    @Override
    public void showPage(BookDetailBean bookDetailBean) {

    }

    @Override
    public void loadSaveComment() {

    }

    @Override
    public void showDownloadDialog(ChapterDownloadOptionResp resp) {

    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        PromptLayoutHelper helper = null;
        if (mPullLayout != null) {
            helper = mPullLayout.getPromptLayoutHelper();
        }
        if (helper != null) {
            return helper;
        }
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private void initPullLayout() {
        onInitPullLayout((PullToRefreshLayout) findView(R.id.pull_layout));
    }

    protected void onInitPullLayout(PullToRefreshLayout pullLayout) {
        if (null == pullLayout) {
            return;
        }
        mPullLayout = pullLayout;

        pullLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                onPullRefresh();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ++pageIndex;
                presenter.loadData(bookId, pageIndex);
            }
        });
    }

    protected void onPullRefresh() {
        commentList.clear();
        pageIndex = 1;
        presenter.loadData(bookId, pageIndex);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.distory();
        }
        commentList.clear();
    }

    @Override
    public void showAdPage(Object adObject) {

    }

    @Override
    public void showRecommend(RecommendBean recommendBean) {

    }

    @Override
    public void showComment(CommentListBean list) {
        dismissLoading();
        if (list != null && list.getCommentList() != null && list.getCommentList().size() > 0) {
            mPullLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            commentList.addAll(list.getCommentList());
            commentAdapter.setData(commentList);
        } else {
            if (pageIndex != 1) {
                mPullLayout.loadMoreFinish(PullToRefreshLayout.FAIL_TEMPORARY_NOT_DATA);
                mPullLayout.setHasMoreData(true);
            } else {
                mPullLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }
        }
    }

    @Override
    public void loadFirstChapterData(String data, String title) {

    }

    @Override
    public void loadOtherReadData(RecommendBean recommendBean) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
