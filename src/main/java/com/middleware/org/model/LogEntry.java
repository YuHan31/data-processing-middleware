package com.middleware.org.model;

/**
 * 日志条目
 * 系统内部使用 className/stackTrace 等
 * 面向用户使用 userMessage
 */
public class LogEntry {
    private Long id;
    private String level; // INFO, WARN, ERROR
    private String message;      // 系统原始日志（管理员可见）
    private String userMessage;  // 用户友好的日志提示
    private String taskId;
    private Long timestamp;
    private String stage;        // 任务阶段：PARSE / CLEAN / EXPORT 等
    private String exceptionMessage; // 异常简短信息（仅 ERROR 级别）
    private String stackTrace;    // 堆栈（管理员可见）

    public LogEntry() {
        this.timestamp = System.currentTimeMillis();
    }

    public LogEntry(String level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 便捷构造器：同时设置系统消息和用户消息
     */
    public static LogEntry of(String level, String message, String userMessage) {
        LogEntry entry = new LogEntry(level, message);
        entry.setUserMessage(userMessage);
        return entry;
    }

    public static LogEntry error(String message, String userMessage) {
        return of("ERROR", message, userMessage);
    }

    public static LogEntry info(String message, String userMessage) {
        return of("INFO", message, userMessage);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
