package com.zydm.base.rx;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.ResourceCompletableObserver;

/**
 * Created by yan on 2017/5/9.
 */

public abstract class ApiCompletableObserver extends ResourceCompletableObserver {

    private CompositeDisposable mComposite;

    public ApiCompletableObserver(@NonNull CompositeDisposable composite) {
        mComposite = composite;
    }

    public ApiCompletableObserver() {
        this(new CompositeDisposable());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mComposite.add(this);
    }

    @Override
    public final void onComplete() {
        mComposite.remove(this);
        onSuccess();
    }

    protected abstract void onSuccess();

    @Override
    public final void onError(@NonNull Throwable e) {
        mComposite.remove(this);
        LoadException error = ExceptionUtils.cast(e);
        onFail(error);
        if (!error.isIntercepted()) {
//            Injection.getAppInject().getDefaultErrorHandler().accept(error);
        }
    }

    protected abstract void onFail(@NonNull LoadException error);
}
