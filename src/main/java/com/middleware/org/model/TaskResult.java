package com.middleware.org.model;

/**
 * 任务结果
 * 包含任务执行的状态和结果信息
 */
public class TaskResult {
    private String taskId;
    private String taskName;
    private String status; // Pending, Running, Completed, Failed, Stopped
    private String message;
    private Long startTime;
    private Long endTime;
    private String inputFilePath;
    private String outputFilePath;
    private Integer processedRecords;
    private Integer errorRecords;

    public TaskResult() {
    }

    public TaskResult(String taskId, String taskName, String status) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public Integer getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(Integer processedRecords) {
        this.processedRecords = processedRecords;
    }

    public Integer getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(Integer errorRecords) {
        this.errorRecords = errorRecords;
    }
}
