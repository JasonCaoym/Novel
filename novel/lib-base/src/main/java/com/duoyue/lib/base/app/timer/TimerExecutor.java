package com.duoyue.lib.base.app.timer;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.cache.GsonParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerExecutor
{
    private static final String TAG = "Base#TimerExecutor";

    private String PATH_TIMER_RECORD = "novel/timer/record";

    private RamCache<RecordMap> recordCache;

    public TimerExecutor()
    {
        File recordFile = new File(BaseContext.getContext().getFilesDir(), PATH_TIMER_RECORD);
        recordCache = new RamCache<>(recordFile, new GsonParser<>(RecordMap.class));
    }

    public void exec(TimerTask task)
    {
        TimerObservable timerObservable = new TimerObservable(task);
        Observable.create(timerObservable)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(timerObservable);
    }

    private class TimerObservable extends DisposableObserver<Long> implements ObservableOnSubscribe<Long>
    {
        private TimerTask mTask;

        private TimerObservable(TimerTask task)
        {
            mTask = task;
        }

        @Override
        public void subscribe(ObservableEmitter<Long> emitter) throws Exception
        {
            if (mTask.requireNetwork() && !PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
            {
                Logger.d(TAG, "subscribe: 网络无效, 放弃定时任务:", mTask.getAction());
                return;
            }
            TimerRecord record = getTimerRecord(mTask.getAction(), mTask.getPollTime());
            if (Math.abs(System.currentTimeMillis() - record.timestamp) < record.interval * 60 * 1000L)
            {
                Logger.d(TAG, "subscribe: 间隔未到, 放弃定时任务:", mTask.getAction());
                return;
            }

            long interval;
            try
            {
                Logger.d(TAG, "subscribe: 开始, 定时任务:", mTask.getAction());
                interval = mTask.timeUp();
                Logger.d(TAG, "subscribe: 结束, 定时任务:", mTask.getAction());
            } catch (Throwable throwable)
            {
                interval = mTask.getErrorTime();
                Logger.e(TAG, "subscribe: 失败, 定时任务:", mTask.getAction(), throwable);
            }

            interval = interval > 0 ? interval : mTask.getPollTime();
            record.timestamp = System.currentTimeMillis();
            record.interval = interval;
            setTimeRecord(mTask.getAction(), record);
        }

        @Override
        public void onNext(Long interval)
        {
        }

        @Override
        public void onError(Throwable e)
        {

        }

        @Override
        public void onComplete()
        {

        }
    }

    private TimerRecord getTimerRecord(String action, long pollTime)
    {
        RecordMap map = recordCache.get();
        if (map == null)
        {
            map = new RecordMap();
            recordCache.set(map);
        }
        TimerRecord record = map.get(action);
        if (record == null)
        {
            record = new TimerRecord();
            record.timestamp = 0;
            record.interval = pollTime;
            map.put(action, record);
        }
        return record;
    }

    private void setTimeRecord(String action, TimerRecord record)
    {
        RecordMap map = recordCache.get();
        if (map != null)
        {
            map.put(action, record);
            recordCache.set(map);
        }
    }

    private static class RecordMap extends HashMap<String, TimerRecord>
    {

    }
}
