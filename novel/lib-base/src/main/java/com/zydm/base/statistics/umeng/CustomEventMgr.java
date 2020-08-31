package com.zydm.base.statistics.umeng;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by wangdy on 2017/4/20.
 */

public class CustomEventMgr {
    private static final String TAG = "CustomEventMgr.EventsHandler";
    private static CustomEventMgr instance;
    private EventMethods mEventMethods;

    private CustomEventMgr() {
    }

    public static CustomEventMgr getInstance() {
        if (null == instance) {
            synchronized (CustomEventMgr.class) {
                if (null == instance) {
                    instance = new CustomEventMgr();
                }
            }
        }
        return instance;
    }

    public EventMethods onEvent() {
        if (null == mEventMethods) {
            InvocationHandler handler = new EventsHandler();
            mEventMethods = (EventMethods) Proxy.newProxyInstance(EventMethods.class.getClassLoader(),
                    new Class[]{EventMethods.class}, handler);
        }
        return mEventMethods;
    }
}
