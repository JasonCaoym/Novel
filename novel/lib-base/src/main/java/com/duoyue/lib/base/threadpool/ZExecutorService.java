package com.duoyue.lib.base.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * @date 2018/8/8
 */
public final class ZExecutorService {

    // Common Thread Pool
    private ExecutorService executorService = new ThreadPoolExecutor(5,  1000,
            10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(512), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private ZExecutorService() {

    }

    private static final class InnerExecutorService {
        private static final ZExecutorService INSTANCE = new ZExecutorService();
    }

    public static ZExecutorService getInstance() {
        return InnerExecutorService.INSTANCE;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            try {
                if (!executorService.isShutdown()) {
                    executorService.execute(runnable);
                } else {
                    new Thread(runnable).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Thread(runnable).start();
            }
        }
    }

}
