package com.duoyue.app.common.data.response.bookshelf;


public class TaskFinishResp {
    private int taskId;
    private int status;//任务状态；1未完成，3已完成，已完成状态下当天不再来请求
    private int bookBean;//书豆
    private int readTime;//阅读时长

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBookBean() {
        return bookBean;
    }

    public void setBookBean(int bookBean) {
        this.bookBean = bookBean;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }
}
