package com.zydm.statistics.motong;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.google.gson.reflect.TypeToken;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.data.tools.DataUtils;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.rx.ApiCompletableObserver;
import com.zydm.base.rx.LoadException;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import io.reactivex.Completable;
import io.reactivex.functions.Action;

import java.util.ArrayList;

/**
 * Created by yan on 2017/3/6.
 */

public class MtStEventMgr {

    private static final String TAG = "MtStEventMgr";

    private static MtStEventMgr sInstance;
    private static int EVENT_UPLOAD = 2000;

    private final int UPLOAD_SPACE_TIME;
    private final int UPLOAD_EVENT_COUNT;

    private static String KEY_EVENTS = "zydm_ebk_st_events";
    private Handler mMsgHandler;
    private ArrayList<MtStEvent> mEventList = new ArrayList<>();

    private ArrayList<MtStEvent> mEventPreSaveList = new ArrayList<>();

    private boolean mIsUploading;

    private MtStEventMgr() {
        boolean testEnv = BaseApplication.context.isTestEnv();
        UPLOAD_SPACE_TIME = testEnv ? Constants.MINUTE_1 : Constants.HOUR_1;
        UPLOAD_EVENT_COUNT = testEnv ? 5 : 20;
        initThread();
    }

    public static MtStEventMgr getInstance() {
        if (sInstance == null) {
            sInstance = new MtStEventMgr();
        }
        return sInstance;
    }

    private void initThread() {
        ZExecutorService.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                initList();
                Looper.prepare();
                initMsgHandler();
                Looper.loop();
            }
        });
    }

    private void initList() {
        mEventList.clear();
        String events = SPUtils.INSTANCE.getString(KEY_EVENTS);
        ArrayList<MtStEvent> list = JsonUtils.parseJson(events, new TypeToken<ArrayList<MtStEvent>>() {
        }.getType());
        if (!DataUtils.isEmptyList(list)) {
            mEventList.addAll(list);
            LogUtils.d(TAG, "onEvent initList1:" + mEventList);
        }
        if (!DataUtils.isEmptyList(mEventPreSaveList)) {
            mEventPreSaveList.clear();
            mEventList.addAll(mEventPreSaveList);
            LogUtils.d(TAG, "onEvent initList2:" + mEventList);
            saveEvent();
        }
    }

    private void initMsgHandler() {
        mMsgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                LogUtils.d(TAG, "handleMessage: what:" + msg.what);
                if (msg.what == EVENT_UPLOAD) {
                    upload();
                }
            }
        };
    }

    public void stop() {
        mMsgHandler.getLooper().quit();
        mMsgHandler = null;
    }

    public void onEvent(String eventId, double eventValue, MtMap<String, String> eventParams, boolean forceUpload) {

        if (TextUtils.isEmpty(eventId)) {
            return;
        }
        MtStEvent event = new MtStEvent(eventId, eventValue, eventParams);
        onEvent(event, forceUpload);
    }

    void onEvent(final MtStEvent event, final boolean forceUpload) {
        LogUtils.d(TAG, "onEvent event:" + event + " size:" + mEventList.size());
        if (mMsgHandler == null) {
            mEventPreSaveList.add(event);
            return;
        }
        mMsgHandler.post(new Runnable() {
            @Override
            public void run() {
                mEventList.add(event);
                saveEvent();
                if (forceUpload || mEventList.size() >= UPLOAD_EVENT_COUNT) {
                    mMsgHandler.removeMessages(EVENT_UPLOAD);
                    upload();
                } else {
                    delayedUpload();
                }
            }
        });
    }

    private void delayedUpload() {
        if (mMsgHandler.hasMessages(EVENT_UPLOAD)) {
            return;
        }
        LogUtils.d(TAG, "delayedUpload()");
        mMsgHandler.sendEmptyMessageDelayed(EVENT_UPLOAD, UPLOAD_SPACE_TIME);
    }

    private void saveEvent() {
        String eventsJson = JsonUtils.toJson(new ArrayList<>(mEventList));
        LogUtils.d(TAG, "saveEvent eventsJson:" + eventsJson);
        SPUtils.INSTANCE.putString(KEY_EVENTS, eventsJson);
    }

    public void upload() {
        if (DataUtils.isEmptyList(mEventList)) {
            return;
        }
        if (mIsUploading) {
            return;
        }
        mIsUploading = true;
        final ArrayList<MtStEvent> uploadEvent = new ArrayList<>(mEventList);
        String eventListStr = JsonUtils.toJson(uploadEvent);
        LogUtils.d(TAG, "upload eventListStr:" + eventListStr + " uploadEvent size:" + uploadEvent.size());
        commit(TimeUtils.getUnixTime(), eventListStr)
                .observeOn(MtSchedulers.mainUi())
                .subscribe(new ApiCompletableObserver() {

                    @Override
                    protected void onSuccess() {
                        mIsUploading = false;
                        mMsgHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mEventList.removeAll(uploadEvent);
                                saveEvent();
                                if (!mEventList.isEmpty()) {
                                    delayedUpload();
                                }
                            }
                        });
                    }

                    @Override
                    protected void onFail(LoadException error) {
                        mIsUploading = false;
                        if (!mEventList.isEmpty()) {
                            delayedUpload();
                        }
                        error.interceptAll();
                    }
                });
    }

    private Completable commit(long currTime, String eventList) {
        String fullUrl = "http://statistics.imotong.com/Api/Event/commit";
        MtMap<String, String> fields = new MtMap<>();
        fields.put("currTime", currTime+"");
        fields.put("eventList", eventList);
        final StApiRequest request = new StApiRequest(fullUrl, null, fields, null);
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                new StApiHttpWorker(request).proceed();
            }
        }).subscribeOn(MtSchedulers.io());
    }
}
