package com.middleware.org.model;

/**
 * 数据统计结果
 */
public class DataStatistics {

    private String taskId;
    private long totalRecords;
    private long validRecords;
    private long invalidRecords;
    private long missingValues;
    private long processingTimeMs;
    private String startTime;
    private String endTime;

    public DataStatistics() {
    }

    public DataStatistics(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getValidRecords() {
        return validRecords;
    }

    public void setValidRecords(long validRecords) {
        this.validRecords = validRecords;
    }

    public long getInvalidRecords() {
        return invalidRecords;
    }

    public void setInvalidRecords(long invalidRecords) {
        this.invalidRecords = invalidRecords;
    }

    public long getMissingValues() {
        return missingValues;
    }

    public void setMissingValues(long missingValues) {
        this.missingValues = missingValues;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "DataStatistics{" +
                "taskId='" + taskId + '\'' +
                ", totalRecords=" + totalRecords +
                ", validRecords=" + validRecords +
                ", invalidRecords=" + invalidRecords +
                ", missingValues=" + missingValues +
                ", processingTimeMs=" + processingTimeMs +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
