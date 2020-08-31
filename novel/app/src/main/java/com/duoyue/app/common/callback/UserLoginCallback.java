package com.duoyue.app.common.callback;

import com.duoyue.lib.base.app.user.UserInfo;

/**
 * 用户登录回调对象
 * @author caoym
 * @data 2019/4/25  0:02
 */
public interface UserLoginCallback
{
    /**
     * 登录开始.
     * @param type 账号类型
     */
    void onLoginStart(int type);

    /**
     * 登录取消.
     * @param type 账号类型
     */
    void onLoginCancel(int type);

    /**
     * 登录成功.
     * @param type 账号类型
     * @param info 用户信息
     */
    void onLoginSucc(int type, UserInfo info);

    /**
     * 登录失败
     * @param type 账号类型
     * @param errMsg
     */
    void onLoginFail(int type,String errMsg);
}
