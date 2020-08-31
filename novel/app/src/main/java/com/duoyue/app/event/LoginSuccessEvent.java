package com.duoyue.app.event;

import com.duoyue.lib.base.app.user.UserInfo;

/**
 * 登录成功
 */
public class LoginSuccessEvent {
    private UserInfo userInfo;

    public LoginSuccessEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
