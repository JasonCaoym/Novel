package com.zydm.base.data.net;


import android.text.TextUtils;
import com.zydm.base.common.Constants;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.data.tools.DataUtils;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.utils.ClassUtils;
import io.reactivex.Completable;
import io.reactivex.functions.Action;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yin on 2016/8/16.
 */
class ApiMethodsHandler implements InvocationHandler {
    private static final String TAG = "ApiMethodsHandler";
    private static final String METHOD_NAME_DIVIDE = "$";

    ApiMethodsHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] paramValues) throws Throwable {
        String fullUrl = parseUrl(method);

        ApiConfigs configs = ClassUtils.findAnnotation(method.getAnnotations(), ApiConfigs.class);

        MtMap<String, String> fields = parseField(method, paramValues);

        ApiRequest request = new ApiRequest(fullUrl, configs, fields, getRespType(method));
        Object dataSources = createDataSources(request);
        return dataSources;
    }

    private String parseUrl(Method method) {
        Annotation[] annotations = method.getDeclaringClass().getAnnotations();
        BasePath basePath = ClassUtils.findAnnotation(annotations, BasePath.class);
        if (basePath != null) {
            String domainName = DomainConfig.INSTANCE.getDomainName(basePath.domainType());
            return domainName + basePath.value() + getShortPath(method);
        }
        return Constants.EMPTY;
    }

    private String getShortPath(Method method) {
        String methodName = method.getName();
        int indexOf$ = methodName.indexOf(METHOD_NAME_DIVIDE);
        if (indexOf$ > 0) {
            return methodName.substring(0, indexOf$);
        }
        return methodName;
    }

    private Type getRespType(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            return ((ParameterizedType) returnType).getActualTypeArguments()[0];
        }
        return null;
    }

    private MtMap<String, String> parseField(Method method, Object[] paramValues) {
        MtMap<String, String> fields = new MtMap<>();
        DefaultParam defaultField = ClassUtils.findAnnotation(method.getAnnotations(), DefaultParam.class);
        if (null != defaultField) {
            for (int i = 0; i < defaultField.keys().length; i++) {
                putParam(defaultField.keys()[i], defaultField.values()[i], fields);
            }
        }

        if (DataUtils.isEmptyArray(paramValues)) {
            return fields;
        }
        int fieldCount = paramValues.length;
        String[] fieldNames = new String[fieldCount];
        parseFieldNames(method, fieldNames);

        for (int i = 0; i < fieldCount; i++) {
            putParam(fieldNames[i], paramValues[i], fields);
        }
        return fields;
    }

    private void parseFieldNames(Method method, String[] fieldNames) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return;
        }
        int fieldCount = parameterAnnotations.length;
        for (int i = 0; i < fieldCount; i++) {
            Param fieldAnn = ClassUtils.findAnnotation(parameterAnnotations[i], Param.class);
            if (null == fieldAnn) {
                continue;
            }
            fieldNames[i] = fieldAnn.value();
        }
    }

    private void putParam(String name, Object value, MtMap<String, String> params) {
        if (TextUtils.isEmpty(name) || value == null) return;
        params.put(name, value.toString());
    }

    private Object createDataSources(final ApiRequest request) {
        if (request.getRespType() != null) {
            return new DataSrcBuilder<>(request);
        }

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                new ApiHttpWorker(request).proceed();
            }
        }).subscribeOn(MtSchedulers.io());
    }

}
