package com.zydm.base.rx;

import android.support.annotation.NonNull;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MtSchedulers {

    @NonNull
    public static Scheduler computation() {
        return Schedulers.computation();
    }

    @NonNull
    public static Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    public static Scheduler mainUi() {
        return AndroidSchedulers.mainThread();
    }
}
