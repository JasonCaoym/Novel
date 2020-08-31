package com.zydm.base.data.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yan on 2017/4/7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiConfigs {

    int expTime() default 0;

    int updateLabel() default 0;

    int[] attentionLabels() default 0;

    boolean isNeedAddTokenParam() default true;
}
