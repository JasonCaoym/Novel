package com.duoyue.app.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.view.RandomPushBookScrollView;
import com.duoyue.app.ui.view.RandomPushView;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class RandomPushBookActivity extends BaseActivity implements RandomPushView {

    private static final String TAG = "App#RandomPushBookActivity";
    private ImageView mIvBackIcon;
    private TextView mTvTitle;
    private TextView mTvAuthor;
    private ImageView mBookCover;
    private TextView mTvBookName;
    private TextView mTvBookType;
    private TextView mTvAddBookShelf;
    private TextView mTvChange;
    private TextView mTvNum;
    private TextView mTvBookTitle;
    private TextView mTvFirst;
    private View mViewBg;
    private XLinearLayout mXrl;
    private ImageView mIvBack;
    private ImageView mIvBookCover;
    private TextView mIvBookNameTitle;
    private TextView mIvBookTypeTitle;
    private TextView mIvBookShelfTitle;
    private Presenter mPresenter;
    private RandomPushBookScrollView mScrollView;
    private XLinearLayout mMLlTitleBg;
    private XRelativeLayout mLayoutTitle;
    private int mTitleHeight;
    private XLinearLayout mLlBookTitle;
    private CardView mCvBookPicTitle;
    private PromptLayoutHelper mPromptLayoutHelper;
    private RandomPushBean.BookBean randomPushBean;
    private int mModelId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_push_book);
        initView();
        getPromptLayoutHelper();
        initData();
    }

    private void initData() {
        mModelId = getIntent().getIntExtra("modelId", 0);
//        mPresenter = new RandomPushBookPresenter(this,this);
//        mPresenter.loadData(0);
    }

    private PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private void initView() {
        mIvBackIcon = findView(R.id.iv_back_icon);
        mTvTitle = findView(R.id.tv_title);
        mTvAuthor = findView(R.id.tv_author);
        mBookCover = findView(R.id.book_cover);
        mTvBookName = findView(R.id.tv_book_name);
        mTvBookType = findView(R.id.tv_book_type);
        mTvAddBookShelf = findView(R.id.tv_add_book_shelf);
        mTvChange = findView(R.id.tv_change);
        mTvNum = findView(R.id.tv_num);
        mTvBookTitle = findView(R.id.tv_book_title);
        mTvFirst = findView(R.id.tv_first);
        mViewBg = findView(R.id.view_bg_w);
        mXrl = findView(R.id.xrl_go);
        mIvBack = findView(R.id.iv_back);
        mIvBookCover = findView(R.id.iv_book_cover);
        mIvBookNameTitle = findView(R.id.tv_book_name_title);
        mIvBookTypeTitle = findView(R.id.tv_book_type_title);
        mIvBookShelfTitle = findView(R.id.tv_add_book_shelf_title);
        mScrollView = findView(R.id.scl_view);
        mMLlTitleBg = findView(R.id.ll_title_bg);
        mLayoutTitle = findView(R.id.layout_title);
        mLlBookTitle = findView(R.id.ll_book_title);
        mCvBookPicTitle = findView(R.id.cv_book_pic_title);

        mIvBackIcon.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvAddBookShelf.setOnClickListener(this);
        mIvBookShelfTitle.setOnClickListener(this);
        mTvChange.setOnClickListener(this);


        mLayoutTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTitleHeight = mLayoutTitle.getHeight();
                mLayoutTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        mMLlTitleBg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mScrollView.setOnScrollListener(new RandomPushBookScrollView.OnScrollListener() {
                    @Override
                    public void onScroll(int scrollY) {
                        mLayoutTitle.setVisibility(View.VISIBLE);
                        if (scrollY <= 0) {
                            mLayoutTitle.setBackgroundColor(Color.argb(0, 245, 245, 245));
                            mIvBack.setVisibility(View.GONE);
                            mCvBookPicTitle.setVisibility(View.GONE);
                            mLlBookTitle.setVisibility(View.GONE);
                            mIvBookShelfTitle.setVisibility(View.GONE);
                        } else if (scrollY < (mMLlTitleBg.getHeight() - mTitleHeight)) {
                            //由于title和上层布局重叠需要减去title的高度，算出去除重叠部分的高度,最后算出透明度
                            float alpha = (255 * ((float) scrollY / (mMLlTitleBg.getHeight() - mTitleHeight)));
                            mLayoutTitle.setBackgroundColor(Color.argb((int) alpha, 245, 245, 245));
                            mIvBack.setVisibility(View.GONE);
                            mCvBookPicTitle.setVisibility(View.GONE);
                            mLlBookTitle.setVisibility(View.GONE);
                            mIvBookShelfTitle.setVisibility(View.GONE);
                        } else {
                            mLayoutTitle.setBackgroundColor(Color.argb(255, 245, 245, 245));
                            mIvBack.setVisibility(View.VISIBLE);
                            mCvBookPicTitle.setVisibility(View.VISIBLE);
                            mLlBookTitle.setVisibility(View.VISIBLE);
                            mIvBookShelfTitle.setVisibility(View.VISIBLE);
                        }
                        mScrollView.requestLayout();
                    }
                });

                mMLlTitleBg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mScrollView.setOnLoadMoreListener(new RandomPushBookScrollView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //页面末尾上拉进入阅读器时上报
                FuncPageStatsApi.randomPushToRead(randomPushBean.getBookId(),mModelId);
                if (!BookShelfPresenter.isAdded(String.valueOf(randomPushBean.getBookId()))) {
                    addToBookshelf();
                }
                ActivityHelper.INSTANCE.gotoRead(RandomPushBookActivity.this, String.valueOf(randomPushBean.getBookId()), 2, new BaseData(getPageName()), "", PageNameConstants.RECOMMEND_PUSH_BOOK);
                finish();
            }
        });

    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back_icon:
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add_book_shelf:
            case R.id.tv_add_book_shelf_title:
                //页面点击加入书架
                FuncPageStatsApi.randomPushClickAddShelf(randomPushBean.getBookId(),mModelId);
                addToBookshelf();
                break;
            case R.id.tv_change:
                //页面点击换一换
                FuncPageStatsApi.randomPushClickChange(mModelId);
                mPresenter.loadData(randomPushBean == null ? 0 : randomPushBean.getBookId());
                break;

        }
    }

    private void addToBookshelf() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return BookShelfPresenter.addBookShelf(randomPushBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(s)) {
                    //添加书架成功.
                    StatisHelper.onEvent().subscription(randomPushBean.getBookName(), getPageName());
                    ToastUtils.showLimited(R.string.add_shelf_success);
                    disableAddButton(false);
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(s);
                }
            }
        });
    }

    /**
     * 添加书架成功enable按钮/未添加书架able
     *
     * @param enable true enable按钮  false able按钮
     */
    private void disableAddButton(boolean enable) {
        setShelfButton(enable, mTvAddBookShelf);
        setShelfButton(enable, mIvBookShelfTitle);
    }

    private void setShelfButton(boolean enable, TextView textView) {
        textView.setText(enable ? getString(R.string.add_shelf) : getString(R.string.has_add_shelf));
        textView.setBackgroundResource(enable ? R.drawable.bg_fe8b13_13 : R.drawable.bg_898989_13);
        textView.setTextColor(enable ? getResources().getColor(R.color.color_FE8B13) : getResources().getColor(R.color.white));
        textView.setEnabled(enable);
    }

    @NotNull
    @Override
    public String getCurrPageId() {
        return "";
    }

    @Override
    public void showSuccess(RandomPushBean.BookBean data) {
        randomPushBean = data;
        if (!BookShelfPresenter.isAdded(String.valueOf(randomPushBean.getBookId()))) {
            disableAddButton(true);
        }else {
            disableAddButton(false);
        }
        setView(data);
        //曝光上报
        FuncPageStatsApi.randomPushExpose(data.getBookId(),mModelId);
    }

    private void setView(RandomPushBean.BookBean data) {
        mTvAuthor.setText(data.getAuthorName() + "带你读");
        GlideUtils.INSTANCE.loadImage(this, data.getCover(), mBookCover);
        mTvBookName.setText(data.getBookName());
        String tags = (data.getState() == 1 ? getString(R.string.updating) : (data.getState() == 2 ? getString(R.string.finished) : getString(R.string.stop_update)))
                + "·" + (int) (data.getWordCount() * 1f / 10000)
                + (TextUtils.isEmpty(data.getCatName()) ? "万字" : ("万字·" + data.getCatName()));
        mTvBookType.setText(tags);
        mTvNum.setText(String.valueOf(data.getStar()));


        GlideUtils.INSTANCE.loadImage(this, data.getCover(), mIvBookCover);
        mIvBookNameTitle.setText(data.getBookName());
        mIvBookTypeTitle.setText(tags);
    }

    @Override
    public void showEmpty() {
    }

    @Override
    public void showError() {
        mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    @Override
    public void loadFirstChapterData(final String content, final String chapterTitle) {
        mTvFirst.post(new Runnable() {
            @Override
            public void run() {
                mTvBookTitle.setText(chapterTitle);
                mTvFirst.setText(content);
                XRelativeLayout.LayoutParams layoutParams = (XRelativeLayout.LayoutParams) mViewBg.getLayoutParams();
                layoutParams.height = mTvFirst.getLineHeight() + (int) (mTvFirst.getLineSpacingExtra() + mTvFirst.getLineSpacingExtra() / 2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    XLinearLayout.LayoutParams params = (XLinearLayout.LayoutParams) mXrl.getLayoutParams();
                    params.topMargin = ViewUtils.dp2px(16);
                    mXrl.setLayoutParams(params);
                }
                mViewBg.setLayoutParams(layoutParams);

                dismissLoading();
            }
        });
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        getPromptLayoutHelper().hide();
    }

    public interface Presenter {
        void loadData(long bookId);

        void preLoadNextChapter(long bookId);
    }
}
