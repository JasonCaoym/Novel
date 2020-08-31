package com.share.platform.login.instance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.share.platform.ShareConfig;
import com.share.platform.ShareLogger;
import com.share.platform.ShareManager;
import com.share.platform.login.LoginListener;
import com.share.platform.login.LoginPlatform;
import com.share.platform.login.LoginResult;
import com.share.platform.login.result.BaseToken;
import com.share.platform.login.result.WxToken;
import com.share.platform.login.result.WxUser;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WxLoginInstance extends LoginInstance {

    public static final String SCOPE_USER_INFO = "snsapi_userinfo";
    private static final String SCOPE_BASE = "snsapi_base";

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    private IWXAPI mIWXAPI;

    private LoginListener mLoginListener;

    private OkHttpClient mClient;

    private boolean fetchUserInfo;

    public WxLoginInstance(Activity activity, LoginListener listener, boolean fetchUserInfo) {
        super(activity, listener, fetchUserInfo);
        mLoginListener = listener;
        mIWXAPI = WXAPIFactory.createWXAPI(activity, ShareManager.CONFIG.getWxId());
        mClient = new OkHttpClient();
        this.fetchUserInfo = fetchUserInfo;
    }

    @Override
    public void doLogin(Activity activity, LoginListener listener, boolean fetchUserInfo) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE_USER_INFO;
        req.state = String.valueOf(System.currentTimeMillis());
        mIWXAPI.sendReq(req);
    }

    /**
     * 设置授权Code
     *
     * @param code
     */
    private void setCode(String code) {
        if (TextUtils.isEmpty(ShareConfig.instance().getWxSecret())) {
            //直接返回Code.
            if (mLoginListener != null) {
                mLoginListener.loginSuccess(new LoginResult(LoginPlatform.WX, code));
            }
        } else {
            //获取Token.
            getToken(code);
        }
    }

    @SuppressLint("CheckResult")
    private void getToken(final String code) {
        Flowable.create(new FlowableOnSubscribe<WxToken>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<WxToken> wxTokenEmitter) throws Exception {
                Request request = new Request.Builder().url(buildTokenUrl(code)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxToken token = WxToken.parse(jsonObject);
                    wxTokenEmitter.onNext(token);
                } catch (IOException | JSONException e) {
                    wxTokenEmitter.onError(e);
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WxToken>() {
                    @Override
                    public void accept(@NonNull WxToken wxToken) throws Exception {
                        if (fetchUserInfo) {
                            mLoginListener.beforeFetchUserInfo(wxToken);
                            fetchUserInfo(wxToken);
                        } else {
                            mLoginListener.loginSuccess(new LoginResult(LoginPlatform.WX, wxToken));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mLoginListener.loginFailure(new Exception(throwable.getMessage()));
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchUserInfo(final BaseToken token) {
        Flowable.create(new FlowableOnSubscribe<WxUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<WxUser> wxUserEmitter) throws Exception {
                Request request = new Request.Builder().url(buildUserInfoUrl(token)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxUser user = WxUser.parse(jsonObject);
                    wxUserEmitter.onNext(user);
                } catch (IOException | JSONException e) {
                    wxUserEmitter.onError(e);
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WxUser>() {
                    @Override
                    public void accept(@NonNull WxUser wxUser) throws Exception {
                        mLoginListener.loginSuccess(
                                new LoginResult(LoginPlatform.WX, token, wxUser));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mLoginListener.loginFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        mIWXAPI.handleIntent(data, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
            }

            @Override
            public void onResp(BaseResp baseResp) {
                if (baseResp instanceof SendAuth.Resp && baseResp.getType() == 1) {
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    switch (resp.errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            setCode(resp.code);
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            mLoginListener.loginCancel();
                            break;
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_SENT_FAILED));
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_UNSUPPORT));
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_AUTH_DENIED));
                            break;
                        default:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_AUTH_ERROR));
                    }
                }
            }
        });
    }

    @Override
    public boolean isInstall(Context context) {
        return mIWXAPI.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        if (mIWXAPI != null) {
            mIWXAPI.detach();
        }
    }

    private String buildTokenUrl(String code) {
        return BASE_URL
                + "oauth2/access_token?appid="
                + ShareManager.CONFIG.getWxId()
                + "&secret="
                + ShareManager.CONFIG.getWxSecret()
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    private String buildUserInfoUrl(BaseToken token) {
        return BASE_URL
                + "userinfo?access_token="
                + token.getAccessToken()
                + "&openid="
                + token.getOpenid();
    }
}