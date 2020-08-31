package com.duoyue.app.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoyue.app.common.mgr.PushMgr;
import com.duoyue.app.event.DayEvent;
import com.duoyue.app.event.UpdateEvent;
import com.duoyue.app.notification.data.NotifiyBookResultBookListBean;
import com.duoyue.app.notification.data.NotifiyBookResultV2Bean;
import com.duoyue.app.notification.presenters.NotificationHolderV2Presenter;
import com.duoyue.app.presenter.ReadHistoryPresenter;
import com.duoyue.app.splash.SplashActivity;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.common.MainApplication;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.ParamKey;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotificationHolderV2Service extends Service {

    private static final String TAG = "NotificationHolderV2Service";

    public static final String INTENT_KEY = "INTENT_KEY";
    public static final String BOOKID = "bookid";

    public static final int NOTIFICATION_DEFAULT = 960;
    public static final int NOTIFICATION_DAY_CODE_ID = 961;
    public static final int NOTIFICATION_CONTINUE_READING_CODE_ID = 962;
    public static final int NOTIFICATION_MORE_REMINDER_CODE_ID = 963;

    /**
     * 每日推荐换一换
     */
    private static final int DAY_CODE = 102;
    /**
     * 金立手机拉活
     */
    public static final int LIVE_CODE = 103;
    public static final int SPPASH_CODE = 104;
    private static final String DATE = "PULL_LIVE_DATE";
    public static final String ALIVE = "ALIVE";


    private Handler handler = new Handler();

    private long mLastClickTime = 0;
    private static final long TIME_INTERVAL = 1000L;

    private NotificationManager mNotificationManager;

    //每日推荐
    private Notification notification;
    private RemoteViews remoteViews;
    private Intent huan_Intent;
    //继续阅读
    private Notification goNotification;
    private RemoteViews goRead;
    //追更提醒
    private Notification updateNotification;
    private RemoteViews updateRemoteViews;

    private final static int CODE_NOTIFICATION = 0x121;

    private NotificationHolderV2Presenter notificationHolderV2Presenter;

    private PendingIntent pendingIntent;

    private boolean isData;

    private final StringBuilder listId = new StringBuilder();

    private boolean isLive;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        notificationHolderV2Presenter = new NotificationHolderV2Presenter(onDataListener);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //兼容android 8.0以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //每日推荐
            NotificationChannel mediaChannel = new NotificationChannel(getString(R.string.notification_hold_channel_id), getString(R.string.notification_hold_channel_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mediaChannel.setSound(null, null);
            mediaChannel.setVibrationPattern(null);
            mNotificationManager.createNotificationChannel(mediaChannel);
            //继续阅读
            NotificationChannel mediaChanne2 = new NotificationChannel(getString(R.string.notification_continue_reading_id), getString(R.string.notification_continue_reading_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mediaChanne2.setSound(null, null);
            mediaChanne2.setVibrationPattern(null);
            mNotificationManager.createNotificationChannel(mediaChanne2);
            //追更提醒
            NotificationChannel mediaChanne3 = new NotificationChannel(getString(R.string.notification_more_reminder_id), getString(R.string.notification_more_reminder_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mediaChanne3.setSound(null, null);
            mediaChanne3.setVibrationPattern(null);
            mNotificationManager.createNotificationChannel(mediaChanne3);

            //默认通知栏
            NotificationChannel mediaChanne4 = new NotificationChannel(getString(R.string.notification_default_channel_id), getString(R.string.notification_default_channel_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mediaChanne4.setSound(null, null);
            mediaChanne4.setVibrationPattern(null);
            mNotificationManager.createNotificationChannel(mediaChanne4);
        }
        startForeground(CODE_NOTIFICATION, new Notification());

        initNotification();
        statsAliveTime();
    }

    /**
     * 活跃时长
     */
    private void statsAliveTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currPageId = "";
                Logger.e("statsAliveTime", "是否在前台界面： ");
                if (MainApplication.Companion.isBackToForeground()) {
                    currPageId = BaseApplication.context.getCurrPageId();
                } else {
                    currPageId = PageNameConstants.BACKGROUND;
                }
                FuncPageStatsApi.aliveTime(currPageId);
                handler.postDelayed(this, 60_000);
            }
        });
    }

    private void initNotification() {
        boolean areNotificationsEnabled = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            if (areNotificationsEnabled) {
                Logger.e(TAG, "通知栏已经开启");
            } else {
                Logger.e(TAG, "通知栏已经关闭，开始跳转");
            }
        }
        //后台是否开启通知栏
        if (AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.NOTIFY_ENABLE, 0) == 0) {
            initDefaultNotification();
        } else {
            initDayViews();
        }
    }

    /**
     * 默认通知栏
     */
    void initDefaultNotification() {
        NotificationCompat.Builder defaultBuilder = new NotificationCompat.Builder(getBaseContext(),
                getString(R.string.notification_default_channel_id));
        RemoteViews defaultView = new RemoteViews(getPackageName(), R.layout.notification_hold_default);
        Notification defaultNotification = defaultBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher)
                .setTicker(getString(R.string.notification_default_title))
                .setContentIntent(PendingIntent.getActivity(NotificationHolderV2Service.this, 0, intentDefault(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setCustomContentView(defaultView).build();
        defaultNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFICATION_DEFAULT, defaultNotification);
    }

    /**
     * 每日推荐
     */
    void initDayViews() {
        final String time = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.NOTIFICATION_BAR_REFRESH_INTERVAL);
        if (!TextUtils.isEmpty(time) && !time.equals("0")) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                    getString(R.string.notification_hold_channel_id));
            remoteViews = new RemoteViews(getPackageName(), R.layout.notification_holder_new);
            if (!TextUtils.isEmpty(PushMgr.getPhoneBrand()) && PushMgr.getPhoneBrand().equals("OPPO")) {
                remoteViews.setTextColor(R.id.notification_hold_new_name1, Color.WHITE);
                remoteViews.setTextColor(R.id.notification_hold_new_name2, Color.WHITE);
            }
            huan_Intent = new Intent(NotificationHolderV2Service.this, NotificationHolderV2Service.class);
            huan_Intent.putExtra(INTENT_KEY, DAY_CODE);
            pendingIntent = PendingIntent.getService(this, 0, huan_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_huan, pendingIntent);

            notification = mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                    .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher)
                    .setTicker(getString(R.string.notification_hold_title))
                    .setWhen(System.currentTimeMillis())
                    .setCustomContentView(remoteViews)
                    .build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationHolderV2Presenter.loadDayData(listId.toString());
                    handler.postDelayed(this, Long.valueOf(time) * 60 * 1000);
                }
            });
