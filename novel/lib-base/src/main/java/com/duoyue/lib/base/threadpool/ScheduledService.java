package com.duoyue.lib.base.threadpool;

import android.support.annotation.NonNull;

import java.util.concurrent.*;

/**
 * 定时任务线程池
 *
 */
public final class ScheduledService {

    private ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5);


    private ScheduledService() {

    }

    private static final class InnerExecutorService {
        private static final ScheduledService INSTANCE = new ScheduledService();
    }

    public static ScheduledService getInstance() {
        return InnerExecutorService.INSTANCE;
    }

    public ConcurrentHashMap<String, Future> getFutureMap() {
        return futureMap;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            executorService.execute(runnable);
        }
    }

    /**
     * @param runnable
     * @param delay    延迟时间
     * @param timeUnit 时间单位
     */
    public void executeDelay(Runnable runnable, long delay, TimeUnit timeUnit) {
        if (runnable != null) {
            executorService.schedule(runnable, delay, timeUnit);
        }
    }

    /**
     * 执行延时周期性任务
     *
     * @param runnable     {@code LgExecutorSercice.JobRunnable}
     * @param initialDelay 延迟时间
     * @param period       周期时间
     * @param timeUnit     时间单位
     */
    public <T extends JobRunnable> void sheduler(T runnable, long initialDelay, long period, TimeUnit timeUnit) {
        if (runnable != null) {
            Future future = executorService.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
            futureMap.put(runnable.getJobId(), future);
        }
    }

    public static abstract class JobRunnable implements Runnable {

        private String jobId;

        public JobRunnable(@NonNull String jobId) {
            this.jobId = jobId;
        }

        /**
         * 强制终止定时线程
         */
        public void terminal() {
            Future future = ScheduledService.getInstance().getFutureMap().remove(jobId);
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        }

        public String getJobId() {
            return jobId;
        }

    }

}
