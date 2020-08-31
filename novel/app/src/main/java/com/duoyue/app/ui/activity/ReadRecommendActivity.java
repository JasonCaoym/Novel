package com.duoyue.app.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.adapter.RecomHotAdapter;
import com.duoyue.app.bean.CommentItemBean;
import com.duoyue.app.bean.RecomHotBean;
import com.duoyue.app.presenter.ReadRecommendPresenter;
import com.duoyue.app.ui.view.SimpleView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReadRecommendActivity extends BaseActivity implements SimpleView<RecomHotBean> {

    private static final String TAG = "App#ReadRecommendActivity";
    private RecomHotAdapter adapter;
    private RecyclerView recyclerView;
    private ReadRecommendActivity.Presenter presenter;
    protected PromptLayoutHelper mPromptLayoutHelper;

    private List<CommentItemBean> commentList = new ArrayList<>();

    private long bookId;
    private boolean isFinished;
    private String sourceStats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_end);
        Logger.e("heheda", "ReadRecommendActivity--onCreate");
        initData();
        initViews();
        presenter = new ReadRecommendPresenter(this, bookId);
    }

    private void initData() {
        bookId = getIntent().getLongExtra(RouterPath.INSTANCE.KEY_BOOK_ID, 0);
        isFinished = getIntent().getBooleanExtra("is_finished", false);
        sourceStats = getIntent().getStringExtra(RouterPath.INSTANCE.KEY_SOURCE);
    }

    private void initViews() {
        if (isFinished) {
            ((TextView)findViewById(R.id.book_end_title)).setText(R.string.book_end);
            ((TextView)findViewById(R.id.book_end_tip)).setText(R.string.recommend_finish_tip);
            findViewById(R.id.book_end_tip).setOnClickListener(this);
        } else {
            ((TextView)findViewById(R.id.book_end_title)).setText(R.string.not_end);
            ((TextView)findViewById(R.id.book_end_tip)).setText(R.string.please_wait_update);
        }
        findViewById(R.id.book_end_tip).setOnClickListener(this);
        findViewById(R.id.book_end_switch).setOnClickListener(this);
        recyclerView = findViewById(R.id.list_view);
        GridLayoutManager manager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter = new RecomHotAdapter(this, sourceStats);
        recyclerView.setAdapter(adapter);
    }

    public String getCurrPageId() {
        return PageNameConstants.READER_END;
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.book_end_switch:
                presenter.loadData();
                break;
            case R.id.book_end_tip:
                //去书城.
                ActivityHelper.INSTANCE.gotoHome(this, new BaseData(getPageName()));
                FunctionStatsApi.readGoBookCity(bookId);
                FuncPageStatsApi.readGoBookCity(isFinished ? 1 : 2, sourceStats);
                FuncPageStatsApi.bookCityShow(4);
                break;
        }
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
    public void loadData(RecomHotBean hotBean) {
        adapter.setList(hotBean.getList());
    }

    @Override
    public void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                presenter.loadData();
            }
        });
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.distory();
        }
        commentList.clear();
    }

    public interface Presenter {
        void loadData();
        void distory();
    }
}
