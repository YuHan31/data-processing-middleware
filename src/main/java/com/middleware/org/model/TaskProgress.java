package com.middleware.org.model;

import com.middleware.org.common.TaskStatus;

/**
 * 任务进度
 */
public class TaskProgress {

    private String taskId;
    private TaskStatus status;
    private int percentage;
    private String currentStage;
    private String message;
    private long updateTime;

    public TaskProgress() {
    }

    public TaskProgress(String taskId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
        this.updateTime = System.currentTimeMillis();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updateTime = System.currentTimeMillis();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TaskProgress{" +
                "taskId='" + taskId + '\'' +
                ", status=" + status +
                ", percentage=" + percentage +
                ", currentStage='" + currentStage + '\'' +
                ", message='" + message + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
