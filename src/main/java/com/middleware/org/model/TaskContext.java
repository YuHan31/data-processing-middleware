package com.middleware.org.model;

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
    private Map<String, Object> parameters;

    public TaskContext() {
        this.parameters = new HashMap<>();
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
}
