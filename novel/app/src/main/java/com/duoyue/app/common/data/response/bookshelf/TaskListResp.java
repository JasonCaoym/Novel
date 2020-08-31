package com.duoyue.app.common.data.response.bookshelf;


import java.io.Serializable;
import java.util.List;

public class TaskListResp implements Serializable {


    private List<List<ListBean>> list;

    public List<List<ListBean>> getList() {
        return list;
    }

    public void setList(List<List<ListBean>> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * taskId : 8
         * name : 账户登录
         * desc : 账户登录
         * bookBean : 10
         * unfinishedName : 去完成
         * claimedName : 待领取
         * unclaimedName : 已完成
         * taskType : 新手任务
         * beanType : 0
         * status : 1
         * hide : 0
         * num : 0
         * sum : 0
         */

        private int taskId;
        private String name;
        private String desc;
        private String bookBean;
        private String unfinishedName;
        private String claimedName;
        private String unclaimedName;
        private String taskType;
        private int beanType;
        private int status;
        private int hide;//0：不隐藏，1：隐藏
        private int num;
        private int sum;

        public ListBean(int taskId) {
            this.taskId = taskId;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getBookBean() {
            return bookBean;
        }

        public void setBookBean(String bookBean) {
            this.bookBean = bookBean;
        }

        public String getUnfinishedName() {
            return unfinishedName;
        }

        public void setUnfinishedName(String unfinishedName) {
            this.unfinishedName = unfinishedName;
        }

        public String getClaimedName() {
            return claimedName;
        }

        public void setClaimedName(String claimedName) {
            this.claimedName = claimedName;
        }

        public String getUnclaimedName() {
            return unclaimedName;
        }

        public void setUnclaimedName(String unclaimedName) {
            this.unclaimedName = unclaimedName;
        }

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public int getBeanType() {
            return beanType;
        }

        public void setBeanType(int beanType) {
            this.beanType = beanType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getHide() {
            return hide;
        }

        public void setHide(int hide) {
            this.hide = hide;
        }

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListBean that = (ListBean) o;

            return taskId == that.taskId;
        }

        @Override
        public int hashCode() {
            return taskId;
        }
    }
}
