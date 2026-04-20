package com.middleware.org.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务启动配置
 */
public class TaskStartConfig {

    private String outputFormat;       // 输出格式：csv, xlsx, json
    private String outputPath;         // 自定义输出路径（可选）
    /**
     * 用户选择的清洗规则列表（ruleCode）
     * 例如：["TRIM", "REMOVE_NULL", "DESENSITIZE"]
     */
    private List<String> rules = new ArrayList<>();

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

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
}