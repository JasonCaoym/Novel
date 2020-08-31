package com.duoyue.mianfei.xiaoshuo.mine.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duoyue.app.bean.FeedConfigBean;
import com.duoyue.app.bean.FeedConfigItemBean;
import com.duoyue.app.ui.adapter.FeedConfigRecyclerAdapter;
import com.duoyue.app.ui.view.GridSpacingItemDecoration;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.presenter.QuestionPresenter;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends BaseFragment implements IQuestionPage {

    private LinearLayout questionLayout;
    /**
     * 问题反馈类型列表
     */
    private RecyclerView recyclerView;
    /**
     * 问题描述字数
     */
    private TextView tvQuesionCount;
    /**
     * 问题描述内容
     */
    private EditText etQuesionContent;
    /**
     * 联系方式
     */
    private EditText etContact;
    /**
     * 提交按钮
     */
    private TextView tvCommit;
    /**
     * 无数据时显示
     */
    private LinearLayout promptLayout;

    private QuestionPresenter questionPresenter;

    private PromptLayoutHelper mPromptLayoutHelper;

    private List<FeedConfigItemBean> configItemBeanList;

    private FeedConfigRecyclerAdapter adapter;

    /**
     * 问题描述最大字数
     */
    private int maxCount = 500;


    @Override
    public void onCreateView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_question);

        initView();
        initData();
    }

    private void initView() {
        questionLayout = findView(R.id.question_layout);
        recyclerView = findView(R.id.recycler_view_quesion);
        tvQuesionCount = findView(R.id.tv_quesion_count);
        etQuesionContent = findView(R.id.et_quesion_content);
        etContact = findView(R.id.et_contact);
        tvCommit = findView(R.id.tv_commit);
        promptLayout = findView(R.id.prompt_layout);

        int spanCount = 2; // 2 columns
        int spacing = 45; // 45px
        boolean includeEdge = false;
        GridLayoutManager layoutManage = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManage);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        //创建Adapter
        adapter = new FeedConfigRecyclerAdapter(getActivity());
        adapter.setOnItemClickListener(new FeedConfigRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                adapter.selectedItem(postion);
            }
        });
        recyclerView.setAdapter(adapter);

        etQuesionContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*告诉父组件不要拦截他的触摸事件*/
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    /*告诉父组件可以拦截他的触摸事件*/
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });

        etQuesionContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCount)});
        etQuesionContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvQuesionCount.setText(s.length() + "/" + maxCount);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvQuesionCount.setText(etQuesionContent.getText().toString().length() + "/" + maxCount);

        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isFastClick()) {
                    return;
                }
                if (adapter != null && adapter.getSelectedBean() != null) {
                    if (etQuesionContent.getText().toString().trim().length() > 0) {
                        if (TextUtils.isEmpty(etContact.getText().toString().trim())) {
                            ToastUtils.show("请留下你的联系方式！");
                        } else {
                            showLoading();
                            questionPresenter.commitProblem(adapter.getSelectedBean().getId(),
                                    adapter.getSelectedBean().getContent(),
                                    etQuesionContent.getText().toString(),
                                    etContact.getText().toString());
                        }
                    } else {
                        ToastUtils.show("请描述您反馈的问题或建议！");
                    }
                } else {
                    ToastUtils.show("请选择反馈类型！");
                }
            }
        });
    }

    private void initData() {

        showLoading();
        configItemBeanList = new ArrayList<>();
        questionPresenter = new QuestionPresenter(this);
        questionPresenter.getProblemList();
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    @Override
    public void showLoading() {
        promptLayout.setVisibility(View.VISIBLE);
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        promptLayout.setVisibility(View.GONE);
        getPromptLayoutHelper().hide();
    }

    @Override
    public void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showLoading();
                questionPresenter.getProblemList();
            }
        });
        promptLayout.setVisibility(View.VISIBLE);
        questionLayout.setVisibility(View.GONE);

    }

    @Override
    public void showEmpty() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        promptLayout.setVisibility(View.VISIBLE);
        questionLayout.setVisibility(View.GONE);
    }

    @Override
    public void showProblemList(FeedConfigBean feedConfigBean) {
        questionLayout.setVisibility(View.VISIBLE);
        configItemBeanList.clear();
        configItemBeanList.addAll(feedConfigBean.getConfigList());
        if (adapter != null) {
            adapter.updateData(configItemBeanList);
        }
    }

    @Override
    public void onCommitSuccess() {
        ToastUtils.show("提交成功！");
        getActivity().finish();
    }

}
