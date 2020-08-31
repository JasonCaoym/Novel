package com.zydm.base.rx;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.ResourceSingleObserver;

/**
 * Created by yan on 2017/4/25.
 */

public abstract class ApiSingleObserver<T> extends ResourceSingleObserver<T> {

    private CompositeDisposable mComposite;

    public ApiSingleObserver() {
        this(new CompositeDisposable());
    }

    public ApiSingleObserver(@NonNull CompositeDisposable composite) {
        this.mComposite = composite;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mComposite.add(this);
    }

    @Override
    public final void onSuccess(@NonNull T t) {
        mComposite.remove(this);
        onLoadSuccess(t);
    }

    public abstract void onLoadSuccess(@NonNull T t);

    @Override
    public final void onError(@NonNull Throwable e) {
        mComposite.remove(this);
        LoadException loadError = ExceptionUtils.cast(e);

        onLoadFail(loadError);
//        Injection.getAppInject().getDefaultErrorHandler().accept(loadError);
    }

    public abstract void onLoadFail(@NonNull LoadException loadError);
}
