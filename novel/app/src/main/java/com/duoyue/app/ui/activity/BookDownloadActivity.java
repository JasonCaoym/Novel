package com.duoyue.app.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.app.adapter.BookDownloadAdapter;
import com.duoyue.app.bean.BookDownloadChapterBean;
import com.duoyue.app.bean.BookDownloadChapterListBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadCheckResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadResp;
import com.duoyue.app.presenter.BookDownloadPresenter;
import com.duoyue.app.ui.view.BookDownloadView;
import com.duoyue.app.ui.widget.CustomImageView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableExpandableListView;
import com.zzdm.ad.router.RouterPath;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 书籍下载页面
 *
 * @author wangtian
 * @date 2019/07/01
 */
public class BookDownloadActivity extends BaseActivity implements BookDownloadView {

    private static final String TAG = "App#BookDownloadActivity";

    public static final String BOOK_ID = "bookId";
    public static final String BOOK_NAME = "bookName";

    private LinearLayout toolbarLayout;
    private CustomImageView toolbarImg;
    private TextView toolbarText;
    private PullToRefreshLayout mPullLayout;
    private PullableExpandableListView listview;
    private TextView tvSelected;
    private TextView tvPrice;
    private TextView tvMyBookMoney;
    private RelativeLayout layoutDownloadNow;
    private TextView tvDownloadNow;
    private ProgressBar progressBar;
    private LinearLayout promptLayout;

    /**
     * 加载状态提示类.
     */
    private PromptLayoutHelper mPromptLayoutHelper;

    private BookDownloadPresenter presenter;
    private BookDownloadAdapter adapter;

    private int price;  //章节单价
    private List<String> downloadList;  //服务器下发的该用户已下载列表
    private int pageIndex = 1;   //当前页
    private int order = 0;      //0 正序 1倒序
    private long bookId;
    private String bookName;

    private List<BookDownloadChapterListBean> mCollect;

