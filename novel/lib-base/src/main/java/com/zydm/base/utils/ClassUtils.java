package com.zydm.base.utils;

import com.zydm.base.data.tools.DataUtils;
import io.reactivex.annotations.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yan on 2017/4/20.
 */

public class ClassUtils {

    public static <A> A findAnnotation(Annotation[] annotations, Class<A> targetClass) {
        if (DataUtils.isEmptyArray(annotations)) {
            return null;
        }
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            boolean assignableFrom = annotationClass.isAssignableFrom(targetClass);
            if (assignableFrom) {
                return (A) annotation;
            }
        }
        return null;
    }

    public static Class getGenericsClass(@NonNull Class curClazz,
                                         @NonNull Class genericsDeclaredSuperClazz,
                                         int genericsIndex) {

        Type superType = curClazz.getGenericSuperclass();
        while (true) {
            if (superType instanceof Class) {
                superType = ((Class) superType).getGenericSuperclass();
                continue;
            }
            if (superType instanceof ParameterizedType) {
                ParameterizedType superParamType = (ParameterizedType) superType;
                if (!genericsDeclaredSuperClazz.equals(superParamType.getRawType())){
                    superType = ((Class)superParamType.getRawType()).getGenericSuperclass();
                    continue;
                }
                Type[] actualTypeArguments = superParamType.getActualTypeArguments();
                Type typeArgument = actualTypeArguments[genericsIndex];
                if (typeArgument instanceof Class) {
                    return (Class) typeArgument;
                } else if (typeArgument instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) typeArgument).getRawType();
                }
            }
            throw new IllegalArgumentException("curClazz:" + curClazz + " not find superclassParam");
        }
    }
}
