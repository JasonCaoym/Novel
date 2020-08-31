package com.duoyue.mianfei.xiaoshuo.data.bean;

import java.io.Serializable;
import java.util.List;

public class SignBean implements Serializable {

    /**
     * num : 5
     * sum : 8
     * show : 已连续签到8天
     * signList : [{"id":1,"signName":"第1天","signBeans":10,"appId":13},{"id":1,"signName":"第2天","signBeans":10,"appId":13},{"id":1,"signName":"第3天","signBeans":10,"appId":13},{"id":1,"signName":"第4天","signBeans":10,"appId":13},{"id":1,"signName":"第5天","signBeans":10,"appId":13},{"id":1,"signName":"第6天","signBeans":10,"appId":13},{"id":1,"signName":"第7天","signBeans":10,"appId":13}]
     */

    private int num;//已签到天数
    private int sum;//连续签到天数
    private String show;//展示名称
    private int signStatus;//1：当天未签到；2：当天已签到
    private List<SignListBean> signList;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public List<SignListBean> getSignList() {
        return signList;
    }

    public void setSignList(List<SignListBean> signList) {
        this.signList = signList;
    }

    public int getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(int signStatus) {
        this.signStatus = signStatus;
    }

    public static class SignListBean implements Serializable{
        /**
         * id : 1
         * signName : 第1天
         * signBeans : 10
         * appId : 13
         */

        private int id;
        private String signName;
        private int signBeans;
        private int appId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }

        public int getSignBeans() {
            return signBeans;
        }

        public void setSignBeans(int signBeans) {
            this.signBeans = signBeans;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }
    }
}
