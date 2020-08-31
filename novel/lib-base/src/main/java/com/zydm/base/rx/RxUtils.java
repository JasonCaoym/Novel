package com.zydm.base.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yan on 2017/5/10.
 */

public class RxUtils {

    public static boolean isDisposed(Disposable disposable) {
        return null == disposable || disposable.isDisposed();
    }

    public static boolean disposed(Disposable disposable) {
        if (disposable == null) {
            return false;
        }
        if (disposable.isDisposed()) {
            return false;
        }
        disposable.dispose();
        return true;
    }

    public static <T> Single<T> zipWith(Single<T> src, Single<Object> zipSrc) {
        return src.zipWith(zipSrc, new BiFunction<T, Object, T>() {
            @Override
            public T apply(@NonNull T t, @NonNull Object o) throws Exception {
                return t;
            }
        });
    }

    public static <D> Single<Packing<D>> mapPacking(Single<D> single) {
        return single.map(new Function<D, Packing<D>>() {
            @Override
            public Packing<D> apply(D data) throws Exception {
                return new Packing<>(data);
            }
        }).onErrorReturnItem(new Packing<D>());
    }

    public static <T> ObservableSource<T> toSimpleSingle(Observable<T> upstream) {
        return upstream.subscribeOn(MtSchedulers.io())
                .observeOn(MtSchedulers.mainUi());
    }

    public static <T> SingleSource<T> toSimpleSingle(Single<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
