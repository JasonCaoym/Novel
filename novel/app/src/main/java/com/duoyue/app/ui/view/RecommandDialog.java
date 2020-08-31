package com.duoyue.app.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

/**
 * 口令弹框/启动弹窗
 */
public class RecommandDialog extends DialogFragment implements View.OnClickListener {

    public static final String KEY_DIALOG_TYPE = "is_launcher";
    public static final String KEY_SHOW_DIALOG = "show_dialog";
    public static final String KEY_HAS_LAUNCHER_DIALOG = "has_launcher_dialog";
    public static final String KEY_HAS_COMMAND_DIALOG = "has_command_dialog";
    public static final String KEY_DATA_JSON = "save_data_json";

    private View rootView;
    private RecommandBean mRecommandBean;
    private CancelListener listener;
    private String mCurrPageId;

    public void setCurrPageId(String currPageId) {
        mCurrPageId = currPageId;
    }

    public interface CancelListener {
        void cancel();
    }

    public void setData(RecommandBean data) {
        mRecommandBean = data;
    }

    public boolean isLauncherDialog;

    public void setCancelListener(CancelListener listener) {
        this.listener = listener;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isLauncherDialog = bundle.getBoolean(KEY_DIALOG_TYPE);
        } else {
            isLauncherDialog = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
        setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Utils.dp2px(getContext(),300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_recommand, container);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        if (mRecommandBean == null) {
            return;
        }
        ImageView bookCover = view.findViewById(R.id.book_cover);
        TextView bookName = view.findViewById(R.id.tv_book_name);
        TextView bookAuthor = view.findViewById(R.id.tv_book_author);
        TextView readCount = view.findViewById(R.id.tv_read_count);
        TextView recommand = view.findViewById(R.id.tv_recommend);
        TextView addBookShelf = view.findViewById(R.id.tv_add_book_shelf);
        ImageView close = view.findViewById(R.id.iv_close);
        View titleView = view.findViewById(R.id.recom_dialog_title);
        ViewGroup imgViewGroup = view.findViewById(R.id.cv_book_pic);

        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) imgViewGroup.getLayoutParams();

        GlideUtils.INSTANCE.loadImage(getContext(), mRecommandBean.getCover(), bookCover);
        bookName.setText(mRecommandBean.getBookName());

        readCount.setText(mRecommandBean.getWeekDownPvMsg());
        if (isLauncherDialog) {
            bookAuthor.setVisibility(View.GONE);
            recommand.setText(mRecommandBean.getChapterTitle());

            titleView.setVisibility(View.VISIBLE);
            imageParams.topMargin = Utils.dp2px(getContext(),23);

            addBookShelf.setText(R.string.recommand_add_start);

        } else {
            bookAuthor.setVisibility(View.VISIBLE);
            bookAuthor.setText(mRecommandBean.getAuthorName());
            recommand.setText(ViewUtils.getString(R.string.recommand_last_read) + mRecommandBean.getChapterTitle());

            titleView.setVisibility(View.GONE);
            imageParams.topMargin = Utils.dp2px(getContext(),44);

            addBookShelf.setText(R.string.recommand_add_continue);
        }
        imgViewGroup.setLayoutParams(imageParams);
        addBookShelf.setOnClickListener(this);
        close.setOnClickListener(this);
        //统计口令曝光
        if (!isLauncherDialog) {
            FuncPageStatsApi.commandShow(mRecommandBean != null ? mRecommandBean.getBookId() : 0);
        } else {
            FuncPageStatsApi.launcherDialogShow(mRecommandBean != null ? mRecommandBean.getBookId() : 0, mCurrPageId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:

                break;
            case R.id.tv_add_book_shelf:
                addToBookshelf();
                //统计口令点击
                if (!isLauncherDialog) {
                    FuncPageStatsApi.commandAddShelfRead(mRecommandBean != null ? mRecommandBean.getBookId() : 0);
                } else {
                    FuncPageStatsApi.launcherDialogClick(mRecommandBean != null ? mRecommandBean.getBookId() : 0,mCurrPageId);
                }
                goRead();
                break;
        }
        dismiss();
    }

//    public void hasDialogWaiting() {
//        // 判断是否有启动弹窗等待显示
//        if (SPUtils.INSTANCE.getBoolean(RecommandDialog.KEY_HAS_LAUNCHER_DIALOG, false)) {
//            String dataJson = SPUtils.INSTANCE.getString(RecommandDialog.KEY_DATA_JSON);
//            if (!StringFormat.isEmpty(dataJson)) {
//                mRecommandBean = JsonUtils.parseJson(dataJson, RecommandBean.class);
//                if (mRecommandBean != null) {
//                    isLauncherDialog = true;
//                    initView(rootView);
//                    show(getFragmentManager(), "recommand");
//                }
//            }
//        }
//    }

    private void addToBookshelf() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return BookShelfPresenter.addBookShelf(mRecommandBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(s)) {
                    //添加书架成功.
                    ToastUtils.showLimited(R.string.add_shelf_success);
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(s);
                }
            }
        });
    }

    /**
     * 目前只支持跳转阅读器
     */
    private void goRead() {
        if (mRecommandBean==null)return;
        switch (mRecommandBean.getJumpType()) {
            case 3:
                //阅读器
                String source = "";
                if (isLauncherDialog) {
                    source = PageNameConstants.SOURCE_LAUNCHER;
                } else {
                    source = PageNameConstants.SOURCE_COMMAND;
                }
                ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(mRecommandBean.getBookId()),
                        mRecommandBean.getLastReadChapter(), new BaseData("小说口令"), "", source);
                break;
            case 2:
                //H5
                //                ActivityHelper.INSTANCE.gotoWeb(getActivity(), mRecommandBean.getLink());
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mRecommandBean = null;
        SPUtils.INSTANCE.putBoolean(KEY_SHOW_DIALOG, true);
//        hasDialogWaiting();
        if (listener != null) {
            listener.cancel();
        }
    }

    public boolean canShowDialog() {
        boolean canShowDialog = SPUtils.INSTANCE.getBoolean(KEY_SHOW_DIALOG, true);
        if (canShowDialog) {
            SPUtils.INSTANCE.putBoolean(KEY_SHOW_DIALOG, false);
        }
        return canShowDialog;
    }
}
