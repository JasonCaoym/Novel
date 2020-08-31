package com.duoyue.app.common.mgr;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import com.duoyue.app.common.data.request.bookshelf.TaskFinishReq;
import com.duoyue.app.common.data.request.bookshelf.TaskListReq;
import com.duoyue.app.common.data.response.bookshelf.TaskFinishResp;
import com.duoyue.app.common.data.response.bookshelf.TaskListResp;
import com.duoyue.app.event.TaskFinishEvent;
import com.duoyue.app.service.CountDownService;
import com.duoyue.app.ui.view.BookDoTaskFragment;
import com.duoyue.app.ui.view.SignVidioTaskFragment;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.PushConfiguration;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.SharePreferenceUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 任务管理类
 */
public class TaskMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#TaskMgr";
    public static final int READ_TASK = 11;
    public static final int SHARE_TASK = 12;
    public static final int MAARK_TASK = 9;
    public static final int LOGIN_TASK = 8;
    public static final int SIGN_TASK = 1;
    public static final int BATTERY_TASK = 14;
    public static final int REWARD_VIDEO_TASK = 15;
    public static final int REWARD_VIDEO_SIGN_TASK = 16;
    public static final int REWARD_VIDEO_EXIT_TASK = 17;
    public static final int TIRED_REWARD_VIDEO_TASK = 18;
    public static final int READ_FIRST_TASK = 19;
    public static final int READ_SECOND_TASK = 20;
    public static final int READ_THIRD_TASK = 21;

    /**
     * 任务完成状态KEY的前缀，后面拼接TASK_ID
     */
    private static final String TASK_AVAILIABLE = "availiable_task_id_";

    /**
     * 获取任务是否有效的key
     * @param taskId
     * @return
     */
    public static String getAvailiableTask(int taskId) {
        return TASK_AVAILIABLE + taskId;
    }

    /**
     * 当前类对象
     */
    private static volatile TaskMgr sInstance;

    private TaskMgr() {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance() {
        if (sInstance == null) {
            synchronized (TaskMgr.class) {
                if (sInstance == null) {
                    sInstance = new TaskMgr();
                }
            }
        }
    }

    /**
     * 完成任务,调用该方法弹框
     *
     * @param context
     * @param fragmentManager
     * @param dialogContent
     */
    public static void show(final Context context, final FragmentManager fragmentManager, final String dialogContent, final int taskId) {
        Single.fromCallable(new Callable<TaskFinishResp>() {
            @Override
            public TaskFinishResp call() throws Exception {
                TaskFinishResp taskFinishResp = TaskMgr.taskFinish(context, taskId);
                return taskFinishResp;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<TaskFinishResp>() {
            @Override
            public void accept(TaskFinishResp taskFinishResp) throws Exception {
                if (taskFinishResp != null) {
                    if (taskFinishResp.getBookBean() != 0) {//完成任务,弹框
                        if (taskId == REWARD_VIDEO_SIGN_TASK) {
                            showVidioSignDialog(taskFinishResp.getBookBean(), fragmentManager, taskId);
                        } else {
                            showDialog(taskFinishResp.getBookBean(), dialogContent, fragmentManager, taskId);
                        }
                    } else {

                    }
                    // 存储该任务是否已经完成,3已完成
                    SPUtils.INSTANCE.putBoolean(TaskMgr.getAvailiableTask(taskId), taskFinishResp.getStatus() != 3);
                    // 通知刷新书豆
                }
            }
        });
    }

    /**
     * 签到激励视频翻倍弹框
     *
     * @param bookBean
     * @param fragmentManager
     * @param taskId
     */
    public static void showVidioSignDialog(int bookBean,  FragmentManager fragmentManager, int taskId) {
        SignVidioTaskFragment signVidioTaskFragment = new SignVidioTaskFragment();
        signVidioTaskFragment.setValue(bookBean);
        signVidioTaskFragment.show(fragmentManager.beginTransaction(), "taskFinish");
        EventBus.getDefault().post(new TaskFinishEvent(taskId));
    }

    /**
     * 弹书豆框
     *
     * @param bookBean
     * @param dialogContent
     * @param fragmentManager
     * @param taskId
     */
    public static void showDialog(int bookBean, String dialogContent, FragmentManager fragmentManager, int taskId) {
        BookDoTaskFragment bookDoTaskFragment = new BookDoTaskFragment();
        bookDoTaskFragment.setValue(dialogContent, bookBean, false, taskId);
        bookDoTaskFragment.show(fragmentManager.beginTransaction(), "taskFinish");
        EventBus.getDefault().post(new TaskFinishEvent(taskId));
    }

    /**
     * 任务完成接口
     */
    public static TaskFinishResp taskFinish(Context context, int taskId) {
        List<TaskListResp.ListBean> list = SharePreferenceUtils.getList(context, SharePreferenceUtils.TASK_LIST_CACHE);
        if (list == null || list.size() == 0 || !list.contains(new TaskListResp.ListBean(taskId))) {//没有下发任务
            return null;
        }

        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Logger.e(TAG, "taskFinish: 网络不可用.");
            return null;
        }
        //创建当前类对象.
        createInstance();

        try {
            JsonResponse<TaskFinishResp> jsonResponse = new JsonPost.SyncPost<TaskFinishResp>().setRequest(new TaskFinishReq(taskId)).setResponseType(TaskFinishResp.class).post();

            //判断是否响应数据成功.
            if (jsonResponse == null || jsonResponse.status != 1) {
                return null;
            }
            TaskFinishResp taskFinishResp = jsonResponse.data;
            return taskFinishResp;
        } catch (Throwable throwable) {

            return null;
        }

    }

    /**
     * 任务列表接口
     */
    public static void taskList(final Context context) {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Logger.e(TAG, "taskList: 网络不可用.");
            return;
        }
        //创建当前类对象.
        createInstance();

        try {

            new JsonPost.AsyncPost<TaskListResp>()
                    .setRequest(new TaskListReq())
                    .setResponseType(TaskListResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .post(new DisposableObserver<JsonResponse<TaskListResp>>() {
                        @Override
                        public void onNext(JsonResponse<TaskListResp> taskListRespJsonResponse) {
                            if (taskListRespJsonResponse != null && taskListRespJsonResponse.status == 1) {
                                Logger.e(TAG, "------------taskList: 成功， \nbody: "
                                        + new Gson().toJson(taskListRespJsonResponse.data.getList()));
                                //缓存
                                List<List<TaskListResp.ListBean>> list = taskListRespJsonResponse.data.getList();
                                if (list == null || list.size() == 0) return;
                                List<TaskListResp.ListBean> listBeans = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    listBeans.addAll(list.get(i));
                                }
                                for (TaskListResp.ListBean bean : listBeans) {
                                    SPUtils.INSTANCE.putBoolean(getAvailiableTask(bean.getTaskId()), bean.getStatus() != 3);
                                    if (bean.getTaskId() == TIRED_REWARD_VIDEO_TASK) {
                                        SPUtils.INSTANCE.putInt(SPUtils.INSTANCE.getTIRED_SHUDOU(), StringFormat.parseInt(bean.getBookBean(), 0));
                                    }
                                }
                                SharePreferenceUtils.putList(context, SharePreferenceUtils.TASK_LIST_CACHE, listBeans);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(TAG, "taskList: 错误");
                        }

                        @Override
                        public void onComplete() {
                            Logger.e(TAG, "taskList: 完成");
                        }
                    });


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    /**
     * 获取阅读历史记录.
     */
    public static void getReadHistory(final Context context) {
        Single.fromCallable(new Callable<BookRecordGatherResp>() {
            @Override
            public BookRecordGatherResp call() throws Exception {
                BookRecordGatherResp bookRecordGather = ReadHistoryMgr.getBookRecordGather();
                return bookRecordGather;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<BookRecordGatherResp>() {
            @Override
            public void accept(BookRecordGatherResp bookRecordGatherResp) throws Exception {
                if (bookRecordGatherResp != null) {
                    CountDownService.setTotalTime(bookRecordGatherResp.getLastSec());
                    SharePreferenceUtils.putObject(context, SharePreferenceUtils.READ_HISTORY_CACHE, bookRecordGatherResp);
                }
            }
        });
    }
}