//            mNotificationManager.notify(NOTIFICATION_DAY_CODE_ID, notification);
        } else {
            initGoRead();
        }
    }

    /**
     * 拉活 每日推荐
     */
    void initLiveDayViews() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                getString(R.string.notification_hold_channel_id));
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_holder_new);
        if (!TextUtils.isEmpty(PushMgr.getPhoneBrand()) && PushMgr.getPhoneBrand().equals("OPPO")) {
            remoteViews.setTextColor(R.id.notification_hold_new_name1, Color.WHITE);
            remoteViews.setTextColor(R.id.notification_hold_new_name2, Color.WHITE);
        }
        huan_Intent = new Intent(NotificationHolderV2Service.this, NotificationHolderV2Service.class);
        huan_Intent.putExtra(INTENT_KEY, DAY_CODE);
        pendingIntent = PendingIntent.getService(this, 0, huan_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_huan, pendingIntent);

        notification = mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher)
                .setTicker(getString(R.string.notification_hold_title))
                .setWhen(System.currentTimeMillis())
                .setCustomContentView(remoteViews)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        handler.post(new Runnable() {
            @Override
            public void run() {
                notificationHolderV2Presenter.loadDayData(listId.toString());
                handler.postDelayed(this, 60 * 60 * 1000);
            }
        });
        mNotificationManager.notify(NOTIFICATION_DAY_CODE_ID, notification);
    }

    /**
     * 继续阅读
     */
    void initGoRead() {
        final String time = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.NOTIFICATION_BAR_READING_INTERVAL);
        if (remoteViews == null) {
            if (!TextUtils.isEmpty(time) && !time.equals("0")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initContinueRead();
                        handler.postDelayed(this, Long.valueOf(time) * 60 * 1000);
                    }
                });
            } else {
                initDefaultNotification();
            }
        }
    }


    void initContinueRead() {
        List<BookRecordBean> bookRecordBeanList = ReadHistoryPresenter.getPageHistoryDataList(0);
        if (bookRecordBeanList != null && !bookRecordBeanList.isEmpty()) {
            BookRecordBean bookRecordBean = bookRecordBeanList.get(0);
            if (bookRecordBean != null) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                        getString(R.string.notification_continue_reading_id));
                goRead = new RemoteViews(getPackageName(), R.layout.notification_go_read);

                goNotification = mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                        .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher).setAutoCancel(true)
                        .setTicker(getString(R.string.notification_continue_reading_title))
                        .setContentIntent(intentBookDetail(Long.valueOf(bookRecordBean.getBookId()), 0, "8", 2))
                        .setWhen(System.currentTimeMillis())
                        .setCustomContentView(goRead)
                        .build();
                Glide
                        .with(NotificationHolderV2Service.this).asBitmap()
                        .load(bookRecordBean.getBookCover()).apply(initRequestOptions(35, 47))
                        .into(initNotificationTarget(R.id.iv_icon,
                                goRead,
                                goNotification,
                                NOTIFICATION_CONTINUE_READING_CODE_ID));
                if (!TextUtils.isEmpty(PushMgr.getPhoneBrand()) && PushMgr.getPhoneBrand().equals("OPPO")) {
                    goRead.setTextColor(R.id.tv_name, Color.WHITE);
                    //兼容OPPO手机  有些OPPO手机无法显示
                    goNotification.flags |= Notification.FLAG_ONGOING_EVENT;
                }
                goRead.setTextViewText(R.id.tv_name, bookRecordBean.getBookName());
                goRead.setTextViewText(R.id.tv_zhang, bookRecordBean.getChapterTitle());
                mNotificationManager.notify(NOTIFICATION_CONTINUE_READING_CODE_ID, goNotification);
                FuncPageStatsApi.pullNotification(Integer.valueOf(bookRecordBean.getBookId()), 2);
            }
        }
    }


    /**
     * 追更提醒
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initUpdateEvent(UpdateEvent updateEvent) {
        if (AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.NOTIFY_ENABLE, 0) == 0) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                getString(R.string.notification_more_reminder_id));
        updateRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_update_read);
        updateNotification = mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher).setAutoCancel(true)
                .setTicker(getString(R.string.notification_more_reminder_title))
                .setContentIntent(intentBookDetail(updateEvent.getBookid(), 30, "1", 1))
                .setWhen(System.currentTimeMillis()).setCustomContentView(updateRemoteViews).build();
        Glide
                .with(NotificationHolderV2Service.this).asBitmap()
                .load(updateEvent.getIcon()).apply(initRequestOptions(35, 47))
                .into(initNotificationTarget(R.id.iv_icon,
                        updateRemoteViews,
                        updateNotification,
                        NOTIFICATION_MORE_REMINDER_CODE_ID));
        if (!TextUtils.isEmpty(PushMgr.getPhoneBrand()) && PushMgr.getPhoneBrand().equals("OPPO")) {
            updateRemoteViews.setTextColor(R.id.tv_name, Color.WHITE);
            updateRemoteViews.setTextColor(R.id.tv_time, Color.WHITE);
        }
        updateRemoteViews.setTextViewText(R.id.tv_name, updateEvent.getBookName());
        updateRemoteViews.setTextViewText(R.id.tv_zhang, "已更新至 第" + updateEvent.getChapter() + "章");
        updateRemoteViews.setTextViewText(R.id.tv_time, TimeUtils.MD_FORMAT_X.format(System.currentTimeMillis()));
        mNotificationManager.notify(NOTIFICATION_MORE_REMINDER_CODE_ID, updateNotification);
        FuncPageStatsApi.pullNotification((int) updateEvent.getBookid(), 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initUpdateDayEvent(DayEvent dayEvent) {
        isLive = false;
        notificationHolderV2Presenter.loadDayData(listId.toString());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(INTENT_KEY, -1)) {
                case DAY_CODE:
                    // 1 正在加载不可点击 2 不可连续重复点击  通知栏刷新太频繁会出问题
                    if (!isData) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime - mLastClickTime > TIME_INTERVAL) {
                            isData = true;
                            notificationHolderV2Presenter.loadDayData(listId.toString());
                            mLastClickTime = nowTime;
                        } else {
                            Logger.d(TAG, "不可重复点击");
                        }
                    } else {
                        Logger.d(TAG, "正在请求数据，不可重复请求数据");
                    }
                    break;
                case LIVE_CODE:
                    if (TextUtils.isEmpty(SPUtils.INSTANCE.getString(DATE)) || !SPUtils.INSTANCE.getString(DATE).equals(TimeUtils.today())) {
                        SPUtils.INSTANCE.putString(DATE, TimeUtils.today());
                        UserManager userManager = UserManager.getInstance();
                        if (userManager.getUserInfo() == null) {
                            FuncPageStatsApi.pullAlive();
                        } else {
                            if (TextUtils.isEmpty(userManager.getUserInfo().uid)) {
                                FuncPageStatsApi.pullAlive();
                            } else {
                                FuncPageStatsApi.pullOutAlive();
                            }
                        }

                    }
                    isLive = true;
                    try {
                        mNotificationManager.cancelAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    initLiveDayViews();
                    break;
                default:

                    break;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationHolderV2Presenter.OnDataListener onDataListener = new NotificationHolderV2Presenter.OnDataListener() {
        @Override
        public void onDayData(NotifiyBookResultBookListBean resultV2Bean) {
            if (resultV2Bean.getBookList() == null) return;
            if (remoteViews == null) return;
            isData = false;
            if (!TextUtils.isEmpty(listId.toString())) {
                listId.delete(0, listId.length());
            }
            if (!resultV2Bean.getBookList().isEmpty() && resultV2Bean.getBookList().get(0) != null) {
                NotifiyBookResultV2Bean notifiyBookResultV2Bean = resultV2Bean.getBookList().get(0);
                if (notifiyBookResultV2Bean == null) return;
                remoteViews.setTextViewText(R.id.notification_hold_new_name1, notifiyBookResultV2Bean.getBookName());
                remoteViews.setTextViewText(R.id.notification_hold_new_grade1, notifiyBookResultV2Bean.getStar() + "分");
                Glide
                        .with(NotificationHolderV2Service.this).asBitmap()
                        .load(notifiyBookResultV2Bean.getCover()).apply(initRequestOptions(35, 47))
                        .into(initNotificationTarget(R.id.notification_hold_new_img1,
                                remoteViews,
                                notification,
                                NOTIFICATION_DAY_CODE_ID));
                listId.append(notifiyBookResultV2Bean.getBookId());
                listId.append(",");
                if (isLive) {
                    remoteViews.setOnClickPendingIntent(R.id.notification_hold_new_first_layout, intentSplash());
                } else {
                    remoteViews.setOnClickPendingIntent(R.id.notification_hold_new_first_layout, intentBookDetail(notifiyBookResultV2Bean.getBookId(), 10, "1", 3));
                }
                FuncPageStatsApi.pullNotification(notifiyBookResultV2Bean.getBookId(), 3);
            }
            if (!resultV2Bean.getBookList().isEmpty() && resultV2Bean.getBookList().size() >= 1 && resultV2Bean.getBookList().get(1) != null) {
                NotifiyBookResultV2Bean notifiyBookResultV2Bean = resultV2Bean.getBookList().get(1);
                if (notifiyBookResultV2Bean == null) return;
                remoteViews.setTextViewText(R.id.notification_hold_new_name2, notifiyBookResultV2Bean.getBookName());
                remoteViews.setTextViewText(R.id.notification_hold_new_grade2, notifiyBookResultV2Bean.getStar() + "分");
                Glide
                        .with(NotificationHolderV2Service.this).asBitmap()
                        .load(notifiyBookResultV2Bean.getCover()).apply(initRequestOptions(35, 47))
                        .into(initNotificationTarget(R.id.notification_hold_new_img2,
                                remoteViews,
                                notification,
                                NOTIFICATION_DAY_CODE_ID));
                listId.append(notifiyBookResultV2Bean.getBookId());
                if (isLive) {
                    remoteViews.setOnClickPendingIntent(R.id.notification_hold_new_second_layout, intentSplash());
                } else {
                    remoteViews.setOnClickPendingIntent(R.id.notification_hold_new_second_layout, intentBookDetail(notifiyBookResultV2Bean.getBookId(), 20, "1", 3));
                }
                FuncPageStatsApi.pullNotification(notifiyBookResultV2Bean.getBookId(), 3);
            }
        }

        @Override
        public void onDataNULL() {
            isData = false;
            if (huan_Intent != null) {
                huan_Intent.putExtra(INTENT_KEY, DAY_CODE);
            }
        }

        @Override
        public void onDataError() {
            isData = false;
            if (huan_Intent != null) {
                huan_Intent.putExtra(INTENT_KEY, DAY_CODE);
            }
        }

    };

    /**
     * 跳转书籍详情页
     *
     * @return
     */
    private PendingIntent intentBookDetail(long bookid, int requestCode, String type, int mid) {
        Intent intent;
        if (Utils.isExsitMianActivity(this, HomeActivity.class)) {
            //已经在堆栈中, 直接启动HomeActivity.
            intent = new Intent(this, HomeActivity.class);
        } else {
            //启动开屏页.
            intent = new Intent(this, SplashActivity.class);
        }
//        intent.putExtra(RouterPath.KEY_BOOK_ID, bookid);
//        intent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.NOTIFICATION);
//        intent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
//        intent.putExtra(RouterPath.KEY_MODEL_ID, 18);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
            jsonObject.put("path", String.valueOf(bookid));
            jsonObject.put("modelId", mid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra(ParamKey.PUSH_DATA_KEY, jsonObject.toString());
        intent.putExtra(BaseActivity.DATA_KEY, new BaseData("PUSH"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(NotificationHolderV2Service.this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 跳转开屏页面
     *
     * @return
     */
    private PendingIntent intentSplash() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(ALIVE, SPPASH_CODE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(NotificationHolderV2Service.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent intentDefault() {
        Intent intent;
        if (Utils.isExsitMianActivity(this, HomeActivity.class)) {
            //已经在堆栈中, 直接启动HomeActivity.
            intent = new Intent(this, HomeActivity.class);
        } else {
            //启动开屏页.
            intent = new Intent(this, SplashActivity.class);
        }
        return intent;
    }

    RequestOptions initRequestOptions(int width, int height) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.skipMemoryCache(true);
        requestOptions.centerCrop();
        requestOptions.dontAnimate();
        requestOptions.override(ViewUtils.dp2px(width), ViewUtils.dp2px(height));
        return requestOptions;
    }

    /**
     * 加载通知栏网络图片
     *
     * @return
     */
    private NotificationV2Target initNotificationTarget(int id, RemoteViews remoteViews, Notification notification, int notiid) {
        final NotificationV2Target notificationTarget = new NotificationV2Target(
                NotificationHolderV2Service.this,
                id,
                remoteViews,
                notification,
                notiid);
        return notificationTarget;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
