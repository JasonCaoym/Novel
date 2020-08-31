package com.duoyue.mianfei.xiaoshuo.mine.ui;

import android.os.Bundle;
import android.view.View;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import org.jetbrains.annotations.Nullable;

public class QuestionActivity extends BaseActivity {
    private QuestionFragment mQuestionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        if (mQuestionFragment == null) {
            mQuestionFragment = new QuestionFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.question_root, mQuestionFragment).commit();
        }
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.FEEDBACK;
    }

    @Override
    public void initStateBar(@Nullable View layoutTitle) {
        super.initStateBar(layoutTitle);
        setToolBarLayout(R.string.title_question_commit);
    }
}
