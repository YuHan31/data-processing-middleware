package com.middleware.org;

import java.util.concurrent.Future;

public class Task {
    private final String taskId;
    private final String taskName;
    private final String filePath;
    private String status;
    private Future<?> future;

    public Task(String taskId, String taskName, String filePath) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.filePath = filePath;
        this.status = "Pending";
    }

    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getFilePath() { return filePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Future<?> getFuture() { return future; }
    public void setFuture(Future<?> future) { this.future = future; }
}