    private List<BookDownloadChapterBean> mSelectedChapterList;
    private String prevPageId;
    private String sourceStats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_book);

        bookId = getIntent().getLongExtra(BOOK_ID, 0);
        bookName = getIntent().getStringExtra(BOOK_NAME);
        prevPageId = getIntent().getStringExtra(RouterPath.KEY_PARENT_ID);
        sourceStats = getIntent().getStringExtra(RouterPath.KEY_SOURCE);

        initView();
        initData();
    }

    private void initView() {
        setToolBarLayout(getString(R.string.download));

        toolbarLayout = findView(R.id.toolbar_right_layout);
        toolbarLayout.setOnClickListener(onClickListener);

        toolbarImg = findViewById(R.id.toolbar_right_layout_img);
        toolbarText = findViewById(R.id.toolbar_right_layout_text);

        toolbarText.setText(getString(R.string.sort_zhengxu));
        toolbarImg.setImageResource(R.mipmap.sort_zhengxu);

        mPullLayout = findViewById(R.id.pull_layout);
        mPullLayout.setCanPullDown(false);
        listview = findViewById(R.id.expandable_listview);
        mCollect = new ArrayList<>();
        adapter = new BookDownloadAdapter(this, String.valueOf(bookId), mCollect);
        listview.setAdapter(adapter);
        onInitPullLayout(mPullLayout);

        tvSelected = findViewById(R.id.tv_selected);
        tvPrice = findViewById(R.id.tv_price);
        tvMyBookMoney = findViewById(R.id.tv_my_book_money);
        tvDownloadNow = findViewById(R.id.tv_download_now);
        progressBar = findViewById(R.id.progress_bar);
        layoutDownloadNow = findViewById(R.id.layout_download_now);
        layoutDownloadNow.setOnClickListener(onClickListener);

        promptLayout = findViewById(R.id.prompt_layout);
    }

    protected void onInitPullLayout(PullToRefreshLayout pullLayout) {
        if (null == pullLayout) {
            return;
        }

        pullLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                onPullRefresh();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                presenter.getChapterData(++pageIndex, order);
            }
        });
    }

    private void initData() {
        presenter = new BookDownloadPresenter(this, bookId);
        //去请求目录列表
        onPullRefresh();
        //请求当前书豆数
        presenter.getbookBeans();
    }

    private void onPullRefresh() {
        showLoading();
        mCollect.clear();
        pageIndex = 1;
        adapter.setData(mCollect, pageIndex);
        presenter.getChapterData(pageIndex, order);
    }

    private void switchSort() {

        if (order == 1) {
            //倒序
            toolbarText.setText(getString(R.string.sort_daoxu));
            toolbarImg.setImageResource(R.mipmap.sort_daoxu);
        } else {
            //正序
            toolbarText.setText(getString(R.string.sort_zhengxu));
            toolbarImg.setImageResource(R.mipmap.sort_zhengxu);
        }
    }

    /**
     * 更新item选中状态
     */
    public void updateSelectedList(List<BookDownloadChapterBean> selectedChapterBean) {

        mSelectedChapterList = selectedChapterBean;

        int count = selectedChapterBean.size();
        if(downloadList != null && downloadList.size() > 0){
            for(BookDownloadChapterBean chapterBean : selectedChapterBean){
                String sSeqNum = String.valueOf(chapterBean.getSeqNum());
                if(downloadList.contains(sSeqNum)){
                    count--;
                }
            }
        }

        tvSelected.setText(String.format(getString(R.string.selected_chapter), selectedChapterBean.size()));
        int totalPrice = count * price;
        tvPrice.setText(String.format(getString(R.string.selected_money), totalPrice));

        if (selectedChapterBean.size() > 0) {
            tvDownloadNow.setText(String.format(getString(R.string.download_now), totalPrice));
            layoutDownloadNow.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_download_book_bg));
        } else {
            tvDownloadNow.setText(getString(R.string.please_select_chapter));
            layoutDownloadNow.setBackgroundColor(ContextCompat.getColor(this, R.color.color_D8D8D8));
        }

    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.DOWNLOAD;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Utils.isFastClick()) {
                return;
            }

            switch (v.getId()) {
                case R.id.toolbar_right_layout:
                    if (order == 0) {
                        order = 1;
                        switchSort();
                        onPullRefresh();
                    } else {
                        order = 0;
                        switchSort();
                        onPullRefresh();
                    }
                    break;
                case R.id.layout_download_now:
                    if (mSelectedChapterList != null && mSelectedChapterList.size() > 0) {
                        layoutDownloadNow.setClickable(false);
                        progressBar.setVisibility(View.VISIBLE);
                        presenter.downloadCheck(mSelectedChapterList);
                    }
                    break;
            }
        }
    };

    /**
     * 获取无数据提示类对象.
     *
     * @return
     */
    private PromptLayoutHelper getPromptLayoutHelper() {
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(findView(R.id.load_prompt_layout));
        }
        return mPromptLayoutHelper;
    }


    @Override
    public void showSuccess(ChapterDownloadResp chapterDownloadResp) {
        if (chapterDownloadResp != null && chapterDownloadResp.getCollect() != null && !chapterDownloadResp.getCollect().isEmpty()) {
            price = chapterDownloadResp.getPrice();
            if(downloadList == null && !TextUtils.isEmpty(chapterDownloadResp.getSeqNumStr())){
                downloadList = Arrays.asList(chapterDownloadResp.getSeqNumStr().split(","));
            }
            if (pageIndex == 1) {
                mCollect.clear();
                mCollect.addAll(chapterDownloadResp.getCollect());
            } else {
                mCollect.addAll(chapterDownloadResp.getCollect());
            }
            adapter.setData(mCollect, pageIndex);
            dismissLoading();
        }
    }

    @Override
    public void bookBeansSuccess(BookRecordGatherResp bookRecordGather) {
        if (bookRecordGather != null) {
            tvMyBookMoney.setText(String.format(getString(R.string.my_money), bookRecordGather.getBookBeans()));
        }
    }

    @Override
    public void downloadCheckSuccess(ChapterDownloadCheckResp chapterDownloadCheckResp) {
        if (chapterDownloadCheckResp.getStatus() == 0) {
            //可以下载
            presenter.downloadChapter(bookId, bookName, mSelectedChapterList);
            finish();
            FuncPageStatsApi.downloadChapter(prevPageId, bookId, "1", sourceStats, String.valueOf(mSelectedChapterList.size()),PageNameConstants.DOWNLOAD);
        } else {
            //书豆不够，不能下载
            layoutDownloadNow.setClickable(true);
            progressBar.setVisibility(View.GONE);
            ToastUtils.show("书豆余额不足");
            FuncPageStatsApi.downloadChapter(prevPageId, bookId, "2", sourceStats, String.valueOf(mSelectedChapterList.size()),PageNameConstants.DOWNLOAD);
        }
    }

    @Override
    public void downloadCheckFailed() {
        //请求下载失败
        layoutDownloadNow.setClickable(true);
        progressBar.setVisibility(View.GONE);
        FuncPageStatsApi.downloadChapter(prevPageId, bookId, "2", sourceStats, String.valueOf(mSelectedChapterList.size()),PageNameConstants.DOWNLOAD);
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
        promptLayout.setVisibility(View.VISIBLE);
        layoutDownloadNow.setClickable(false);
    }

    @Override
    public void dismissLoading() {
        getPromptLayoutHelper().hide();
        promptLayout.setVisibility(View.GONE);
        layoutDownloadNow.setClickable(true);
    }

    @Override
    public void showNetworkError() {
        mCollect.clear();
        if (pageIndex > 1) {
            --pageIndex;
        }
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showLoading();
                //加载数据数据.
                onPullRefresh();
                //请求当前书豆数
                presenter.getbookBeans();
            }
        });
        promptLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showForceUpdateFinish(int result) {
        mPullLayout.refreshFinish(result);
    }

    @Override
    public void showLoadMoreFinish(int result) {
        mPullLayout.loadMoreFinish(result);
    }

    @Override
    public boolean isVisibleToUser() {
        return true;
    }

    @Override
    public void showEmpty() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        promptLayout.setVisibility(View.VISIBLE);
    }

}
