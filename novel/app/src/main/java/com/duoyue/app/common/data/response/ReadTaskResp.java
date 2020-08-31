package com.duoyue.app.common.data.response;

import java.io.Serializable;
import java.util.List;

/**
 * 阅读任务响应
 */
public class ReadTaskResp implements Serializable {

    /**
     * readTime : 0
     * stage : 1
     * status : 1
     * readStage : [{"showName":"阅读30分钟","bookBean":10,"stage":1},{"showName":"阅读60分钟","bookBean":20,"stage":2},{"showName":"阅读120分钟","bookBean":20,"stage":3}]
     */

    private long readTime;//当天阅读时长
    private int stage;//下阶段阅读
    private int status;//1：可完成；3：已完成
    private List<ReadStageBean> readStage;

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ReadStageBean> getReadStage() {
        return readStage;
    }

    public void setReadStage(List<ReadStageBean> readStage) {
        this.readStage = readStage;
    }

    public static class ReadStageBean implements Serializable{
        /**
         * showName : 阅读30分钟
         * bookBean : 10
         * stage : 1
         */

        private String showName;
        private int bookBean;
        private int stage;
        private long time;

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        public int getBookBean() {
            return bookBean;
        }

        public void setBookBean(int bookBean) {
            this.bookBean = bookBean;
        }

        public int getStage() {
            return stage;
        }

        public void setStage(int stage) {
            this.stage = stage;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    @Override
    public String toString() {
        return "ReadTaskResp{" +
                "readTime=" + readTime +
                ", stage=" + stage +
                ", status=" + status +
                ", readStage=" + readStage +
                '}';
    }
}
