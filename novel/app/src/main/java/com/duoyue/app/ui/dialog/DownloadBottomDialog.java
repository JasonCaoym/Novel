package com.duoyue.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadCheckResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.presenter.BookDownloadDialogPresenter;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.view.BookDownloadDialogView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.ScreenSizeUtils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.utils.ToastUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 底部下载弹窗
 *
 * @author wangtian
 * @date 2019/07/17
 */
public class DownloadBottomDialog extends Dialog implements BookDownloadDialogView {

    private String source;
    private String prePageId;
    private Context context;
    private View view;
    private ChapterDownloadOptionResp resp;
    private BookDetailBean bookDetailBean;
    private BookDownloadDialogPresenter presenter;
    /**
     * 书豆单价
     */
    private int price;
    /**
     * 已下载章节序号
     */
    private List<String> downloadList;

    private int seqNum; //下载起始章节
    private String seqChapter;  //起始章节标题

    private int selected;      //选中的选项
    private int totalChapter;   //选中的章节数量
    private int totalPrice;     //选中所需要的书豆

    private TextView tvSeqnumChapter;

    private LinearLayout layoutOption1;
    private TextView tvOptionChapter1;
    private TextView tvOptionBean1;
    private LinearLayout layoutOption2;
    private TextView tvOptionChapter2;
    private TextView tvOptionBean2;
    private LinearLayout layoutOption3;
    private TextView tvOptionChapter3;
    private TextView tvOptionBean3;
    private LinearLayout layoutOption4;
    private TextView tvOptionChapter4;

    private TextView tvMyBookBean;
    private RelativeLayout layoutDownloadNow;
    private TextView tvDownloadNow;
    private ProgressBar progressBar;

    public DownloadBottomDialog(Context context, ChapterDownloadOptionResp resp, BookDetailBean bookDetailBean, String prePageId, String source) {
        super(context, R.style.BottomDialogStyle);
        view = View.inflate(context, R.layout.dialog_download_book_bottom, null);
        setContentView(view);

        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenSizeUtils.getScreenWidth(context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);

        this.context = context;
        this.resp = resp;
        this.bookDetailBean = bookDetailBean;
        this.prePageId = prePageId;
        this.source = source;

        presenter = new BookDownloadDialogPresenter(this);

        initView();
        initData();
    }

    private void initView() {

        tvSeqnumChapter = view.findViewById(R.id.tv_seqnum_chapter);

        layoutOption1 = view.findViewById(R.id.layout_option_1);
        tvOptionChapter1 = view.findViewById(R.id.tv_option_chapter_1);
        tvOptionBean1 = view.findViewById(R.id.tv_option_bean_1);

        layoutOption2 = view.findViewById(R.id.layout_option_2);
        tvOptionChapter2 = view.findViewById(R.id.tv_option_chapter_2);
        tvOptionBean2 = view.findViewById(R.id.tv_option_bean_2);

        layoutOption3 = view.findViewById(R.id.layout_option_3);
        tvOptionChapter3 = view.findViewById(R.id.tv_option_chapter_3);
        tvOptionBean3 = view.findViewById(R.id.tv_option_bean_3);

        layoutOption4 = view.findViewById(R.id.layout_option_4);
        tvOptionChapter4 = view.findViewById(R.id.tv_option_chapter_4);

        tvMyBookBean = view.findViewById(R.id.tv_my_book_bean);
        layoutDownloadNow = view.findViewById(R.id.layout_download_now);
        tvDownloadNow = view.findViewById(R.id.tv_download_now);
        progressBar = view.findViewById(R.id.progress_bar);

        layoutOption1.setOnClickListener(onClickListener);
        layoutOption2.setOnClickListener(onClickListener);
        layoutOption3.setOnClickListener(onClickListener);
        layoutOption4.setOnClickListener(onClickListener);
        layoutDownloadNow.setOnClickListener(onClickListener);
    }

