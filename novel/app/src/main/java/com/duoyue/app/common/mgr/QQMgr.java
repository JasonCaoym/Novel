package com.duoyue.app.common.mgr;

import android.app.Activity;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.log.Logger;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * QQ管理类.
 * @author caoym
 * @data 2019/4/4  15:58
 */
public class QQMgr
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#QQMgr";

    /**
     * 当前类对象.
     */
    private static QQMgr sInstance;

    /**
     * Tencent
     */
    private Tencent mTencent;

    /**
     * 构造方法
     */
    private QQMgr()
    {
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, BaseContext.getContext());
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (QQMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new QQMgr();
                }
            }
        }
    }

    /**
     * 加入QQ群
     * @param activity
     * @return
     */
    public static boolean joinQQGroup(Activity activity)
    {
        //创建当前类对象.
        createInstance();
        try
        {
            return sInstance.mTencent.joinQQGroup(activity, Constants.QQ_GROUP_KEY);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "joinQQGroup: {}", throwable);
            return false;
        }
    }

    /**
     * 发送QQ授权登录
     * @param activity
     */
    public static boolean sendAuth(Activity activity, IUiListener uiListener)
    {
        //创建当前类对象.
        createInstance();
        try
        {
            int result = sInstance.mTencent.login(activity, "all", uiListener, false);
            return true;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "sendAuth: {}, {}", activity, throwable);
            return false;
        }
    }

    /**
     * 判断QQ是否已安装.
     * @return
     */
    public static boolean isInstallApp()
    {
        //创建当前类对象.
        createInstance();
        return sInstance.mTencent.isQQInstalled(BaseContext.getContext());
    }
}
