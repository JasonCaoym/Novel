package com.duoyue.lib.base.app.user;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("uid")
    public String uid;

    /**
     * 用户类型(1.游客,2.微信,3.QQ,4.微博,5手机)
     */
    @SerializedName("type")
    public int type;

    @SerializedName("headImg")
    public String headImg;

    @SerializedName("nickName")
    public String nickName;

    @SerializedName("token")
    public String token;

    @SerializedName("bindedTypes")
    public String bindedTypes;

    @SerializedName("status")
    public int status;

    @SerializedName("inviteCode")
    public String inviteCode;

    /**
     * 性别(  1 男  2 女  0 未知)
     */
    @SerializedName("sex")
    public int sex;

    /**
     * 0:A模式;1:B模式；前端需要设置默认值0
     */
    @SerializedName("model")
    public int model;

    /**
     * 书豆
     */
    @SerializedName("bookBeans")
    public int bookBeans;

    @Override
    public String toString() {
        return "uid:" + uid + ", type:" + type + ", headImg:" + headImg + ", nickName:" + nickName + ", token:" + token + ", bindedTypes:" + bindedTypes + ", status:" + status + ", inviteCode:" + inviteCode
                + ", sex:" + sex + ", model:" + model;
    }
}
