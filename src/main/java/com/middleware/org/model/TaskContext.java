package com.middleware.org.model;

import com.middleware.org.common.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务上下文
 * 包含任务执行所需的所有信息
 */
public class TaskContext {
    private String taskId;
    private String taskName;
    private String inputFilePath;
    private String outputFilePath;
    private String fileType;
    private String outputFormat; // 输出格式：csv, xlsx, json
    private boolean enableCleaning = true; // 是否启用数据清洗
    private boolean enableNormalization = true; // 是否启用数据标准化
    private TaskStatus status;
    private ProcessedData processedData;
    private com.middleware.org.model.DataStatistics statistics;
    private Map<String, Object> parameters;
    private String originalFileName;
    private long fileSize;
    private long uploadTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId; // 用户ID，用于数据隔离

    public TaskContext() {
        this.parameters = new HashMap<>();
        this.status = TaskStatus.UPLOADED;
    }

    public TaskContext(String taskName, String inputFilePath, String outputFilePath) {
        this.taskName = taskName;
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.parameters = new HashMap<>();
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public ProcessedData getProcessedData() {
        return processedData;
    }

    public void setProcessedData(ProcessedData processedData) {
        this.processedData = processedData;
    }

    public com.middleware.org.model.DataStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(com.middleware.org.model.DataStatistics statistics) {
        this.statistics = statistics;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isEnableCleaning() {
        return enableCleaning;
    }

    public void setEnableCleaning(boolean enableCleaning) {
        this.enableCleaning = enableCleaning;
    }

    public boolean isEnableNormalization() {
        return enableNormalization;
    }

    public void setEnableNormalization(boolean enableNormalization) {
        this.enableNormalization = enableNormalization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
