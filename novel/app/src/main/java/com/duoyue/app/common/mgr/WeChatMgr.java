package com.duoyue.app.common.mgr;

import android.os.Handler;
import android.os.Looper;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.log.Logger;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信授权管理类
 * @author caoym
 * @data 2019/4/4  14:06
 */
public class WeChatMgr
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#WeChatMgr";

    /**
     * 错误码-请求授权失败, AppId为空.
     */
    public static final int ERROR_CODE_APP_ID_EMPTY = 10010;

    /**
     * 错误码-请求授权失败, 解析参数异常.
     */
    public static final int ERROR_CODE_AUTH_PARAM_EXCEPTION = 10011;

    /**
     * 错误码-请求授权失败, 未安装微信.
     */
    public static final int ERROR_CODE_WX_NOT_INSTALLED = 10012;

    /**
     * 错误码-请求授权失败, 用户取消授权.
     */
    public static final int ERROR_CODE_AUTH_USER_CANCEL = 10013;

    /**
     * 错误码-请求授权失败, 发送被拒绝(用户拒绝授权).
     */
    public static final int ERROR_CODE_AUTH_DENIED = 10014;

    /**
     * 错误码-请求授权失败, 不支持错误.
     */
    public static final int ERROR_CODE_AUTH_UNSUPPORT = 10015;

    /**
     * 错误码-请求授权失败, 未知错误.
     */
    public static final int ERROR_CODE_AUTH_UNKNOWN = 10016;

    /**
     * 错误码-请求授权失败, 参数不合法, 未被SDK处理.
     */
    public static final int ERROR_CODE_AUTH_PARAM_ERROR = 10017;

    /**
     * 当前类对象.
     */
    private static WeChatMgr sInstance;

    /**
     * 微信API
     */
    private IWXAPI mWXApi;

    /**
     * 回调对象
     */
    private Callback mCallback;

    /**
     * 构造方法
     */
    private WeChatMgr()
    {
        //注册微信.
        regToWx();
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (WeChatMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new WeChatMgr();
                }
            }
        }
    }

    /**
     * 注册到微信.
     */
    private void regToWx()
    {
        try {
            // 通过WXAPIFactory工厂, 获取IWXAPI的实例
            mWXApi = WXAPIFactory.createWXAPI(BaseContext.getContext(), Constants.WX_APP_ID, true);
            //将应用的appId注册到微信
            mWXApi.registerApp(Constants.WX_APP_ID);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "regToWx: ", throwable);
        }
    }

    /**
     * 微信授权
     */
    public static void sendAuth(Callback callback)
    {
        try
        {
            //创建当前类对象.
            createInstance();
            sInstance.mCallback = callback;
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wx_login_novel";
            sInstance.mWXApi.sendReq(req);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "sendAuth: {}", throwable);
        }
    }

    /**
     * 获取WXApi
     * @return
     */
    public static IWXAPI getWXApi()
    {
        //创建当前类对象.
        createInstance();
        return sInstance.mWXApi;
    }

    /**
     * 判断微信是否已安装.
     * @return
     */
    public static boolean isInstallApp()
    {
        //创建当前类对象.
        createInstance();
        return sInstance.mWXApi.isWXAppInstalled();
    }

    /**
     * 微信授权成功.
     * @param code
     */
    public static void authWXSucc(String code)
    {
        if (sInstance != null && sInstance.mCallback != null)
        {
            sInstance.mCallback.onAuthWXSucc(code);
            sInstance.mCallback = null;
        }
    }

    /**
     * 微信授权失败
     * @param errorCode
     */
    public static void authWXFail(int errorCode)
    {
        if (sInstance != null && sInstance.mCallback != null)
        {
            sInstance.mCallback.onAuthWXFail(errorCode, null);
            sInstance.mCallback = null;
        }
    }

    /**
     * 监听类
     */
    interface Callback
    {
        /**
         * 授权微信成功.
         * @param code
         */
        void onAuthWXSucc(String code);

        /**
         * 授权微信失败.
         * @param errCode
         * @param message
         */
        void onAuthWXFail(int errCode, String message);
    }
}
