package com.duoyue.app.event;

/**
 * 任务完成更新书豆
 */
public class TaskFinishEvent {

    private int taskId;

    public TaskFinishEvent(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
