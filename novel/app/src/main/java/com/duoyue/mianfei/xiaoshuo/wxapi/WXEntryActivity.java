package com.duoyue.mianfei.xiaoshuo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.duoyue.app.common.mgr.WeChatMgr;
import com.duoyue.lib.base.log.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

//public class WXEntryActivity extends WXCallbackActivity {
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#WXEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        boolean isFinish = !WeChatMgr.getWXApi().handleIntent(getIntent() , this);
        if (isFinish)
        {
            finish();
        }
        Logger.i(TAG, "onCreate: {}", isFinish);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, "onActivityResult: {}, {}", requestCode, resultCode);
        WeChatMgr.getWXApi().handleIntent(data, this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Logger.i(TAG, "onNewIntent: {}", intent);
        setIntent(intent);
        WeChatMgr.getWXApi().handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时, 会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {
        Logger.i(TAG, "onReq: {}", (baseReq != null ? baseReq.getType() : "NULL"));
        /*switch (baseReq.getType())
        {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

                break;
        }*/
    }

    @Override
    public void onResp(BaseResp baseResp)
    {
        Logger.i(TAG, "onResp: {}", baseResp);
        switch (baseResp.getType())
        {
            //请求授权.
            case ConstantsAPI.COMMAND_SENDAUTH:
                String code = baseResp instanceof SendAuth.Resp ? ((SendAuth.Resp) baseResp).code : null;
                if (!TextUtils.isEmpty(code))
                {
                    //用户换取access_token的code, 仅在ErrCode为0时有效.
                    //var code = baseResp.code
                    //第三方程序发送时用来标识其请求的唯一性的标志, 由第三方程序调用sendReq时传入, 由微信终端回传, state字符串长度不能超过1K.
                    //var state = baseResp.state
                    //微信客户端当前语言.
                    //var lang = baseResp.lang
                    //微信用户当前国家信息
                    //var country = baseResp.country
                    //通过code获取access_token
                    WeChatMgr.authWXSucc(code);
                    break;
                }
                switch (baseResp.errCode)
                {
                    case BaseResp.ErrCode.ERR_OK:
                        //发送成功(用户同意)
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        //发送取消(用户取消)
                        WeChatMgr.authWXFail(WeChatMgr.ERROR_CODE_AUTH_USER_CANCEL);
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        //发送被拒绝(用户拒绝授权)
                        //授权操作(用户拒绝授权)
                        WeChatMgr.authWXFail(WeChatMgr.ERROR_CODE_AUTH_DENIED);
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        //不支持错误
                        WeChatMgr.authWXFail(WeChatMgr.ERROR_CODE_AUTH_UNSUPPORT);
                    default:
                        //未知错误
                        WeChatMgr.authWXFail(WeChatMgr.ERROR_CODE_AUTH_UNKNOWN);
                }
        }
        //结束当前Activity
        finish ();
    }
}
