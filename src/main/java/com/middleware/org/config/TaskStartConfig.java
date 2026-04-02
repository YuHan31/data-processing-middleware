package com.middleware.org.config;

/**
 * 任务启动配置
 */
public class TaskStartConfig {

    private String outputFormat;       // 输出格式：csv, xlsx, json
    private String outputPath;         // 自定义输出路径（可选）
    private boolean enableCleaning = true;      // 是否启用数据清洗
    private boolean enableNormalization = true;  // 是否启用数据标准化

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
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
}