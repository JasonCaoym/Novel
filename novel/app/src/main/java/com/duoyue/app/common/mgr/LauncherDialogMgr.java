package com.duoyue.app.common.mgr;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.duoyue.app.bean.LauncherDialogBean;
import com.duoyue.app.common.data.request.bookcity.LauncherDialogReq;
import com.duoyue.app.ui.view.RecommandDialog;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LauncherDialogMgr {

    private static final String KEY_DATE = "show_date";

    public static void requestData(final FragmentActivity activity, final String currPageId) {
        // 新用户第一次进来不推书
        if (Constants.IS_AIGAO && SPUtils.INSTANCE.getBoolean(SPUtils.INSTANCE.getSHARED_FIRST_SHOW_RECOMMAND_BOOK(), true)) {
            SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getSHARED_FIRST_SHOW_RECOMMAND_BOOK(), false);
            return;
        }
        // 一天只显示一次
        if (SPUtils.INSTANCE.getString(KEY_DATE).equals(TimeUtils.today())) {
            return;
        }

        LauncherDialogReq request = new LauncherDialogReq();
        new JsonPost.AsyncPost<LauncherDialogBean>()
                .setRequest(request)
                .setResponseType(LauncherDialogBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<LauncherDialogBean>>() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(JsonResponse<LauncherDialogBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null  && jsonResponse.data.getBookId() != 0
                                && !activity.isFinishing() && !activity.isDestroyed()) {
                            showDialog(activity, jsonResponse.data,currPageId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("App#LauncherDialog", "启动弹框失败： " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void showDialog(final FragmentActivity activity, final LauncherDialogBean data,String currPageId) {
        // 数据类型转换
        RecommandBean recommandBean = new RecommandBean();
        recommandBean.setBookId(data.getBookId());
        recommandBean.setCover(data.getCover());
        recommandBean.setBookName(data.getBookName());
        recommandBean.setAuthorName(data.getAuthorName());
        recommandBean.setJumpType(3);
        recommandBean.setWeekDownPvMsg("" + data.getWeekDownPv());
        recommandBean.setChapterTitle(data.getResume());

        final RecommandDialog recommandDialog = new RecommandDialog();
        if (recommandDialog.canShowDialog()) {
            Logger.e("App#", "显示启动弹窗");
            SPUtils.INSTANCE.putBoolean(RecommandDialog.KEY_HAS_LAUNCHER_DIALOG, false);
            Bundle bundle = new Bundle();
            bundle.putBoolean(RecommandDialog.KEY_DIALOG_TYPE, true);
            recommandDialog.setArguments(bundle);
            recommandDialog.setData(recommandBean);
            recommandDialog.setCurrPageId(currPageId);
            recommandDialog.setCancelListener(new RecommandDialog.CancelListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void cancel() {
                    if (!activity.isDestroyed() && SPUtils.INSTANCE.getBoolean(RecommandDialog.KEY_HAS_COMMAND_DIALOG, false)) {
                        RecommandDialog recommandDialog = new RecommandDialog();
                        RecommandBean commandBean = JsonUtils.parseJson(SPUtils.INSTANCE.getString(RecommandDialog.KEY_DATA_JSON), RecommandBean.class);
                        if (commandBean != null) {
                            // 有启动弹窗时，跳过，等待下次触发
                            recommandDialog.setData(commandBean);
                            recommandDialog.show(activity.getSupportFragmentManager(), "recommand");
                        }
                    }
                }
            });
            recommandDialog.show(activity.getSupportFragmentManager(), "launcher");
            SPUtils.INSTANCE.putString(KEY_DATE, TimeUtils.today());
        } else {
            Logger.e("App#", "不显示启动弹窗");
            SPUtils.INSTANCE.putBoolean(RecommandDialog.KEY_HAS_LAUNCHER_DIALOG, true);
            SPUtils.INSTANCE.putString(RecommandDialog.KEY_DATA_JSON, JsonUtils.toJson(data));
        }
    }

}
