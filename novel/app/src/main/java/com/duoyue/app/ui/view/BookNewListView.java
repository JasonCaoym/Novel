package com.duoyue.app.ui.view;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.bean.BookNewBookBean;
import com.duoyue.app.bean.BookNewBookListBean;
import com.duoyue.app.ui.activity.TaskWebViewActivity;
import com.duoyue.app.ui.adapter.BookNewListAdapter;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.item.AbsItemView;

import java.util.ArrayList;
import java.util.List;


public class BookNewListView extends AbsItemView<BookNewBookListBean> {

    private RecyclerView recyclerView;
    private BookNewListAdapter bookNewListAdapter;

    private List<BookNewBookBean> bookNewBookBeans;

    private GridLayoutManager pullableLayoutManager;

    private TextView textView;

    @Override
    public void onCreate() {
        setContentView(R.layout.item_book_new_list);

        initViews();

    }

    private void initViews() {
        textView = mItemView.findViewById(R.id.tv_more);
        textView.setOnClickListener(this);
        recyclerView = mItemView.findViewById(R.id.rv_hot_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        bookNewBookBeans = new ArrayList<>();
        bookNewListAdapter = new BookNewListAdapter(mActivity, bookNewBookBeans, this);
        recyclerView.setAdapter(bookNewListAdapter);
        pullableLayoutManager = new GridLayoutManager(mActivity, 2);
        pullableLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (i == 0 || i == 1) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        recyclerView.setLayoutManager(pullableLayoutManager);
    }

    @Override
    public void onClick(View view) {
        if (Utils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.xrl_bg:
            case R.id.xll_item_two:
                Intent intent = new Intent(mActivity, TaskWebViewActivity.class);
                String url = TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLISTDETAIL)) ? "http://taskcenter.duoyueapp.com/readListDetail/" : AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLISTDETAIL);
                intent.putExtra("url", url + "?"
                        + getLoginInfo() + "&id=" + bookNewBookBeans.get((int) view.getTag()).getId());
                mActivity.startActivity(intent);
                FuncPageStatsApi.nearBookListClick(bookNewBookBeans.get((int) view.getTag()).getId()/*,view.getId() == R.id.xrl_bg ? 1 : 3*/);
                break;

            case R.id.tv_go_more:
            case R.id.tv_more:
                Intent intent2 = new Intent(mActivity, TaskWebViewActivity.class);
                String url2 = TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLIST)) ? "http://taskcenter.duoyueapp.com/readList/" : AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOKLIST);
                intent2.putExtra("url", url2 + "?" + getLoginInfo());
                mActivity.startActivity(intent2);
//                FuncPageStatsApi.nearBookListClick(bookNewBookBeans.get((int) view.getTag()).getId(), 3);
                break;
        }
    }


    /**
     * 获取参数
     */
    private String getLoginInfo() {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        Boolean isLogin;
        if (userInfo == null) return "";
        isLogin = userInfo.type != 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("uid=").append(userInfo.uid)
                .append("&version=").append(PhoneStatusManager.getInstance().getAppVersionName())
                .append("&appId=").append(Constants.APP_ID)
                .append("&channelCode=").append(PhoneStatusManager.getInstance().getAppChannel())
                .append("&isLogin=").append(isLogin)
                .append("&token=").append(userInfo.token)
                .append("&mid=").append(UserManager.getInstance().getMid());

        return stringBuilder.toString();
    }

    @Override
    public void onSetData(boolean isFirstSetData, boolean isPosChanged, boolean isDataChanged) {
        if (isDataChanged) {
            bookNewBookBeans.clear();
            bookNewBookBeans.addAll(mItemData.getList());
            bookNewListAdapter.notifyDataSetChanged();
        }

    }
}
