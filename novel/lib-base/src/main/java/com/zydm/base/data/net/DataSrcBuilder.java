package com.zydm.base.data.net;

import android.text.TextUtils;
import com.zydm.base.common.BaseErrorCode;
import com.zydm.base.common.Constants;
import com.zydm.base.data.tools.DataUtils;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.rx.LoadException;
import com.zydm.base.rx.MtSchedulers;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by yan on 2017/4/14.
 */

public class DataSrcBuilder<T> {

    private static final String TAG = "DataSrcBuilder";
    private ApiRequest mRequest;
    private boolean mIsForceUpdate = false;
    private boolean mIsCheckEmpty = false;

    DataSrcBuilder(ApiRequest request) {
        mRequest = request;
    }

    public void reset() {
        mIsForceUpdate = false;
        mIsCheckEmpty = false;
        mRequest.reset();
    }

    public ApiRequest getRequest() {
        return mRequest;
    }

    public DataSrcBuilder<T> addReqParam(String key, String value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return this;
        }
        mRequest.addParam(key, value);
        return this;
    }

    public DataSrcBuilder<T> addReqParams(Map<String, String> params) {
        if (DataUtils.isEmptyMap(params)) {
            return this;
        }
        mRequest.addParams(params);
        return this;
    }

    public DataSrcBuilder<T> setForceUpdate(boolean forceUpdate) {
        mIsForceUpdate = forceUpdate;
        return this;
    }

    public DataSrcBuilder<T> setCheckEmpty(boolean isCheckEmpty) {
        mIsCheckEmpty = isCheckEmpty;
        return this;
    }

    public Single<T> buildNoScheduler() {
        if (mIsForceUpdate) {
            mRequest.setRequestMode(ApiRequest.REQ_MODE_FORCE_UPDATE);
        }
//        LogUtils.d(TAG, "@@@buildNoScheduler  fromCallable:" + this);
        return Single.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
//                LogUtils.d(TAG, "@@@build  fromCallable:" + this);
                new ApiHttpWorker(mRequest).proceed();
                try {
                    Type type = mRequest.getRespType();
                    if (type.equals(String.class)) {
                        return (T) mRequest.getResponseData();
                    }
                    return JsonUtils.fromJson(mRequest.getResponseData(), mRequest.getRespType());
                } catch (Exception e) {
                    throw new RuntimeException(mRequest.getUrl(), e);
                }
            }
        }).doOnSuccess(new Consumer<T>() {
            @Override
            public void accept(@NonNull T data) throws Exception {
                if (mIsCheckEmpty && DataUtils.isEmptyData(data)) {
                    throw new LoadException(BaseErrorCode.DATA_EMPTY, Constants.EMPTY, mRequest.getUrl());
                }
            }
        });
    }

    public Single<T> build() {
        return buildNoScheduler().subscribeOn(MtSchedulers.io());
    }

    public Single<T> buildObserveOnMainUi() {
        return build().observeOn(MtSchedulers.mainUi());
    }
}
