package com.middleware.org.model;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    WAITING("等待中"),
    RUNNING("运行中"),
    SUCCESS("成功"),
    FAILED("失败");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}