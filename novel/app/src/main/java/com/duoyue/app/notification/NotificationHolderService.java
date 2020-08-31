package com.duoyue.app.notification;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.duoyue.app.notification.data.*;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.activity.SearchV2Activity;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.io.IOUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.common.MainApplication;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.tools.TooFastChecker;
import com.zydm.base.ui.activity.BaseActivity;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class NotificationHolderService extends Service {

    private static final String TAG = "App#NotificationHolderService";

    private final static int CODE_NOTIFICATION = 0x121;

    private NotificationManager mNotificationManager;
    private Notification mNotification;

    // 老的数据通知栏接口类型
    private NotifyPushListBean listBean;
    // 新的数据通知栏接口类型
    private NotifiyBookResultBean bookListBean;

    private boolean isRegistered;
    public static boolean mABoolean;
    public boolean newNotifyEnable;
    private CyclicBarrier barrier;
    private TooFastChecker tooFastChecker = new TooFastChecker(2000);

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel mediaChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mediaChannel = new NotificationChannel(getString(R.string.notification_hold_channel_id), getString(R.string.notification_hold_channel_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mediaChannel.setSound(null, null);
            mediaChannel.setVibrationPattern(null);

            mNotificationManager.createNotificationChannel(mediaChannel);
        }
//        mABoolean = SharePreferenceUtils.getBoolean(getApplicationContext(), SharePreferenceUtils.IS_FIRST_IN, true);
        statsAliveTime();
        startForeground(CODE_NOTIFICATION, new Notification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String bookId = intent.getStringExtra("book_id");
            if (!TextUtils.isEmpty(bookId) && bookListBean != null) {
                boolean fountIt = false;
                List<NotifyBookBean> list = bookListBean.getMaleBookList();
                if (list != null && !list.isEmpty()) {
                    for (NotifyBookBean bean : list) {
                        if (bean.getBookId().equals(bookId)) {
                            list.remove(bean);
                            fountIt = true;
                            break;
                        }
                    }
                }
                if (list == null || list.isEmpty()) {
                    starRequestBooks();
                }
                List<NotifyBookBean> femaleList = bookListBean.getFemaleBookList();
                if (!fountIt) {
                    if (femaleList != null && !femaleList.isEmpty()) {
                        for (NotifyBookBean bean : femaleList) {
                            if (bean.getBookId().equals(bookId)) {
                                femaleList.remove(bean);
                                break;
                            }
                        }
                    }
                }
                if (femaleList == null || femaleList.isEmpty()) {
                    starRequestBooks();
                }
            }
        }
        starRequestBooks();
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String currPageId = "";
            Logger.e("PageStatsUploadMgr", "是否在前台界面： " + MainApplication.Companion.isBackToForeground());
            if (MainApplication.Companion.isBackToForeground()) {
                currPageId = BaseApplication.context.getCurrPageId();
            } else {
                currPageId = PageNameConstants.BACKGROUND;
            }
            FuncPageStatsApi.aliveTime(currPageId);
        }
    };

    private void starRequestBooks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.NOTIFY_ENABLE, 0) == 0) {
                    newNotifyEnable = false;
                    requestBooksInfo();
                } else {
                    newNotifyEnable = true;
                    mHandler.postDelayed(timeoutRunnable, 15_000);
                    requestBooksPush();
                }
            }
        }).start();
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (barrier != null) {
                barrier.reset();
                barrier = null;
            }
            initNotification();
        }
    };

    private void statsAliveTime() {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                        if (mABoolean) {
//                            Thread.sleep(5000);
//                        } else {
                            Thread.sleep(60_000);
//                        }
                    } catch (Throwable throwable) {
                    }
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
    }
    RemoteViews mRemoteViews = null;

    private void initNotification() {
        if (tooFastChecker.isTooFast()) {
            return;
        }
        mHandler.removeCallbacks(timeoutRunnable);
        // 判断通知栏是否被关闭
        boolean areNotificationsEnabled = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

            if (areNotificationsEnabled) {
                Logger.e(TAG, "通知栏已经开启");
            } else {
                Logger.e(TAG, "通知栏已经关闭，开始跳转");
                /*try {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                    intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);

                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                        Intent intent2 = new Intent();

                        //下面这种方案是直接跳转到当前应用的设置界面。
                        //https://blog.csdn.net/ysy950803/article/details/71910806
                        intent2.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent2.setData(uri);
                        intent2.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                    Intent intent = new Intent();

                    //下面这种方案是直接跳转到当前应用的设置界面。
                    //https://blog.csdn.net/ysy950803/article/details/71910806
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }*/
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                getString(R.string.notification_hold_channel_id));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher))
                .setSmallIcon(Constants.IS_AIGAO ?  R.mipmap.ic_launcher_ag : R.mipmap.ic_launcher)
                .setTicker(getString(R.string.notification_hold_title))
                .setWhen(System.currentTimeMillis());

        try {
            if (!newNotifyEnable) {
                mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_holder);
                mRemoteViews.setTextColor(R.id.notification_hold_title, NotificationStyleUtil.getNotificationColor(this));
                // 搜索按钮事件
                Intent searchIntent = new Intent(this, SearchV2Activity.class);
                searchIntent.putExtra(BaseActivity.ID_KEY, "notification");
                PendingIntent searchPendingIntent = PendingIntent.getActivity(this, 0, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mRemoteViews.setOnClickPendingIntent(R.id.notification_hold_search, searchPendingIntent);

                if (listBean == null || listBean.getList().isEmpty()) {
                    mRemoteViews.setViewVisibility(R.id.notification_hold_books, View.GONE);
                    mRemoteViews.setViewVisibility(R.id.notification_hold_empty_title, View.VISIBLE);
                } else {
                    if (areNotificationsEnabled) {
                        FuncPageStatsApi.notifyBookShow(listBean.getList().get(0).getBookId());
                    }
                    mRemoteViews.setViewVisibility(R.id.notification_hold_books, View.VISIBLE);
                    mRemoteViews.setViewVisibility(R.id.notification_hold_empty_title, View.GONE);

                    // 书籍点击事件 1
                    mRemoteViews.setTextViewText(R.id.notification_hold_first, listBean.getList().get(0).getBookName());
                    mRemoteViews.setTextColor(R.id.notification_hold_first, NotificationStyleUtil.getNotificationColor(this));

                    Intent firstIntent = new Intent(this, BookDetailActivity.class);
                    firstIntent.putExtra(RouterPath.KEY_BOOK_ID, listBean.getList().get(0).getBookId());
                    Logger.e(TAG, "BookName = " + listBean.getList().get(0).getBookName() + "ID = " + listBean.getList().get(0).getBookId());
                    firstIntent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.NOTIFICATION);
                    firstIntent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
                    firstIntent.putExtra(RouterPath.KEY_MODEL_ID, 18);
                    firstIntent.putExtra(BaseActivity.DATA_KEY, new BaseData("Notification"));

                    PendingIntent firstPendingIntent = PendingIntent.getActivity(this, 10, firstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mRemoteViews.setOnClickPendingIntent(R.id.notification_hold_first_layout, firstPendingIntent);
                    if (listBean.getList().size() > 1) {
                        if (areNotificationsEnabled) {
                            FuncPageStatsApi.notifyBookShow(listBean.getList().get(1).getBookId());
                        }
                        mRemoteViews.setViewVisibility(R.id.notification_hold_second_layout, View.VISIBLE);
                        // 书籍点击事件 2
                        mRemoteViews.setTextViewText(R.id.notification_hold_second, listBean.getList().get(1).getBookName());
                        mRemoteViews.setTextColor(R.id.notification_hold_second, NotificationStyleUtil.getNotificationColor(this));
                        Intent secondIntent = new Intent(this, BookDetailActivity.class);
                        secondIntent.putExtra(RouterPath.KEY_BOOK_ID, listBean.getList().get(1).getBookId());
                        Logger.e(TAG, "BookName = " + listBean.getList().get(1).getBookName() + "ID = " + listBean.getList().get(1).getBookId());
                        secondIntent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.NOTIFICATION);
                        secondIntent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
                        secondIntent.putExtra(RouterPath.KEY_MODEL_ID, 18);
                        secondIntent.putExtra(BaseActivity.DATA_KEY, new BaseData("Notification"));

                        PendingIntent secondPendingIntent = PendingIntent.getActivity(this, 20, secondIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mRemoteViews.setOnClickPendingIntent(R.id.notification_hold_second_layout, secondPendingIntent);
                    } else {
                        mRemoteViews.setViewVisibility(R.id.notification_hold_second_layout, View.GONE);
                    }
                }
            } else {
                mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_holder_new);
                mRemoteViews.setTextColor(R.id.notification_hold_new_title, NotificationStyleUtil.getNotificationColor(this));
                if (bookListBean == null || ((bookListBean.getMaleBookList() == null || bookListBean.getMaleBookList().isEmpty())
                        && (bookListBean.getFemaleBookList() == null || bookListBean.getFemaleBookList().isEmpty()))) {
                    mRemoteViews.setViewVisibility(R.id.notification_hold_new_books, View.GONE);
                    mRemoteViews.setViewVisibility(R.id.notification_hold_empty_new_title, View.VISIBLE);
                } else {
                    if (areNotificationsEnabled && bookListBean.getMaleBookList() != null && !bookListBean.getMaleBookList().isEmpty()) {
                        FuncPageStatsApi.notifyBookShow(StringFormat.parseLong(bookListBean.getMaleBookList().get(0).getBookId(), 0));
                    }
                    mRemoteViews.setViewVisibility(R.id.notification_hold_new_books, View.VISIBLE);
                    mRemoteViews.setViewVisibility(R.id.notification_hold_empty_new_title, View.GONE);


                    // 书籍点击事件 1
                    // 判断图片1是否存在
                    if (bookListBean.getMaleBookList() != null && !bookListBean.getMaleBookList().isEmpty()
                            && !TextUtils.isEmpty(getApkStorePath(getApplicationContext()))) {
                        try {
                            mRemoteViews.setViewVisibility(R.id.notification_hold_new_img1, View.VISIBLE);
                            File imgFile = new File(getApkStorePath(getApplicationContext()), bookListBean.getMaleBookList().get(0).getBookId() + ".jpg");
                            if (imgFile.exists()) {
                                mRemoteViews.setImageViewBitmap(R.id.notification_hold_new_img1, BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                            } else {
//                                mRemoteViews.setViewVisibility(R.id.notification_hold_new_img1, View.GONE);
                                mRemoteViews.setImageViewResource(R.id.notification_hold_new_img1, R.mipmap.a);
                                Logger.e(TAG, "图片1加载失败了： 图片不存在" );
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            mRemoteViews.setImageViewResource(R.id.notification_hold_new_img1, R.mipmap.a);
                            Logger.e(TAG, "图片1加载失败了： " + ex.getMessage());
                        }
                    } else {
                        mRemoteViews.setViewVisibility(R.id.notification_hold_new_img1, View.GONE);
                    }
                    // 图2
                    if (bookListBean.getFemaleBookList() != null && !bookListBean.getFemaleBookList().isEmpty()
                            && !TextUtils.isEmpty(getApkStorePath(getApplicationContext()))) {
                        try {
                            mRemoteViews.setViewVisibility(R.id.notification_hold_new_img2, View.VISIBLE);
                            File imgFile = new File(getApkStorePath(getApplicationContext()), bookListBean.getFemaleBookList().get(0).getBookId() + ".jpg");
                            if (imgFile.exists()) {
                                mRemoteViews.setImageViewBitmap(R.id.notification_hold_new_img2, BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                            } else {
//                                mRemoteViews.setViewVisibility(R.id.notification_hold_new_img2, View.GONE);
                                mRemoteViews.setImageViewResource(R.id.notification_hold_new_img2, R.mipmap.a);
                                Logger.e(TAG, "图片2加载失败了： 图片不存在" );
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            mRemoteViews.setImageViewResource(R.id.notification_hold_new_img2, R.mipmap.a);
                            Logger.e(TAG, "图片2加载失败了： " + ex.getMessage());
                        }
                    } else {
                        mRemoteViews.setViewVisibility(R.id.notification_hold_new_img2, View.GONE);
                    }

                    if (bookListBean.getMaleBookList() != null && !bookListBean.getMaleBookList().isEmpty()) {
                        NotifyBookBean maleBean = bookListBean.getMaleBookList().get(0);
                        mRemoteViews.setTextViewText(R.id.notification_hold_new_grade1, maleBean.getGrade() + "分·" + maleBean.getBookCategory());
                        mRemoteViews.setTextViewText(R.id.notification_hold_new_name1, bookListBean.getMaleBookList().get(0).getBookName());
                        mRemoteViews.setTextColor(R.id.notification_hold_new_name1, NotificationStyleUtil.getNotificationColor(this));
                        Intent firstIntent = new Intent(this, BookDetailActivity.class);
                        firstIntent.putExtra(RouterPath.KEY_BOOK_ID, StringFormat.parseLong(bookListBean.getMaleBookList().get(0).getBookId(), 0));
                        Logger.e(TAG, "BookName = " + bookListBean.getMaleBookList().get(0).getBookName() + "ID = " + bookListBean.getMaleBookList().get(0).getBookId());
                        firstIntent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.NOTIFICATION);
                        firstIntent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
                        firstIntent.putExtra(RouterPath.KEY_MODEL_ID, 18);
                        firstIntent.putExtra(BaseActivity.DATA_KEY, new BaseData("Notification"));

                        PendingIntent firstPendingIntent = PendingIntent.getActivity(this, 10, firstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mRemoteViews.setOnClickPendingIntent(R.id.notification_hold_new_first_layout, firstPendingIntent);
                    }

                    if (bookListBean.getFemaleBookList() != null && !bookListBean.getFemaleBookList().isEmpty()) {
                        NotifyBookBean femaleBean = bookListBean.getFemaleBookList().get(0);
                        if (areNotificationsEnabled) {
                            FuncPageStatsApi.notifyBookShow(StringFormat.parseLong(femaleBean.getBookId(), 0));
                        }
                        mRemoteViews.setTextViewText(R.id.notification_hold_new_grade2, femaleBean.getGrade() + "分·" + femaleBean.getBookCategory());
                        mRemoteViews.setViewVisibility(R.id.notification_hold_new_second_layout, View.VISIBLE);
                        // 书籍点击事件 2
                        mRemoteViews.setTextViewText(R.id.notification_hold_new_name2, femaleBean.getBookName());
                        mRemoteViews.setTextColor(R.id.notification_hold_new_name2, NotificationStyleUtil.getNotificationColor(this));
                        Intent secondIntent = new Intent(this, BookDetailActivity.class);
                        secondIntent.putExtra(RouterPath.KEY_BOOK_ID, StringFormat.parseLong(femaleBean.getBookId(), 0));
                        Logger.e(TAG, "BookName = " + femaleBean.getBookName()
                                + "ID = " + femaleBean.getBookId());
                        secondIntent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.NOTIFICATION);
                        secondIntent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.NOTIFICATION_BOOK_RECOMMEND);
                        secondIntent.putExtra(RouterPath.KEY_MODEL_ID, 18);
                        secondIntent.putExtra(BaseActivity.DATA_KEY, new BaseData("Notification"));

                        PendingIntent secondPendingIntent = PendingIntent.getActivity(this, 20, secondIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mRemoteViews.setOnClickPendingIntent(R.id.notification_hold_new_second_layout, secondPendingIntent);
                    } else {
                        mRemoteViews.setViewVisibility(R.id.notification_hold_new_second_layout, View.GONE);
                    }
                }
            }
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(BaseActivity.DATA_KEY, new BaseData("Notification"));
            NotificationCompat.Builder builder = mBuilder.setContent(mRemoteViews);
            if (!newNotifyEnable) {
                builder.setTicker(getString(R.string.notification_hold_title));
                if (listBean == null || listBean.getList().isEmpty()) {
                    builder.setContentIntent(PendingIntent.getActivity(this, 30, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
            } else {
                builder.setTicker(getString(R.string.notification_hold_new_title));
                if (bookListBean == null || ((bookListBean.getMaleBookList() == null || bookListBean.getMaleBookList().isEmpty())
                        && (bookListBean.getFemaleBookList() != null || bookListBean.getFemaleBookList().isEmpty()))) {
                    builder.setContentIntent(PendingIntent.getActivity(this, 30, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mNotification = mBuilder.build();
        //  设置常驻通知栏
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.contentView = mRemoteViews;
        mNotificationManager.notify(CODE_NOTIFICATION, mNotification);
    }

    private void initNetBroadcast() {
        if (!isRegistered) {
            isRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mReceiver, intentFilter);
            initNotification();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (PhoneUtil.isNetworkAvailable(context) && (listBean == null || listBean.getList().isEmpty())) {
                    starRequestBooks();
                }
            }
        }
    };

    private void requestBooksInfo() {
        Logger.e(TAG, "开始请求通知栏数据");
        NotifyPushRequest request = new NotifyPushRequest();

        new JsonPost.AsyncPost<NotifyPushListBean>()
                .setRequest(request)
                .setResponseType(NotifyPushListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<NotifyPushListBean>>() {
                    @Override
                    public void onNext(JsonResponse<NotifyPushListBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                                && !jsonResponse.data.getList().isEmpty()) {
                            listBean = jsonResponse.data;
                            initNotification();
                        } else {
                            initNetBroadcast();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i(TAG, e.toString(), new Throwable());
                        initNetBroadcast();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void requestBooksPush() {
        Logger.e(TAG, "开始请求《新样式》通知栏数据");
        NotifyBookPushRequest request = new NotifyBookPushRequest();
        if (barrier != null) {
            barrier.reset();
            barrier = null;
        }
        barrier = new CyclicBarrier(2);

        /*bookListBean = new NotifiyBookResultBean();
        List<NotifyBookBean> femaleBookList = new ArrayList<>();
        NotifyBookBean femaleBean = new NotifyBookBean();
        femaleBean.setBookId("30736");
        femaleBean.setBookCategory("玄幻");
        femaleBean.setBookName("我的老板很厉害");
        femaleBean.setCover("http://rsaydyz.yule37.cn/img/201905/11/84/28227.jpg");
        femaleBean.setGrade("9.6");
        femaleBookList.add(femaleBean);
        bookListBean.setFemaleBookList(femaleBookList);

        List<NotifyBookBean> maleBookList = new ArrayList<>();
        NotifyBookBean maleBean = new NotifyBookBean();
        maleBean.setBookId("22020");
        maleBean.setBookCategory("都市");
        maleBean.setBookName("都市至尊仙医");
        maleBean.setCover("http://rsaydyz.yule37.cn/img/201905/10/43/17548.jpg");
        maleBean.setGrade("9.0");
        maleBookList.add(maleBean);
        bookListBean.setMaleBookList(maleBookList);

        if (bookListBean.getMaleBookList() != null && !bookListBean.getMaleBookList().isEmpty()) {
            setIamge(bookListBean.getMaleBookList().get(0).getBookId(),
                    bookListBean.getMaleBookList().get(0).getCover());
        } else {
            setIamge("", "");
        }
        if (bookListBean.getFemaleBookList() != null && !bookListBean.getFemaleBookList().isEmpty()) {
            setIamge(bookListBean.getFemaleBookList().get(0).getBookId(),
                    bookListBean.getFemaleBookList().get(0).getCover());
        } else {
            setIamge("", "");
        }*/
        new JsonPost.AsyncPost<NotifiyBookResultBean>()
                .setRequest(request)
                .setResponseType(NotifiyBookResultBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<NotifiyBookResultBean>>() {
                    @Override
                    public void onNext(JsonResponse<NotifiyBookResultBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            bookListBean = jsonResponse.data;
                            barrier = new CyclicBarrier(2);
                            if (bookListBean.getMaleBookList() != null && !bookListBean.getMaleBookList().isEmpty()) {
                                setIamge(bookListBean.getMaleBookList().get(0).getBookId(),
                                        bookListBean.getMaleBookList().get(0).getCover());
                            } else {
                                setIamge("", "");
                            }
                            if (bookListBean.getFemaleBookList() != null && !bookListBean.getFemaleBookList().isEmpty()) {
                                setIamge(bookListBean.getFemaleBookList().get(0).getBookId(),
                                        bookListBean.getFemaleBookList().get(0).getCover());
                            } else {
                                setIamge("", "");
                            }
                        } else {
                            initNetBroadcast();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i(TAG, e.toString(), new Throwable());
                        initNetBroadcast();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String getApkStorePath(Context context) {
        String savePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                savePath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + "img";
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return savePath;
    }

    private void setIamge(final String bookId, final String imgUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(bookId) || TextUtils.isEmpty(imgUrl)) {
                        waitDownloaded();
                        return;
                    }
                    Glide.with(getApplicationContext()).asBitmap().load(imgUrl).into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (!TextUtils.isEmpty(getApkStorePath(getApplicationContext()))) {
                                File imgFile = new File(getApkStorePath(getApplicationContext()), bookId + ".jpg");
                                if (imgFile.exists()) {
                                    waitDownloaded();
                                } else {
                                    try {
                                        imgFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    FileOutputStream fos = null;
                                    try {
                                        fos = new FileOutputStream(imgFile);
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos); //质量压缩  10倍
                                        fos.flush();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        IOUtil.close(fos);
                                        waitDownloaded();
                                    }
                                }
                            } else {
                                waitDownloaded();
                            }

                        }
                    }); //方法中设置asBitmap可以设置回调类型
                } catch (Exception e) {
                    e.printStackTrace();
                    waitDownloaded();
                }
            }
        });
//            }
//        });
    }

    private void waitDownloaded() {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (barrier != null) {
                        barrier.await();
                    }
                    if (barrier == null) {
                        return;
                    }
                    if (barrier != null) {
                        barrier = null;
                        initNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    initNotification();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Logger.d(TAG, "onDestroy");
        if (isRegistered) {
            unregisterReceiver(mReceiver);
        }
    }

    private interface BitmapCallback {
        void success(Bitmap bitmap);
        void failed(String msg);
    }
}
