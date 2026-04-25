package com.middleware.org.service;

import com.middleware.org.model.LogEntry;

import java.util.List;

/**
 * 日志服务接口
 */
public interface ILogService {

    /**
     * 记录信息日志
     */
    void info(String taskId, String message);

    /**
     * 记录信息日志（带阶段）
     */
    void info(String taskId, String stage, String message);

    /**
     * 记录警告日志
     */
    void warn(String taskId, String message);

    /**
     * 记录警告日志（带阶段）
     */
    void warn(String taskId, String stage, String message);

    /**
     * 记录错误日志（同时记录友好提示给用户）
     */
    void error(String taskId, String systemMessage, String userMessage);

    /**
     * 记录错误日志（带阶段）
     */
    void error(String taskId, String stage, String systemMessage, String userMessage);

    /**
     * 记录错误日志（原始重载兼容）
     */
    void error(String taskId, String message);

    /**
     * 查询日志（用户视图，仅返回友好日志）
     */
    List<LogEntry> queryLogs(String taskId);

    /**
     * 获取任务失败原因（仅 ERROR，最后一条错误）
     */
    String getTaskErrorReason(String taskId);
}