    private void initData() {

        price = resp.getPrice();

        if (!TextUtils.isEmpty(resp.getSeqNumStr())) {
            downloadList = Arrays.asList(resp.getSeqNumStr().split(","));
        }

        //起始章节
        BookRecordBean bookRecordBean = BookRecordHelper.getsInstance().findBookRecordById(bookDetailBean.getBookId());
        if (bookRecordBean != null) {
            seqNum = bookRecordBean.getSeqNum();
            seqChapter = bookRecordBean.getChapterTitle();
        } else {
            //没有读过
            if (resp.getChapterDownload() != null) {
                seqNum = Integer.valueOf(resp.getChapterDownload().getSeqNum());
                seqChapter = resp.getChapterDownload().getTitle();
            }
        }

        tvSeqnumChapter.setText("起始章节：" + seqChapter);

        //选项1
        int count1 = resp.getNumList().get(0);
        tvOptionChapter1.setText(count1 + "章");

        int total = getRealDownloadBeans(count1);
        tvOptionBean1.setText(total + "书豆");
        if (bookDetailBean.getLastChapter() - seqNum < count1) {
            //剩余没有这么多章节,无法选中
            setNotClickable(layoutOption1, tvOptionChapter1, tvOptionBean1);
        } else {
            //可以点击，那么默认选中第一个
            selected = 1;
            setSelected(layoutOption1, tvOptionChapter1, tvOptionBean1);
        }

        //选项2
        int count2 = resp.getNumList().get(1);
        tvOptionChapter2.setText(count2 + "章");
        int tota2 = getRealDownloadBeans(count2);
        tvOptionBean2.setText(tota2 + "书豆");
        if (bookDetailBean.getLastChapter() - seqNum < count2) {
            //剩余没有这么多章节,无法选中
            setNotClickable(layoutOption2, tvOptionChapter2, tvOptionBean2);
        } else {
            //可以点击
            if (selected == 0) {
                //如果前面的选项没有默认选中，可以默认选中该项
                setSelected(layoutOption2, tvOptionChapter2, tvOptionBean2);
            } else {
                setClickable(layoutOption2, tvOptionChapter2, tvOptionBean2);
            }
        }

        //选项3
        int count3 = resp.getNumList().get(2);
        if (bookDetailBean.getLastChapter() - seqNum < count3) {
            //剩余没有这么多章节, 显示剩余章节
            tvOptionChapter3.setText("后续所有章");
            count3 = bookDetailBean.getLastChapter() - seqNum;
            int tota3 = getRealDownloadBeans(count3);
            tvOptionBean3.setText(count3 + "章·" + tota3 + "书豆");
        } else {
            tvOptionChapter3.setText(count3 + "章");
            int tota3 = getRealDownloadBeans(count3);
            tvOptionBean3.setText(tota3 + "书豆");
        }
        if (selected == 0) {
            //如果前面的选项没有默认选中，可以默认选中该项
            setSelected(layoutOption3, tvOptionChapter3, tvOptionBean3);
        } else {
            setClickable(layoutOption3, tvOptionChapter3, tvOptionBean3);
        }

        //选项4
        layoutOption4.setClickable(true);
        tvOptionChapter4.setText("自定义");


        //我的书豆
        String content = "我的书豆" + resp.getBeans();
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_FE8B13)), 4, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMyBookBean.setText(spannableString);

        //设置下载需要豆数
        setNeedBeans();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == layoutOption1) {
                if (selected != 1) {
                    revert();
                    setSelected(layoutOption1, tvOptionChapter1, tvOptionBean1);
                    selected = 1;
                    setNeedBeans();
                }
            } else if (v == layoutOption2) {
                if (selected != 2) {
                    revert();
                    setSelected(layoutOption2, tvOptionChapter2, tvOptionBean2);
                    selected = 2;
                    setNeedBeans();
                }
            } else if (v == layoutOption3) {
                if (selected != 3) {
                    revert();
                    setSelected(layoutOption3, tvOptionChapter3, tvOptionBean3);
                    selected = 3;
                    setNeedBeans();
                }
            } else if (v == layoutOption4) {
                dismiss();
                if (context instanceof BookDetailActivity) {
                    ((BookDetailActivity) context).gotoBookDownloadActivity();
                } else if(context instanceof ReadActivity){
                    ((ReadActivity)context).gotoBookDownloadActivity();
                }
            } else if (v == layoutDownloadNow) {
                if(totalChapter != 0 && totalPrice != 0){
                    showLoading();
                    presenter.downloadCheck(totalChapter, seqNum, Long.valueOf(bookDetailBean.getBookId()));
                }
            }
        }
    };

    /**
     * 设置为不可点击
     *
     * @param layoutOption
     * @param tvOptionChapter
     * @param tvOptionBean
     */
    private void setNotClickable(LinearLayout layoutOption,
                                 TextView tvOptionChapter,
                                 TextView tvOptionBean) {
        layoutOption.setClickable(false);
        layoutOption.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_f5f5f5));
        tvOptionChapter.setTextColor(ContextCompat.getColor(context, R.color.color_b2b2b2));
        tvOptionBean.setVisibility(View.GONE);
    }

    /**
     * 可以点击
     *
     * @param layoutOption
     * @param tvOptionChapter
     * @param tvOptionBean
     */
    private void setClickable(LinearLayout layoutOption,
                              TextView tvOptionChapter,
                              TextView tvOptionBean) {
        layoutOption.setClickable(true);
        layoutOption.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_f5f5f5));
        tvOptionChapter.setTextColor(ContextCompat.getColor(context, R.color.color_1b1b1b));
        tvOptionBean.setTextColor(ContextCompat.getColor(context, R.color.color_b2b2b2));
        tvOptionBean.setVisibility(View.VISIBLE);
    }

    /**
     * 设置选中状态
     *
     * @param layoutOption
     * @param tvOptionChapter
     * @param tvOptionBean
     */
    private void setSelected(LinearLayout layoutOption,
                             TextView tvOptionChapter,
                             TextView tvOptionBean) {
        layoutOption.setClickable(true);
        layoutOption.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_fe8b13_5));
        tvOptionChapter.setTextColor(ContextCompat.getColor(context, R.color.color_FE8B13));
        tvOptionBean.setTextColor(ContextCompat.getColor(context, R.color.color_FE8B13));
        tvOptionBean.setVisibility(View.VISIBLE);
    }

    /**
     * 已选中项还原为未选中状态
     */
    private void revert() {
        if (selected == 1) {
            setClickable(layoutOption1, tvOptionChapter1, tvOptionBean1);
        } else if (selected == 2) {
            setClickable(layoutOption2, tvOptionChapter2, tvOptionBean2);
        } else if (selected == 3) {
            setClickable(layoutOption3, tvOptionChapter3, tvOptionBean3);
        }
    }

    /**
     * 计算下载所需豆数
     */
    private void setNeedBeans() {
        totalChapter = 0;
        if (selected == 1) {
            totalChapter = resp.getNumList().get(0);
        } else if (selected == 2) {
            totalChapter = resp.getNumList().get(1);
        } else if (selected == 3) {
            totalChapter = resp.getNumList().get(2);
            if (bookDetailBean.getLastChapter() - seqNum < totalChapter) {
                totalChapter = bookDetailBean.getLastChapter() - seqNum;
            }
        }

        totalPrice = getRealDownloadBeans(totalChapter);
        tvDownloadNow.setText(String.format(context.getString(R.string.download_now), totalPrice));
    }


    /**
     * 获取真实的下载所需书豆（去除已下载章节）
     *
     * @return
     */
    private int getRealDownloadBeans(int count) {

        if (downloadList == null || downloadList.isEmpty()) {
            return count * price;
        }

        int readCount = count;
        for (String sNum : downloadList) {

            int num = Integer.valueOf(sNum);
            if (num > seqNum && num <= seqNum + count) {
                readCount--;
                continue;
            }

        }

        return readCount;
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        layoutDownloadNow.setClickable(false);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
        layoutDownloadNow.setClickable(true);
    }

    @Override
    public void downloadCheckSuccess(ChapterDownloadCheckResp chapterDownloadCheckResp) {
        if (chapterDownloadCheckResp.getStatus() == 0) {
            //可以下载
            presenter.getDownloadChapterList(Long.valueOf(bookDetailBean.getBookId()), seqNum, totalChapter, bookDetailBean.getBookName());
            FuncPageStatsApi.downloadChapter(prePageId, Long.valueOf(bookDetailBean.getBookId()), "1", source, String.valueOf(totalChapter), PageNameConstants.DOWNLOADPOP);
        } else {
            //书豆不够，不能下载
            layoutDownloadNow.setClickable(true);
            ToastUtils.show("书豆余额不足");
            FuncPageStatsApi.downloadChapter(prePageId, Long.valueOf(bookDetailBean.getBookId()), "2", source, String.valueOf(totalChapter), PageNameConstants.DOWNLOADPOP);
        }
    }

    @Override
    public void downloadCheckFailed() {
        layoutDownloadNow.setClickable(true);
        FuncPageStatsApi.downloadChapter(prePageId, Long.valueOf(bookDetailBean.getBookId()), "2", source, String.valueOf(totalChapter), PageNameConstants.DOWNLOADPOP);
    }
}
