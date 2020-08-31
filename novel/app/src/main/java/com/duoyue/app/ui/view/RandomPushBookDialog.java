package com.duoyue.app.ui.view;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;
import com.duoyue.mianfei.xiaoshuo.presenter.RandomPushBookPresenter;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.SharePreferenceUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public class RandomPushBookDialog extends BaseFragmentDialog implements View.OnClickListener, RandomPushView {
    private static final String TAG = "App#RandomPushBookDialog";
    private ImageView mBookCover;
    private TextView mTvBookName;
    private TextView mTvBookType;
    private TextView mTvAddBookShelf;
    private TextView mTvChange;
    private TextView mTvNum;
    private TextView mTvBookTitle;
    private TextView mTvFirst;
    private Presenter mPresenter;
    private PromptLayoutHelper mPromptLayoutHelper;
    private RandomPushBean.BookBean randomPushBean;
    private View mView;
    private LinearLayout llBookContent;
    private View mViewClose;
    private TextView mTvIntroduction;
    private int mModelId;
    private String mContent;
    private String mChapterTitle;
    private String mTitle;
    private TextView mTvTitle;
    private TextView mTvKeyWord;
    private TextView mTvRandomNum;

    public void setModelId(int modelId) {
        mModelId = modelId;
    }

    public void setBookBean(RandomPushBean.BookBean bookBean) {
        randomPushBean = bookBean;
    }

    public void setFirstChapterData(String content, String chapterTitle) {
        mContent = content;
        mChapterTitle = chapterTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void initView(View view) {
        mView = view;
        mViewClose = view.findViewById(R.id.view_close);
        mTvTitle = view.findViewById(R.id.tv_title);
        mBookCover = view.findViewById(R.id.book_cover);
        mTvBookName = view.findViewById(R.id.tv_book_name);
        mTvIntroduction = view.findViewById(R.id.tv_introduction);
        mTvBookType = view.findViewById(R.id.tv_book_type);
        mTvAddBookShelf = view.findViewById(R.id.tv_add_book_shelf);
        mTvChange = view.findViewById(R.id.tv_change);
        mTvNum = view.findViewById(R.id.tv_num);
        mTvBookTitle = view.findViewById(R.id.tv_book_title);
        mTvFirst = view.findViewById(R.id.tv_first);
        llBookContent = view.findViewById(R.id.ll_book_content);
        mTvKeyWord = view.findViewById(R.id.tv_key_word);
        mTvRandomNum = view.findViewById(R.id.tv_random_num);


        mViewClose.setOnClickListener(this);
        mTvAddBookShelf.setOnClickListener(this);
        mTvChange.setOnClickListener(this);

        getPromptLayoutHelper();

        llBookContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (randomPushBean != null && getActivity() != null) {
                    FuncPageStatsApi.randomPushToRead(randomPushBean.getBookId(), mModelId);
//                    if (!BookShelfPresenter.isAdded(String.valueOf(randomPushBean.getBookId()))) {
//                        addToBookshelf();
//                    }
                    ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(randomPushBean.getBookId()), 1, new BaseData(""), "", PageNameConstants.RECOMMEND_PUSH_BOOK);
                    dismiss();
                }
                return true;
            }
        });

        showSuccess(randomPushBean);
        loadFirstChapterData(mContent, mChapterTitle);

        //弹过随机推书框,本次启动不在弹
        SharePreferenceUtils.putBoolean(getContext(), SharePreferenceUtils.IS_IN_DETAIL, true);
    }

    private PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = mView.findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    @Override
    public void initData() {
        if (randomPushBean == null) {
            mPresenter = new RandomPushBookPresenter(getActivity(), this);
            mPresenter.loadData(0);
        }
    }

    @Override
    public int getInflateLayout() {
        return R.layout.dialog_random_push_book;
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.view_close:
                dismiss();
                break;
            case R.id.tv_add_book_shelf:
                //页面点击加入书架
                FuncPageStatsApi.randomPushClickAddShelf(randomPushBean.getBookId(), mModelId);
                addToBookshelf();
                break;
            case R.id.tv_change:
                //页面点击换一换
                FuncPageStatsApi.randomPushClickChange(mModelId);
                if (mPresenter == null) {
                    mPresenter = new RandomPushBookPresenter(getActivity(), this);
                }
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
                    StatisHelper.onEvent().subscription(randomPushBean.getBookName(), "");
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
    }

    private void setShelfButton(boolean enable, TextView textView) {
        textView.setText(enable ? getString(R.string.add_shelf) : getString(R.string.has_add_shelf));
        textView.setBackgroundResource(enable ? R.drawable.bg_fe8b13_13 : R.drawable.bg_898989_13);
        textView.setTextColor(enable ? getResources().getColor(R.color.color_FE8B13) : getResources().getColor(R.color.white));
        textView.setEnabled(enable);
    }

    @Override
    public void showSuccess(RandomPushBean.BookBean data) {
        if (data != null) {
            randomPushBean = data;
            if (!BookShelfPresenter.isAdded(String.valueOf(randomPushBean.getBookId()))) {
                disableAddButton(true);
            } else {
                disableAddButton(false);
            }
            setView(data);
            //曝光上报
            FuncPageStatsApi.randomPushExpose(data.getBookId(), mModelId);
        }
    }

    private void setView(RandomPushBean.BookBean data) {
        if (!TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText("搜索《");
            mTvKeyWord.setText(mTitle);
            mTvRandomNum.setText("》" + Utils.getNum(85, 91) + "%的用户都在看");
            mTvKeyWord.setVisibility(View.VISIBLE);
            mTvRandomNum.setVisibility(View.VISIBLE);
        } else {
            mTvTitle.setText("今日必读热门小说");
            mTvKeyWord.setVisibility(View.GONE);
            mTvRandomNum.setVisibility(View.GONE);
        }
        GlideUtils.INSTANCE.loadImage(getContext(), data.getCover(), mBookCover);
        mTvBookName.setText(data.getBookName());
        mTvIntroduction.setText(data.getResume());
        String tags = (data.getState() == 1 ? getString(R.string.updating) : (data.getState() == 2 ? getString(R.string.finished) : getString(R.string.stop_update)))
                + " · " + (int) (data.getWordCount() * 1f / 10000)
                + (TextUtils.isEmpty(data.getCatName()) ? "万字" : ("万字 · " + data.getCatName()));
        mTvBookType.setText(tags);
        mTvNum.setText(String.valueOf(data.getStar()));


    }

    @Override
    public void showEmpty() {
//        mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        ToastUtils.showLimited("没有更多书籍了");
    }

    @Override
    public void showError() {
        ToastUtils.showLimited("网络出错了,请稍后重试");
//        mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                initData();
//            }
//        });
    }

    @Override
    public void loadFirstChapterData(final String content, final String chapterTitle) {
        mTvFirst.post(new Runnable() {
            @Override
            public void run() {
                mTvBookTitle.setText(chapterTitle);
                mTvFirst.setText(content);

                dismissLoading();
            }
        });
    }

    @Override
    public void showLoading() {
//        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
//        getPromptLayoutHelper().hide();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    public interface Presenter {
        void loadData(long bookId);

        void preLoadNextChapter(long bookId);

        void destroy();
    }
}
