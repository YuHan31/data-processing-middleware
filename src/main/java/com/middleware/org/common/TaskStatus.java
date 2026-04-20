package com.middleware.org.common;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    UPLOADED("已上传"),
    PARSING("解析中"),
    CLEANING("清洗中"),
    EXPORTING("导出中"),
    FINISHED("已完成"),
    FAILED("失败");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
