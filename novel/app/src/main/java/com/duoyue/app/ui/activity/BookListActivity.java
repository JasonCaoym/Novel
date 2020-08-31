package com.duoyue.app.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.ui.fragment.BookListFragment;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.ad.utils.AdConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.utils.ViewUtils;
import org.jetbrains.annotations.Nullable;

public class BookListActivity extends BaseActivity {

    private int adSite;
    private String currPageId;
    private int mChan;
    private String mChannel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);

        initView();
    }

    @Override
    public String getCurrPageId() {
        return currPageId;
    }

    private void initView() {
        String title = getIntent().getStringExtra(BaseActivity.DATA_KEY);
//        currPageId = getIntent().getStringExtra(BaseActivity.DATA_KEY);
        mChan = getIntent().getIntExtra("chan", -1);
        currPageId = getIntent().getStringExtra(ListAdapter.EXT_KEY_CURRENT_PAGE_ID);
        mChannel = getIntent().getStringExtra(BookExposureMgr.PAGE_CHANNEL);
//        if (!TextUtils.isEmpty(title)) {
//            TextView tvTile = findView(R.id.page_title);
//            tvTile.setText(title);
//            //Typeface typeFace = TitleTypeface.getTypeFace(getApplication());
//            tvTile.setTextSize(18f);
//            //tvTile.setTypeface(typeFace);
//        }
//        findView(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setToolBarLayout(title);
        adSite = getIntent().getIntExtra("adSite", 0);

        BookListFragment listFragment = new BookListFragment();
        //设置标题.
        listFragment.setTitle(title + "-" + ViewUtils.getString(adSite == AdConstants.Position.BOOK_FINISH ? R.string.entrances_complete : R.string.entrances_new));
        Bundle bundle = new Bundle();
        bundle.putInt("type", getIntent().getIntExtra("type", 0));
        bundle.putInt("adSite", adSite);
        String parentId = getIntent().getStringExtra(ListAdapter.EXT_KEY_PARENT_ID);
        bundle.putString(ListAdapter.EXT_KEY_PARENT_ID, parentId);
        bundle.putString(BookExposureMgr.PAGE_CHANNEL, mChannel);
        bundle.putInt("chan", mChan);
        bundle.putInt(ListAdapter.EXT_KEY_MODEL_ID, adSite == AdConstants.Position.BOOK_FINISH ? 8 : 7);
        listFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, listFragment);
        transaction.commit();
    }

}
