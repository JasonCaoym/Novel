package com.duoyue.lib.base.threadpool;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZSchedulers {

    private static final ZSchedulers INSTANCE = new ZSchedulers();

    private ExecutorService executorService;
    private Scheduler scheduler;

    private ZSchedulers() {
        executorService = new ThreadPoolExecutor(3, 30,
                10L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        scheduler = Schedulers.from(executorService);
    }

    public static ZSchedulers getInstance() {
        return INSTANCE;
    }

    public Scheduler io() {
        return scheduler;
    }

}
