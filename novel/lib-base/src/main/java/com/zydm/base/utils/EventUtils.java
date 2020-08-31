package com.zydm.base.utils;

import org.greenrobot.eventbus.EventBus;

public class EventUtils {

    private static String TAG = "EventUtils";

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void isRegistered(Object subscriber) {
        EventBus.getDefault().isRegistered(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void post(Object event) {
        LogUtils.d(TAG, "-------------Post event:" + event);
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object stickyEvent) {
        EventBus.getDefault().postSticky(stickyEvent);
    }
}
